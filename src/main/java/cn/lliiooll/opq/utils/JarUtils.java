package cn.lliiooll.opq.utils;


import cn.lliiooll.opq.OPQMain;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {

    /**
     * 扫描全部类
     *
     * @return
     */
    @SneakyThrows
    public static Collection<Class<?>> scanByPackage(String pack) {
        Collection<Class<?>> classes = Lists.newArrayList();
        String path = JarUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();// 获得当前jar包位置
        JarFile jarFile = new JarFile(path, false);
        scanClasses(jarFile.entries(), allLoadClasses, pack);
        return classes;
    }

    @Getter
    private static final Set<Class<?>> allLoadClasses = Sets.newHashSet();

    private static Collection<Class<?>> scanClasses(Enumeration<JarEntry> entries, Collection<Class<?>> classes, String pack) {
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.endsWith(".class")) {
                String className = name.replaceAll("/", ".").replace(".class", "");
                if (!className.startsWith(pack) && !className.startsWith("cn.lliiooll")) {
                    continue;
                }
                try {
                    if (OPQMain.isDebug()) LogManager.getLogger().info("加载类: " + className);
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    LogManager.getLogger().error("加载失败的类: " + className);
                }
            }
        }
        return classes;
    }
}
