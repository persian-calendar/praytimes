# Pray Times
[![](https://jitpack.io/v/persian-calendar/praytimes.svg)](https://jitpack.io/#persian-calendar/praytimes)

PrayTimes, a Java port of [PrayTimes.js](http://praytimes.org/) (LGPLv3)
```
  Copyright (C) 2007-2011 PrayTimes.org

  Developer: Hamid Zarrabi-Zadeh
  License: GNU LGPL v3.0

  TERMS OF USE:
    Permission is granted to use this code, with or
    without modification, in any website or application
    provided that credit is given to the original work
    with a link back to PrayTimes.org.

  This program is distributed in the hope that it will
  be useful, but WITHOUT ANY WARRANTY.
```
  
# Usage

Add this in your root build.gradle at the end of repositories section:
```kotlin
allprojects {
    repositories {
        ...
        maven("https://jitpack.io")
    }
}
```

Now actually add the dependency:
```kotlin
dependencies {
    implementation("com.github.persian-calendar:praytimes:x.y.z")
}
```

For other build tools support have a look at [this](https://jitpack.io/#persian-calendar/praytimes).
