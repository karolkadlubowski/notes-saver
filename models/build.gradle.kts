plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.cashapp.sqldelight)
    alias(libs.plugins.kotlin.serialization)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.example")
        }
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}