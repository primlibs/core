/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse;

import com.prim.support.ToXml;
import com.prim.support.MyString;
import com.prim.support.primXml;
import java.io.FileInputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 *
 * @author kot
 */
public class OptionsKeeper implements ToXml {

  private static String path;
  /**
   * драйвер БД
   */
  private final String dbDriver;
  /**
   * адрес драйвера БД
   */
  private final String dbDriverUrl;
  /**
   * название базы данных
   */
  private final String dbName;
  /**
   * название хоста БД
   */
  private final String dbHost;
  /**
   * пользователь БД
   */
  private final String dbUser;
  /**
   * пароль БД
   */
  private final String dbPass;
  /**
   * кодировка БД
   */
  private final String dbEncoding;
  /**
   * путь к логам
   */
  private final String appLogPath;
  /**
   * локаль ошибок
   */
  private final String appLocale;
  /**
   * путь к настройкам
   */
  private final String appUserDataConfigPath;
  /**
   * путь к хранимым файлам
   */
  private final String filePath;
  /**
   * время жизни сессии, в минутах
   */
  private final Integer sessionLifeTime;
  /**
   * путь к bi
   */
  private final String biPath;
  /**
   * путь к дампам ДБ
   */
  private final String dumpPath;
  /**
   * email для получения служебных сообщений
   */
  private final String emailNotification;
  /**
   * путь к рендерам
   */
  private final String renderPath;
  /**
   * существует ли файл конфига
   */
  private Boolean configFileExist = false;
  /**
   * пусть к конфигам приложения
   */
  private final String appConfigPath;
  /**
   * максимальный размер файла, который может быть загружен в систему
   */
  private final Integer maxUploadSizeMB;
  private Boolean valid = false;
  /**
   * путь к директории, где хранятся временные файлы при загрузке файлов
   */
  private final String uploadPath;
  /**
   * ошибки
   */
  private ArrayList<String> error = new ArrayList<String>();
  
  /**
   * путь к контроллерам
   */
  private String controllerPath = "";
  
  public String info = "";

  public static OptionsKeeper getInstance(String path) throws Exception {
    FileInputStream fis = null;    
    try {
      fis = new FileInputStream(path);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setValidating(true);
      DocumentBuilder builder = dbf.newDocumentBuilder();
      Document doc = builder.parse(fis);
      NodeList list = doc.getChildNodes();
      Element root = (Element) list.item(0);
      Element OptionsKeeper = (Element) root.getElementsByTagName("OptionsKeeper").item(0);
      String dbDriver =OptionsKeeper.getElementsByTagName("dbDriver").item(0).getChildNodes().item(0).getNodeValue();
      String dbDriverUrl = OptionsKeeper.getElementsByTagName("dbDriverUrl").item(0).getChildNodes().item(0).getNodeValue();
      String dbName = OptionsKeeper.getElementsByTagName("dbName").item(0).getChildNodes().item(0).getNodeValue();
      String dbHost = OptionsKeeper.getElementsByTagName("dbHost").item(0).getChildNodes().item(0).getNodeValue();
      String dbUser = OptionsKeeper.getElementsByTagName("dbUser").item(0).getChildNodes().item(0).getNodeValue();
      String dbPass = OptionsKeeper.getElementsByTagName("dbPass").item(0).getChildNodes().item(0).getNodeValue();
      String dbEncoding = OptionsKeeper.getElementsByTagName("dbEncoding").item(0).getChildNodes().item(0).getNodeValue();
      String appLogPath = OptionsKeeper.getElementsByTagName("appLogPath").item(0).getChildNodes().item(0).getNodeValue();
      String appLocale = OptionsKeeper.getElementsByTagName("appLocale").item(0).getChildNodes().item(0).getNodeValue();
      String appUserDataConfigPath = OptionsKeeper.getElementsByTagName("appUserDataConfigPath").item(0).getChildNodes().item(0).getNodeValue();
      String sessionLifeTime = OptionsKeeper.getElementsByTagName("sessionLifeTime").item(0).getChildNodes().item(0).getNodeValue();
      String biPath = OptionsKeeper.getElementsByTagName("biPath").item(0).getChildNodes().item(0).getNodeValue();
      String dumpPath = OptionsKeeper.getElementsByTagName("dumpPath").item(0).getChildNodes().item(0).getNodeValue();
      String emailNotification = OptionsKeeper.getElementsByTagName("emailNotification").item(0).getChildNodes().item(0).getNodeValue();
      String renderPath = OptionsKeeper.getElementsByTagName("renderPath").item(0).getChildNodes().item(0).getNodeValue();
      String maxUploadSizeMB = OptionsKeeper.getElementsByTagName("maxUploadSizeMB").item(0).getChildNodes().item(0).getNodeValue();
      String uploadPath = OptionsKeeper.getElementsByTagName("uploadPath").item(0).getChildNodes().item(0).getNodeValue();
      String appConfigPath = OptionsKeeper.getElementsByTagName("appConfigPath").item(0).getChildNodes().item(0).getNodeValue();
      String filePath = OptionsKeeper.getElementsByTagName("filePath").item(0).getChildNodes().item(0).getNodeValue();
      
      String controllerPath = "";
      
      NodeList nodeList = OptionsKeeper.getElementsByTagName("controllerPath");
      if (nodeList != null && nodeList.getLength() > 0 && nodeList.item(0).getChildNodes().getLength() > 0) {
        controllerPath = nodeList.item(0).getChildNodes().item(0).getNodeValue();
      }
      
      return new OptionsKeeper(str(dbDriver), str(dbDriverUrl), str(dbName), str(dbHost), str(dbUser), str(dbPass),
              str(dbEncoding), str(appLogPath), str(appLocale), str(appUserDataConfigPath),
              str(filePath), in(sessionLifeTime), str(biPath), str(dumpPath), str(emailNotification),
              str(renderPath), str(appConfigPath), in(maxUploadSizeMB), str(uploadPath), str(controllerPath));
    } catch (Exception e) {
      OptionsKeeper o = new OptionsKeeper(null, null, null, null, null, null, null, null, null, null,
             null, null, null, null, null, null,path, null, null, null);
      return o; 
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
  }

  public static OptionsKeeper getInstance(String dbDriver, String dbDriverUrl, String dbName,
          String dbHost, String dbUser, String dbPass, String dbEncoding, String appLogPath,
          String appLocale, String appUserDataConfigPath, String filePath, Integer sessionLifeTime,
          String biPath, String dumpPath, String emailNotification, String renderPath,
          String appConfigPath, Integer maxUploadSizeMB, String uploadPath, String controllerPath) {
    return new OptionsKeeper(dbDriver, dbDriverUrl, dbName, dbHost, dbUser, dbPass,
            dbEncoding, appLogPath, appLocale, appUserDataConfigPath,
            filePath, sessionLifeTime, biPath, dumpPath, emailNotification,
            renderPath, appConfigPath, maxUploadSizeMB, uploadPath, controllerPath);
  }

  private OptionsKeeper(String dbDriver, String dbDriverUrl, String dbName, String dbHost, String dbUser,
          String dbPass, String dbEncoding, String appLogPath, String appLocale, String appUserDataConfigPath, String filePath, Integer sessionLifeTime, String biPath, String dumpPath, String emailNotification,
          String renderPath, String appConfigPath, Integer maxUploadSizeMB, String uploadPath, String controllerPath) {
    this.dbDriver = dbDriver;
    this.dbDriverUrl = dbDriverUrl;
    this.dbName = dbName;
    this.dbHost = dbHost;
    this.dbUser = dbUser;
    this.dbPass = dbPass;
    this.dbEncoding = dbEncoding;
    this.appLogPath = appLogPath;
    this.appLocale = appLocale;
    this.appUserDataConfigPath = appUserDataConfigPath;
    this.filePath = filePath;
    if (sessionLifeTime == null) {
      this.sessionLifeTime = 200;
    } else {
      this.sessionLifeTime = sessionLifeTime;
    }
    this.biPath = biPath;
    this.dumpPath = dumpPath;
    this.emailNotification = emailNotification;
    this.renderPath = renderPath;
    this.appConfigPath = appConfigPath;
    if (maxUploadSizeMB == null) {
      this.maxUploadSizeMB = 20;
    } else {
      this.maxUploadSizeMB = maxUploadSizeMB;
    }
    this.uploadPath = uploadPath;
    this.controllerPath = controllerPath;
  }

  /**
   * провера параметров
   *
   * @return
   */
  public Boolean validate() {
    error.clear();
    valid = false;
    if (getDbDriver() == null || getDbDriver().equals("")) {
      error.add("not defined dbDriver");
    } else if (getDbDriverUrl() == null || getDbDriverUrl().equals("")) {
      error.add("not defined dbDriverUrl");
    } else if (getDbName() == null || getDbName().equals("")) {
      error.add("not defined dbName");
    } else if (getDbHost() == null || getDbHost().equals("")) {
      error.add("not defined dbHost");
    } else if (getDbUser() == null || getDbUser().equals("")) {
      error.add("not defined dbUser");
    } else if (getDbDriver() == null || getDbDriver().equals("")) {
      error.add("not defined dbPass");
    } else if (getAppConfigPath() == null || getAppConfigPath().equals("")) {
      error.add("not defined appConfigPath");
    } else if (getAppLocale() == null || getAppLocale().equals("")) {
      error.add("not defined appLocale");
    } else if (getAppUserDataConfigPath() == null || getAppUserDataConfigPath().equals("")) {
      error.add("not defined appUserDataConfigPath");
    } else if (getFilePath() == null || getFilePath().equals("")) {
      error.add("not defined filePath");
    } else {
      valid = true;
    }
    return valid;
  }

  /**
   *
   * @return путь к рендерам
   */
  public String getRenderPath() {
    return renderPath;
  }

  /**
   * Возвращает имя базы данныx
   */
  public String getDbName() {
    return dbName;
  }

  /**
   * Возвращает хост базы данных
   */
  public String getDbHost() {
    return dbHost;
  }

  /**
   * Возвращает пользователя
   */
  public String getDbUser() {
    return dbUser;
  }

  /**
   * Возвращает пароль
   */
  public String getDbPass() {
    return dbPass;
  }

  /**
   * Возвращает кодировку DB
   */
  public String getDbEncoding() {
    return dbEncoding;
  }

  public Integer getSessionLifeTime() {
    return sessionLifeTime;
  }

  public String getBiPath() {
    return biPath;
  }

  public String getDumpPath() {
    return dumpPath;
  }

  public String getEmailNotification() {
    return emailNotification;
  }

  public Integer getMaxUploadSizeMB() {
    return maxUploadSizeMB;
  }

  public String getUploadPath() {
    return uploadPath;
  }

  /**
   * Возвращает путь к конфиг файлам
   */
  public String getAppConfigPath() {
    return appConfigPath;
  }

  /**
   * Возвращает путь к логам
   */
  public String getAppLogPath() {
    return appLogPath;
  }

  /**
   * Возвращает локализацию
   */
  public String getAppLocale() {
    return appLocale;
  }

  /**
   * Возвращает драйвер бд
   */
  public String getDbDriver() {
    return dbDriver;
  }

  /**
   * Возвращает url драйвера бд
   */
  public String getDbDriverUrl() {
    return dbDriverUrl;
  }
 
  /**
   *
   * @return путь к настройкам
   */
  public String getAppUserDataConfigPath() {
    return appUserDataConfigPath;
  }

  /**
   * существует ли файл конфигов
   *
   * @return
   */
  public Boolean getConfigFileExist() {
    return configFileExist;
  }

  /**
   * @return путь до директории, где находятся пользовательские файлы
   */
  public String getFilePath() {
    return filePath;
  }

  public Boolean getValid() {
    return valid;
  }

  /**
   * ошибки
   *
   * @return
   */
  public ArrayList<String> getError() {
    return error;
  }

  public String getControllerPath() {
    return controllerPath;
  }

  public void setControllerPath(String controllerPath) {
    this.controllerPath = controllerPath;
  }

  @Override
  public void getSelfInXml(Document doc, Element optionsKeeper) throws Exception {
    primXml.createElement(doc, optionsKeeper, "dbDriver", dbDriver);
    primXml.createElement(doc, optionsKeeper, "dbDriverUrl", dbDriverUrl);
    primXml.createElement(doc, optionsKeeper, "dbName", dbName);
    primXml.createElement(doc, optionsKeeper, "dbHost", dbHost);
    primXml.createElement(doc, optionsKeeper, "dbUser", dbUser);
    primXml.createElement(doc, optionsKeeper, "dbPass", dbPass);
    primXml.createElement(doc, optionsKeeper, "dbEncoding", dbEncoding);
    primXml.createElement(doc, optionsKeeper, "appLogPath", appLogPath);
    primXml.createElement(doc, optionsKeeper, "appLocale", appLocale);
    primXml.createElement(doc, optionsKeeper, "appUserDataConfigPath", appUserDataConfigPath);
    primXml.createElement(doc, optionsKeeper, "filePath", filePath);
    primXml.createElement(doc, optionsKeeper, "sessionLifeTime", sessionLifeTime);
    primXml.createElement(doc, optionsKeeper, "biPath", biPath);
    primXml.createElement(doc, optionsKeeper, "dumpPath", dumpPath);
    primXml.createElement(doc, optionsKeeper, "emailNotification", emailNotification);
    primXml.createElement(doc, optionsKeeper, "renderPath", renderPath);
    primXml.createElement(doc, optionsKeeper, "appConfigPath", appConfigPath);
    primXml.createElement(doc, optionsKeeper, "maxUploadSizeMB", maxUploadSizeMB);
    primXml.createElement(doc, optionsKeeper, "uploadPath", uploadPath);
    primXml.createElement(doc, optionsKeeper, "controllerPath", controllerPath);
  }

  private static String str(Object ob) {
    return MyString.getString(ob);
  }

  private static Integer in(Object ob) {
    if(ob!=null&&!ob.toString().equals("null")&&!ob.toString().equals("")){
      return Integer.parseInt(ob.toString());
    }else{
      return null;
    }
    
  }
}
