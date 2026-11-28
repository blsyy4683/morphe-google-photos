# -*- coding: utf-8 -*-
"""判断是否需要重新构建中文补丁包。

不依赖仓库里的状态文件（人工推送会把它覆盖掉），而是直接比较：
  上游参照物的发布时间/提交时间  vs  我们自己上次发布的中文包发布时间
前者更新才构建。

用法：python3 decide.py <stable|dev>
环境变量：GH_TOKEN、GITHUB_REPOSITORY、FORCE、GITHUB_OUTPUT
"""
import datetime
import json
import os
import sys
import urllib.request

UP_REPO = "Akash-Sriram/morphe-google-photos"
OUR_REPO = os.environ.get("GITHUB_REPOSITORY", "")
TOK = os.environ.get("GH_TOKEN", "")
HEADERS = {"User-Agent": "zh-cn-bot", "Accept": "application/vnd.github+json"}
if TOK:
    HEADERS["Authorization"] = "Bearer " + TOK

CHANNEL = sys.argv[1] if len(sys.argv) > 1 else "stable"
OUR_TAG = "zh-cn-latest" if CHANNEL == "stable" else "zh-cn-dev-latest"


def api(url):
    try:
        with urllib.request.urlopen(urllib.request.Request(url, headers=HEADERS), timeout=30) as r:
            return json.load(r)
    except Exception as exc:  # 401/403/404 都当作“未知”，由调用方处理
        return {"__error": str(exc)}


def epoch(value):
    if not value:
        return 0
    try:
        return datetime.datetime.fromisoformat(value.replace("Z", "+00:00")).timestamp()
    except ValueError:
        return 0


def show(ts):
    if not ts:
        return "<无>"
    return datetime.datetime.fromtimestamp(ts, datetime.timezone.utc).strftime("%Y-%m-%d %H:%M UTC")


# ---- 上游参照 ----
if CHANNEL == "stable":
    up = api(f"https://api.github.com/repos/{UP_REPO}/releases/latest")
    ref = up.get("tag_name", "") if "__error" not in up else ""
    up_time = epoch(up.get("published_at"))
else:
    up = api(f"https://api.github.com/repos/{UP_REPO}/commits/dev")
    ref = up.get("sha", "") if "__error" not in up else ""
    up_time = epoch((up.get("commit") or {}).get("committer", {}).get("date"))

# ---- 我们上次的构建 ----
ours = api(f"https://api.github.com/repos/{OUR_REPO}/releases/tags/{OUR_TAG}")
our_time = epoch(ours.get("published_at") or ours.get("created_at"))

force = os.environ.get("FORCE", "").strip().lower() == "true"

if not ref:
    changed, why = True, "拿不到上游参照，按需要构建处理"
elif not our_time:
    changed, why = True, "尚无已构建的中文包"
elif up_time > our_time:
    changed, why = True, "上游已更新，发布时间晚于上次构建"
else:
    changed, why = False, "上游无更新，跳过构建"
if force:
    changed, why = True, "手动勾选了强制构建"

print(f"通道: {'稳定版' if CHANNEL == 'stable' else '测试版'}")
print(f"上游参照: {ref or '<空>'}")
print(f"上游时间: {show(up_time)}")
print(f"上次构建: {show(our_time)}")
print(f"结论: {why}（changed={str(changed).lower()}）")

out = os.environ.get("GITHUB_OUTPUT")
if out:
    with open(out, "a", encoding="utf-8") as fh:
        fh.write(f"changed={'true' if changed else 'false'}\n")
        fh.write(f"upstream_ref={ref}\n")
        fh.write(f"upstream_time={show(up_time)}\n")
