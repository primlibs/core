package com.prim.core;

import java.io.File;

/**
 * 
 * файл, загруженный в приложение
 * 
 * @author Pavel Rice 
 */
public class UploadedFile {

  /**
   * полный временный путь
   */
  private String temporaryPath;
  /**
   * название, под которым файл был загружен
   */
  private String namr;

  /**
   * 
   * @param temporaryPath полное имя файл во временной директории
   * @param name название, под которым файл был загружен
   */
  public UploadedFile(String temporaryPath, String name) {
    this.temporaryPath = temporaryPath;
    this.namr = name;
  }

  /**
   * 
   * @return 
   */
  public String getTemporaryPath() {
    return temporaryPath;
  }

  /**
   * 
   * @return 
   */
  public String getName() {
    return namr;
  }

  /**
   * возвращает расширение файла
   *
   * @return
   */
  public String getExtension() {
    String extension = temporaryPath.substring(temporaryPath.lastIndexOf(".") + 1);
    extension = extension.toLowerCase();
    return extension;
  }

  /**
   * размер файл в байтах
   *
   * @return
   */
  public long getSize() {
    File file = new File(temporaryPath);
    return file.length();
  }
}
