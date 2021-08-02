## SubprocessJ

You can execute arbitrary OS command using `com.kazurayam.subprocessj.Subprocess` in your Java application.

### API

Javadoc is [here](./api/index.html)

### Example

You just call `com.kazurayam.subprocessj.Subprocess.run(List<String> command)`. The `run()` will wait for the sub-process to finish, and returns a `com.kazurayam.subprocessj.CompletedProcess` object which contains the return code, STDOUT and STDERR emitted by the sub-process.

```markdown
package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class SubprocessTest {

    @Test
    void test_demo() throws Exception {
        Subprocess subprocess = new Subprocess();
        subprocess.cwd(new File(System.getProperty("user.home")));
        CompletedProcess cp = subprocess.run(Arrays.asList("ls", "-la", "."));
        System.out.println(cp.returncode());
        cp.stdout().forEach(System.out::println);
        cp.stderr().forEach(System.out::println);
    }
}
```

This will emit the following output in the console:
```
0
total 4712
drwxr-xr-x+  90 kazurayam       staff     2880  7 31 21:01 .
drwxr-xr-x    6 root            admin      192  1  1  2020 ..
...
```

## Motivation, etc.

There are many articles that tell how to use [`java.lang.ProcessBuilder`](https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html). For example, I learned ["Baeldung article: Run Shell Command in Java"](https://www.baeldung.com/run-shell-command-in-java). The ProcessBuilder class is a state of the art with rich set of functionalities. But for me it is not very easy to write a program that utilized ProcessBuilder. It involves multi-threading to consume the output streams (STDOUT and STDERR) from subprocess. I do not want to repeat writing it.

So I have made a simple wrapper of ProcessBuilder which exposes a limited subset of its functionalities.

I named this as `subprocjessj` as I meant it to be a homage to the [Subprocess](https://docs.python.org/3/library/subprocess.html) module of Python.
