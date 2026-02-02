plugins {
    java
    alias(libs.plugins.kover)
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose) apply false
}

kover.merge.allProjects()

val installGitHooks by tasks.registering(Copy::class) {
    group = "git"
    description = "Copies all git hooks from .githooks/ to .git/hooks/"

    val srcDir = layout.projectDirectory.dir(".githooks")
    val dstDir = layout.projectDirectory.dir(".git/hooks")

    from(srcDir)
    into(dstDir)

    filesMatching("*") {
        file.setExecutable(true)
    }

    includeEmptyDirs = false

    doFirst {
        if (!srcDir.asFile.exists()) {
            logger.warn(".githooks directory not found — nothing to install.")
        }
    }

    doLast {
        logger.lifecycle("Installed git hooks from ${srcDir.asFile} to ${dstDir.asFile}")
    }
}

tasks.build {
    dependsOn(installGitHooks)
}
