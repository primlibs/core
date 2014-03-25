/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.controller;

import com.prim.core.AbstractApplication;
import com.prim.core.UploadedFile;
import com.prim.core.service.Service;
import com.prim.core.service.ServiceFactory;
import com.prim.core.warehouse.controllerStructure.ControllerMethod;
import com.prim.core.warehouse.controllerStructure.ControllerOrigin;
import com.prim.core.warehouse.controllerStructure.ControllerService;
import com.prim.core.warehouse.controllerStructure.ServiceParameter;
import com.prim.core.warehouse.controllerStructure.StructureController;
import com.prim.support.MyString;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * класс выполняет команду, которая поступила в приложение
 *
 * @author Pavel Rice
 */
public class Controller {

  /**
   * название контроллера
   */
  private String objectName;
  /**
   * команда контроллера, которую нужно выполнить
   */
  private String methodName;
  /**
   * объект, который содержит в себе результат выполнения команды.
   */
  private ActionResult actionResult = ActionResultPrim.getInstance();
  /**
   * массив с данными запроса 
   */
  private Map<String, Object> request = new HashMap<String, Object>();
  /**
   * массив с данными сессии
   */
  private Map<String, Object> session = new HashMap<String, Object>();
  /**
   * массив для хранения данных внутри контроллера
   */
  private Map<String, Object> innerParams = new HashMap<String, Object>();
  
  private List<UploadedFile> fileList = new ArrayList();
  
  /**
   * запущено ли в тестовом режиме
   */
  private Boolean test = false;
  /**
   * объект правs
   */
  private RightsObject rightsObject;
  /**
   * ид авторизованного пользователя
   */
  private Integer authorizedUserId;
  private Connection connection;
  private String info = "";
  private AbstractApplication app;

  /**
   *
   * @param objectName - название контроллера
   * @param methodName - название метода контроллера
   * @param request - объект с данными запроса
   * @param session - обект с данными сессии
   * @param test - является ли запуск тестовым
   */
  public Controller(String objectName, String methodName, Map<String, Object> request, Map<String, Object> session, Boolean test) {
    this.objectName = objectName;
    this.methodName = methodName;
    this.request = (request != null ? request : new HashMap<String, Object>());
    this.session = (session != null ? session : new HashMap<String, Object>());
    this.test = test;
  }

  /**
   * установить Application
   * @param app
   * @throws Exception 
   */
  final public void setApplication(AbstractApplication app) throws Exception{
    if(app!=null){
      this.app=app;
      this.rightsObject=app.getRightsObject();
    }else{
      throw new Exception("Application is null on controller");
    }
  }

  public void setFileList(List<UploadedFile> fileList) {
    this.fileList = fileList;
  }
  
  /**
   * получить информацию
   * @return 
   */
  public String getInfo() {
    return info;
  }

  /**
   * выполняет всю работу класса - вызывает сервисы(объекты бизнес-логики),
   * получает от них параметры
   *
   * @return
   * @throws Exception
   */
  public StatusCodes run() throws Exception {
    // получить массив информации о действиях данного контроллера
    ControllerMethod method = app.getKeeper().getControllerKeeper().getOneControllerMethod(objectName, methodName);
    if (method == null) {
      throw new Exception("Controller: method is null");
    }
    if (!rightsObject.methodInRight(objectName, methodName)) {
      String msg="";
      throw new Exception("Недостаточно прав для выполнения: " + objectName + " " + methodName + " для пользователя " + rightsObject.getUserId());
    }
    StatusCodes result = StatusCodes.BIZ;
    connection.setAutoCommit(false);
    for (ControllerService cs : method.getServiceList()) {
      result = executeService(cs);
      if (!result.equals(StatusCodes.TRUE)) {
        break;
      }
    }
    if (result.equals(StatusCodes.TRUE) && test == false) {
      connection.commit();
    } else {
      connection.rollback();
    }
    connection.setAutoCommit(true);
    return result;
  }

  /**
   * установить Connection
   * @param cnt 
   */
  public void setConnection(Connection cnt) {
    connection = cnt;
  }

  /**
   * получить объект результата
   *
   * @return
   */
  public ActionResult getActionResult() {
    return actionResult;
  }

  /**
   * получить объект прав
   * @return 
   */
  public RightsObject getRightsObject() {
    return rightsObject;
  }

  /**
   * установить объект прав
   * @param rightsObject 
   */
  public void setRightsObject(RightsObject rightsObject) {
    this.rightsObject = rightsObject;
  }

  /**
   * установить Id авторизованного пользователя
   * @param AuthorizedUserId 
   */
  public void setAuthorizedUserId(Integer AuthorizedUserId) {
    this.authorizedUserId = AuthorizedUserId;
  }

  /**
   * выполняет действие одного сервиса
   *
   * @param actionData
   */
  private StatusCodes executeService(ControllerService serv) throws Exception {
    String serviceName = serv.getServiceName();
    String serviceMethod = serv.getServiceAction();
    info += "request: " + request + "</br>";
    info += "session: " + session + "</br>";
    info += "inner: " + innerParams + "</br>";
    info += "Service: " + serv.getServiceName() + ":" + serv.getServiceAction() + "</br>";
    boolean result = false;
    try {
      // создать объект сервиса
      Service service = ServiceFactory.service(serviceName, app);
      if (service != null) {
        // получить массив входящих параметров
        Map<String, ServiceParameter> inputParams = serv.getInnerParams();

        HashMap<String, Object> newRequest = new HashMap<String, Object>();

        for (String paramName : inputParams.keySet()) {
          // название параметра в сервисе
          String paramAlias = inputParams.get(paramName).getAlias();
          // определить источник параметра
          ControllerOrigin paramSource = inputParams.get(paramName).getOrigin();
          // получить параметр
          Object parameter = null;
          if (paramSource == ControllerOrigin.Input) {
            parameter = innerParams.get(paramName);
          } else if (paramSource == ControllerOrigin.Request) {
            parameter = request.get(paramName);
          } else if (paramSource == ControllerOrigin.Session) {
            parameter = session.get(paramName);
          } else if (paramSource == ControllerOrigin.ReqSession) {
            parameter = (request.get(paramName) == null ? session.get(paramName) : request.get(paramName));
          } else if (paramSource == ControllerOrigin.SesRequest) {
            parameter = (session.get(paramName) == null ? request.get(paramName) : session.get(paramName));
          }

          if (parameter != null) {
            newRequest.put(paramAlias, parameter);
          }
        }
        info += "входящие:" + newRequest + "</br>";
        // вызвать метод сервиса, этот метод непосредственно выполняет действия, то есть бизнес-логику
        Method actionMethod = service.getClass().getMethod(serviceMethod);
        // передать в сервис все параметры запроса
        service.setRequest(newRequest);
        service.setFileList(fileList);
        app.getConnection().setAutoCommit(false);
        actionMethod.invoke(service);
        StructureController cnt = app.getKeeper().getControllerKeeper().getController(objectName);
        actionResult = service.getActionResult();
        actionResult.setObjectName(objectName);
        actionResult.setAppName(cnt.getAlias());
        // получить массив исходящих параметров
        Map<String, ServiceParameter> outputParams = serv.getOuterParams();
        info += "исходящие:" + serv.getOuterParams() + "</br>";
        info += "Результат:" + actionResult.getStatus() + "</br>";
        info += "Ошибки:" + actionResult.getErrors() + "</br>";
        for (String paramName : outputParams.keySet()) {
          paramName = paramName.trim();
          String paramAlias = outputParams.get(paramName).getAlias();
          ControllerOrigin paramSource = outputParams.get(paramName).getOrigin();
          // вызвать метод сервиса, получить параметр
          Object parameter = actionResult.get(paramName);
          // установить параметр
          if (paramSource == ControllerOrigin.Input) {
            innerParams.put(paramAlias, parameter);
          } else if (paramSource == ControllerOrigin.Session) {
            session.put(paramAlias, parameter);
          }
        }

      } else {
        throw new Exception("service " + serviceName + " not exists");
      }
    } catch (Exception e) {
      actionResult = ActionResultPrim.getInstance();
      actionResult.addError(MyString.getStackExeption(e));
      actionResult.setStatusCode(StatusCodes.CONTROLLER);
      throw new Exception("method  " + serviceMethod + " service " + serviceName + ": " + MyString.getStackExeption(e));
    }
    return actionResult.getStatus();
  }

  /**
   * установить массив внутренних параметров
   * @param innerParams 
   */
  public void setInnerParams(Map<String, Object> innerParams) {
    this.innerParams = innerParams;
  }

  /**
   * получить массив внутренних параметров
   * @return 
   */
  public Map<String, Object> getInnerParams() {
    return innerParams;
  }
}
