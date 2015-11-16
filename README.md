# Console
Android console implementation allows static console calls to easy debug your application without memory leaks.

[![Build Status](https://travis-ci.org/jraska/Console.svg)](https://travis-ci.org/jraska/Console)
[![Sample](https://img.shields.io/badge/Download-Sample-blue.svg)](https://drive.google.com/file/d/0B0T1YjC17C-rTDBWNDBaSWVhcjg/view?usp=sharing)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg) ](https://github.com/jraska/Falcon/blob/master/LICENSE)
[![Download](https://api.bintray.com/packages/jraska/maven/com.jraska%3Aconsole/images/download.svg) ](https://bintray.com/jraska/maven/com.jraska%3Aconsole/_latestVersion)

## Usage

Include Console anywhere in your layout:

```xml
<com.jraska.console.Console
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

then write to it:

```java
// Writing to console
Console.write("This is cool");
Console.writeLine("More cool");
// Clear it
Console.clear();
```

## Download

Grab via Gradle: 
```groovy
compile 'com.jraska:console:0.1.0'
```

## License

    Copyright 2015 Josef Raska

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
