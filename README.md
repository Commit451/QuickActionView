# QuickActionView

# Gradle Dependency

Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file:

```Gradle
repositories {
  maven {
    credentials {
        username project.OVENBITS_ARTIFACTORY_USERNAME
        password project.OVENBITS_ARTIFACTORY_PASSWORD
    }
    url project.OVENBITS_ARTIFACTORY_URL
  }

}
```

```Gradle
dependencies {
    compile 'com.ovenbits:quickactionview:1.0.0’
}
```

# Basic Usage
See the sample app for usage within a normal layout, as well as within a list using RecyclerView

# Configuring Action Items
Use the `QuickActionConfig` builder to create custom configurations for each action item you create.
```java
//Give one of the quick actions custom colors
QuickActionConfig quickActionConfig = new QuickActionConfig.Builder(this)
	.setBackgroundColor(Color.BLUE)
	.setTextBackgroundColor(Color.RED)
	.setTextColor(Color.BLACK)
	//etc.
	.build();
quickActionView.setQuickActionConfig(R.id.actionAddToCart, quickActionConfig);
```
