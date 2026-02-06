plugins {
    id(conventions.feature.database)
}

sqldelight {
    databases.create("TimerDatabase") {
        generateAsync = true
        packageName = "cadento.timers.sqldelight"
        dialect(libs.sqldelight.sqlite.dialect)
    }
}

dependencies {
    commonMainImplementation(libs.kotlinx.coroutines)
    commonMainImplementation(libs.sqldelight.coroutines)

    jvmTestImplementation(libs.turbine)
    jvmTestImplementation(libs.sqldelight.runtime)
    jvmTestImplementation(libs.sqldelight.jvm.driver)
    jvmTestImplementation(libs.xerial.sqlite.jdbc)
}
