plugins {
    id(conventions.feature.application)
}

dependencies {
    commonMainImplementation(projects.feature.tasks.domain)
    commonMainImplementation(libs.kotlinx.coroutines)
    commonMainImplementation(libs.kotlinx.datetime)

    commonTestImplementation(libs.turbine)
}
