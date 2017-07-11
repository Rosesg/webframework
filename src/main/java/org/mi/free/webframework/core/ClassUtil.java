package org.mi.free.webframework.core;

import org.mi.free.webframework.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *  类操作工具类
 *  我们需要开发一个"类加载器"来加载该基础包下的所有类，比如注入了某注解的类，或者实现了某接口的类，再或者继承了某父类的所有子类
 */
public final class ClassUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        //获取当前线程中的类加载器即可
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     *      加载类需要提供类名与是否初始化的标志，这里提到的初始化指的是是否执行类的静态代码块！！！
     *      为了提高加载类的性能，可将isInitialized设置了false。
     */
    public static Class<?> loadClass(String className,boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className,isInitialized,getClassLoader());
        }catch (ClassNotFoundException e) {
            LOGGER.error("load class failure",e);
            throw new RuntimeException(e);//不加这句话，代码报错，加上这句话，JVM停止执行代码，就不会报错了。
        }
        return cls;
    }

    /**
     * 获取指定包名下的 所有类
     */
    public static Set<Class<?>> getClassSet(String packageName) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
            while(urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath().replace("%20","");
                        addClass(classSet,packagePath,packageName);
                    }else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replace(".","/");
                                        doAddClass(classSet,className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("get class set failure",e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    private static void doAddClass(Set<Class<?>> classSet,String className) {
        Class<?> cls = loadClass(className,false);
        classSet.add(cls);
    }

    private static void addClass(Set<Class<?>> classSet,String packagePath,String packageName) {
        File[] files = new File(packagePath).listFiles((file)-> file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
        for(File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if (StringUtil.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet,className);
            }else {
                String subPackagePath = fileName;
                if (StringUtil.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtil.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet,subPackagePath,subPackageName);
            }
        }
    }
}
