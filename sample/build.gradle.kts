plugins {
    kotlin("jvm")
    java
}

java {
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
        }
    }
}

dependencies {
    project(":library")

    //https://github.com/ReactiveX/RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.1.2")

    //http://kotlinlang.org/docs/reference/using-gradle.html#configuring-dependencies
    implementation(kotlin("stdlib"))
}
