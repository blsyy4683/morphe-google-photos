package app.morphe.extension.shared.patches.flags;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import app.morphe.extension.shared.Logger;

/**
 * Registry for Morphe Google Photos Flags.
 * Pre-populated with verified flags, tool-centric categories, and subsystem trigger explanations.
 * Contains only the flags that override Google's defaults or provide custom features.
 */
public final class PhotoFlagsRegistry {

    private PhotoFlagsRegistry() {}

    public enum FlagType {
        BOOLEAN,
        LONG,
        STRING,
        FLOAT
    }

    public static class CuratedFlag {
        public final String key;
        public final String title;
        public final String description;
        public final String category;
        public final String triggerTarget;
        public final FlagType type;
        public final Object defaultValue;

        public CuratedFlag(String key, String title, String description, String category, String triggerTarget, FlagType type, Object defaultValue) {
            this.key = key;
            this.title = title;
            this.description = description;
            this.category = category;
            this.triggerTarget = triggerTarget;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public boolean isBoolean() {
            return type == FlagType.BOOLEAN;
        }

        public boolean isLong() {
            return type == FlagType.LONG;
        }

        public boolean isFloat() {
            return type == FlagType.FLOAT;
        }
    }

    public static final List<CuratedFlag> CURATED_FLAGS = new ArrayList<>();
    public static final Map<String, CuratedFlag> FLAG_MAP = new LinkedHashMap<>();
    public static final Map<String, String> CATEGORY_TRIGGERS = new LinkedHashMap<>();

    static {
        CATEGORY_TRIGGERS.put("🤖 Gemini 与 Ask Photos 搜索", "⚡ 触发：自然语言照片搜索、对话式查询引擎与 Gemini 星光效果");
        CATEGORY_TRIGGERS.put("🪄 AI 照片与视频编辑工具", "⚡ 触发：魔术编辑器套索选择、AI Enhance V2 与 Bluejay AI 后端");
        CATEGORY_TRIGGERS.put("🎞️ 创建页：动效与动画", "⚡ 触发：动画工具卡、连拍循环生成与动态照片防抖浮标");
        CATEGORY_TRIGGERS.put("🧭 现代导航与筛选", "⚡ 触发：「本机」媒体筛选按钮与本机照片隔离");
        CATEGORY_TRIGGERS.put("🎬 创建页与 Reels 混剪中心", "⚡ 触发：现代 AuraTab 商店页、视频混剪配方与 Reels 风格预览卡");
        CATEGORY_TRIGGERS.put("⭕ OneGoogle：订阅者头像光环", "⚡ 触发：头像外的蓝色 Gemini 光环");
        initFlags();
    }

    private static void initFlags() {
        // 🤖 Gemini & Ask Photos Search (2 Flags)
        register("3013", "Gemini 搜索界面与星光标志", "启用现代 Gemini AI 搜索界面与可交互星光高亮（1 = 现代流体展开）。", "🤖 Gemini 与 Ask Photos 搜索", "Gemini 界面与星光动效", FlagType.LONG, 1L);
        register("45724258", "Ask Photos AI 自然语言搜索", "用自然对话的方式提问，查找特定照片与回忆。", "🤖 Gemini 与 Ask Photos 搜索", "Ask Photos AI 搜索", FlagType.BOOLEAN, Boolean.TRUE);

        // 🪄 AI Photo & Video Editor Tools (3 Flags)
        register("45683689", "AI 增强 V2", "多级神经网络画质增强，改善曝光、HDR 与细节。", "🪄 AI 照片与视频编辑工具", "AI 神经网络照片增强", FlagType.BOOLEAN, Boolean.TRUE);
        register("45705305", "魔术编辑器手势套索选择", "在魔术编辑器里圈选或点选即可识别对象。", "🪄 AI 照片与视频编辑工具", "魔术编辑器对象套索", FlagType.BOOLEAN, Boolean.TRUE);
        register("45753336", "Bluejay AI 模型 V3", "强制使用带缓存的 Bluejay AI 模型 V3 后端来生成视频。", "🪄 AI 照片与视频编辑工具", "Bluejay V3 后端", FlagType.BOOLEAN, Boolean.TRUE);

        // 🎞️ Create Tab: Motion & Animation (2 Flags)
        register("4311", "动态照片防抖与悬浮播放条", "控制查看器里的悬浮播放条与动态视频防抖。", "🎞️ 创建页：动效与动画", "动态照片播放条", FlagType.BOOLEAN, Boolean.TRUE);
        register("45694311", "动态照片抽帧", "从动态照片中提取稳定帧，用于 GIF 循环动图。", "🎞️ 创建页：动效与动画", "工具 7：动画卡片", FlagType.BOOLEAN, Boolean.TRUE);

        // 🧭 Modern Navigation & Filters (1 Flag)
        register("45753590", "「本机」筛选按钮", "在顶栏新增筛选按钮，只显示确实存在本机上的照片。", "🧭 现代导航与筛选", "本机媒体筛选", FlagType.BOOLEAN, Boolean.TRUE);

        // 🎬 Create Tab & Reels Remix Hub (15 Flags)
        register("45754248", "创建页总开关", "在底部导航栏启用现代 AuraTab 创建页。", "🎬 创建页与 Reels 混剪中心", "AuraTab 底部栏", FlagType.BOOLEAN, Boolean.TRUE);
        register("45754250", "商店页顶部横幅", "显示动态顶部横幅与可交互的创作卡片。", "🎬 创建页与 Reels 混剪中心", "顶部横幅", FlagType.BOOLEAN, Boolean.TRUE);
        register("45774010", "商店页快捷建议", "顶部快捷操作按钮，快速进入常用创作。", "🎬 创建页与 Reels 混剪中心", "建议按钮", FlagType.BOOLEAN, Boolean.TRUE);
        register("45778870", "分类筛选按钮", "按分类筛选创作模板（视频混剪、情绪风等）。", "🎬 创建页与 Reels 混剪中心", "分类按钮", FlagType.BOOLEAN, Boolean.TRUE);
        register("45812600", "创建页工具网格布局", "创建工具模块采用现代网格布局。", "🎬 创建页与 Reels 混剪中心", "工具网格布局", FlagType.BOOLEAN, Boolean.TRUE);
        register("45815512", "AuraTab 商店主页", "创作模块与工具的商店主入口。", "🎬 创建页与 Reels 混剪中心", "商店主页", FlagType.BOOLEAN, Boolean.TRUE);
        register("45815129", "「为你生成」轮播与建议", "显示「为你生成」轮播与智能建议。", "🎬 创建页与 Reels 混剪中心", "「为你生成」栏", FlagType.BOOLEAN, Boolean.TRUE);
        register("45815130", "混剪创作与生成控制", "视频与照片混剪生成控制的总开关。", "🎬 创建页与 Reels 混剪中心", "混剪生成器", FlagType.BOOLEAN, Boolean.TRUE);
        register("45815131", "工具模块与已保存创作栏", "启用工具模块与已保存创作栏。", "🎬 创建页与 Reels 混剪中心", "已保存创作栏", FlagType.BOOLEAN, Boolean.TRUE);
        register("45852461", "「混剪你的视频」区块", "启用「混剪你的视频」栏，含短视频模板。", "🎬 创建页与 Reels 混剪中心", "视频混剪栏", FlagType.BOOLEAN, Boolean.TRUE);
        register("45852493", "「混剪你的照片」区块", "启用「混剪你的照片」栏，可做图片风格重塑。", "🎬 创建页与 Reels 混剪中心", "照片混剪栏", FlagType.BOOLEAN, Boolean.TRUE);
        register("45856154", "Reels 风格竖版卡片布局", "视频混剪预览采用 9:16 竖版 Reels 风格卡片。", "🎬 创建页与 Reels 混剪中心", "Reels 卡片布局", FlagType.BOOLEAN, Boolean.TRUE);
        register("45856155", "视频预览自动循环播放", "创作卡片内的视频预览自动循环播放。", "🎬 创建页与 Reels 混剪中心", "视频循环播放器", FlagType.BOOLEAN, Boolean.TRUE);
        register("45850306", "视频混剪预设：更换背景", "神经网络视频背景替换模板（别墅、日落）。", "🎬 创建页与 Reels 混剪中心", "更换背景", FlagType.BOOLEAN, Boolean.TRUE);
        register("45850307", "视频混剪预设：换装与风格", "服装试穿与生成式风格迁移模板。", "🎬 创建页与 Reels 混剪中心", "换装迁移", FlagType.BOOLEAN, Boolean.TRUE);

        // ⭕ OneGoogle: Subscriber Avatar Rings (1 Flag)
        register("45781251", "Google One 蓝色光环（Gemini 风格）", "把 Google One 的经典四色环换成现代蓝色渐变光环。", "⭕ OneGoogle：订阅者头像光环", "Google One 蓝环", FlagType.BOOLEAN, Boolean.TRUE);
    }

    private static void register(String key, String title, String description, String category, String triggerTarget, FlagType type, Object defaultValue) {
        CuratedFlag flag = new CuratedFlag(key, title, description, category, triggerTarget, type, defaultValue);
        CURATED_FLAGS.add(flag);
        FLAG_MAP.put(key, flag);
    }

    public static String getCategoryTriggerDescription(String category) {
        String trig = CATEGORY_TRIGGERS.get(category);
        return trig != null ? trig : "控制该子系统内的功能与参数";
    }

    public static List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (CuratedFlag f : CURATED_FLAGS) {
            if (!categories.contains(f.category)) {
                categories.add(f.category);
            }
        }
        return categories;
    }

    public static List<CuratedFlag> getFlagsForCategory(String category) {
        List<CuratedFlag> list = new ArrayList<>();
        for (CuratedFlag f : CURATED_FLAGS) {
            if (f.category.equals(category)) {
                list.add(f);
            }
        }
        return list;
    }

    public static void applyCuratedDefaults(SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        for (CuratedFlag f : CURATED_FLAGS) {
            if (f.defaultValue instanceof Boolean) {
                edit.putBoolean(f.key, (Boolean) f.defaultValue);
            } else if (f.defaultValue instanceof Long) {
                edit.putLong(f.key, (Long) f.defaultValue);
            } else if (f.defaultValue instanceof Integer) {
                edit.putInt(f.key, (Integer) f.defaultValue);
            } else if (f.defaultValue instanceof Float) {
                edit.putFloat(f.key, (Float) f.defaultValue);
            } else if (f.defaultValue instanceof String) {
                edit.putString(f.key, (String) f.defaultValue);
            }
        }
        edit.putBoolean("_presets_loaded", true);
        edit.commit();
    }

    public static void applyAll26Defaults(SharedPreferences prefs) {
        applyCuratedDefaults(prefs);
    }
}
