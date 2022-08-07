dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            from(files("../config/deps.versions.toml"))
        }
    }
}
