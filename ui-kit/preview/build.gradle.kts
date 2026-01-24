plugins {
    id(conventions.jvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

dependencies {
    implementation(projects.uiKit)
    implementation(projects.uiKit.material3)

    implementation(libs.compose.preview)
}
