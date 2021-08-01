How I published this project at Maven Central Repository
=====

- @author kazurayam
- @date 31 JULY 2021

## Summary

I wanted to reuse the `SubprocessJ` package in other projects of mine, and I wanted to automate the dependency management by Gradle built-in feature. If I could publish the package of `SubprocessJ` in the Maven Central Repository, it is no doubt the best shortest path. 

But I have never published my artifacts in the Maven Central. How can I do it? So I studied some and tried. I will write the story here as a memorundom for myself.

## Reference

I read the following article and did as it tells.

- [Qiita: GitHub＋Maven Centralで自作ライブラリを公開する](https://qiita.com/yoshikawaa/items/a7a7c1d927f6e7e75320)


## What I did...

### Create my credential for Sonatype JIRA

At first, I need to be able to login to [Sonatype JIRA](https://issues.sonatype.org/secure/Dashboard.jspa).

I Singed up there to create a credential for me.

### My domain

I already have created my own public domain name: `com.kazurayam`. I use AWS Route53.

### Developed the project

Of course, I developed this project.
- I wrote enough unit tests.
- I wrote Gradle build.gradle to build the project's jar.
- I wrote the javadoc.
- I added LICENSE file, which states the Apache License 2.0.

### create pom.xml

In the `build.gradle`, I added `maven-publish` plugin.

```
plugins {
    id 'java'
    id 'maven-publish'
}
```

The plugin adds the convention method named `pom`.

I write a task `createPom` in the build.gradle.


## Create an Issue in Sonatype JIRA for request publication.

