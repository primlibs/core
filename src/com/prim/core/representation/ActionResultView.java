/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.representation;

import com.google.gson.Gson;
import com.prim.core.controller.ActionResult;
import com.prim.core.controller.StatusCodes;
import com.prim.core.model.DinamicModel;
import com.prim.support.ToJson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * содержит в себе результаты выполнения приложения. Предназначен для передачи данных в отображение
 * 
 * @author Rice Pavel
 */
 class ActionResultView implements  ToJson {

  /**
   * массив DinamicModel - данные результата запроса
   */
  private List<DinamicModel> dinamicArrayList = new ArrayList<DinamicModel>();
  /**
   * массив для хранения параметров
   */
  private Map<String, Object> resultArray = new HashMap<String, Object>();
  /**
   * словарь, т.е. массив информации для вывода комбо
   */
  private Map<String, Object> dictionary = new LinkedHashMap<String, Object>();
  /**
   * массив для хранения произвольных сообщений
   */
  private List<String> messageList = new ArrayList<String>();
  /**
   * ошибки
   */
  private List<String> errors = new ArrayList<String>();
  /**
   * результат выполнения действия - успешно или неуспешно
   */
  private Integer status = StatusCodes.TRUE.getId();
  /**
   * название объекта, который выполнил действие
   */
  private String objectName = "";
  /**
   * алиас объекта, который выполнил действие
   */
  private String objectAlias = "";
  /**
   * имя в приложении объекта, который выполнил действие
   */
  private String appName = "";

  public ActionResultView(ActionResult ar) throws CloneNotSupportedException {
    resultArray = ar.getParams();
    dictionary = ar.getDictionary();
    messageList = ar.getMessageList();
    errors = ar.getErrors();
    status = ar.getStatus().getId();
    objectName = ar.getObjectName();
    objectAlias = ar.getObjectAlias();
    appName = ar.getAppName();
    dinamicArrayList = ar.getDinamicArrayList();
  }

  public List<DinamicModel> getDinamicArrayList() {
    return dinamicArrayList;
  }

  public Map<String, Object> getResultArray() {
    return resultArray;
  }

  public Map<String, Object> getDictionary() {
    return dictionary;
  }

  public List<String> getMessageList() {
    return messageList;
  }

  public List<String> getErrors() {
    return errors;
  }

  public Integer getStatus() {
    return status;
  }

  public String getObjectName() {
    return objectName;
  }

  public String getObjectAlias() {
    return objectAlias;
  }

  public String getAppName() {
    return appName;
  }
    
  @Override
  public String getSelfInJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }
  
}
