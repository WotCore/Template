package wot.buildconfig

/**
 * 自定义 Flavor 名称、渠道
 * 1.ProductFlavors 统一管理 flavor 名称
 * 2.在 build.gradle.kts 和代码中都可以安全引用
 * 3.适合多渠道、多环境的项目
 *
 * @author : yangsn
 * @date : 2025/5/27
 */
object ProductFlavors {
    const val dev = "dev"
    const val prod = "prod"
}