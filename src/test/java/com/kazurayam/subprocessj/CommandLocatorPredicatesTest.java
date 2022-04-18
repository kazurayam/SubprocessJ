package com.kazurayam.subprocessj;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class CommandLocatorPredicatesTest {

    static String mingw64 = "C:\\Program Files\\Git\\mingw64\\bin\\git.exe";
    static String cmd = "C:\\Program Files\\Git\\cmd\\git.exe";
    static List<Path> pathList;

    @BeforeAll
    public static void beforeAll() {
        pathList = new ArrayList<>();
        pathList.add(Paths.get(mingw64));
        pathList.add(Paths.get(cmd));
    }

    @Test
    public void test_predicate_startsWith() {
        Predicate<Path> startsWith = CommandLocator.startsWith("C:\\Program Files\\Git\\cmd");
        List<Path> result = pathList.stream()
                .filter(startsWith)
                .collect(Collectors.toList());
        assertEquals(1, result.size());
    }

    @Test
    public void test_predicate_endsWith() {
        Predicate<Path> endsWith = CommandLocator.endsWith("cmd\\git.exe");
        List<Path> result = pathList.stream()
                .filter(endsWith)
                .collect(Collectors.toList());
        assertEquals(1, result.size());
    }

    @Test
    public void test_Paths_get() {
        Path p = Paths.get(cmd);
        System.out.println(p);   // C:\Program Files\Git\cmd\git.exe
        System.out.println(p.getParent());   // C:\Program Files\Git\cmd
        System.out.println(p.getParent().getParent());   // C:\Program File\Git
        System.out.println(p.getParent().getParent().getParent());   // C:\Program Files
        System.out.println(p.getParent().getParent().getParent().getParent());   // C:\
        System.out.println(p.getParent().getParent().getParent().getParent().getParent());   // null
    }

    @Test
    public void test_filter_positive_by_startsWith() {
        List<Path> result = pathList.stream()
                .filter(p -> {
                    return p.startsWith(Paths.get("C:\\Program Files\\Git\\cmd"));
                })
                .collect(Collectors.toList());
        assertEquals(1, result.size());
    }

    @Test
    public void test_filter_positive_by_endsWith() {
        List<Path> result = pathList.stream()
                .filter(p -> {
                    return p.endsWith(Paths.get("cmd\\git.exe"));
                })
                .collect(Collectors.toList());
        assertEquals(1, result.size());
    }

    @Test
    public void test_filter_positive_by_endsWith_forward_slash() {
        List<Path> result = pathList.stream()
                .filter(p -> {
                    return p.endsWith(Paths.get("cmd/git.exe"));
                })
                .collect(Collectors.toList());
        assertEquals(1, result.size());
    }




}
