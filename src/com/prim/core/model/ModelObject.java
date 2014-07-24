/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.model;

import com.prim.core.AbstractApplication;
import com.prim.core.db.ExecutorFabric;
import com.prim.core.db.QueryExecutor;
import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.Structure;
import com.prim.core.modelStructure.Unique;
import com.prim.core.select.AgrTypes;
import com.prim.core.select.Select;
import com.prim.core.select.Table;
import com.prim.core.select.TableSelectFactory;
import com.prim.support.FileExecutor;
import com.prim.support.FormatDate;
import com.prim.support.MyString;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.prim.support.filterValidator.ChainValidator;
import com.prim.support.filterValidator.entity.ValidatorAbstract;

/**
 * класс реализует патерн ActiveRecord, т.е. объект этого класса представляет собой запись из таблицы БД
 *
 * @author User1
 */
final class ModelObject implements Model {

  /**
   * объект приложения
   */
  AbstractApplication app;
  final ExpandedDinamicModel expDinamicModel;
  private String parseQuery = "";
  private Map<String, Object> reqParams = new HashMap();
  /**
   * id последнего сохраненного файла
   */
  private int lastFileId = 0;

  /**
   *
   * @return реальное название первичного ключа
   */
  public String getPrimaryRealName() {
    return expDinamicModel.getStructure().getPrimaryRealName();
  }

  @Override
  public String getLastQueryText() {
    return parseQuery;
  }

  /**
   * @param structure - объект структуры
   * @param app - объект приложения
   */
  protected ModelObject(Structure structure, AbstractApplication app) throws CloneNotSupportedException {
    expDinamicModel = ModelFactory.getExpendedDinamicModel(structure);
    this.app = app;
    setDef();
  }

  @Override
  public void set(String name, Object value) {
    expDinamicModel.set(name, value);
    reqParams.put(name, value);
  }

  @Override
  public void set(Map<String, Object> params) {
    for (String name : params.keySet()) {
      if (expDinamicModel.getStructure().getField(name) != null) {
        expDinamicModel.set(name, params.get(name));
        reqParams.put(name, params.get(name));
      }
    }

  }

  /**
   *
   * @return разрешена ли работа с файлами
   */
  public Boolean getFileWork() {
    return expDinamicModel.getStructure().isFileWork();
  }

  @Override
  public int getLastFileId() {
    return lastFileId;
  }

  /**
   * возвращает значение первичного ключа
   *
   * @return
   */
  public Object getPrimary() {
    String primaryAlias = structure().getPrimaryAlias();
    Field primaryField = structure().getField(primaryAlias);
    return primaryField.getValue();
  }

  /**
   *
   * @return алиас первичного ключа
   */
  public String getPrimaryAlias() {
    return expDinamicModel.getStructure().getPrimaryAlias();
  }

  /**
   * определяет, системная ли модель
   *
   * @return
   */
  public Boolean isModelSystem() {
    return expDinamicModel.getStructure().isSystem();
  }

  /**
   * выполняет поиск в БД по первичному ключу. устанавливает параметры модели.
   *
   * @return
   * @throws Exception
   */
  public Boolean findByPrimary() throws Exception {
    return findByPrimary(false);
  }

  /**
   * выполняет поиск в БД по первичному ключу. устанавливает параметры модели.
   *
   * @param onlyActive искать только активные
   * @return - успешно ли выполнился поиск
   */
  public Boolean findByPrimary(Boolean onlyActive) throws Exception {
    boolean result = false;
    Object primaryValue = getPrimary();
    if (primaryValue != null) {
      TableSelectFactory tf = new TableSelectFactory(app);
      Table tb = tf.getTable(structure().getTableAlias());
      Select sel = tf.getSelect();
      sel.select(tb);
      sel.from(tb);
      sel.and(tb.getPrimary().eq(expDinamicModel.getStructure().getField(expDinamicModel.getStructure().getPrimaryAlias()).getValue()));
      if (onlyActive == true) {
        sel.and(tb.get("delete_date").isNull());
      }
      sel.executeSelect(app.getConnection());
      parseQuery = sel.getPrepareSelect();
      List<DinamicModel> list = sel.getDinamicList();
      if (list.size() > 0) {
        DinamicModel dm = list.get(0);
        if (!isModelSystem()) {
          Object typeStructure = expDinamicModel.getStructure().getField("user_data_type_id");
          Object typeDm = expDinamicModel.getStructure().getField("user_data_type_id");
          if (typeStructure == null || typeDm == null || typeStructure.equals("") || !typeDm.equals(typeStructure)) {
            expDinamicModel.addError(app.ANOTHER_TYPE_ERROR);
          }
        }
        if (expDinamicModel.getError().isEmpty()) {
          expDinamicModel.set(dm.getParams());
          result = true;
        }
      } else {
        expDinamicModel.addError(app.FIND_ERROR);
      }
    } else {
      expDinamicModel.addError(app.PRIMARY_SEARCH_ERROR);
    }
    return result;
  }

  /**
   * сохранение записи. то есть, добавление в таблицу новой записи, либо обновление существующей.
   *
   * @return
   */
  public Boolean save() throws Exception {
    boolean done = false;
    Object primary = getPrimary();
    if (primary == null) {
      done = insert();
    } else {
      done = update();
    }
    return done;
  }

 

  /**
   * добавить новую запись в таблицу
   *
   * @return - успешно ли добавилась запись в БД
   */
  private boolean insertNoValidate() throws Exception {
    setParamsBeforeInsert();

    // составить запрос
    String queryString = "insert into " + expDinamicModel.getStructure().getTableName() + " set";
    queryString = queryString + prepareSaveParams();
    // записать в базу данных, получить результат операции
    QueryExecutor query = ExecutorFabric.getExecutor(app.getConnection(), queryString);
    boolean result = query.update();
    parseQuery = query.getQueryText();
    // TODO записать QueryLog
    if (result == false) {
      expDinamicModel.addError(query.getError());
    } else {
      try {
        QueryExecutor query2 = ExecutorFabric.getExecutor(app.getConnection(), "SELECT LAST_INSERT_ID() id");
        query2.select();
        parseQuery = query2.getQueryText();
        List<Map<String, Object>> resultSet = query2.getResultList();
        expDinamicModel.set(expDinamicModel.getStructure().getPrimaryAlias(), resultSet.get(0).get("id"));
      } catch (SQLException e) {
        expDinamicModel.addError(app.LAST_ID_ERROR + expDinamicModel.getStructure().getTableAlias());
      }
    }

    return result;
  }

  /**
   * обновить запись в БД
   *
   * @return - успешно ли обновилась запись
   */
  private boolean updateNoValidate() throws Exception {

    setParamsBeforeUpdate();

    ModelFactory mf = new ModelFactory(app);

    Model model = mf.getModel(expDinamicModel.getStructure().getTableAlias());
    model.set(expDinamicModel.getParams());

    boolean result = model.findByPrimary();
    TableSelectFactory tf = new TableSelectFactory(app);
    Select sel = tf.getSelect();
    // если уже есть такая запись в БД
    if (result == true) {
      // устанавливаем значения для обновляемых параметров
      Map<String, Object> searchResult = expDinamicModel.getParams();
      for (String fieldName : expDinamicModel.getStructure().getCloneFields().keySet()) {
        Field field = expDinamicModel.getStructure().getField(fieldName);
        if (field.isUpdatable() && reqParams.containsKey(fieldName)) {
          Object value = searchResult.get(fieldName);
          model.set(fieldName, value);
        }
      }
      String queryString = "update " + expDinamicModel.getStructure().getTableName() + " set "
              + model.prepareSaveParams() + " where " + expDinamicModel.getStructure().getPrimaryRealName() + "=" + sel.validateParameter(expDinamicModel.getStructure().getField(expDinamicModel.getStructure().getPrimaryAlias()).getValue().toString(), true);
      QueryExecutor query = ExecutorFabric.getExecutor(app.getConnection(), queryString);
      result = query.update();
      parseQuery = query.getQueryText();
      if (result == false) {
        expDinamicModel.addError(query.getError());
      }
    } else {
      expDinamicModel.addError(app.UPDATE_ERROR);
      expDinamicModel.addError(model.getError());
    }
    return result;
  }

  /**
   * удаление записи
   *
   * @return
   */
  public Boolean delete() throws Exception {
    boolean done = false;
    Object primary = getPrimary();
    if (primary != null) {
      findByPrimary();
      if (expDinamicModel.getError().isEmpty()) {
        TableSelectFactory tf = new TableSelectFactory(app);
        Select sel = tf.getSelect();
        String queryString = "delete from " + expDinamicModel.getStructure().getTableName() + " where " + structure().getPrimaryRealName()
                + "= " + sel.validateParameter(structure().getField(structure().getPrimaryAlias()).getValue().toString(), true);
        QueryExecutor query = ExecutorFabric.getExecutor(app.getConnection(), queryString);
        done = query.update();
        parseQuery = query.getQueryText();
        if (!done) {
          expDinamicModel.addError(query.getError());
        }
      }
    } else {
      expDinamicModel.addError(app.DELETE_PRIMARY_ERROR);
    }
    return done;
  }

  /**
   * возвращает имя директории, в которой хранятся файлы этой модели.
   *
   * @return
   */
  public String getFileDirectory() throws Exception {
    String path = null;
    if (!structure().isSystem() & structure().isFileWork() & getPrimary() != null) {
      //path = OptionsSingleton.getInstance().getFilePath() + "/" + getPrimary();
      path = app.getFilePath() + "/" + getPrimary();
    }
    return path;
  }

  /**
   * возвращает массив с информацией обо всех файлах этой модели
   *
   * @return
   * @throws Exception
   */
  public void createFillesInfo() throws Exception {
    TableSelectFactory tf = new TableSelectFactory(app);
    Select sel = tf.getSelect();
    if (!structure().isSystem() & structure().isFileWork() & getPrimary() != null) {
      QueryExecutor query = ExecutorFabric.getExecutor(app.getConnection(), "select * from files where model_id = " + sel.validateParameter(structure().getField(structure().getPrimaryAlias()).getValue().toString(), true));
      query.select();
      expDinamicModel.addFileArray(query.getResultList());
    }
  }

  /**
   * сохранение нового файла с заданным контентом в виде строки
   *
   * @param name - имя файла
   * @param content - содержимое файла
   * @return - успешно ли добавился файл
   * @throws Exception
   */
  public Boolean saveFile(String name, String content, Integer userId, Date date) throws Exception {
    if (content != null) {
      return saveFile(name, content.getBytes(), userId, date);
    } else {
      expDinamicModel.addError("content is null");
      return false;
    }

  }

  /**
   * сохранение нового файла с контентом в виде массива байтов
   *
   * @param name - название файла
   * @param content - массив байтов для сохранения
   * @return - удалось ли сохранение
   * @throws Exception
   */
  public Boolean saveFile(String name, byte[] content, Integer userId, Date date) throws Exception {
    if (name != null) {
      boolean result = allowedFileWork();
      if (result) {
        Integer id = addFileToTable(name, userId, date);
        lastFileId = id;
        result = (id > 0 ? true : false);
        if (result == true) {
          FileExecutor file = new FileExecutor(getFileDirectory(), id.toString());
          result = file.writeBytes(content);
          // если не удалось сохранить файл, то откатить транзакцию
          if (!result) {
            expDinamicModel.addError(app.FILE_SAVE_ERROR);
          }
        }
      }
      return result;
    } else {
      expDinamicModel.addError("Имя файла не определены");
      return false;
    }

  }

  /**
   * сохранить новый файл, который является копией файла, расположенного по указанному пути
   *
   * @param path - полный путь к файлу
   * @return - удалось ли сохранение
   * @throws Exception
   */
  public Boolean copyFile(String path, String newName, Integer userId, Date date) throws Exception {
    boolean result = allowedFileWork();
    FileExecutor sourceFile = new FileExecutor(path);
    if (!sourceFile.exists()) {
      result = false;
      expDinamicModel.addError(app.FILE_COPY_ERROR);
    }
    if (result == true) {
      // начать транзакцию
      // сделать запись в БД
      Integer id = 0;
      if (newName != null && !newName.equals("")) {
        id = addFileToTable(newName, userId, date);
      } else {
        id = addFileToTable(sourceFile.getName(), userId, date);
      }
      lastFileId = id;
      result = (id > 0 ? true : false);
      // если запись успешна
      if (result == true) {
        // выполнить действия по сохранению
        result = sourceFile.copy(getFileDirectory());
        if (result == true) {
          result = sourceFile.rename(id.toString());
        }
        if (result == false) {
          expDinamicModel.addError(app.FILE_SAVE_ERROR);
        }
      }
    }
    return result;
  }

  /**
   * копировать файл
   *
   * @param path путь к файлу
   * @param userId id пользователя
   * @param date дата операции
   * @return
   * @throws Exception
   */
  public boolean copyFile(String path, Integer userId, Date date) throws Exception {
    return copyFile(path, null, userId, date);
  }

  /**
   * скопировать все файлы из данной директории
   *
   * @param dirName путь до директории
   * @param userId id пользователя
   * @param date дата
   * @return
   * @throws Exception
   */
  public boolean copyFilesFromDir(String dirName, Integer userId, Date date) throws Exception {
    boolean ok = false;
    File dir = new File(dirName);
    if (dir.isDirectory()) {
      // получаем массив имен всех объектов, который сдержатся в директории
      String[] fileList = dir.list();
      for (String name : fileList) {
        String fullName = dirName + "/" + name;
        // если объект является файлом, то копируем его в директорию этой модели
        if (new File(fullName).isFile()) {
          ok = copyFile(fullName, null, userId, date);
          if (!ok) {
            break;
          }
        }
      }
    }
    return ok;
  }

  /**
   * удалить файл по его ИД
   *
   * @param fileId - ИД файла
   * @return - удален ли файл
   * @throws Exception
   */
  public Boolean deleteFile(Integer fileId) throws Exception {
    boolean ok = allowedFileWork();
    FileExecutor file = new FileExecutor(getFileDirectory(), fileId.toString());
    if (ok) {
      if (!file.exists()) {
        ok = deleteFileToTable(fileId);
        //ok = false;
        //errors.add(app.FILE_DELETE_EXISTS_ERROR);
      }
    }
    if (ok) {
      ok = deleteFileToTable(fileId);
      if (ok) {
        ok = file.delete();
        // если не удалось удалить файл, то откатить транзакцию
        if (!ok) {
          expDinamicModel.addError(app.FILE_DELETE_ERROR);
        }
      }
    }
    return ok;
  }

  /**
   * удалить все файлы данной модели
   *
   * @return
   */
  public Boolean deleteAllFiles() throws Exception {
    // получить массив всех своих файлов 
    boolean ok = true;
    createFillesInfo();
    // пройти по массиву, удалить каждый файл по ИД
    for (Map<String, Object> file : expDinamicModel.getFileArray()) {
      int id = Integer.parseInt(file.get("file_id").toString());
      ok = deleteFile(id);
      if (!ok) {
        break;
      }
    }
    if (ok) {
      File dir = new File(getFileDirectory());
      dir.delete();
    }
    return ok;
  }

  /**
   * получить файл
   *
   * @param fileId ид файла
   * @return
   */
  public File getFile(String fileId) {
    try {
      String path = getFileDirectory();
      if (path != null) {
        File f = new File(path + "/" + fileId);
        if (f.exists() && !f.isDirectory()) {
          return f;
        } else {
          expDinamicModel.addError(app.FILE_SEARCH_ERROR);
        }
      } else {
        expDinamicModel.addError(app.PRIMARY_SEARCH_ERROR);
      }
    } catch (Exception ex) {
    }
    return null;
  }

  /**
   * получить все файлы модели в архиве zip
   *
   * @return
   * @throws Exception
   */
  public byte[] doZip() throws Exception {
    createFillesInfo();
    ZipArchive zip = new ZipArchive();
    for (Map<String, Object> map : expDinamicModel.getFileArray()) {
      if (map.get("file_id") != null && map.get("rusname") != null) {
        zip.addFile(getFileDirectory() + "/" + map.get("file_id"), map.get("rusname").toString());
      }
    }
    byte[] bytes = zip.doZip();
    expDinamicModel.addError(zip.getErrors());
    return bytes;
  }

  /**
   * установить значение первичного ключа
   *
   * @param ob
   */
  public void setPrimary(Object ob) {
    expDinamicModel.set(expDinamicModel.getStructure().getPrimaryAlias(), ob);
  }

  /**
   * применить валидацию и фильтрацию к значению одного поля
   *
   * @param name название поля
   * @return успешно ли прошла валидация
   */
  public Boolean validateField(String name) {
    Boolean res = false;
    //наличие строки
    if (MyString.NotNull(name)) {
      //валидация
      res = validateField(expDinamicModel.getStructure().getField(name));

    } else {
      expDinamicModel.addError("При валидаци поля не передано имя");
    }
    return res;
  }

  /**
   * отдает файл в виде контента
   *
   * @param fileId ид файла
   */
  public byte[] getFileContentByte(String fileId) {
    File fl = getFile(fileId);
    if (fl != null && expDinamicModel.getError().isEmpty()) {
      FileExecutor fe = new FileExecutor(fl);
      return fe.readBytes();
    }
    return null;
  }

  /**
   * отдает файл в виде строки
   *
   * @param fileId ид файла
   */
  public String getFileContentString(String fileId) {
    File fl = getFile(fileId);
    if (fl != null && expDinamicModel.getError().isEmpty()) {
      FileExecutor fe = new FileExecutor(fl);
      return fe.readString();
    }
    return null;
  }

  /**
   * устанавливает значения по умолчанию
   */
  private void setDef() throws CloneNotSupportedException {
    for (Field fi : expDinamicModel.getStructure().getCloneFields().values()) {
      if (fi.getDef() != null) {
        fi.setValue(fi.getDef());
      }
    }
  }

  /**
   * добавить новую запись в таблицу
   *
   * @return - успешно ли добавилась запись в БД
   */
  private boolean insert() throws Exception {
    setParamsBeforeInsert();
    boolean result = validateAllFields(false, false);

    // если результат равен истина
    if (result == true) {

      // составить запрос
      String queryString = "insert into " + expDinamicModel.getStructure().getTableName() + " set";
      queryString = queryString + prepareSaveParams();
      // записать в базу данных, получить результат операции
      QueryExecutor query = ExecutorFabric.getExecutor(app.getConnection(), queryString);
      result = query.update();
      parseQuery = query.getQueryText();
      // TODO записать QueryLog
      if (result == false) {
        expDinamicModel.addError(query.getError());
      } else {
        try {
          QueryExecutor query2 = ExecutorFabric.getExecutor(app.getConnection(), "SELECT LAST_INSERT_ID() id");
          query2.select();
          parseQuery = query2.getQueryText();
          List<Map<String, Object>> resultSet = query2.getResultList();
          expDinamicModel.set(expDinamicModel.getStructure().getPrimaryAlias(), resultSet.get(0).get("id"));
        } catch (SQLException e) {
          expDinamicModel.addError(app.LAST_ID_ERROR + expDinamicModel.getStructure().getTableAlias());
        }
      }
    }
    return result;
  }

  /**
   * обновить запись в БД
   *
   * @return - успешно ли обновилась запись
   */
  private boolean update() throws Exception {

    setParamsBeforeUpdate();

    ModelFactory mf = new ModelFactory(app);

    Model model = mf.getModel(expDinamicModel.getStructure().getTableAlias());
    model.set(expDinamicModel.getParams());

    boolean result = model.findByPrimary();
    TableSelectFactory tf = new TableSelectFactory(app);
    Select sel = tf.getSelect();
    // если уже есть такая запись в БД
    if (result == true) {
      // устанавливаем значения для обновляемых параметров
      Map<String, Object> searchResult = expDinamicModel.getParams();
      for (String fieldName : expDinamicModel.getStructure().getCloneFields().keySet()) {
        Field field = expDinamicModel.getStructure().getField(fieldName);
        if (field.isUpdatable() && reqParams.containsKey(fieldName)) {
          Object value = searchResult.get(fieldName);
          model.set(fieldName, value);
        }
      }
      result = model.validateAllFields(true, true);
      if (result == true) {
        String queryString = "update " + expDinamicModel.getStructure().getTableName() + " set "
                + model.prepareSaveParams() + " where " + expDinamicModel.getStructure().getPrimaryRealName() + "=" + sel.validateParameter(expDinamicModel.getStructure().getField(expDinamicModel.getStructure().getPrimaryAlias()).getValue().toString(), true);
        QueryExecutor query = ExecutorFabric.getExecutor(app.getConnection(), queryString);
        result = query.update();
        parseQuery = query.getQueryText();
        if (result == false) {
          expDinamicModel.addError(query.getError());
        }
      } else {
        expDinamicModel.addError(model.getError());
      }
    } else {
      expDinamicModel.addError(app.UPDATE_ERROR);
      expDinamicModel.addError(model.getError());
    }
    return result;
  }

  /**
   * установка параметров по умолчанию перед вставкой данных
   */
  private void setParamsBeforeInsert() {
  }

  /**
   * установка параметров по умолчанию перед изменением
   */
  private void setParamsBeforeUpdate() {
  }

  /**
   * возвращает часть строки запроса на update или insert
   *
   * @return
   */
  @Override
  public String prepareSaveParams() throws CloneNotSupportedException {
    String query = "";
    int i = 0;
    Map<String, Field> fieldsMap = structure().getCloneFields();
    TableSelectFactory tf = new TableSelectFactory(app);
    Select sel = tf.getSelect();
    for (String alias : fieldsMap.keySet()) {
      Field field = fieldsMap.get(alias);
      String fieldName = field.getName();
      if (!alias.equals(structure().getPrimaryAlias())) {
        String dot = (i == 0 ? " " : ", ");
        query = query + dot + fieldName + "= " + sel.validateParameter(field.getValue(), true);
        i++;
      }
    }
    return query;
  }

  /**
   * возвращает список параметров модели в строке, для SQL-запроса
   *
   * @return
   */
  private String getModelParamsToString() throws CloneNotSupportedException {
    String queryString = "";
    int i = 0;
    String dot;
    Map<String, Field> fields = structure().getCloneFields();
    for (String name : fields.keySet()) {
      Field field = fields.get(name);
      dot = (i == 0 ? " " : ", ");
      queryString = queryString + dot + field.getName() + " " + field.getAlias();
      i++;
    }
    return queryString;
  }

  /**
   * валидировать все параметры
   *
   * @param validatePrimary - валидировать ли первичный ключ
   * @param update - если true - то это запрос на обновление (update), если false - то это запрос на вставку (insert)
   * @return - валидны ли все параметры
   */
  @Override
  public Boolean validateAllFields(Boolean validatePrimary, boolean update) throws Exception {
    boolean allParameterValid = true;
    Map<String, Field> fieldsMap = structure().getCloneFields();

    for (String fieldName : fieldsMap.keySet()) {

      Field field = fieldsMap.get(fieldName);
      // если это первичный ключ и если установлен флаг false - то пропустить
      if (fieldName.equals(structure().getPrimaryAlias()) & !validatePrimary) {
        continue;
      }
      // валидировать

      if (!validateField(field) | !validateFieldToRelations(field)) {
        allParameterValid = false;
        break;
      }
    }

    // проверка уникальных значений
    if (allParameterValid) {
      allParameterValid = validateUnique(update);
    }

    return allParameterValid;
  }

  /**
   * валидация одного поля модели
   *
   * @param field - объект поля
   * @return - результат валидации
   */
  private boolean validateField(Field field) {
    if (field != null) {
      boolean isValid;
      // создать цепочку валидаторов
      for (ValidatorAbstract validator : field.getValidatorList()) {
        validator.setTerminate(true);
      }
      ChainValidator chain = new ChainValidator(field.getValidatorList());
      // получить значение параметра, которое соответствует имени поля
      String fieldAlias = field.getAlias();
      String fieldAppName = field.getAppName();
      Object parameter = structure().getField(fieldAlias).getValue();

      if (parameter != null && !parameter.equals("")) {
        // провести валидацию
        isValid = chain.execute(parameter);

        structure().getField(fieldAlias).setValue(chain.getData());
        if (!isValid) {
          List<String> errors = chain.getErrors();
          int size = errors.size();
          String lastError = errors.get(size - 1);
          lastError = lastError += "; ";
          errors.set(size - 1, lastError);
          expDinamicModel.addError("Параметр " + fieldAppName + ": ");
          expDinamicModel.addError(errors);
        }
      } else {
        // если значение обязательное
        if (field.isMandatory()) {
          isValid = false;
          expDinamicModel.addError(app.NESSESARY_PARAM_ERROR + fieldAppName);
        } else {
          isValid = true;
        }
      }
      return isValid;
    } else {
      expDinamicModel.addError("Model: переданное поле не существует");
      return false;
    }
  }

  /**
   * валидация поля на relations
   *
   * @param field - объект поля
   * @return
   */
  private boolean validateFieldToRelations(Field field) {

    boolean status = true;
    /*
     try {
     if (field.getRelations() != null && !field.getRelations().equals("") && field.getValue()!= null && !field.getValue().equals("")) {
     Model model = ModelFactory.getModel(field.getRelations(), connection);
     model.setParameter(model.getPrimaryAlias(), field.getValue());
     if (model.findByPrimary()) {
     status = true;
     } else {
     errors.add("ошибка валидации зависимости, параметр " + field.getAlias());
     status = false;
     }
     }
     } catch (Exception e) {
     errors.add("ошибка валидации зависимости, параметр " + field.getAlias() + ", " + e.getMessage());
     status = false;
     }
     * */
    return status;

  }

  /**
   * проверка полей на уникальность
   *
   * @param update - если true - то это запрос на обновление (update), если false - то это запрос на вставку (insert)
   * @return
   * @throws Exception
   */
  private boolean validateUnique(boolean update) throws Exception {
    for (Unique unique : structure().getUniqueList()) {
      List<String> fieldsNames = new ArrayList();
      for (String fieldName : unique.getFieldNames()) {
        if (structure().getCloneFields().containsKey(fieldName)) {
          fieldsNames.add(fieldName);
        }
      }
      // если есть более одного поля, по которым надо проверить - составить запрос
      if (!fieldsNames.isEmpty()) {
        TableSelectFactory tf = new TableSelectFactory(app);
        Select sel = tf.getSelect();
        Table tb = tf.getTable(structure().getTableAlias());
        // найти количество моделей такого же типа
        sel.select(tb.getPrimary(), "count", AgrTypes.COUNT);
        sel.from(tb);
        for (String name : fieldsNames) {
          sel.and(tb.get(name).eq(structure().getField(name).getValue()));
        }
        // добавить условие по delete_date
        if (!unique.isCheckDeleted() && structure().getCloneFields().containsKey("delete_date")) {
          sel.and(tb.get("delete_date").isNull());
        }
        // исполнить запрос
        boolean ok = sel.executeSelect(app.getConnection());
        if (!ok) {
          expDinamicModel.addError(sel.getError());
          return false;
        } else {
          int count = 1;
          try {
            count = Integer.parseInt(sel.getDinamicList().get(0).get("count").toString());
          } catch (Exception e) {
          }

          int limit;
          if (update) {
            limit = 1;
          } else {
            limit = 0;
          }

          if (count > limit) {
            expDinamicModel.addError("найдена другая запись с таким же значением " + unique.getFieldNames());
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * проверяет, разрешена ли работа с файлами для данной модели. Также создает директорию для файлов модели, если директория ещё не
   * существует.
   *
   * @return
   */
  private boolean allowedFileWork() throws Exception {
    boolean ok = true;
    if (structure().isSystem() | !structure().isFileWork()) {
      ok = false;
      expDinamicModel.addError(app.FILE_PERMISSION_ERROR);
    }
    String fileDir = getFileDirectory();
    if (fileDir == null) {
      ok = false;
      expDinamicModel.addError(app.FILE_PRIMARY_ERROR);
    }
    if (ok) {
      // если не существует директории - создать её.
      File dir = new File(fileDir);
      if (!dir.isDirectory()) {
        ok = dir.mkdirs();
      }
      if (!ok) {
        expDinamicModel.addError(app.DIRECTORY_ERROR);
      }
    }
    return ok;
  }

  /**
   * добавляет запись о файле в БД
   *
   * @param rusName - пользовательское имя файла
   * @return - в случае успешного добавление ИД новой записи, в случае неудачи -1
   */
  private int addFileToTable(String rusName, Integer userId, Date date) throws Exception {
    int id = -1;
    if (userId != null && date != null) {
      if (getPrimary() != null) {
        ModelFactory mf = new ModelFactory(app);
        Model filesModel = mf.getModel("files");
        filesModel.set("model_id", getPrimary());
        filesModel.set("rusname", rusName);
        filesModel.set("user_id", userId);
        filesModel.set("insert_date", FormatDate.getDateInMysql(date));
        // TODO получать user_id из сессии
        boolean done = filesModel.save();
        if (done) {
          id = Integer.parseInt(filesModel.getPrimary().toString());
        } else {
          expDinamicModel.addError(app.FILE_SAVE_ERROR);
          expDinamicModel.addError(filesModel.getError());
        }
      } else {
        expDinamicModel.addError(app.FILE_PRIMARY_ERROR);
      }
    } else {
      expDinamicModel.addError("Добавивший пользователь или дата добавления не определены");
    }
    return id;
  }

  /**
   * удаляет запись о файле из БД
   *
   * @param id - ИД файла
   * @return - успешно ли удаена запись
   */
  private boolean deleteFileToTable(Integer id) throws CloneNotSupportedException {
    boolean done = false;
    if (getPrimary() != null) {
      TableSelectFactory tf = new TableSelectFactory(app);
      Select sel = tf.getSelect();
      QueryExecutor query = ExecutorFabric.getExecutor(app.getConnection(),
              "delete from files where file_id = " + sel.validateParameter(id.toString(), true));
      done = query.update();
      if (!done) {
        expDinamicModel.addError(app.FILE_DATA_DELETE_ERROR);
        expDinamicModel.addError(query.getError());
      }
    } else {
      expDinamicModel.addError(app.FILE_PERMISSION_ERROR);
    }
    return done;
  }

  private Structure structure() {
    return expDinamicModel.getStructure();
  }

  @Override
  public List<String> getError() {
    return expDinamicModel.getError();
  }

  @Override
  public List<Map<String, Object>> getFileArray() {
    return expDinamicModel.getFileArray();
  }

  @Override
  public Object get(String name) {
    return expDinamicModel.get(name);
  }

  @Override
  public Map<String, Object> getParams() throws CloneNotSupportedException {
    return expDinamicModel.getParams();
  }

  @Override
  public DinamicModel getDinamicModel() throws CloneNotSupportedException {
    return ModelFactory.getDinamicModel(expDinamicModel);
  }

  @Override
  public Structure getStructure() throws CloneNotSupportedException {
    return expDinamicModel.getStructureClone();
  }

}
