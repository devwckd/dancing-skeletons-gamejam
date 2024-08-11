plugins {
    id("java")
}

group = "me.devwckd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.minestom:minestom-snapshots:461c56e749")
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("ch.qos.logback:logback-core:1.5.6")
    implementation("ch.qos.logback:logback-classic:1.5.6")
//    implementation("dev.hollowcube:schem:1.2.0")
    implementation("dev.hollowcube:polar:1.11.1")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("org.joml:joml:1.10.8")


}

tasks.compileJava {
    options.encoding = "UTF-8"
}