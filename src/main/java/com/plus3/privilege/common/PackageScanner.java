package com.plus3.privilege.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/12/14.
 */
public class PackageScanner {

    public List<Class> scanPackage(String packageName) throws Exception {
        String classpath = PackageScanner.class.getResource("/").getPath();
        String packagePath = "";

        if (packageName.equals("*") == false)
            packagePath = packageName.replace(".", File.separator);

        return scan(new File(classpath + packagePath));
    }

    private List<Class> scan(File file) throws Exception {
        List<Class> classList = new ArrayList<>();

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tmpFile : files)
                classList.addAll(scan(tmpFile));
        } else {
            if (file.getName().endsWith(".class"))
                classList.add(getClassFromClassPath(file.getPath()));
        }

        return classList;
    }

    private Class getClassFromClassPath(String filePath) throws ClassNotFoundException {
        String classpath = PackageScanner.class.getResource("/").getPath();
        classpath = classpath.replace("/", "\\").replaceFirst("\\\\", "");

        String className = filePath.replace(classpath, "").replace("\\", ".").replace(".class", "");
        Class clazz = Class.forName(className);

        return clazz;
    }

    public static void main(String[] args) throws Exception {
        PackageScanner scanner = new PackageScanner();
        List<Class> classList = scanner.scanPackage("*");

        for (Class clazz : classList)
            System.out.println(clazz);
    }
}
