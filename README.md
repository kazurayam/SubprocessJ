Subprocess in Java
====

An instance of `com.kazurayam.subprocessj.Subprocess` class executes arbitrary OS command in a forked OS Process, waits for it to finish, returns an object that contains the return code as int, STDOUT and STDERR as List<String>.

`Subprocess` wraps [`java.lang.ProcessBuilder`](https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html) and provides an easier interface to ProcessBuilder. `Subprocess` hides the technical complexity of multi-Threading to consume STDOUT and STDERR out of the forked process.

Javadoc is [here](./docs/api/index.html).

For usage examples, see [Test code](src/test/java/com/kazurayam/subprocessj/SubprocessTest.java).

`com.kazurayam.subprocessj.Subprocess` was developed on Java 8. I tested it in my Java/Groovy applications. Should run OK in other JVM languages as well.
