plugins {
    id(conventions.feature.infrastructure)
}

dependencies {
    commonMainImplementation(projects.feature.tasks.infrastructure.database)
    commonMainImplementation(projects.feature.tasks.application)
    commonMainImplementation(projects.foundation.coroutines)

    jvmTestImplementation(libs.turbine)
    jvmTestImplementation(libs.koin.test)
    jvmTestImplementation(libs.sqldelight.jvm.driver)
    jvmTestImplementation(libs.xerial.sqlite.jdbc)
}
