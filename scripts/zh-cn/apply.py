# -*- coding: utf-8 -*-
"""把中文词典重放到 Morphe Google Photos 的扩展源码，并列出待补词条的英文。

设计要点（保证不破坏功能）：
1. 只处理 extensions/ 下的 .java；
2. 日志行（Logger./SafeLog./printInfo/printException）整行跳过；
3. 先全局扫描 contains(/getHeaderField( 的参数位，命中的字符串列入“禁止翻译”，
   保证与外部数据比较时不失效；同一字符串要么全局都翻，要么都不翻；
4. 词典里没有的文案保持英文，只在日志里列出，等人工补词条（不做机器翻译）。
"""
import io, os, re, json, sys

HERE = os.path.dirname(os.path.abspath(__file__))
DICT_PATH = os.path.join(HERE, "zh-cn-dict.json")
ROOT = sys.argv[1] if len(sys.argv) > 1 else "."
SCAN = os.path.join(ROOT, "extensions")

DICT = json.load(io.open(DICT_PATH, encoding="utf-8"))

LOG_LINE = re.compile(r"Logger\.|SafeLog\.|printInfo|printDebug|printException")
CALL_ARG = re.compile(r'(?:contains|getHeaderField)\(\s*"((?:[^"\\]|\\.)*)"')
LITERAL = re.compile(r'"((?:[^"\\]|\\.)*)"')
ALLOW = {"Custom Flags"}
UI_CALL = re.compile(r"\.setText\(|Toast\.makeText|createM3|createSectionHeader|createPreferenceItem|createSwitchPreferenceItem|setHint\(|setMessage\(")
CJK = re.compile(r"[\u4e00-\u9fff]")
PROSE = re.compile(r"[A-Za-z]{3,}")
SKIP_TEXT = re.compile(r"^[a-z0-9._:\-]+$|^quantum_|^com\.|^android\.|^app\.|^http|^/|sans-serif|^[0-9]")
NO_LIT = re.compile(r'"(?:[^"\\]|\\.)*"')


def java_files():
    for dirpath, _, names in os.walk(SCAN):
        for name in names:
            if name.endswith(".java"):
                yield os.path.join(dirpath, name)


def read(path):
    return io.open(path, encoding="utf-8", newline="").read()


def blocks(text):
    """按语句切块；先抹掉字符串内容再数括号，避免文案里的括号打乱边界。"""
    result, buf, depth = [], [], 0
    for i, line in enumerate(text.splitlines(keepends=True), 1):
        buf.append((i, line))
        clean = NO_LIT.sub('""', line)
        depth += clean.count("(") - clean.count(")")
        if depth <= 0:
            result.append((buf[0][0], [l for _, l in buf], "".join(l for _, l in buf)))
            buf, depth = [], 0
    if buf:
        result.append((buf[0][0], [l for _, l in buf], "".join(l for _, l in buf)))
    return result


# ---------- 1. 禁止翻译集合 ----------
protected = set()
for path in java_files():
    for line in read(path).splitlines():
        if LOG_LINE.search(line):
            continue
        for m in CALL_ARG.finditer(line):
            protected.add(m.group(1))
protected -= ALLOW


def worth_reporting(v):
    if v in DICT or v in protected:
        return False
    if not PROSE.search(v) or CJK.search(v) or SKIP_TEXT.search(v):
        return False
    if "\\" in v or "%" in v or '\\"' in v:
        return False
    if any(c in v for c in "{}<>=") or v != v.strip():
        return False
    if re.search(r"(failed|error|exception|thread|mutex)$", v) or re.match(r"^[a-z][a-z0-9_.]*:\s", v):
        return False
    if re.search(r"[a-z][A-Z]", v) and " " not in v:
        return False
    if ("/" in v or "_" in v) and " " not in v:   # MIME / 路径 / 标识符
        return False
    if re.fullmatch(r"[A-Z]+", v):     # 全大写常量
        return False
    return True


# ---------- 2. 重放 ----------
def apply_line(line):
    if LOG_LINE.search(line):
        return line, 0
    n = 0

    def repl(m):
        nonlocal n
        key = m.group(1)
        if key in protected or key not in DICT:
            return m.group(0)
        n += 1
        return '"' + DICT[key] + '"'

    return LITERAL.sub(repl, line), n


hits = files = 0
left = []
for path in java_files():
    raw = read(path)
    out, total = [], 0
    for line in raw.splitlines(keepends=True):
        new_line, n = apply_line(line)
        out.append(new_line)
        total += n
    if total:
        io.open(path, "w", encoding="utf-8", newline="").write("".join(out))
        files += 1
        hits += total
    for start, raw2, joined2 in blocks("".join(out)):
        if not UI_CALL.search(joined2):
            continue
        for idx, line in enumerate(raw2):
            if LOG_LINE.search(line):
                continue
            for m in LITERAL.finditer(line):
                v = m.group(1)
                if CJK.search(v) or v in DICT:
                    continue
                if not worth_reporting(v):
                    continue
                left.append((os.path.relpath(path, ROOT), start + idx, v))

print(f"重放完成：{files} 个文件，{hits} 处文案（词典 {len(DICT)} 条，保护 {len(protected)} 条）")
print(f"待补词条的界面英文：{len(left)} 处（保持英文）")
for f, i, v in left[:40]:
    print(f"  {f}:{i}  {v[:70]}")
if len(left) > 40:
    print(f"  ... 其余 {len(left) - 40} 处见日志")
