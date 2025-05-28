package wot.deps

/**
 * 依赖配置扩展，支持根据不同 profile 启用相应的依赖模块
 *
 * @author : yangsn
 * @date : 2025/5/27
 */
open class DepsConfig {

    /**
     * 预定义的配置档案类型枚举 (使用枚举赋值，避免写错字符串)
     */
    enum class Profile {
        MINIMAL,    // 最简配置，只启用必要模块
        FULL,       // 完整配置，启用所有模块
        CUSTOM;     // 自定义配置，由用户自行设置具体模块

        companion object {
            /**
             * 根据字符串获取对应枚举，若无匹配则返回 CUSTOM
             */
            fun from(value: String): Profile {
                return try {
                    valueOf(value.uppercase())
                } catch (e: IllegalArgumentException) {
                    CUSTOM
                }
            }
        }
    }

    /**
     * 当前使用的 profile，设置时调用 applyProfile
     */
    var profile: Profile = Profile.CUSTOM
        set(value) {
            field = value
            applyProfile()
        }

    // 各模块开关，默认关闭
    var enableLifecycle: Boolean = false // 生命周期相关依赖
        private set
    var enableCoroutines: Boolean = false // 协程相关依赖
        private set
    var enableNavigation: Boolean = false // Navigation 依赖
        private set
    var enableRoom: Boolean = false // Room 依赖及 kapt 插件检查
        private set
    var enablePaging: Boolean = false // Paging
        private set
    var enableOkhttp3: Boolean = false // okhttp3
        private set

    /**
     * 根据 profile 自动设置模块开关
     */
    private fun applyProfile() {
        when (profile) {
            Profile.MINIMAL -> lifecycle(true)
            Profile.FULL -> {
                lifecycle(true)
                coroutines(true)
                navigation(true)
                room(true)
                paging(true)
                okhttp3(true)
            }

            Profile.CUSTOM -> { /* 用户自定义，不自动修改 */
            }
        }
    }

    // DSL 风格链式配置方法，方便 build.gradle.kts 中调用

    fun lifecycle(enable: Boolean) = apply { enableLifecycle = enable }
    fun coroutines(enable: Boolean) = apply { enableCoroutines = enable }
    fun navigation(enable: Boolean) = apply { enableNavigation = enable }
    fun room(enable: Boolean) = apply { enableRoom = enable }
    fun paging(enable: Boolean) = apply { enablePaging = enable }
    fun okhttp3(enable: Boolean) = apply { enableOkhttp3 = enable }

    override fun toString(): String = buildString {
        appendLine("DepsConfig:")
        appendLine("  profile = $profile")
        appendLine("  enableLifecycle = $enableLifecycle")
        appendLine("  enableCoroutines = $enableCoroutines")
        appendLine("  enableNavigation = $enableNavigation")
        appendLine("  enableRoom = $enableRoom")
        appendLine("  enablePaging3 = $enablePaging")
        appendLine("  enableOkhttp3 = $enableOkhttp3")
    }
}
