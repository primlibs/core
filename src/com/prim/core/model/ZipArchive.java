/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.prim.support.MyString;

//import java.util.zip.*;
import org.apache.tools.zip.*;

/**
 *
 * @author Pavel Rice
 */
public class ZipArchive {

  /**
   * файлы, которые нужно включить в архив
   */
  private List<FileToZip> files = new ArrayList();
  /**
   * ошибки
   */
  private List<String> errors = new ArrayList();
  /**
   * массив имен файлов
   */
  private Map<String, Integer> names = new HashMap();

  /**
   *
   * @return ошибки
   */
  public List<String> getErrors() {
    return errors;
  }

  /**
   * добавить файл к архиву
   *
   * @param filePath путь к файлу
   * @param newName название файла в архиве
   */
  public void addFile(String filePath, String newName) {
    if (filePath != null & newName != null) {
      files.add(new FileToZip(filePath, newName));
    }
  }

  /**
   * создать архив
   *
   * @throws Exception
   */
  public byte[] doZip() throws Exception {
    //ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archivePath));
    ByteArrayOutputStream ba = null;
    ZipOutputStream out = null;
    try {
      ba = new ByteArrayOutputStream();
      out = new ZipOutputStream(ba);
      out.setEncoding("CP866");

      out.setLevel(9);

      for (FileToZip file : files) {
        if (file.exists()) {
          addFile(out, file);
        } else {
          errors.add("Файл " + file.getPath() + " не существует");
        }
      }
    } catch (Exception e) {
      errors.add(MyString.getStackExeption(e));
    } finally {
      if (out != null) {
        out.close();
      }
      if (ba != null) {
        ba.close();
      }
    }
    return ba.toByteArray();
  }

  private void addFile(ZipOutputStream out, FileToZip file) throws Exception {
    byte[] buf = new byte[1024];
    String filePath = file.getPath();
    String name = file.getName();
    // если такое имя файла раньше не встречалось, то заносим его в массив имен
    if (!names.containsKey(name)) {
      names.put(name, 1);
      // если такое имя файла уже встречалось, то добавляем к имени номер
    } else {
      names.put(name, names.get(name) + 1);
      name += "_" + names.get(name);
    }
    FileInputStream in = null;
    try {
      in = new FileInputStream(filePath);
      ZipEntry entry = new ZipEntry(name);
      out.putNextEntry(entry);
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    } catch (Exception e) {
      errors.add(MyString.getStackExeption(e));
    } finally {
      out.closeEntry();
      if (in != null) {
        in.close();
      }
    }
  }
}

/**
 * файл, который нужно добавить к архиву
 *
 * @author Pavel Rice
 */
class FileToZip {

  /**
   * путь
   */
  private String path;
  /**
   * название
   */
  private String name;

  /**
   *
   * @param path путь к файлу
   * @param name название файла в архиве
   */
  FileToZip(String path, String name) {
    this.path = path;
    this.name = name;
  }

  /**
   *
   * @return путь до файла
   */
  String getPath() {
    return path;
  }

  /**
   *
   * @return имя файла в архиве
   */
  String getName() {
    return name;
  }

  /**
   *
   * @return существует ли файл
   */
  boolean exists() {
    File file = new File(path);
    return (file.exists() && file.isFile());
  }
}
