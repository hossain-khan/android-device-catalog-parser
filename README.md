![Kotlin CI with Gradle](https://github.com/amardeshbd/android-device-catalog-parser/workflows/Kotlin%20CI%20with%20Gradle/badge.svg) [![](https://jitpack.io/v/hossain-khan/android-device-catalog-parser.svg)](https://jitpack.io/#hossain-khan/android-device-catalog-parser)


# android-device-catalog-parser
Android Device catalog CSV parser that is available from Google Play developer [console](https://play.google.com/console/about/devicecatalog/).

[![](https://user-images.githubusercontent.com/99822/99319347-5e93f800-2837-11eb-9600-779663f580e3.png)](https://play.google.com/console/about/devicecatalog/)



### CSV Snapshot
![](https://user-images.githubusercontent.com/99822/99319610-cf3b1480-2837-11eb-8a60-532d974c2151.png)

### Java/Kotlin output
The CSV is parsed into a list of `AndroidDevice` [class](https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/main/kotlin/dev/hossain/android/catalogparser/models/AndroidDevice.kt).

Here is a snapshot of parsed [CSV file](https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.csv)
![](https://user-images.githubusercontent.com/99822/103190386-b09c5480-489e-11eb-961b-38215a0d7af5.png)

## Snapshot Files
Device catalog can always be downloaded from the [Google Play Console](https://play.google.com/console/about/devicecatalog/)

### CSV
Here is snapshot taken on November, 2022
* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.csv

### JSON
Here is the converted JSON of the snapshot above

* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog-min.json
* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.json
