# Morphe Google Photos (MGP)

> 本页为中文译本。上游 README 更新时，本页内容不会自动跟随，仅供参考。

**Google 相册**的 Morphe 补丁，源自 [RookieEnough/De-Vanced](https://github.com/RookieEnough/De-Vanced)，并在此基础上加入 Pixel 功能伪装、GmsCore/MicroG 支持、应用内开关控制（参考 [polodarb/GMS-Flags-Reborn](https://github.com/polodarb/GMS-Flags-Reborn)）、DCIM 文件夹备份控制（参考 [RevealedSoulEven/XposedPhotosFIX](https://github.com/RevealedSoulEven/XposedPhotosFIX)），以及内置工具箱（参考 [xob0t/Google-Photos-Toolkit](https://github.com/xob0t/Google-Photos-Toolkit)）。

---

## 🚀 主要功能与增强

### 1. 🔄 应用内动态备份配置档与 Video Boost 直通
可直接在 **Morphe 设置 $\rightarrow$ 备份存储配置档** 中切换备份与设备身份配置，无需重新编译或重新打补丁：
* **自动同步模式（推荐）**：动态映射原画质 $\rightarrow$ **Pixel XL**（不计流量的原画质）、存储空间节省模式 $\rightarrow$ **Pixel 5**（不计流量的存储空间节省）。在相册设置里切换备份画质时，会自动同步设备配置档。
* **Google Pixel XL**：强制使用不计流量的原画质备份。相册内的画质选项被锁定为原画质，防止误操作消耗配额。
* **Google Pixel 5**：强制使用不计流量的存储空间节省模式备份。
* **原设备（已关闭伪装）**：临时关闭设备伪装，恢复真实硬件身份（`Build.MODEL`、`Build.DEVICE` 等）。
  * **可使用 Video Boost**：真实 Pixel 设备（Pixel 8 Pro、9 Pro）的用户可触发 Google 云端的 **Video Boost** 与 **Night Sight Video** 处理，无需安装两份应用。
  * **完整备份控制**：重新开放全部 3 档官方备份画质（**原画质**、**存储空间节省**、**极速**）。
* **补丁级配置档选择（精简构建适用）**：`Spoof features` 补丁提供补丁级选项（`profile`：`auto`、`pixel-xl`、`pixel-5`）。没有工具箱 / 开关管理器的精简版本，可在初次打补丁时直接选定要伪装的配置档。

---

### 2. ⚡ 字节码级设备伪装（Android 12 – 16）
* **直接替换 Smali 的 SGET**：在 smali 字节码层直接注入 `invoke-static` 调用，改写身份字段（`Build.MODEL`、`Build.DEVICE`、`Build.PRODUCT`、`Build.FINGERPRINT`、`Build.MANUFACTURER`、`Build.BRAND`）。绕过 Android 12+ 对 `static final` 字段的反射限制（否则会抛 `IllegalAccessException`），避免账号配额被消耗。
* **编解码与硬件隔离**：把媒体播放、ExoPlayer、视频解码器相关类（`Landroidx/media`、`Lcom/google/android/exoplayer2`、`/videoplayer/`、`/codec/`）排除在伪装之外，保留硬件加速的 4K/60fps/HDR 解码，防止播放卡顿。

---

### 3. 🎨 独立的 Material 3 Morphe 设置页
用原生全屏 Material 3 设置界面取代旧的悬浮层，可从账号菜单或应用设置进入：
* **动态 Material You 主题**：跟随系统与 AMOLED 深色模式的配色（`Theme.get(activity)`）。
* **模块化分组**：备份配置档、Phenotype 开关、工具箱、AI 模型、创作保存记录、系统诊断分区清晰。

---

### 4. 🧪 应用内 Phenotype 开关管理器与精选预设
* **实时搜索与筛选**：在应用内直接查看、搜索、切换数百个 Google 相册实验性改版与功能开关，无需 root，也不需要电脑。
* **一键精选预设**：快速应用经过验证的开关组合，包括 **Ultra Pixel 体验**、**现代界面与 Bento 布局**、**悬浮导航栏**、**动态壁纸**。
* **备份与重置**：导出 / 导入开关配置，或一键把所有覆盖项恢复为官方默认值。

---

### 5. 🛠️ 原生工具箱与媒体工具
* **集成 Google 相册工具箱**：内置创作批量下载、本地去重与媒体筛选工具。
* **存储与缓存清理**：清理缩略图内存缓存与临时下载文件，回收本机存储空间。
* **创作保存记录管理**：重置「为你生成」（MFY）卡片与故事精选的下载、保存记录，让此前保存过的创作可以重新下载与恢复。

---

### 6. 🤖 端侧 AI 与机器学习模型管理
* **AI 模型检查与预置**：校验、下载并预置端侧 AI 资源，涵盖 **Gemini Nano**、**魔术橡皮擦**、**抠图滤镜**、**人像光效**。
* **0MB MDD 绕过**：绕过 Mobile Data Download 的 0MB 检查，避免 AI 资源在非 Pixel 设备或不计流量网络下卡住不下载。

---

### 7. 📁 DCIM 与按文件夹备份控制
* **相机文件夹独立控制**：解除 `DCIM/Camera` 强制常开备份的限制，**收藏集 $\rightarrow$ 本机** 下每个设备文件夹都可单独开关。
* **本地创作拦截**：把「为你生成」创作卡片与故事精选直接保存到 `DCIM/Google Photos`，并写入原始 EXIF 拍摄时间，使相册能以 Pixel 的不计流量通道备份（**占用 0 字节配额**）。

---

### 8. 🌐 通用 GmsCore / MicroG 账号与头像桥接
* **完整的免 root MicroG 支持**：无需专有 Google Play 服务即可正常使用。
* **同步头像桥接**：在所有界面组件中显示高清 Google 账号头像：
  * 主工具栏账号圆盘（`og_apd_internal_image_view`）
  * Bento 中心大头像（`og_bento_selected_account_avatar`）
  * 收起与展开的账号切换行（`og_bento_header_account_avatar`）

---

### 9. 📊 内置诊断与实时日志
* **实时 logcat 流**：在应用内直接读取 Google 相册与 Morphe 的 logcat 输出。
* **诊断报告导出**：生成并分享完整调试报告，含 Phenotype 开关导出、当前存储策略与设备参数，便于排查问题。

---

## 🩹 补丁列表

<!-- PATCHES_START EXPANDED -->
> **[v1.18.0](https://github.com/Akash-Sriram/morphe-google-photos/releases/tag/v1.18.0)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;共 8 个补丁
<details open>
<summary>📦 Google 相册&nbsp;&nbsp;•&nbsp;&nbsp;8 个补丁</summary>
<br>

**🎯 支持的相册版本：**

| 🧪&nbsp;8.0.0.1011730683&nbsp;（实验性） | 7.99.0.1007655401 |
| :---: | :---: |

| 💊&nbsp;补丁 | 📜&nbsp;说明 |
|----------|----------------|
| [AMOLED dark theme](#amoled-dark-theme) | 把相册的深色界面改为纯黑，浅色模式保持不变。 |
| [Custom branding](#custom-branding) | 替换相册的应用名、桌面图标与应用内标识为自定义品牌（紫 / 青 / 靛 / 灰四色风车，手绘风格）。 |
| [DCIM & backup control](#dcim-backup-control) | 关闭相机及其他 DCIM 文件夹的强制常开备份，改为按文件夹单独控制；在 Morphe 设置中新增「仅上传照片」开关，让自动备份跳过视频；并把回忆拼图与创作导出到 DCIM/Google Photos，实现不占配额的备份。 |
| [Flag manager & Toolkit](#flag-manager-toolkit) | 在相册设置中启用应用内开关管理器与 Morphe 工具箱，可切换实验性界面改版、视频编辑工具与功能开关。 |
| [GmsCore support](#gmscore-support) | 启用 GmsCore / MicroG 支持，并为免 root 安装重命名包名。免 root 的标准安装请保持启用；只有使用官方 Google Play 服务的 root 系统安装才需要关闭。 |
| [Media engine & AI fixes](#media-engine-ai-fixes) | 绕过 AI 模型的 0MB 下载检查，并修复故事与界面中的字体加载问题，避免文字空白与字体回退。 |
| [Official package name](#official-package-name) | 保留官方包名（`com.google.android.apps.photos`）而不重命名为 `app.morphe.android.apps.photos`，同时把版本号设为最大值以阻止 Play 商店自动更新覆盖。root 挂载安装必须启用；免 root 但设备出厂未预装相册的用户也可选用。 |
| [Spoof features](#spoof-features) | 伪装设备以启用 Google Pixel 独占功能，包括无限存储。 |

</details>

<!-- PATCHES_END -->

---

## ⚠️ 限制与服务端注意事项

1. **回忆 / 故事创作与「为你生成」（MFY）**：
   * 直接通过云端渲染保存创作，会触发 Google 后端的服务端处理，从而绕过设备伪装并消耗 Google Drive 存储配额。
   * **解决办法**：`DCIM & backup control` 补丁会拦截创作保存，改为下载到本地 `DCIM/Google Photos` 并写入 EXIF 时间戳。相册随后通过 Pixel 的不计流量通道备份，**占用 0 字节配额**。

2. **Video Boost 与 Night Sight Video（Pixel 8 Pro / 9 Pro）**：
   * Google 数据中心的 GPU 集群只处理已验证合格设备的 Video Boost 上传。伪装成 Pixel XL 会使 Video Boost 的云端处理失效。
   * **解决办法**：在 Morphe 设置中切到 **原设备（已关闭伪装）** 配置档，用真实硬件身份上传 Video Boost 原始文件；处理完成后再切回 **Pixel XL**，把生成的视频永久免费备份。

3. **「释放空间」的统计方式**：
   * 「释放空间」页显示的百分比（*「设备存储：已用 X%」*）统计的是**整台手机的内部存储（`/data`）**（系统 + 应用 + 缓存 + 照片），由 Android 的 `StatFs` 直接读取，并非按伪装后的 Pixel 容量计算。

---

## 📦 可直接安装的构建

已签名的预编译 APK 由自动流程编译，可在 **[Akash-Sriram/GooglePhotos-Patched](https://github.com/Akash-Sriram/GooglePhotos-Patched/releases)** 获取。
