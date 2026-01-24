plugins {
    id(conventions.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

dependencies {
    commonMainImplementation(projects.uiKit)
    commonMainApi(libs.compose.material3)
}
