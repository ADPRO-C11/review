plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	jacoco
	id("org.sonarqube") version "4.4.1.3373"
}

group = "snackscription"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}


configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("io.micrometer:micrometer-core")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
	classDirectories.setFrom(files(classDirectories.files.map {
		fileTree(it) { exclude("**/*Application**")}
	}))
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}
}

sonar {
  properties {
    property("sonar.projectKey","ADPRO-C11_snackscription-review")
    property("sonar.organization", "adpro-c11") 
    property("sonar.host.url", "https://sonarcloud.io")
  }
}