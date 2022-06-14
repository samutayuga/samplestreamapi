plugins {
    id("java")
    application
    jacoco
}
jacoco {
    toolVersion = "0.8.7"
    reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}
tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}
group = "pmm.thomet.srvmonitor"
version = "1.0"

val jupyterVersion: String by project
val gsonVersion: String by project
val restitoVersion: String by project
val restAssured: String by project
val wiremockVersion: String by project

repositories {
    mavenCentral()
}
application {
    mainClass.set("pmm.thomet.srvmonitor.CpxCli")
}
dependencies {
    implementation("com.google.code.gson:gson:${gsonVersion}")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.yaml:snakeyaml:1.30")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupyterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${jupyterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupyterVersion}")
    testImplementation("ru.lanwen.wiremock:wiremock-junit5:1.3.1")
    testImplementation("com.xebialabs.restito:restito:${restitoVersion}")
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "processResources"))
        //archiveClassifier.set("")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourceMain = sourceSets.main.get()
        val contents =
            configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } + sourceMain.output
        from(contents)

    }
    build {
        dependsOn(fatJar)
    }

}