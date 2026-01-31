plugins {
    id(conventions.multiplatform.library)
}

dependencies {
    commonMainImplementation(libs.kotlinx.coroutines)
    commonMainImplementation(libs.kotlinx.datetime)

    jvmTestImplementation(libs.turbine)
}

tasks.withType<Test>().configureEach {
    jvmArgs("--add-opens", "java.base/java.util=ALL-UNNAMED")
}
