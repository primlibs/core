package com.prim.core;

import java.io.File;

/**
 * 
 * файл, загруженный в приложение
 * 
 * @author Rice Pavel
 */
public class UploadedFile {

  /**
   * полный временный путь
   */
  private String temporaryPath;
  /**
   * пользовательское название
   */
  private String initialName;

  /**
   * 
   * @param temporaryPath полное имя файл во временной директории
   * @param clientName пользовательское название файла
   */
  public UploadedFile(String temporaryPath, String clientName) {
    this.temporaryPath = temporaryPath;
    this.initialName = clientName;
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
  public String getIntialName() {
    return initialName;
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
