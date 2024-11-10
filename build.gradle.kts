plugins {
    id("java")
}

group = "org.example"
version = "Pre-0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.4.1")
    implementation(files("lib/json.jar"))
}

tasks.jar {
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
    from(sourcesMain.output)

    manifest {
        attributes(mapOf("Main-Class" to "hywt.fractal.animator.Main"))
    }
}

tasks.test {
    useJUnitPlatform()
}