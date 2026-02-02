plugins {
    id(conventions.application)
}

dependencies {
    commonMainApi(projects.feature.tasks.domain)
    commonMainImplementation(libs.kotlinx.coroutines)
    commonMainImplementation(libs.kotlinx.datetime)

    commonTestImplementation(libs.turbine)
}
