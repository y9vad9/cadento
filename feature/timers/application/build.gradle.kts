plugins {
    id(conventions.application)
}

dependencies {
    commonMainApi(projects.feature.timers.domain)
    commonMainImplementation(libs.kotlinx.coroutines)
    commonMainImplementation(libs.kotlinx.datetime)

    commonTestImplementation(libs.turbine)
}
