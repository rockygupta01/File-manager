pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FileManager"
include(":app")
// Core modules
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:domain")
include(":core:security")
include(":core:ui")
// Feature modules
include(":feature:filemanager")
include(":feature:storage")
include(":feature:security")
include(":feature:search")
