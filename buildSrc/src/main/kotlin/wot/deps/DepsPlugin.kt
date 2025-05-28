package wot.deps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider

/**
 * 自定义 Gradle 插件，根据 depsConfig 配置自动添加依赖库
 *
 * @author : yangsn
 * @date : 2025/5/27
 */
class DepsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        /**
         * 向插件系统注册一个扩展对象，名字是 depsConfig，类型是 DepsConfigExtension
         * 该对象可以被两种方式访问：
         * 通过名字（如：depsConfig { ... }）
         * 通过类型（如：extensions.configure<DepsConfigExtension> { ... }）
         */
        val ext = project.extensions.create("depsConfig", DepsConfig::class.java)

        val libs = project.libs()

        // 打印 libs
//        project.logger.lifecycle("所有可用 alias:")
//        libs.libraryAliases.forEach { alias ->
//            project.logger.lifecycle(" - $alias")
//        }

        val deps = project.dependencies
        val addedDeps = mutableSetOf<String>()

        fun addDeps(configuration: String, vararg aliases: String) {
            aliases.forEach { alias ->
                if (addedDeps.add(alias)) {
                    val dep = libs.findLibrary(alias).orElseGet(null)
                    if ((dep as? Provider<MinimalExternalModuleDependency>) == null) {
                        project.logger.warn("⚠️ 找不到 alias: $alias")
                    } else {
                        deps.add(configuration, dep)
                        project.logger.lifecycle(" - $alias")
                    }
                } else {
                    project.logger.info("跳过重复依赖: $alias")
                }
            }
        }

        project.afterEvaluate {
            project.logger.lifecycle("\n> DepsPlugin: start\n$ext")

            addDeps("implementation", "core-ktx", "appcompat", "material", "constraintlayout")

            if (ext.enableLifecycle) {
                addDeps(
                    "implementation",
                    "lifecycle-viewmodel", "lifecycle-livedata", "lifecycle-runtime"
                )
            }
            if (ext.enableCoroutines) {
                addDeps("implementation", "coroutines-core", "coroutines-android")
            }
            if (ext.enableNavigation) {
                addDeps("implementation", "navigation-fragment", "navigation-ui")
            }
            if (ext.enableRoom) {
                addDeps("implementation", "room-runtime", "room-ktx")
                if (!project.plugins.hasPlugin("kotlin-kapt")) {
                    project.pluginManager.apply("kotlin-kapt")
                    project.logger.lifecycle("✅ 自动应用 kotlin-kapt 插件")
                }
                addDeps("kapt", "room.compiler")
            }
            if (ext.enablePaging) {
                addDeps("implementation", "paging-runtime")
                if (ext.enableRoom) {
                    addDeps("implementation", "room-paging")
                }
            }
            if (ext.enableOkhttp3) {
                addDeps("implementation", "okhttp3")
            }
            project.logger.lifecycle("> DepsPlugin: end")
        }
    }

    private fun Project.libs(): VersionCatalog =
        extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
}
