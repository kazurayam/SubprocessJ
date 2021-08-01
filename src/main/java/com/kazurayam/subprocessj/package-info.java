/**
 *
 * <p>Executes arbitrary OS command in a sub-process, returns an
 * object that includes the return code as int, STDOUT and STDERR as List&lt;String&gt;.</p>
 *
 * <p>{@link com.kazurayam.subprocessj.Subprocess Subprocess} is a simple utility class that wraps
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html">java.lang.ProcessBuilder</a>.
 * It hides technical complexities of Threading to consume STDOUT and STDERR out of the forked subprocess.</p>
 *
 *
 * <p>The package is named as "subprocessj" because it is an homage to the Python
 * <a href="https://docs.python.org/3/library/subprocess.html">Subprocess</a> module.</p>
 *
 */
package com.kazurayam.subprocessj;

