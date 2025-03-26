plugins {
  kotlin("jvm") version "2.1.20"
  `maven-publish`
}

group = "org.veupathdb.lib"
version = "1.1.0"

repositories {
  mavenCentral()
}

java {
  targetCompatibility = JavaVersion.VERSION_1_8
  sourceCompatibility = JavaVersion.VERSION_1_8

  withSourcesJar()
  withJavadocJar()
}

kotlin {
  jvmToolchain(8)
}

dependencies {
  implementation("org.slf4j:slf4j-api:2.0.17")
  implementation("com.unboundid:unboundid-ldapsdk:7.0.2")

  testImplementation(kotlin("test"))
}

testing {
  suites {
    withType<JvmTestSuite> {
      useJUnitJupiter("5.12.0")
      dependencies {
        implementation("org.mockito:mockito-core:5.16.1")
      }
    }
  }
}

publishing {
  repositories {
    maven {
      name = "GitHub"
      url  = uri("https://maven.pkg.github.com/veupathdb/lib-ldap-util")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }
  }

  publications {
    create<MavenPublication>("gpr") {
      from(components["java"])
      pom {
        name.set("LDAP Utils")
        description.set("Provides utilities for looking up LDAP records.")
        url.set("https://github.com/VEuPathDB/lib-ldap-util")
        developers {
          developer {
            id.set("epharper")
            name.set("Elizabeth Paige Harper")
            email.set("epharper@upenn.edu")
            url.set("https://github.com/foxcapades")
            organization.set("VEuPathDB")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/VEuPathDB/lib-ldap-util.git")
          developerConnection.set("scm:git:ssh://github.com/VEuPathDB/lib-ldap-util.git")
          url.set("https://github.com/VEuPathDB/lib-ldap-util")
        }
      }
    }
  }
}
