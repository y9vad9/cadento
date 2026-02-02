package timemate.client.gradle.convention

plugins {
    id("timemate.client.gradle.convention.tests-convention")
}

tasks.register<Test>("jvmIntegrationTest") {
    description = "Runs JVM integration tests"
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    useJUnitPlatform()

    val jvmTest = tasks.named<Test>("jvmTest").get()
    testClassesDirs = jvmTest.testClassesDirs
    classpath = jvmTest.classpath

    filter {
        includeTestsMatching("timemate.**.integrationtest.**")
        isFailOnNoMatchingTests = false
    }
}
