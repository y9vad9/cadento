plugins {
    id(conventions.uiPreview)
}

dependencies {
    implementation(projects.uiKit)
    implementation(projects.uiKit.material3)
    implementation(projects.uiKit.unstyledDesktop)
}