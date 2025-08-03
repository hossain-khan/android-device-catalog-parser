![Kotlin CI with Gradle](https://github.com/amardeshbd/android-device-catalog-parser/workflows/Kotlin%20CI%20with%20Gradle/badge.svg) [![](https://jitpack.io/v/hossain-khan/android-device-catalog-parser.svg)](https://jitpack.io/#hossain-khan/android-device-catalog-parser)


# android-device-catalog-parser
Android Device catalog CSV parser that is available from Google Play developer [console](https://play.google.com/console/about/devicecatalog/).

[![](https://user-images.githubusercontent.com/99822/99319347-5e93f800-2837-11eb-9600-779663f580e3.png)](https://play.google.com/console/about/devicecatalog/)
![](https://user-images.githubusercontent.com/99822/263503515-f5910fb5-02c1-4bef-bdc7-1328085b32d9.png)

### Usage
Follow jitpack [guideline](https://jitpack.io/#hossain-khan/android-device-catalog-parser) for latest instructions.

```groovy
// Step 1. Add the JitPack repository to your build file
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}


// Step 2. Add the dependency
dependencies {
    implementation 'com.github.hossain-khan:android-device-catalog-parser:1.8'
}
```


### CSV Snapshot
![](https://user-images.githubusercontent.com/99822/99319610-cf3b1480-2837-11eb-8a60-532d974c2151.png)

### Java/Kotlin output
The CSV is parsed into a list of `AndroidDevice` [class](https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/main/kotlin/dev/hossain/android/catalogparser/models/AndroidDevice.kt).

Here is a snapshot of parsed [CSV file](https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.csv)
![](https://github.com/user-attachments/assets/616aaf39-c179-4847-b965-df226b266026)


## Snapshot Files
Device catalog can always be downloaded from the [Google Play Console](https://play.google.com/console/about/devicecatalog/)

### CSV
Here is snapshot taken on August 2025
* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.csv

### JSON
Here is the converted JSON of the snapshot above

* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog-min.json
* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.json
