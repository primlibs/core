/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.controller;

import com.prim.support.ToJson;
import com.prim.support.ToXml;
import com.prim.core.model.DinamicModel;
import com.prim.core.select.Select;
import java.util.List;
import java.util.Map;


/**
 *
 * @author кот
 */
public interface ActionResult extends ToXml, ToJson {

  /**
   * установить модель <br/> <br/>
   * очищает dinamicArrayList, добавляет в dinamicArrayList одну модель,
   * устанавливает ошибки из ошибок модели, если есть ошибки модели, то
   * утсанавливает status == false
   */
  public void model(DinamicModel model);

  /**
   *
   * @return алиас объекта, который выполнил действие
   */
  public String getObjectAlias();

  /**
   *
   * @return результат выполнения действия - успешно или неуспешно
   */
  public StatusCodes getStatus();
  
  /**
   *
   * @return имя в приложении объекта, который выполнил действие
   */
  public String getAppName();

  /**
   *
   * @return алиас объекта, который выполнил действие
   */
  public String getObjectName();

  /**
   *
   * @param objectAlias алиас объекта, который выполнил действие
   */
  public void setObjectAlias(String objectAlias);

  /**
   *
   * @param objectName алиас объекта, который выполнил действие
   */
  public void setObjectName(String objectName);

  /**
   *
   * @param appName имя в приложении объекта, который выполнил действие
   */
  public void setAppName(String appName);

  /**
   *
   * @return ошибки
   */
  public List<String> getErrors();

  /**
   * установить результаты select <br/><br/>
   * очищает dinamicArrayList, устанавливает в dinamicArrayList массив данных
   * полученных из select, устанавливает ошибки из ошибок select, если в select
   * есть ошибки, то устанавливает status false
   *
   * @param select
   */
  public void select(Select select);

  /**
   * добавить массив параметров в resultArray
   *
   * @param map
   */
  public void setParams(Map<String, Object> map);

  /**
   * добавить параметр в resultArray
   *
   * @param name - имя параметра
   * @param param - параметр
   */
  public void set(String name, Object param);

  /**
   * получить параметр из resultArray
   *
   * @param name - имя параметра
   * @return
   */
  public Object get(String name);

  /**
   * возвращает клон массива с параметрами
   *
   * @return
   */
  public Map<String, Object> getParams();

  /**
   *
   * @return DinamicArrayList - данные результата запроса
   */
  public List<DinamicModel> getDinamicArrayList();

  /**
   * установить результат выполнения действия - успешно или неуспешно
   *
   * @param boo
   */

  
  public void setStatusCode(StatusCodes boo);
  
    /**
   * установить результат выполнения действия - успешно или неуспешно
   *
   * @param boo
   */
  public void setStatus(Boolean boo);

  /**
   * добавить ошибку
   *
   * @param err
   */
  public void addError(String... err);

  /**
   * установить DinamicArrayList - данные результата запроса
   *
   * @param dinamicModels
   */
  public void setDinamicArrayList(List<DinamicModel> dinamicModels);

  /**
   * добавить ошибки
   *
   * @param li
   */
  public void addError(List<String> li);

  /**
   * установить словарь, т.е. массив информации для вывода комбо
   *
   * @param di
   */
  public void setDictionary(Map<String, Object> di);

  /**
   *
   * @return словарь, т.е. массив информации для вывода комбо
   */
  public Map<String, Object> getDictionary();

  /**
   * добавить сообщение
   *
   * @param msg
   */
  public void addMessage(String... msg);

  /**
   * добавить сообщение
   *
   * @param msg
   */
  public void addMessage(List<String> msg);

  /**
   * получить массив сообщений
   *
   * @return
   */
  public List<String> getMessageList();
}
