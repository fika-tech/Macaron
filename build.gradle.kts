allprojects {
    group = Config.group
    version = Property.get(Property.Version) ?: Config.version
}

setupTestCoverage()
setupKotlinCompile()
