plugins {
    id(conventions.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

dependencies {
    commonMainApi(libs.compose.ui)
    commonMainApi(libs.compose.foundation)
}
