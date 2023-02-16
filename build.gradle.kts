plugins {
  kotlin("jvm") version "1.8.0"
}

repositories {
  mavenCentral()
}

kotlin {
  jvmToolchain(18)
}

dependencies {
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("com.unboundid:unboundid-ldapsdk:6.0.7")

  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
}

tasks.test {
  useJUnitPlatform()
}