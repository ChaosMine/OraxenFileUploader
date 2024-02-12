@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven("https://repo.papermc.io/repository/maven-public/")
		maven("https://repo.oraxen.com/releases")
	}

	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "OraxenFileUploader"
