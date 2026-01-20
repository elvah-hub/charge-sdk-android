// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.parcelize) apply false
    alias(libs.plugins.serializable) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.version.catalog.linter)
    alias(libs.plugins.detekt)
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
}

subprojects {
    detekt {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom("$rootDir/codequality/detekt/detekt.yml")
        baseline = file("$rootDir/codequality/detekt//detekt-baseline.xml")
    }

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.8")
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-ruleauthors:1.23.8")
        detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:2.0.0-alpha.1")
    }
}

// Install git hooks
tasks.register<Copy>("installGitHooks") {
    group = "git hooks"
    description = "Installs the pre-commit git hook from scripts/pre-commit"

    from("scripts/pre-commit")
    into(".git/hooks")
    fileMode = 0b111101101  // 0755 in octal (rwxr-xr-x)

    doLast {
        println("✅ Pre-commit hook installed successfully!")
        println("   The hook will run lint and Detekt checks before each commit.")
        println("   To bypass in emergencies, use: git commit --no-verify")
    }
}

// Automatically install hooks when syncing the project
tasks.register("setupProject") {
    group = "git hooks"
    description = "Sets up the project by installing git hooks"
    dependsOn("installGitHooks")

    doLast {
        println("✅ Project setup complete!")
    }
}

// Run setup automatically on first build
gradle.projectsEvaluated {
    val gitHooksDir = File(rootDir, ".git/hooks")
    val preCommitHook = File(gitHooksDir, "pre-commit")

    if (!preCommitHook.exists()) {
        println("⚠️  Git hooks not installed. Run './gradlew installGitHooks' to install them.")
        println("   Or run './gradlew setupProject' for full project setup.")
    }
}
