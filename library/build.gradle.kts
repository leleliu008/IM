import java.util.Properties

plugins {
    kotlin("jvm")
    id("com.jfrog.bintray").version("1.7.3")
    java
    maven
}

java {
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    //https://github.com/ReactiveX/RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.1.2")

    //http://kotlinlang.org/docs/reference/using-gradle.html#configuring-dependencies
    implementation(kotlin("stdlib"))
}

val rootProjectName: String = rootProject.name

base {
    archivesBaseName = rootProjectName
}

// 这里是groupId,必须填写,一般填你唯一的包名
group = "com.fpliu"

//这个是版本号，必须填写
version = "1.0.0"

// 项目的主页,这个是说明，可随便填
val siteUrl = "https://github.com/leleliu008/$rootProjectName"

// GitHub仓库的URL,这个是说明，可随便填
val gitUrl = "https://github.com/leleliu008/$rootProjectName"


tasks {
    "install"(Upload::class) {
        repositories {
            withConvention(MavenRepositoryHandlerConvention::class) {
                mavenInstaller {
                    configuration = configurations.getByName("archives")
                    pom.project {
                        withGroovyBuilder {
                            "packaging"("jar")
                            "artifactId"(rootProjectName)
                            "name"(rootProjectName)
                            "url"(siteUrl)
                            "licenses" {
                                "license" {
                                    "name"("The Apache Software License, Version 2.0")
                                    "url"("service://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }
                            "developers" {
                                "developer" {
                                    "id"("fpliu")
                                    "name"("fpliu")
                                    "email"("leleliu008@gmail.com")
                                }
                            }
                            "scm" {
                                "connection"(gitUrl)
                                "developerConnection"(gitUrl)
                                "url"(siteUrl)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 生成jar包的task
val sourcesJarTask = task("sourcesJar", Jar::class) {
    from(java.sourceSets["main"].java.srcDirs)
    baseName = rootProjectName
    classifier = "sources"
}

// 生成jarDoc的task
val javadocTask = task("javadoc_", Javadoc::class) {
    source(java.sourceSets["main"].java.srcDirs)
//    classpath += project.files(java.)
    isFailOnError = false
}

// 生成javaDoc的jar
val javadocJarTask = task("javadocJar", Jar::class) {
    from(javadocTask.destinationDir)
    baseName = rootProjectName
    classifier = "javadoc"
}.dependsOn(javadocTask)

artifacts {
    add("archives", javadocJarTask)
    add("archives", sourcesJarTask)
}

val properties = Properties().apply { load(project.rootProject.file("local.properties").inputStream()) }
bintray {
    user = properties.getProperty("bintray.user")
    key = properties.getProperty("bintray.apikey")

    setConfigurations("archives")
    pkg = PackageConfig().apply {
        userOrg = "fpliu"
        repo = "newton"
        name = rootProjectName
        websiteUrl = siteUrl
        vcsUrl = gitUrl
        setLicenses("Apache-2.0")
        publish = true
    }
}