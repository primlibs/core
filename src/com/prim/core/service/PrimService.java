/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.service;

import com.prim.core.AbstractApplication;
import com.prim.core.UploadedFile;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.prim.core.controller.ActionResult;
import com.prim.core.controller.ActionResultPrim;
import com.prim.core.controller.RightsObject;
import com.prim.core.model.DinamicModel;
import com.prim.core.model.Model;
import com.prim.core.model.ModelFactory;
import com.prim.core.select.Condition;
import com.prim.core.select.OrdTypes;
import com.prim.core.select.Select;
import com.prim.core.select.Table;
import com.prim.core.select.TableSelectFactory;
import com.prim.support.filterValidator.ChainValidator;
import com.prim.support.FormatDate;
import com.prim.support.MyString;

/**
 * класс сервиса, то есть бизнес-логики. От него наследуются другие классы
 * бизнес-логики
 *
 * @author Кот
 */
final class PrimService implements Service {

  private List<UploadedFile> fileList = new ArrayList();
  /**
   * название типа модели
   */
  private final String modelName;
  /**
   * алиас словаря
   */
  private ArrayList<String> descAliases = new ArrayList<String>();
  /**
   * массив с информацией о файлах
   */
  protected Map<String, String> filesMap = new HashMap<String, String>();
  /**
   * объект приложения
   */
  private final AbstractApplication app;
  /**
   * объект прав
   */
  private final RightsObject rightsObject;
  /**
   * массив с данными запроса
   */
  private Map<String, Object> request = new HashMap<String, Object>();
  /**
   * объект, который содержит в себе результаты запроса
   */
  private ActionResult actionResult = ActionResultPrim.getInstance();
  private Integer fileId;
  /**
   * ид текущего авторизованного пользователя
   */
  private final Integer authorizedUserId;
  /**
   * объект ModelFactory
   */
  private final ModelFactory modelFactory;
  final private Date operationDate;

  public static PrimService getInstance(String modelName, AbstractApplication app) throws Exception {
    return new PrimService(modelName, app, null, null);
  }

  public static PrimService getInstance(String modelName, AbstractApplication app, Integer authorizedUserId, Date operationDate) throws Exception {
    return new PrimService(modelName, app, authorizedUserId, operationDate);
  }

  /**
   *
   * @param modelName название модели, к которой относится сервис
   * @throws Exception
   */
  private PrimService(String modelName, AbstractApplication app, Integer authorizedUserId, Date operationDate) throws Exception {
    this.modelName = modelName;
    this.app = app;
    modelFactory = new ModelFactory(app);
    this.rightsObject = app.getRightsObject();
    if (authorizedUserId != null) {
      this.authorizedUserId = authorizedUserId;
    } else {
      String userId = "";
      if (rightsObject != null) {
        userId = rightsObject.getUserId();
      }
      if (MyString.NotNull(userId)) {
        this.authorizedUserId = Integer.parseInt(userId.toString());
      } else {
        this.authorizedUserId = -1;
      }
    }
    if (operationDate == null) {
      this.operationDate = Calendar.getInstance().getTime();
    } else {
      this.operationDate = operationDate;
    }
  }

  /**
   * добавить параметр в массив request
   *
   * @param name название параметра
   * @param val значение параметра
   */
  @Override
  public void setReq(String name, Object val) {
    if (name != null) {
      request.put(name, val);
    }
  }

  /**
   * получить параметр из массива request
   *
   * @param name название параметра
   * @return
   */
  public Object getReq(String name) {
    return request.get(name);
  }

  /**
   * найти активные (не удаленные) записи в БД
   *
   * @throws Exception
   */
  @Override
  public void findActive() {
    try {
      TableSelectFactory tf = new TableSelectFactory(app);
      Table table = tf.getTable(modelName);
      Select select = tf.getSelect();
      select.setCashable(Boolean.TRUE);
      select.select(table);
      select.from(table);
      if (modelFactory.getModel(modelName).isModelSystem() == false) {
        select.and(table.get("delete_user_id").isNull());
        select.and(table.get("delete_date").isNull());
      } else {
        select.and(table.get("active_to").isNull());
      }
      if (!descAliases.isEmpty()) {
        for (String al : descAliases) {
          select.order(table.get(al), OrdTypes.ASC);
        }
      }
      boolean status = select.executeSelect(app.getConnection());
      actionResult.select(select);
      setDictionary();
    } catch (Exception ex) {
      actionResult.setStatus(false);
      actionResult.addError(MyString.getStackExeption(ex));
    }
  }

  @Override
  public void findActiveByDate(String date) {

    ChainValidator chVal = ChainValidator.getInstance("DateToFormatFilter", "DateFormatValidator");
    Map<String, Object> map = new HashMap<String, Object>();


    if (chVal.execute(date) != false) {
      try {
        TableSelectFactory tf = new TableSelectFactory(app);
        Table table = tf.getTable(modelName);

        Select select = tf.getSelect();
        select.setCashable(Boolean.TRUE);
        select.select(table);
        select.from(table);
        select.and(table.getPrimary().isNotNull());
        if (modelFactory.getModel(modelName).isModelSystem() == false) {
          select.and(table.get("insert_date").lesserEq(chVal.getData()));
          select.and(table.get("delete_date").bigger(chVal.getData()));
          ArrayList<Condition> rs = new ArrayList<Condition>();
          rs.add(table.get("delete_date").bigger(chVal.getData()));
          rs.add(table.get("delete_date").isNull());
          select.andOr(rs);

        } else {
          ArrayList<Condition> rs = new ArrayList<Condition>();
          rs.add(table.get("active_to").bigger(chVal.getData()));
          rs.add(table.get("active_to").isNull());
          select.andOr(rs);
        }
        if (!descAliases.isEmpty()) {
          for (String al : descAliases) {
            select.order(table.get(al), OrdTypes.ASC);
          }
        }
        select.executeSelect(app.getConnection());
        actionResult.select(select);
        setDictionary();
      } catch (Exception ex) {
        actionResult.setStatus(false);
        actionResult.addError(MyString.getStackExeption(ex));
      }
    } else {
      actionResult.setStatus(false);
      actionResult.addError(chVal.getErrors());
    }

  }

  /**
   * получить все файлы модели в zip-архиве
   */
  public void getZipFile() {
    try {
      Model innerPost = modelFactory.getModel(modelName);
      innerPost.set(request);
      if (innerPost.findByPrimary()) {
        byte[] bytes = innerPost.doZip();
        actionResult.set("zip", bytes);
        actionResult.addError(innerPost.getError());
      } else {
        actionResult.setStatus(false);
        actionResult.addError("не найдена модель по ИД");
      }
    } catch (Exception e) {
      actionResult.setStatus(false);
      actionResult.addError(MyString.getStackExeption(e));
    }
  }

  /**
   * установить массив с информацией о файлах
   *
   * @param filesPath массив с информацией о файлах
   */
  public void setFilesPath(Map<String, String> filesPath) {
    if (filesPath != null) {
      this.filesMap = filesPath;
    }
  }

  /**
   * устанавливает массив параметров
   *
   * @param request
   */
  public void setRequest(Map<String, Object> request) {
    this.request = request;
  }

  public void setFileId(int fileId) {
    this.fileId = fileId;
  }

  /**
   * получить объект ActionResult, который представляет собой результат
   * выполнения сервиса
   *
   * @return
   */
  public ActionResult getActionResult() {
    return actionResult;
  }

  /**
   * поиск по первичному ключу
   */
  public void searchById() {
    try {
      Model model = modelFactory.getModel(modelName);
      model.set(request);
      boolean status = model.findByPrimary();
      if (status) {
        model.createFillesInfo();
      }
      actionResult.model(model.getDinamicModel());
      // присвоить в actionResult все параметры модели
      for (String name : model.getParams().keySet()) {
        actionResult.set(name, model.getParams().get(name));
      }
      ArrayList<DinamicModel> list = new ArrayList<DinamicModel>();
      list.add(model.getDinamicModel());
      actionResult.setDinamicArrayList(list);
      setDictionary();
    } catch (Exception ex) {
      actionResult.addError(MyString.getStackExeption(ex) + " " + modelName + " searchById()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * поиск автивных записей по первичному ключу
   */
  public void searchActiveById() {
    try {
      Model model = modelFactory.getModel(modelName);
      model.set(request);
      boolean status = model.findByPrimary(true);
      if (status) {
        model.createFillesInfo();
      }
      actionResult.model(model.getDinamicModel());
      // присвоить в actionResult все параметры модели
      for (String name : model.getParams().keySet()) {
        actionResult.set(name, model.getParams().get(name));
      }
      ArrayList<DinamicModel> list = new ArrayList<DinamicModel>();
      list.add(model.getDinamicModel());
      actionResult.setDinamicArrayList(list);
      setDictionary();
    } catch (Exception ex) {
      actionResult.addError(ex.toString() + " " + modelName + " searchById()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * удалить по первичному ключу
   */
  public void deleteById() {
    try {
      Model model = modelFactory.getModel(modelName);
      String primaryName = model.getPrimaryAlias();
      model.set(request);
      boolean status = model.delete();
      clearBaseCashes();
      actionResult.model(model.getDinamicModel());
      // присвоить в actionResult все параметры модели
      for (String name : model.getParams().keySet()) {
        actionResult.set(name, model.getParams().get(name));
      }
    } catch (Exception ex) {
      actionResult.addError(ex.toString() + " " + modelName + " deleteById()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * обновить данные модели
   */
  public void updateModel() {
    try {
      Model model = modelFactory.getModel(modelName);
      boolean status = false;
      model.set(request);
      if (!getModel().isModelSystem()) {
        model.set("update_user_id", authorizedUserId);
        model.set("update_date", FormatDate.getDateInMysql(operationDate));
      }
      if (model.getPrimary() == null || "".equals(model.getPrimary())) {
        status = false;
        actionResult.addError("Не обнаружен первичный ключ " + model.getPrimaryAlias());
      } else {
        status = model.save();
        actionResult.model(model.getDinamicModel());
      }
      // присвоить в actionResult все параметры модели
      for (String name : model.getParams().keySet()) {
        actionResult.set(name, model.get(name));
      }
      clearBaseCashes();
    } catch (Exception ex) {
      actionResult.addError(ex.toString() + " " + modelName + " updateModel()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * закрыть модель
   */
  public void closeModel() {
    try {
      Model model = modelFactory.getModel(modelName);
      boolean status = false;
      model.set(request);
      if (model.findByPrimary() == false) {
        status = false;
        actionResult.addError("Не обнаружен первичный ключ " + model.getPrimaryAlias());
      } else {
        if (getModel().isModelSystem()) {
          model.set("active_to", FormatDate.getDateInMysql(operationDate));
        } else {
          model.set("delete_user_id", authorizedUserId);
          model.set("delete_date", FormatDate.getDateInMysql(operationDate));
        }
        status = model.save();
        actionResult.model(model.getDinamicModel());
        // присвоить в actionResult все параметры модели
        for (String name : model.getParams().keySet()) {
          actionResult.set(name, model.get(name));
        }
      }
      actionResult.setStatus(status);
      clearBaseCashes();
    } catch (Exception ex) {
      actionResult.addError(ex.toString() + " " + modelName + " closeModel()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * получить пустую структуру модели. Например, для вывода формы.
   *
   * @return
   * @throws Exception
   */
  public void setStructure() {
    try {
      Model model = modelFactory.getModel(modelName);
      actionResult.model(model.getDinamicModel());
    } catch (Exception ex) {
      actionResult.addError(ex.toString() + " " + modelName + " setStructure()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * сохранить данные модели
   */
  @Override
  public void saveModel() {
    try {
      Model model = modelFactory.getModel(modelName);
      boolean status = false;
      model.set(request);
      if (!model.isModelSystem()) {

        model.set("insert_user_id", authorizedUserId);
        model.set("update_user_id", authorizedUserId);

        model.set("update_date", FormatDate.getDateInMysql(operationDate));
        model.set("insert_date", FormatDate.getDateInMysql(operationDate));
      } else {
        model.set("active_from", FormatDate.getDateInMysql(operationDate));
      }
      if (model.getPrimary() != null && !"".equals(model.getPrimary())) {
        status = false;
        actionResult.addError("Обнаружен первичный ключ " + model.getPrimaryAlias() + model.getPrimary());
      } else {
        status = model.save();
        actionResult.model(model.getDinamicModel());
      }
      // присвоить в actionResult все параметры модели
      for (String name : model.getParams().keySet()) {
        actionResult.set(name, model.get(name));
      }
      actionResult.setStatus(status);
    } catch (Exception ex) {
      actionResult.addError(MyString.getStackExeption(ex) + " " + modelName + " saveModel()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * найти всё
   */
  public void findAll() {
    try {
      TableSelectFactory tf = new TableSelectFactory(app);
      Table table = tf.getTable(modelName);
      Select select = tf.getSelect();
      select.setCashable(Boolean.TRUE);
      select.select(table);
      select.from(table);
      select.and(table.getPrimary().isNotNull());
      if (!descAliases.isEmpty()) {
        for (String al : descAliases) {
          select.order(table.get(al), OrdTypes.ASC);
        }
      }
      boolean status = select.executeSelect(app.getConnection());
      actionResult.select(select);
      setDictionary();
    } catch (Exception ex) {
      actionResult.addError(MyString.getStackExeption(ex) + " " + modelName + " findAll()");
      actionResult.setStatus(Boolean.FALSE);
    }

  }

  /**
   * удалить все файлы
   *
   * @return
   * @throws Exception
   */
  public void deleteAllFiles() {
    try {
      Model model = modelFactory.getModel(modelName);
      model.set(request);
      boolean status = false;
      if (model.findByPrimary()) {
        status = model.deleteAllFiles();
      }
      actionResult.model(model.getDinamicModel());
    } catch (Exception ex) {
      actionResult.addError(MyString.getStackExeption(ex) + " " + modelName + " deleteAllFiles()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * удалить файл по ID
   *
   * @return
   * @throws Exception
   */
  public void deleteFile() {
    try {
      boolean status = false;
      Model model = modelFactory.getModel(modelName);
      model.set(request);
      if (request.get("file_id") != null && model.findByPrimary()) {
        fileId = Integer.parseInt(request.get("file_id").toString());
        status = model.deleteFile(fileId);
      }
      actionResult.model(model.getDinamicModel());
      // присвоить в actionResult все параметры модели
      for (String name : model.getParams().keySet()) {
        actionResult.set(name, model.getParams().get(name));
      }
      actionResult.setStatus(status);
    } catch (Exception ex) {
      actionResult.addError(MyString.getStackExeption(ex) + " " + modelName + " deleteFile()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * сохранить файлы
   */
  @Override
  public void saveFiles() {
    try {
      Model model = modelFactory.getModel(modelName);
      model.set(request);
      boolean status = false;
      if (fileList == null || fileList.isEmpty()) {
        actionResult.addError("Не переданы файлы или размер одного из файлов превышает " + app.getMaxUploadSizeMB() + " МВ");
        actionResult.set(model.getPrimaryAlias(), model.get(model.getPrimaryAlias()));
      } else {
        if (model.findByPrimary()) {
          for (UploadedFile file : fileList) {
            if (file.getTemporaryPath() != null && !file.getTemporaryPath().isEmpty()) {
              status = model.copyFile(file.getTemporaryPath(), file.getName(), authorizedUserId, operationDate);
            } else {
              actionResult.addError("Невозможно определить файл");
            }
            if (status == false) {
              break;
            }
          }
        } else {
          actionResult.addError("Не найдена модель");
        }
        actionResult.model(model.getDinamicModel());
        // присвоить в actionResult все параметры модели
        for (String name : model.getParams().keySet()) {
          actionResult.set(name, model.get(name));
        }
      }
      actionResult.setStatus(status);
    } catch (Exception ex) {
      actionResult.addError(MyString.getStackExeption(ex) + " " + modelName + " saveFiles()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * получить файлы
   */
  public void getFile() {
    try {
      boolean status = false;
      Model model = modelFactory.getModel(modelName);
      model.set(request);
      if (request.get("file_id") != null && model.findByPrimary()) {
        fileId = Integer.parseInt(request.get("file_id").toString());
        if (fileId != null) {
          File f = model.getFile(fileId.toString());
          if (f != null) {
            actionResult.set("filePath", f.getAbsolutePath());
            Model fm = modelFactory.getModel("files");
            fm.set("file_id", fileId);
            if (fm.findByPrimary() == true) {
              actionResult.set("fileName", fm.get("rusname"));
              status = true;
            } else {
              actionResult.addError("Не найдена модель файла");
            }
          } else {
            actionResult.addError("Файл не найден");
          }
        } else {
          actionResult.addError("fileId не передан");
        }
      }
      // присвоить в actionResult все параметры модели
      for (String name : model.getParams().keySet()) {
        actionResult.set(name, model.get(name));
      }
      actionResult.setStatus(status);
    } catch (Exception ex) {
      actionResult.addError(MyString.getStackExeption(ex) + " " + modelName + " getFile()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * в объекте ActionResult установить словарь, т.е. массив информации для
   * вывода комбо
   *
   * Этот метод должен вызываться после того, как результаты запроса загружены в
   * actionResult.
   */
  public void setDictionary() {
    try {
      Map<String, Object> resultHash = new LinkedHashMap<String, Object>();
      if (descAliases != null) {
        String primaryAlias = modelFactory.getModel(modelName).getPrimaryAlias();
        for (DinamicModel model : actionResult.getDinamicArrayList()) {
          String value = model.get(primaryAlias).toString();
          String desc = "";
          for (String descAlias : descAliases) {
            if (model.get(descAlias) != null) {
              desc = desc + " " + model.get(descAlias);
            }
          }
          if (value != null) {
            if (!desc.equals("")) {
              resultHash.put(value, desc);
            } else {
              resultHash.put(value, value);
            }
          }
        }

      }
      actionResult.setDictionary(resultHash);
    } catch (Exception e) {
      actionResult.addError(MyString.getStackExeption(e) + " " + modelName + " setDictionary()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  protected void clearBaseCashes() throws Exception {
    //CasheSingleton.getInstance().clearAll();
  }

  /**
   * сохранить файл
   *
   * @param Id ИД модели, к которой принадлежит файл
   * @param name название файла
   * @param content контент файла
   */
  protected void saveFile(String Id, String name, byte[] content) {
    try {
      Boolean res = false;
      Model md = modelFactory.getModel(modelName);
      md.set(md.getPrimaryAlias(), Id);
      res = md.findByPrimary();
      if (res != false) {
        res = md.saveFile(name, content, authorizedUserId, operationDate);
      }
      actionResult.setStatus(res);
      actionResult.addError(md.getError());
    } catch (Exception e) {
      actionResult.addError(MyString.getStackExeption(e) + " " + modelName + " saveFile()");
      actionResult.setStatus(Boolean.FALSE);
    }
  }

  /**
   * возвращает модель того типа, которому соответствует сервис
   *
   * @return
   * @throws Exception
   */
  @Override
  public Model getModel() throws Exception {
    return modelFactory.getModel(modelName);
  }

  @Override
  public Model getModel(String name) throws Exception {
    return modelFactory.getModel(name);
  }

  /**
   * копировать файл в другую модель
   *
   * @param model модель, в которую надо копировать файл
   * @param fileId ИД файла
   * @return
   * @throws Exception
   */
  public boolean copyFile(Model model, String fileId) throws Exception {
    boolean status = false;
    // создать модель files
    Model file = modelFactory.getModel("files");
    // присвоить id
    file.set("file_id", fileId);
    // findByPrimary
    if (file.findByPrimary()) {
      // получить id model
      Object modelId = file.get("model_id");
      // получить путь к директории
      String path = app.getFilePath() + "/" + modelId;
      // создать полный путь к файлу
      path = path + "/" + fileId;
      // сохранить
      status = model.copyFile(path, file.get("rusname").toString(), authorizedUserId, operationDate);
      if (status == false) {
        actionResult.addError(model.getError());
      }
    } else {
      actionResult.addError(file.getError());
    }
    return status;
  }

  @Override
  public void addError(String... error) {
    actionResult.addError(error);
  }

  @Override
  public void addError(List<String> error) {
    actionResult.addError(error);
  }

  @Override
  public void setStatus(Boolean sts) {
    actionResult.setStatus(sts);
  }

  @Override
  public Map<String, Object> getRequestClone() {
    Map<String, Object> result = new HashMap();
    for (String name : request.keySet()) {
      result.put(name, request.get(name));
    }
    return result;
  }

  @Override
  public String getModelName() {
    return modelName;
  }

  @Override
  public void model(DinamicModel dm) {
    actionResult.model(dm);
  }

  @Override
  public void set(String name, Object value) {
    actionResult.set(name, value);
  }

  @Override
  public Connection getConnection() {
    return app.getConnection();
  }

  @Override
  public void select(Select sel) {
    actionResult.select(sel);
  }

  @Override
  public Integer getAuthorizedUserId() {
    return authorizedUserId;
  }

  @Override
  public Date getOperationDate() {
    Calendar result = Calendar.getInstance();
    result.setTime(operationDate);
    return result.getTime();
  }

  @Override
  public AbstractApplication getApp() {
    return app;
  }

  /**
   * установить имена полей, которые будут выводиться в комбо.
   *
   * @param name
   */
  @Override
  public void setDictAliases(String... name) {
    descAliases = new ArrayList<String>();
    for (String nm : name) {
      if (MyString.NotNull(nm)) {
        descAliases.add(nm);
      }
    }
  }

  @Override
  public RightsObject getRightsObject() {
    return app.getRightsObject();
  }

  @Override
  public void setDinamicArrayList(List<DinamicModel> list) {
    actionResult.setDinamicArrayList(list);
  }

  @Override
  public void setDict(Map<String, Object> hs) {
    actionResult.setDictionary(hs);
  }

  @Override
  public void addMessage(String... msg) {
    actionResult.addMessage(msg);
  }

  @Override
  public void addMessage(List<String> msg) {
    actionResult.addMessage(msg);
  }

  @Override
  public String[] getReqArray(String paramName) {
    if (getReq(paramName) != null) {
      try {
        String[] array = (String[]) getReq(paramName);
        return array;
      } catch (Exception e) {
        String param = getReq(paramName).toString();
        String[] array = {param};
        return array;
      }
    }
    String[] array = new String[0];
    return array;
  }

  @Override
  public void registerException(Exception exc) {
    setStatus(false);
    addError(MyString.getStackExeption(exc));
  }

  @Override
  public void setStandartFields(Model model, boolean isNewModel) {
    model.set("update_date", FormatDate.getCurrentDateInMysql());
    model.set("update_user_id", getAuthorizedUserId());
    if (isNewModel) {
      model.set("insert_date", FormatDate.getCurrentDateInMysql());
      model.set("insert_user_id", getAuthorizedUserId());
    }
  }

  @Override
  public void setFileList(List<UploadedFile> fileList) {
    this.fileList = fileList;
  }
  
/*
  public void executeSelect(Select select) throws Exception {
    boolean ok = select.executeSelect(getConnection());
    if (!ok) {
      throw new Exception(select.getError().toString());
    }
  }

  public void saveModel(Model model) throws Exception {
    boolean ok = model.save();
    if (!ok) {
      throw new Exception(model.getError().toString());
    }
  }
*/
  
  @Override
  public void executeSelect(Select select) throws Exception {
    boolean ok = select.executeSelect(getConnection());
    if (!ok) {
      setStatus(false);
      addError(select.getError());
      throw new Exception();
    }
  }
  
  @Override
  public void saveModel(Model model) throws Exception {
    boolean ok = model.save();
    if (!ok) {
      setStatus(false);
      addError(model.getError());
      throw new Exception();
    }
  }

  @Override
  public List<UploadedFile> getFileList() {
    return fileList;
  }
}
