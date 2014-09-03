/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.service;

import com.prim.core.AbstractApplication;
import com.prim.core.UploadedFile;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.prim.core.controller.ActionResult;
import com.prim.core.controller.RightsObject;
import com.prim.core.model.DinamicModel;
import com.prim.core.model.Model;
import com.prim.core.select.Select;

/**
 *
 * @author кот
 */
public interface Service {

  public void setFileList(List<UploadedFile> fileList);

  /**
   * установить массив с информацией о файлах
   *
   * @param filesPath массив с информацией о файлах
   */
  public void setFilesPath(Map<String, String> filesPath);

  /**
   * устанавливает массив параметров
   *
   * @param request
   */
  public void setRequest(Map<String, Object> request);

  public void setFileId(int fileId);
  
  public List<UploadedFile> getFileList();

  /**
   * получить объект ActionResult, который представляет собой результат
   * выполнения сервиса
   *
   * @return
   */
  public ActionResult getActionResult();

  /**
   * поиск по первичному ключу
   */
  public void searchById();

  /**
   * поиск автивных записей по первичному ключу
   */
  public void searchActiveById();

  /**
   * удалить по первичному ключу
   */
  public void deleteById();

  /**
   * обновить данные модели
   */
  public void updateModel();

  /**
   * закрыть модель
   */
  public void closeModel();

  /**
   * сохранить данные модели
   */
  public void saveModel();

  /**
   * найти всё
   */
  public void findAll();

  /**
   * удалить все файлы
   *
   * @return
   */
  public void deleteAllFiles();

  /**
   * удалить файл по ID
   *
   * @return
   */
  public void deleteFile();

  /**
   * сохранить файлы
   */
  public void saveFiles();

  /**
   * получить файлы
   */
  public void getFile();

  /**
   * добавить параметр в массив request
   *
   * @param name название параметра
   * @param val значение параметра
   */
  public void setReq(String name, Object val);

  /**
   * получить параметр из массива request
   *
   * @param name название параметра
   * @return
   */
  public Object getReq(String name);

  /**
   * получить из request массив параметров с одинаковым именем
   *
   * @param paramName имя параметров
   * @return
   */
  public String[] getReqArray(String paramName);

  /**
   * устанавливает статус == false и записывает в ошибки полный стек исключения
   *
   * @param exc
   */
  public void registerException(Exception exc);

  /**
   * установить значения стандартных полей (insert_date и т.п.) для несистемной
   * модели. Если isNewModel == true - то это новая запись (insert). Если
   * isNewModel == false - то это обновление старой записи (update)
   *
   * @param model модель
   * @param isNewModel новая ли это запись
   */
  public void setStandartFields(Model model, boolean isNewModel);

  /**
   * найти активные (не удаленные) записи в БД
   */
  public void findActive();

  /**
   * найти активные (не удаленные) на дату
   */
  public void findActiveByDate(String date);

  /**
   * получить все файлы модели в zip-архиве
   */
  public void getZipFile();

  public Model getModel(String name) throws Exception;

  public Model getModel() throws Exception;
  /*
   * Установить ошибки  в ActionResult
   */

  public void addError(String... error);
  /*
   * Установить ошибки  в ActionResult
   */

  public void addError(List<String> error);
  /*
   * Установить статус
   */

  public void setStatus(Boolean sts);

  public Map<String, Object> getRequestClone();

  public String getModelName();

  public void model(Model dm) throws CloneNotSupportedException;

  /*
   * Установить параметр в ActionResult
   */
  public void set(String name, Object value);

  public Connection getConnection();

  public void select(Select sel);

  public Date getOperationDate();

  public Integer getAuthorizedUserId();

  public AbstractApplication getApp();

  /**
   * в объекте ActionResult установить словарь, т.е. массив информации для
   * вывода комбо.
   *
   * Этот метод должен вызываться после того, как результаты запроса загружены в
   * actionResult.
   */
  public void setDictionary();

  /**
   * установить имена полей, которые будут выводиться в комбо.
   *
   * @param name
   */
  public void setDictAliases(String... name);

  public RightsObject getRightsObject();

  public void setDinamicArrayList(List<DinamicModel> list);

  public void setDict(Map<String, Object> hs);

  public void addMessage(String... msg);

  public void addMessage(List<String> msg);

  public boolean copyFile(Model model, String fileId) throws Exception;

  @Deprecated
  public void executeSelect(Select select) throws Exception;

  @Deprecated
  public void saveModel(Model model) throws Exception;
  
  public DAOInterface getDao () throws Exception;
  
  
  
  
}
