allprojects {
    group = Config.group
    version = Property.get(Property.Version) ?: Config.defaultVersion
}

setupTestCoverage()
setupKotlinCompile()
