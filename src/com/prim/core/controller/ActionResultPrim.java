package com.prim.core.controller;

import com.prim.support.ToXml;
import com.prim.core.model.DinamicModel;
import com.prim.core.model.Model;
import com.prim.core.representation.Xml;
import com.prim.core.select.Select;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.prim.support.MyString;
import com.prim.support.primXml;


/**
 * объект этого класса содержит в себе результаты выполения действия сервиса
 *
 * @author User
 */
public class ActionResultPrim implements ActionResult, ToXml {

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
  Map<String, Object> dictionary = new LinkedHashMap<String, Object>();
  /**
   * массив для хранения произвольных сообщений
   */
  List<String> messageList = new ArrayList<String>();
  /**
   * ошибки
   */
  List<String> errors = new ArrayList<String>();
  
  /**
   * результат выполнения действия - успешно или неуспешно
   */

  /**
   * название объекта, который выполнил действие
   */
  String objectName = "";
  /**
   * алиас объекта, который выполнил действие
   */
  String objectAlias = "";
  /**
   * имя в приложении объекта, который выполнил действие
   */
  String appName = "";
  
  StatusCodes statusCode= StatusCodes.TRUE;
  

  private ActionResultPrim() {
  }

  public static ActionResultPrim getInstance() {
    return new ActionResultPrim();
  }

  /**
   * установить модель <br/> <br/>
   * очищает dinamicArrayList, добавляет в dinamicArrayList одну модель,
   * устанавливает ошибки из ошибок модели, если есть ошибки модели, то
   * утсанавливает status == false
   */
  public void model(Model model) throws CloneNotSupportedException {
    dinamicArrayList.clear();
    dinamicArrayList.add(model.getDinamicModel());
    errors = model.getError();
    if (!errors.isEmpty()) {
      statusCode = StatusCodes.BIZ;
    }
  }

  /**
   *
   * @return алиас объекта, который выполнил действие
   */
  public String getObjectAlias() {
    return objectAlias;
  }

  /**
   *
   * @return результат выполнения действия - успешно или неуспешно
   */
  @Override
  public StatusCodes getStatus() {
      return statusCode;
  }
  

  /**
   *
   * @return имя в приложении объекта, который выполнил действие
   */
  public String getAppName() {
    return appName;
  }

  /**
   *
   * @return алиас объекта, который выполнил действие
   */
  public String getObjectName() {
    return objectName;
  }

  /**
   *
   * @param objectAlias алиас объекта, который выполнил действие
   */
  public void setObjectAlias(String objectAlias) {
    if (objectAlias != null) {
      this.objectAlias = objectAlias;
    }
  }

  /**
   *
   * @param objectName алиас объекта, который выполнил действие
   */
  public void setObjectName(String objectName) {
    if (objectName != null) {
      this.objectName = objectName;
    }
  }

  /**
   *
   * @param appName имя в приложении объекта, который выполнил действие
   */
  public void setAppName(String appName) {
    if (appName != null) {
      this.appName = appName;
    }
  }

  /**
   *
   * @return ошибки
   */
  public List<String> getErrors() {
    return errors;
  }

  /**
   * установить результаты select <br/><br/>
   * очищает dinamicArrayList, устанавливает в dinamicArrayList массив данных
   * полученных из select, устанавливает ошибки из ошибок select, если в select
   * есть ошибки, то устанавливает status false
   *
   * @param select
   */
  @Override
  public void select(Select select) {
    dinamicArrayList.clear();
    dinamicArrayList.addAll(select.getDinamicList());
    errors = select.getError();
    if (!errors.isEmpty()) {
      statusCode = StatusCodes.BIZ;
    }
  }

  /**
   * добавить массив параметров в resultArray
   *
   * @param map
   */
  @Override
  public void setParams(Map<String, Object> map) {
    if (map != null) {
      for (String name : map.keySet()) {
        set(name, map.get(name));
      }
    }
  }

  /**
   * добавить параметр в resultArray
   *
   * @param name - имя параметра
   * @param param - параметр
   */
  public void set(String name, Object param) {
    if (name != null) {
      resultArray.put(name, param);
    }
  }

  /**
   * получить параметр из resultArray
   *
   * @param name - имя параметра
   * @return
   */
  public Object get(String name) {
    return resultArray.get(name);
  }

  /**
   * возвращает клон массива с параметрами
   *
   * @return
   */
  public Map<String, Object> getParams() {
    Map<String, Object> params = new HashMap();
    for (String key : resultArray.keySet()) {
      params.put(key, resultArray.get(key));
    }
    return params;
  }

  /**
   *
   * @return DinamicArrayList - данные результата запроса
   */
  public List<DinamicModel> getDinamicArrayList() {
    return dinamicArrayList;
  }

  /**
   * установить результат выполнения действия - успешно или неуспешно
   *
   * @param boo
   */
  public void setStatusCode(StatusCodes boo) {
    statusCode = boo;
  }

  /**
   * добавить ошибку
   *
   * @param err
   */
  public void addError(String... err) {
    for (String er : err) {
      if (MyString.NotNull(er)) {
        errors.add(er);
      }
    }
  }

  /**
   * установить DinamicArrayList - данные результата запроса
   *
   * @param dinamicModels
   */
  public void setDinamicArrayList(List<DinamicModel> dinamicModels) {
    this.dinamicArrayList = dinamicModels;
  }

  /**
   * добавить ошибки
   *
   * @param li
   */
  public void addError(List<String> li) {
    for (String er : li) {
      if (MyString.NotNull(er)) {
        errors.add(er);
      }
    }
  }

  /**
   * установить словарь, т.е. массив информации для вывода комбо
   *
   * @param di
   */
  public void setDictionary(Map<String, Object> di) {
    if (!di.isEmpty()) {
      dictionary = di;
    }
  }

  /**
   *
   * @return словарь, т.е. массив информации для вывода комбо
   */
  public LinkedHashMap<String, Object> getDictionary() {
    LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
    for (String dd : dictionary.keySet()) {
      result.put(dd, dictionary.get(dd));
    }
    return result;
  }

  /**
   * добавить сообщение
   *
   * @param msg
   */
  public void addMessage(String... msg) {
    for (String ms : msg) {
      if (MyString.NotNull(ms)) {
        messageList.add(ms);
      }
    }
  }

  /**
   * добавить сообщение
   *
   * @param msg
   */
  public void addMessage(List<String> msg) {
    for (String ms : msg) {
      if (MyString.NotNull(ms)) {
        messageList.add(ms);
      }
    }
  }

  /**
   * получить массив сообщений
   *
   * @return
   */
  public List<String> getMessageList() {
    return messageList;
  }

  @Override
  public void getSelfInXml(Document doc, Element root) throws Exception {
     primXml.createElement(doc, root, "status", getStatus());
    

    Element er =primXml.createEmptyElement(doc, root, "errors");
    for (String error : getErrors()) {
      primXml.createElement(doc, er, "error",error);
    }
    // сообщения
    Element msg =primXml.createEmptyElement(doc, root, "messages");
    for (String message : getMessageList()) {
      primXml.createElement(doc, msg, "message",message);
    }

    // параметры
    Element selfParams = primXml.createEmptyElement(doc, root, "selfParams");
    for (String key : getParams().keySet()) {
      Object parameter = getParams().get(key);
      Element paramet = primXml.createEmptyElement(doc, selfParams, "parameter");
      primXml.createElement(doc, msg, "paramName",key);
      primXml.createElement(doc, msg, "value",(parameter != null ? parameter.toString() : "null"));
    }

    // параметры DinamicModelList
    Element innerParams = primXml.createEmptyElement(doc, root, "innerParams");
    for (DinamicModel model : getDinamicArrayList()) {
      Element mod = primXml.createEmptyElement(doc, selfParams, "model");
      //model.getSelfInXml(doc, mod);
      Xml.dinamicModelToXml(doc, mod, model);
    }
  }

    @Override
    public void setStatus(Boolean boo) {
        if(boo==true){
            setStatusCode(statusCode.TRUE);
        }else{
            setStatusCode(statusCode.BIZ);
        }
    }

}
