/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core;

import com.prim.core.controller.ActionResult;
import com.prim.core.controller.ActionResultPrim;
import com.prim.core.controller.RightsObject;
import com.prim.core.warehouse.Keeper;
import com.prim.core.warehouse.OptionsKeeper;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * единая точка входа в приложение
 *
 * @author пользователь
 */
public abstract class AbstractApplication implements Serializable {

   public String STRUCTURE_ERROR = "Не существует структуры с подобным именем ";
  public String ANOTHER_TYPE_ERROR = "Зафиксированы данные другого типа ";
  public String FIND_ERROR = "Ничего не найдено ";
  public String PRIMARY_SEARCH_ERROR = "Ошибка при поиске, первичный ключ не определен ";
  public String DELETE_PRIMARY_ERROR = "Ошибка при удалении, первичный ключ не определен ";
  public String LAST_ID_ERROR = "Ошибка, не занесен последний ИД ";
  public String UPDATE_ERROR = "Ошибка при обновлении информации ";
  public String NESSESARY_PARAM_ERROR = "Необходимые параметры не переданы ";
  public String FILE_SAVE_ERROR = "Ошибка при сохранении файла ";
  public String FILE_COPY_ERROR = "Ошибка при копировании файла ";
  public String FILE_DELETE_ERROR = "Ошибка при удалении файла  ";
  public String FILE_DELETE_EXISTS_ERROR = "Ошибка при сохранении файла, его не существует ";
  public String FILE_PERMISSION_ERROR = "В этом типе данных не разрешена работа с файлами ";
  public String FILE_PRIMARY_ERROR = "Ошибка при работе с файлом, не определен первичный ключ ";
  public String DIRECTORY_ERROR = "Ошибка при создании директории  ";
  public String FILE_DATA_ERROR = "Ошибка при попытке сохранить сопутствующую файлу информацию ";
  public String FILE_SEARCH_ERROR = "Файл не найден ";
  public String FILE_DATA_DELETE_ERROR = "Ошибка в процессе удаления информации о файле ";
  public String SERVICE_NOT_FOUND = "Сервис не был найден ";
  // start новые свойства
  /**
   * драйвер БД
   */
  protected String dbDriver;
  /**
   * адрес драйвера БД
   */
  protected String dbDriverUrl;
  /**
   * название базы данных
   */
  protected String dbName;
  /**
   * название хоста БД
   */
  protected String dbHost;
  /**
   * пользователь БД
   */
  protected String dbUser;
  /**
   * пароль БД
   */
  protected String dbPass;
  /**
   * кодировка БД
   */
  protected String dbEncoding;
  /**
   * путь к логам
   */
  protected String appLogPath;
  /**
   * локаль ошибок
   */
  protected String appLocale;
  /**
   * путь к настройкам
   */
  protected String appUserDataConfigPath;
  /**
   * путь к хранимым файлам
   */
  protected String filePath;
  /**
   * время жизни сессии, в минутах
   */
  protected int sessionLifeTime;
  /**
   * путь к bi абсолютный
   */
  protected String biPath;
  /**
   * относительный путь к bi
   */
  protected String biClassPath;
  /**
   * путь к дампам ДБ
   */
  protected String dumpPath;
  /**
   * email для получения служебных сообщений
   */
  protected String emailNotification;
  /**
   * абсолютный путь к рендерам
   */
  protected String renderPath;
  /**
   * относительный путь к рендерам
   */
  protected String renderClassPath;
  /**
   * существует ли файл конфига
   */
  protected Boolean configFileExist = false;
  /**
   * максимальный размер файла, который может быть загружен в систему
   */
  protected Integer maxUploadSizeMB = 20;
  protected Boolean valid = false;
  
  protected String controllerClassPath = "controllers.entities";
  
  /**
   * путь к директории, где хранятся временные файлы при загрузке файлов
   */
  protected String uploadPath = "";
  /**
   * параметры сессии
   */
  protected Map<String, Object> session = new HashMap<String, Object>();
  /**
   * параметры запроса
   */
  protected Map<String, Object> request = new HashMap<String, Object>();
  /**
   * параметры внутреннего запроса
   */
  protected Map<String, Object> innerParams = new HashMap<String, Object>();
  /**
   * название вызывамого объекта (то есть название модуля)
   */
  protected String objectName;
  /**
   * действие
   */
  protected String action;
  /**
   * массив ошибок
   */
  protected ArrayList<String> error = new ArrayList<String>();
  /**
   * объект результата выполнения действия
   */
  protected ActionResult actionResult = ActionResultPrim.getInstance();
  /**
   * определяет тестовый ли набор данных
   */
  protected Boolean test = false;
  /**
   * объект прав
   */
  protected RightsObject rightsObject;
  /**
   * информация о выполненных модулях
   */
  protected String info = "";
  /**
   * объект соединения с БД
   */
  protected Connection connection;
  /**
   * параметры редиректа
   */
  private Map<String, Object> redirectParams = new HashMap<String, Object>();
  /**
   * дополнительные свойства
   */
  protected Properties props = new Properties();

  protected String appName = "";
  
  protected Keeper keeper;
  
  protected List<UploadedFile> fileList = new ArrayList();

  
  protected String optionKeeperPath;
  
  public final String getOptionSingletonPath(){
    return optionKeeperPath;
  }
  
  public final Keeper getKeeper() {
    return keeper;
  }
  
  public String getAppName() {
    return appName;
  }

  public void setFileList(List<UploadedFile> fileList) {
    this.fileList = fileList;
  }

  public String getControllerClassPath() {
    return controllerClassPath;
  }

  public void setControllerClassPath(String controllerClassPath) {
    this.controllerClassPath = controllerClassPath;
  }
  
  
  
  /**
   * относительный путь к bi
   * @return
   */
  public String getBiClassPath() {
    return biClassPath;
  }

  /**
   * относительный путь к рендерам
   * @return
   */
  public String getRenderClassPath() {
    return renderClassPath;
  }

  // start новые свойства
  /**
   * путь к директории, где хранятся временные файлы при загрузке файлов
   * @return
   */
  public String getUploadPath() {
    return uploadPath;
  }

  /**
   * драйвер БД
   * @return
   */
  public String getDbDriver() {
    return dbDriver;
  }

  /**
   * адрес драйвера БД
   * @return
   */
  public String getDbDriverUrl() {
    return dbDriverUrl;
  }

  /**
   * название базы данных
   * @return
   */
  public String getDbName() {
    return dbName;
  }

  /**
   * название хоста БД
   * @return
   */
  public String getDbHost() {
    return dbHost;
  }

  /**
   * пользователь БД
   * @return
   */
  public String getDbUser() {
    return dbUser;
  }

  /**
   * пароль БД
   * @return
   */
  public String getDbPass() {
    return dbPass;
  }

  /**
   * кодировка БД
   * @return
   */
  public String getDbEncoding() {
    return dbEncoding;
  }

  /**
   * путь к логам
   * @return
   */
  public String getAppLogPath() {
    return appLogPath;
  }

  /**
   * локаль ошибок
   * @return
   */
  public String getAppLocale() {
    return appLocale;
  }

  /**
   * путь к настройкам
   * @return
   */
  public String getAppUserDataConfigPath() {
    return appUserDataConfigPath;
  }

  /**
   * путь к хранимым файлам
   * @return
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * время жизни сессии, в минутах
   * @return
   */
  public Integer getSessionLifeTime() {
    return sessionLifeTime;
  }

  /**
   * путь к bi абсолютный
   * @return
   */
  public String getBiPath() {
    return biPath;
  }

  /**
   * путь к дампам ДБ
   * @return
   */
  public String getDumpPath() {
    return dumpPath;
  }

  /**
   * email для получения служебных сообщений
   * @return
   */
  public String getEmailNotification() {
    return emailNotification;
  }

  /**
   * абсолютный путь к рендерам
   * @return
   */
  public String getRenderPath() {
    return renderPath;
  }

  /**
   * существует ли файл конфига
   * @return
   */
  public Boolean getConfigFileExist() {
    return configFileExist;
  }

  /**
   * максимальный размер файла, который может быть загружен в систему
   * @return
   */
  public Integer getMaxUploadSizeMB() {
    return maxUploadSizeMB;
  }

  /**
   * параметры запроса
   * @return
   */
  public Map<String, Object> getRequest() {
    return request;
  }

  /**
   * название вызывамого объекта (то есть название модуля)
   * @return
   */
  public String getObjectName() {
    return objectName;
  }

  /**
   * действие
   * @return
   */
  public String getAction() {
    return action;
  }

  /**
   * массив ошибок
   * @return
   */
  public ArrayList<String> getError() {
    return error;
  }

  /**
   * объект прав
   * @return
   */
  public RightsObject getRightsObject() {
    return rightsObject;
  }

  /**
   * дополнительные свойства
   * @return
   */
  public Properties getProps() {
    return props;
  }


  /**
   * получить свойство
   * @param name название свойства
   * @return 
   */
  public final String getProperty(String name) {
    return props.getProperty(name);
  }

  /**
   * 
   * @return информацию о вызванных модулях
   */
  public final String getInfo() {
    return info;
  }

  /**
   * установить параметры сессии
   */
  public final void setSession(Map<String, Object> session) {
    this.session = session;
  }

  /**
   * установить параметры запроса
   */
  public final void setRequest(Map<String, Object> request) {
    this.request = request;
  }

  /**
   *
   * @return параметры редиректа
   */
  public final Map<String, Object> getRedirectParams() {
    return redirectParams;
  }

  /**
   * установить параметры редиректа
   */
  public final void setRedirectParams(Map<String, Object> params) {
    if (params != null) {
      redirectParams.putAll(params);
    }
  }

  /**
   * получить объект результата
   *
   * @return
   */
  public final ActionResult getActionResult() {
    return actionResult;
  }

  /**
   *
   * @return параметры сессии
   */
  public final Map<String, Object> getSession() {
    return session;
  }

  /**
   *
   * @return название вызывамого объекта (то есть название модуля)
   */
  public final String objectName() {
    return objectName;
  }

  /**
   *
   * @return
   */
  public final Boolean objectName(String objectName) {
    Boolean result = false;
    if (checkObjName(objectName)) {
      this.objectName = objectName;
      result = true;
    }
    return result;
  }

  /**
   *
   * @return
   */
  public final Boolean action(String action) {
    Boolean result = false;
    if (checkObjName(action)) {
      this.action = action;
      result = true;
    }
    return result;
  }

  /**
   *
   * @return
   */
  public final String action() {
    return action;
  }

  /**
   *
   * @return
   */
  public final ArrayList<String> error() {
    return error;
  }

  /**
   *
   * @return
   */
  private final Boolean checkObjName(String objectName) {
    Boolean result = true;
    return result;
  }

  private final Boolean checkAction(String action) {
    Boolean result = true;
    return result;
  }

  public final Connection getConnection() {
    return connection;
  }

  public final void closeConnection() throws SQLException {
    if (connection != null) {
      connection.close();
    }
  }

  public final Map<String, Object> getInnerParams() {
    return innerParams;
  }

  public final void setInnerParams(Map<String, Object> innerParams) {
    if (innerParams != null) {
      this.innerParams = innerParams;
    }
  }
  
  
  
  /**
   * установить OptionsSingleton
   * @param os
   * @throws Exception 
   */
  abstract public void set(OptionsKeeper os) throws Exception;
    
  public abstract void processing() throws Exception;
  
  public Connection createCloneConnection()throws Exception{
    return null;
  }
}
