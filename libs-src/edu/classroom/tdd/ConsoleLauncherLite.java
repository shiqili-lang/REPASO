package edu.classroom.tdd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ConsoleLauncherLite {

    public static void main(String[] args) throws Exception {
        List<Class<?>> testClasses;
        if (args.length > 0 && !args[0].isBlank()) {
            testClasses = List.of(Class.forName(args[0]));
        } else {
            testClasses = discoverTests();
        }

        int total = 0;
        int passed = 0;
        int failed = 0;

        for (Class<?> clazz : testClasses) {
            Result result = runTestClass(clazz);
            total += result.total;
            passed += result.passed;
            failed += result.failed;
        }

        System.out.println();
        System.out.println("Resumen: total=" + total + ", ok=" + passed + ", fallo=" + failed);
        System.exit(failed == 0 ? 0 : 1);
    }

    private static Result runTestClass(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> beforeEach = new ArrayList<>();
        List<Method> afterEach = new ArrayList<>();
        List<Method> tests = new ArrayList<>();

        for (Method m : methods) {
            if (m.isAnnotationPresent(BeforeEach.class)) beforeEach.add(m);
            if (m.isAnnotationPresent(AfterEach.class)) afterEach.add(m);
            if (m.isAnnotationPresent(Test.class)) tests.add(m);
        }

        beforeEach.sort(Comparator.comparing(Method::getName));
        afterEach.sort(Comparator.comparing(Method::getName));
        tests.sort(Comparator.comparing(Method::getName));

        System.out.println();
        System.out.println("Clase: " + clazz.getName());

        int total = 0, passed = 0, failed = 0;

        for (Method test : tests) {
            total++;
            try {
                Object instance = Modifier.isStatic(test.getModifiers()) ? null : clazz.getDeclaredConstructor().newInstance();

                for (Method m : beforeEach) {
                    m.setAccessible(true);
                    m.invoke(instance);
                }

                test.setAccessible(true);
                test.invoke(instance);

                for (Method m : afterEach) {
                    m.setAccessible(true);
                    m.invoke(instance);
                }

                passed++;
                System.out.println("  [OK]    " + test.getName());
            } catch (InvocationTargetException e) {
                failed++;
                Throwable cause = e.getCause();
                System.out.println("  [FALLO] " + test.getName() + " -> " + cause);
            } catch (Throwable t) {
                failed++;
                System.out.println("  [FALLO] " + test.getName() + " -> " + t);
            }
        }

        return new Result(total, passed, failed);
    }

    private static List<Class<?>> discoverTests() throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL rootUrl = cl.getResource("");
        if (rootUrl == null) return classes;

        File root = new File(URLDecoder.decode(rootUrl.getPath(), StandardCharsets.UTF_8));
        List<File> classFiles = new ArrayList<>();
        walk(root, classFiles);

        for (File file : classFiles) {
            String rel = root.toPath().relativize(file.toPath()).toString();
            if (!rel.endsWith("Test.class")) continue;
            String className = rel.substring(0, rel.length() - 6).replace(File.separatorChar, '.');
            classes.add(Class.forName(className));
        }

        classes.sort(Comparator.comparing(Class::getName));
        return classes;
    }

    private static void walk(File dir, List<File> out) {
        File[] children = dir.listFiles();
        if (children == null) return;
        Arrays.sort(children, Comparator.comparing(File::getName));
        for (File f : children) {
            if (f.isDirectory()) walk(f, out);
            else if (f.getName().endsWith(".class")) out.add(f);
        }
    }

    private record Result(int total, int passed, int failed) {}
}
