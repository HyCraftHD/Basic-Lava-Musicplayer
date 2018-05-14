# Basic-Lava-Musicplayer

This is a basic implemention of https://github.com/sedmelluq/lavaplayer for direct playback usage without discord.

You can use maven or gradle to include this libary to your repository.

* Repository: jcenter
* Artifact: **net.hycrafthd:basiclavamusicplayer:1.0.0**

Using in Gradle:
```gradle
repositories {
  jcenter()
  maven { url = "https://raw.githubusercontent.com/HyCraftHD/Basic-Lava-Musicplayer/master/maven" }
}

dependencies {
  compile 'net.hycrafthd:basiclavamusicplayer:1.0.0'
}
```

Using in Maven:
```xml
<repositories>
  <repository>
    <id>central</id>
    <name>bintray</name>
    <url>http://jcenter.bintray.com</url>
  </repository>
  <repository>
    <id>central</id>
    <name>github-hycrafthd</name>
    <url>https://raw.githubusercontent.com/HyCraftHD/Basic-Lava-Musicplayer/master/maven</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>net.hycrafthd</groupId>
    <artifactId>basiclavamusicplayer</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```
