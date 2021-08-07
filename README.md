Subprocess in Java
====

An instance of `com.kazurayam.subprocessj.Subprocess` class executes arbitrary OS command in a forked OS Process, waits for it to finish, returns an object that contains the return code as int, STDOUT and STDERR as List<String>.

`Subprocess` wraps [`java.lang.ProcessBuilder`](https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html) and provides an easier interface to ProcessBuilder. `Subprocess` hides the technical complexity of multi-Threading to consume STDOUT and STDERR out of the forked process.

For usage examples, see [Test code](src/test/java/com/kazurayam/subprocessj/SubprocessTest.java).

`com.kazurayam.subprocessj.Subprocess` was developed on Java 8. I tested it in my Java/Groovy applications. Should run OK in other JVM languages as well.


Project URL is [here](https://kazurayam.github.io/subprocessj/) where you can find a quick example to use this library.

Javadoc is [here](https://kazurayam.github.io/subprocessj/api/index.html).

Subprocessj is available at [the Maven Central repository](https://mvnrepository.com/artifact/com.kazurayam/subprocessj)


I wrote a memo to record gow I published the subprocessj artifacts to the Maven Central Repository. It is avaliable at:

- https://github.com/kazurayam/subprocessj_publishedToMavenCentral