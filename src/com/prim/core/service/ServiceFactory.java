/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.service;

import com.prim.core.AbstractApplication;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

//import prim.warehouse.OptionsSingleton;

/**
 * фабрика для объектов сервисов
 *
 * @author Кот
 */
public class ServiceFactory {

  /**
   * получить объект сервиса
   *
   * @param modelName название модели
   * @param app объект приложени
   * @return
   * @throws Exception
   */
  public static Service service(String modelName, AbstractApplication app) throws Exception {
    Service s;
    String className = app.getBiClassPath() + "." + modelName;
    if (classExists(className) == true) {
      Class sx = Class.forName(className);
      Constructor constructor = sx.getConstructor(AbstractApplication.class);
      s = (Service) constructor.newInstance(app);
    } else {
      throw new Exception(app.SERVICE_NOT_FOUND);
    }
    return s;
  }
  
  public static Service primService(String modelName, AbstractApplication app) throws Exception {
    return PrimService.getInstance(modelName, app);
  }
  public static Service primService(String modelName, AbstractApplication app,Integer userId,Date operationDate) throws Exception {
    return PrimService.getInstance(modelName, app, userId, operationDate);
  }
  

  public static Boolean classExists(String className) {
    Boolean res = false;
    try {
      Object o = Class.forName(className);
      res = true;
    } catch (ClassNotFoundException e) {
      res = false;
    }
    return res;
  }

  /**
   * Просканировать classpath и вернуть коллекцию найденных классов
   *
   */
  public static Collection<String> scan(String cp) throws IOException {
    Collection<String> classes = new ArrayList<String>();
    scan(cp, classes);
    return classes;
  }

  /**
   * Просканировать classpath и добавить все найденные классы в коллекцию
   */
  public static void scan(String cp, Collection<String> classes) throws IOException {
    String[] entries = cp.split(File.pathSeparator);

    for (String entryName : entries) {
      File file = new File(entryName);
      if (file.isDirectory()) {
        scanDir("", file, classes);
      } else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
        scanJar(file, classes);
      } else {
        throw new IOException("Unknown classpath entry " + file.getName());
      }
    }

  }

  /**
   * Сканировать Jar-файл на предмет наличия class-файлов
   */
  private static void scanJar(File jarFile, Collection<String> classes) throws IOException {
    JarFile jar = new JarFile(jarFile);

    for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();) {
      JarEntry entry = e.nextElement();

      if (entry.isDirectory()) {
        continue;
      }

      scanFileName(entry.getName(), classes);
    }

    jar.close();
  }

  /**
   * Сканировать директорию, представляющую пакет на предмет наличия
   * class-файлов.
   */
  private static void scanDir(String pkg, File dir, Collection<String> classes) {
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        scanDir(pkg + file.getName() + File.separator, file, classes);
      } else {
        scanFileName(pkg + file.getName(), classes);
      }
    }
  }

  /**
   * Проверить имя файла и извлечь имя класса
   */
  private static void scanFileName(String name, Collection<String> classes) {
    if (!name.endsWith(".class")) {
      return;
    }
    // Извлекаем имя класса из имени файла
    classes.add(name.substring(0, name.length() - 6).replace(File.separator, "."));
  }
}
