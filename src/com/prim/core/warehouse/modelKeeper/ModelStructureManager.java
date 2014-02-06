package com.prim.core.warehouse.modelKeeper;

import com.prim.core.AbstractApplication;
import com.prim.core.db.ExecutorFabric;
import com.prim.core.db.QueryExecutor;
import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.FieldFabric;
import com.prim.core.modelStructure.Structure;
import com.prim.core.modelStructure.StructureFabric;
import com.prim.core.modelStructure.Unique;
import com.prim.core.modelStructure.UniqueObject;
import com.prim.core.select.Select;
import com.prim.core.select.TableSelectFactory;
import com.prim.core.warehouse.WarehouseSingleton;
import com.prim.support.MyString;
import com.prim.support.filterValidator.entity.DateFormatValidator;
import com.prim.support.filterValidator.entity.DateToFormatFilter;
import com.prim.support.filterValidator.entity.DecimalFilter;
import com.prim.support.filterValidator.entity.DecimalValidator;
import com.prim.support.filterValidator.entity.DigitsFilter;
import com.prim.support.filterValidator.entity.DigitsValidator;
import com.prim.support.filterValidator.entity.QuantityValidator;
import com.prim.support.filterValidator.entity.StringLenghtValidator;
import com.prim.support.filterValidator.entity.ValidatorAbstract;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * предоставляет методы для изменения данных в ModelStructureSingleton
 * 
* @author Pavel
 */
public class ModelStructureManager {

  /**
   * ошибки
   */
  private ArrayList<String> errors = new ArrayList<String>();
  /**
   * названия стандартных полей
   */
  private List<String> systemFieldNames = new ArrayList<String>();
  /**
   * объект приложния
   */
  private AbstractApplication app;

  public ModelStructureManager(AbstractApplication app) throws Exception {
    String names[] = {"user_data_type_id", "insert_date", "insert_user_id",
      "update_date", "update_user_id", "delete_date", "delete_user_id"};
    this.app = app;
    systemFieldNames = Arrays.asList(names);
    //mss = ModelStructureSingleton.getInstance();
  }

  /**
   * возвращает ошибки
   *   
* @return
   */
  public ArrayList<String> getErrors() {
    return errors;
  }

  /**
   * добавить поле
   *
   * @param fieldAlias
   * @param appName
   * @param mandatory
   * @param updatable
   * @param type
   * @param relations
   * @param structureAlias
   * @param def
   * @return
   * @throws Exception
   */
  public boolean addField(String fieldAlias, String appName, String mandatory, String updatable,
          String type, String relations, String structureAlias, String def) throws Exception {
    boolean status = false;
    if (fieldAlias != null && !fieldAlias.equals("")
            && type != null && relations != null && structureAlias != null && !structureAlias.equals("")) {
      ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
      //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
      Structure struct = mss.getStructure(structureAlias);
      if (struct != null && struct.isSystem() != true) {
        if (checkFieldAlias(struct, fieldAlias) == true) {
          // подобрать для поля имя
          boolean rel = false;
          if (!relations.equals("0")) {
            rel = true;
          }
          String fieldName = getName(struct, type, rel);
          if (!fieldName.equals("")) {
            boolean mandatoryBool = (mandatory != null ? true : false);
            boolean updatableBool = (updatable != null ? true : false);
            List<ValidatorAbstract> validators = getValidators(type);
            if (relations.equals("0")) {
              relations = null;
            }
            if (def == null || def.equals("")) {
              def = null;
            }
            Field newField = FieldFabric.getField(fieldName, fieldAlias, appName, mandatoryBool, updatableBool, type, relations, def, validators, true);

            Map<String, Field> fields = struct.getCloneFields();
            fields.put(newField.getAlias(), newField);
            Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

            mss.updateStructure(structureAlias, newStruct);

            status = (mss.getErrors().isEmpty() ? true : false);

            if (status == false) {
              errors.addAll(mss.getErrors());
            }
          } else {
            errors.add("ошибка при добавлении поля");
          }
        } else {
          errors.add("В структуре уже существует поле с таким алиасом");
        }
      } else {
        errors.add("Не существует сруктуры с таким именем");
      }
    } else {
      errors.add("Не все параметры переданы добавление поля");
    }
    refreshStructure();
    return status;
  }

  /**
   * изменить поле
   *
   * @param appName
   * @param mandatory
   * @param updatable
   * @param relations
   * @param structureAlias
   * @param fieldAlias
   * @param def
   * @return
   * @throws Exception
   */
  public boolean changeField(String appName, String mandatory, String updatable,
          String relations, String structureAlias, String fieldAlias, String def) throws Exception {
    boolean status = false;
    // renders.config.models.OneStructure.out2.print(1);
    if (relations != null && structureAlias != null
            && !structureAlias.equals("") && fieldAlias != null && !fieldAlias.equals("")) {
      //renders.config.models.OneStructure.out2.print(2);
      ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
      //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
      Structure struct = mss.getStructure(structureAlias);
      if (struct != null && struct.isSystem() != true) {
        //renders.config.models.OneStructure.out2.print(3);
        Field field = struct.getField(fieldAlias);
        if (field != null) {
          // renders.config.models.OneStructure.out2.print(4);
          boolean mandatoryBool = (mandatory != null ? true : false);
          boolean updatableBool = (updatable != null ? true : false);
          if (relations.equals("0")) {
            relations = null;
          }
          if (def == null || def.equals("")) {
            def = null;
          }

          Field newField = FieldFabric.getField(field.getName(), fieldAlias, appName, mandatoryBool, updatableBool, field.getType(), relations, def, field.getCloneValidatorList(), field.isEditable());

          Map<String, Field> fields = struct.getCloneFields();
          fields.put(newField.getAlias(), newField);
          Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

          mss.updateStructure(structureAlias, newStruct);

          status = (mss.getErrors().isEmpty() ? true : false);

          if (status == false) {
            errors.addAll(mss.getErrors());
          }
        }
      }
    } else {
      errors.add("Не все параметры переданы изменение поля");
    }
    refreshStructure();
    return status;
  }

  /**
   * удалить поле
   *
   * @param structureAlias
   * @param fieldAlias
   * @return
   * @throws Exception
   */
  public boolean deleteField(String structureAlias, String fieldAlias) throws Exception {
    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
    if (structureAlias != null) {
      if (fieldAlias != null) {
        Structure struct = mss.getStructure(structureAlias);
        if (struct != null && struct.isSystem() != true) {

          Map<String, Field> fields = struct.getCloneFields();
          fields.remove(fieldAlias);
          Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

          mss.updateStructure(structureAlias, newStruct);

          status = (mss.getErrors().isEmpty() ? true : false);
        }
        if (status == false) {
          errors.addAll(mss.getErrors());
        }
      }
    }
    refreshStructure();
    return status;
  }

  /**
   * удалить поле вместе с данными
   *
   * @param structureAlias
   * @param fieldAlias
   * @return
   * @throws Exception
   */
  public boolean deleteFieldWithData(String structureAlias, String fieldAlias) throws Exception {
    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
     Select sel=new TableSelectFactory(app).getSelect();
    if (structureAlias != null) {
      if (fieldAlias != null) {
        Structure struct = mss.getStructure(structureAlias);
        Field field = struct.getField(fieldAlias);
        String fieldName = field.getName();
        if (struct != null && struct.isSystem() != true) {

          Map<String, Field> fields = struct.getCloneFields();
          fields.remove(fieldAlias);
          Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

          mss.updateStructure(structureAlias, newStruct);

          status = (mss.getErrors().isEmpty() ? true : false);

          if (status == false) {
            errors.addAll(mss.getErrors());
          } else {
            // удалить данные
            String queryText = "update all_user_data set " + sel.validateParameter(fieldName, true) + " = null where user_data_type_id = "
                    + struct.getField("user_data_type_id").getDef().toString();
            QueryExecutor exec = ExecutorFabric.getExecutor(app.getConnection(), queryText);
            status = exec.update();
            if (!status) {
              errors.addAll(exec.getError());
            }
          }
        }

      }
    }
    refreshStructure();
    return status;
  }

  /**
   * добавить новую структуру
   *
   * @param alias
   * @param primaryName
   * @param fileWork
   * @return
   * @throws Exception
   */
  public boolean addStructure(String alias, String primaryName, String fileWork) throws Exception {
    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
    errors.clear();
    if (MyString.NotNull(alias)) {
      if (MyString.NotNull(primaryName)) {
        if (!mss.hasStructure(alias)) {
          boolean fileWorkBool = (fileWork != null ? true : false);
          Map<String, Field> fields = new HashMap();
          List<Unique> uniques = new ArrayList();
          Structure structure = StructureFabric.getStructure("all_user_data", alias, alias, primaryName, false, fileWorkBool, fields, uniques);
          mss.addStructure(alias, structure);
          status = (mss.getErrors().isEmpty() ? true : false);
          if (status == false) {
            errors.addAll(mss.getErrors());
          }
        } else {
          errors.add("Модель с таким алиасом уже существует");
        }
      } else {
        errors.add("Не передан PrymaryName");
      }
    } else {
      errors.add("Не передан алиас структуры");
    }
    refreshStructure();
    return status;
  }

  /**
   * изменить структуру
   *
   * @param structureAlias
   * @param fileWork
   * @return
   * @throws Exception
   */
  public boolean changeStructure(String structureAlias, String fileWork) throws Exception {
    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
    errors.clear();
    if (MyString.NotNull(structureAlias)) {
      if (mss.hasStructure(structureAlias)) {

        Structure struct = mss.getStructure(structureAlias);
        boolean fileWorkBool = (fileWork != null ? true : false);

        Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), fileWorkBool, struct.getCloneFields(), struct.getUniqueList());

        mss.updateStructure(structureAlias, newStruct);
        status = (mss.getErrors().isEmpty() ? true : false);
        if (status == false) {
          errors.addAll(mss.getErrors());
        }
      } else {
        errors.add("Модель с таким алиасом не существует");
      }
    } else {
      errors.add("Не передан алиас структуры");
    }
    refreshStructure();
    return status;
  }

  /**
   * удалить структуру
   *
   * @param structureAlias
   * @return
   * @throws Exception
   */
  public boolean deleteStructure(String structureAlias) throws Exception {
    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
    if (structureAlias != null) {
      if (mss.hasStructure(structureAlias)) {
        mss.removeStructure(structureAlias);
        status = (mss.getErrors().isEmpty() ? true : false);
        // удалить рендеры
        removeRenders(structureAlias);
        // удалить контроллеры
        removeControllers(structureAlias);
        // удалить пары
        if (status == false) {
          errors.addAll(mss.getErrors());
        }
      }
    }
    refreshStructure();
    return status;
  }

  /**
   * устанавливает параметры валидатора и сохраняет структуру
   *
   * @param struct
   * @param validator
   * @param params
   * @return
   * @throws Exception
   */
  public boolean changeValidator(Structure struct, ValidatorAbstract validator, Map<String, Object> params) throws Exception {

    /*
     ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
     boolean status = false;
     Map<String, Object> validatorParams = validator.getParameters();
     for (String name : validatorParams.keySet()) {
     Object value = (params.get(name) != null ? params.get(name) : "");
     validator.setParameter(name, value);
     }
     status = mss.updateStructure(struct);
     if (status == false) {
     errors.addAll(mss.getErrors());
     }
     return status;
     */
    return false;

  }

  /**
   * устанавливает параметры валидатора и сохраняет структуру
   *
   * @param struct
   * @param validator
   * @param params
   * @return
   * @throws Exception
   */
  public boolean changeValidator(String structureAlias, String fieldAlias, String validatorId, Map<String, Object> params) throws Exception {

    /*
     ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
     boolean status = false;
     Map<String, Object> validatorParams = validator.getParameters();
     for (String name : validatorParams.keySet()) {
     Object value = (params.get(name) != null ? params.get(name) : "");
     validator.setParameter(name, value);
     }
     status = mss.updateStructure(struct);
     if (status == false) {
     errors.addAll(mss.getErrors());
     }
     return status;
     */

    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
    Structure struct = mss.getStructure(structureAlias);
    if (struct != null) {
      Map<String, Field> fields = struct.getCloneFields();
      Field field = fields.get(fieldAlias);
      if (field != null & validatorId != null) {
        int idx = Integer.parseInt(validatorId);

        List<ValidatorAbstract> validators = field.getCloneValidatorList();
        ValidatorAbstract validator = validators.get(idx);
        Map<String, Object> validatorParams = validator.getParameters();
        for (String name : validatorParams.keySet()) {
          Object value = (params.get(name) != null ? params.get(name) : "");
          validator.setParameter(name, value);
        }

        Field newField = FieldFabric.getField(field.getName(), fieldAlias, field.getAppName(),
                field.isMandatory(), field.isUpdatable(), field.getType(), field.getRelations(),
                field.getDef(), validators, field.isEditable());
        fields.put(newField.getAlias(), newField);

        Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

        mss.updateStructure(newStruct.getName(), newStruct);
        status = (mss.getErrors().isEmpty() ? true : false);
        if (status == false) {
          errors.addAll(mss.getErrors());
        }
      }
    }
    refreshStructure();
    return status;

  }

  /**
   * добавить валидатор к структуре
   *
   * @param struct
   * @param fieldAlias
   * @param validatorName
   * @return номер вновь добавленного валидатора
   * @throws Exception
   */
  public int addValidator(Structure struct, String fieldAlias, String validatorName) throws Exception {
    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    int idx = -1;
    boolean status = false;
    if (struct.hasField(fieldAlias)) {

      // получить массив полей
      Map<String, Field> fields = struct.getCloneFields();

      // получить нужное поле
      Field field = fields.get(fieldAlias);
      // получить валидаторы
      List<ValidatorAbstract> validators = field.getCloneValidatorList();
      // добавить новый валидатор
      ValidatorAbstract validator = ValidatorAbstract.getValidator(validatorName);
      validators.add(validator);
      idx = validators.indexOf(validator);

      // создать новое поле, добавить его к другим полям
      Field newField = FieldFabric.getField(field.getName(), fieldAlias, field.getAppName(),
              field.isMandatory(), field.isUpdatable(), field.getType(), field.getRelations(),
              field.getDef(), validators, field.isEditable());
      fields.put(newField.getAlias(), newField);

      // создать новую структуру
      Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

      // записать структуру в БД
      mss.updateStructure(newStruct.getName(), newStruct);
      status = (mss.getErrors().isEmpty() ? true : false);

      if (status == false) {
        errors.addAll(mss.getErrors());
        idx = -1;
      }
    }
    refreshStructure();
    return idx;
  }

  /**
   * удалить валидатор
   *
   * @param structureAlias
   * @param fieldAlias
   * @param validatorId
   * @return
   * @throws Exception
   */
  public boolean deleteValidator(String structureAlias, String fieldAlias, String validatorId) throws Exception {
    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
    Structure struct = mss.getStructure(structureAlias);
    if (struct != null) {
      Map<String, Field> fields = struct.getCloneFields();
      Field field = fields.get(fieldAlias);
      if (field != null & validatorId != null) {
        int idx = Integer.parseInt(validatorId);

        List<ValidatorAbstract> validators = field.getCloneValidatorList();
        validators.remove(idx);

        Field newField = FieldFabric.getField(field.getName(), fieldAlias, field.getAppName(),
                field.isMandatory(), field.isUpdatable(), field.getType(), field.getRelations(),
                field.getDef(), validators, field.isEditable());
        fields.put(newField.getAlias(), newField);

        Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

        mss.updateStructure(newStruct.getName(), newStruct);
        status = (mss.getErrors().isEmpty() ? true : false);
        if (status == false) {
          errors.addAll(mss.getErrors());
        }
      }
    }
    refreshStructure();
    return status;
  }

  /**
   * поднять валидатор
   *
   * @param structureAlias
   * @param fieldAlias
   * @param validatorId
   * @throws Exception
   */
  public void upValidator(String structureAlias, String fieldAlias, String validatorId) throws Exception {
    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    Structure struct = mss.getStructure(structureAlias);
    if (struct != null) {
      Map<String, Field> fields = struct.getCloneFields();
      Field field = fields.get(fieldAlias);
      if (field != null & validatorId != null) {

        int idx = Integer.parseInt(validatorId);

        List<ValidatorAbstract> validators = field.getCloneValidatorList();

        if (idx > 0) {
          ValidatorAbstract v = validators.get(idx - 1);
          validators.set(idx - 1, validators.get(idx));
          validators.set(idx, v);
        }

        Field newField = FieldFabric.getField(field.getName(), fieldAlias, field.getAppName(),
                field.isMandatory(), field.isUpdatable(), field.getType(), field.getRelations(),
                field.getDef(), validators, field.isEditable());
        fields.put(newField.getAlias(), newField);

        Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

        mss.updateStructure(newStruct.getName(), newStruct);

      }
    }
    refreshStructure();
  }

  /**
   * опустить валидатор
   *
   * @param structureAlias
   * @param fieldAlias
   * @param validatorId
   * @throws Exception
   */
  public void downValidator(String structureAlias, String fieldAlias, String validatorId) throws Exception {

    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    Structure struct = mss.getStructure(structureAlias);
    if (struct != null) {
      Map<String, Field> fields = struct.getCloneFields();
      Field field = fields.get(fieldAlias);
      if (field != null & validatorId != null) {

        int idx = Integer.parseInt(validatorId);

        List<ValidatorAbstract> validators = field.getCloneValidatorList();

        if (idx < validators.size() - 1) {
          ValidatorAbstract v = validators.get(idx + 1);
          validators.set(idx + 1, validators.get(idx));
          validators.set(idx, v);
        }

        Field newField = FieldFabric.getField(field.getName(), fieldAlias, field.getAppName(),
                field.isMandatory(), field.isUpdatable(), field.getType(), field.getRelations(),
                field.getDef(), validators, field.isEditable());
        fields.put(newField.getAlias(), newField);

        Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), fields, struct.getUniqueList());

        mss.updateStructure(newStruct.getName(), newStruct);

      }
    }
    refreshStructure();
  }

  /**
   * добавить Unique
   *   
* @param structureAlias
   * @param names
   * @param checkDeleted
   * @throws Exception
   */
  public void addUnique(String structureAlias, String names, String checkDeleted) throws Exception {

    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
    Structure struct = mss.getStructure(structureAlias);

    List<Unique> uniqueList = struct.getUniqueList();
    String[] newNames = names.split("[\\s\\.,;]+s");
    boolean checkDeletedBool = (checkDeleted != null ? true : false);
    Unique unique = UniqueObject.valueOf(Arrays.asList(newNames), checkDeletedBool);
    uniqueList.add(unique);
    
    // создать новую структуру
    Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), struct.getCloneFields(), uniqueList);
    // записать структуру в БД
    mss.updateStructure(newStruct.getName(), newStruct);
    status = (mss.getErrors().isEmpty() ? true : false);
    if (status == false) {
      errors.addAll(mss.getErrors());
    }
    refreshStructure();
  }

  /**
   * изменить unique
   *   
* @param structureAlias
   * @param names
   * @param checkDeleted
   * @throws Exception
   */
  public void changeUnique(String structureAlias, String names, String checkDeleted, String index) throws Exception {

    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    int indexInt = -1;
    try {
      indexInt = Integer.parseInt(index);
    } catch (Exception e) {
      indexInt = -1;
    }
    boolean status = false;
    Structure struct = mss.getStructure(structureAlias);

    List<Unique> uniqueList = struct.getUniqueList();
    String[] newNames = names.split("[\\s\\.,;]+s");

    boolean checkDeletedBool = (checkDeleted != null ? true : false);
    Unique unique = UniqueObject.valueOf(Arrays.asList(newNames), checkDeletedBool);
    uniqueList.remove(indexInt);
    uniqueList.add(unique);
    // создать новую структуру
    Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), struct.getCloneFields(), uniqueList);
    // записать структуру в БД
    mss.updateStructure(newStruct.getName(), newStruct);
    status = (mss.getErrors().isEmpty() ? true : false);
    if (status == false) {
      errors.addAll(mss.getErrors());
    }
    refreshStructure();
  }

  /**
   * удалить unique
   *   
* @param structureAlias
   * @param index
   * @throws Exception
   */
  public void deleteUnique(String structureAlias, String index) throws Exception {

    ModelStructureKeeper mss = ModelStructureKeeper.getInstance(app);
    //ModelStructureKeeper mss = app.getKeeper().getModelStructureKeeper();
    boolean status = false;
    Structure struct = mss.getStructure(structureAlias);

    int indexInt = -1;
    try {
      indexInt = Integer.parseInt(index);
    } catch (Exception e) {
      indexInt = -1;
    }

    List<Unique> uniqueList = struct.getUniqueList();
    uniqueList.remove(indexInt);

    // создать новую структуру
    Structure newStruct = StructureFabric.getStructure(struct.getTableName(), struct.getName(), struct.getTableAlias(), struct.getPrimaryAlias(), struct.isSystem(), struct.isFileWork(), struct.getCloneFields(), uniqueList);
    // записать структуру в БД
    mss.updateStructure(newStruct.getName(), newStruct);
    status = (mss.getErrors().isEmpty() ? true : false);
    if (status == false) {
      errors.addAll(mss.getErrors());
    }
    refreshStructure();
  }

  private void removeRenders(String structureAlias) throws Exception {
    //;
  }

  private void removeControllers(String structureAlias) throws Exception {
    //;
  }

  // получить имя поля в таблице, для нового поля
  private String getName(Structure struct, String type, boolean rel) throws CloneNotSupportedException {
    String fieldName = "";
    String intArray[] = {"int01", "int02", "int03", "int04", "int05", "int06", "int07", "int08", "int09", "int10"};
    String varcharArray[] = {"varchar1", "varchar2", "varchar3", "varchar4", "varchar5", "varchar6", "varchar7", "varchar8", "varchar9", "varchar10",
      "varchar11", "varchar12", "varchar13", "varchar14", "varchar15", "varchar16", "varchar17"};
    String datetimeArray[] = {"datetime1", "datetime2", "datetime3", "datetime4", "datetime5", "datetime6", "datetime7", "datetime8", "datetime9", "datetime10"};
    String decimalArray[] = {"decimal1", "decimal2", "decimal3", "decimal4", "decimal5", "decimal6", "decimal7", "decimal8", "decimal9", "decimal10"};
    String textArray[] = {"text1", "text2", "text3", "text4", "text5", "text6", "text7", "text8", "text9", "text10"};
    String refArray[] = {"ref1", "ref2", "ref3", "ref4", "ref5", "ref6"};
    List<String> intNames = Arrays.asList(intArray);
    List<String> varcharNames = Arrays.asList(varcharArray);
    List<String> datetimeNames = Arrays.asList(datetimeArray);
    List<String> decimalNames = Arrays.asList(decimalArray);
    List<String> textNames = Arrays.asList(textArray);
    List<String> refNames = Arrays.asList(refArray);
    // определить один общий массив для всех типов
    Map<String, List<String>> map = new HashMap<String, List<String>>();
    map.put("int", intNames);
    map.put("bool", intNames);
    map.put("char", varcharNames);
    map.put("datetime", datetimeNames);
    map.put("decimal", decimalNames);
    map.put("text", textNames);
    map.put("ref", refNames);
    String newType = type;
    if (type.equals("int") && rel == true) {
      newType = "ref";
    }
    // получить массив названий полей, соответствующий типу
    // если такой массив существует
    if (map.containsKey(newType)) {
      List<String> list = map.get(newType);
      for (String name : list) {
        if (checkName(struct, name)) {
          fieldName = name;
          break;
        }
      }
    }
    return fieldName;
  }

  private boolean checkName(Structure struct, String fieldName) throws CloneNotSupportedException {
    boolean status = true;
    Map<String, Field> map = struct.getCloneFields();
    for (Field field : map.values()) {
      // если имя совпадает с именем поля
      if (fieldName.equals(field.getName())) {
        // прервать цикл, вернуть false
        status = false;
        break;
      }
    }
    return status;
  }

  private boolean checkFieldAlias(Structure struct, String fieldAlias) {
    boolean status = true;
    // проверить, есть ли в структуре поле с таким алиасом
    if (struct.hasField(fieldAlias)) {
      status = false;
    }

    // проверить, есть ли такой алиас среди зарезервированных полей
    if (systemFieldNames.contains(fieldAlias)) {
      status = false;
    }

    return status;
  }

  /**
   * возвращает массив валидаторов, в зависимости от типа поля
   *
   * @param type
   * @return
   */
  private List<ValidatorAbstract> getValidators(String type) {
    List<ValidatorAbstract> validators = new ArrayList();
    if (type.equals("char")) {
      StringLenghtValidator sv = new StringLenghtValidator();
      sv.setMin(1);
      sv.setMax(255);
      validators.add(sv);
    } else if (type.equals("int")) {
      DigitsFilter df = new DigitsFilter();
      DigitsValidator dv = new DigitsValidator();
      StringLenghtValidator sv = new StringLenghtValidator();
      sv.setMin(1);
      sv.setMax(11);
      QuantityValidator quantity = new QuantityValidator();
      quantity.setMin(0);
      quantity.setMax(20000000000L);
      validators.add(df);
      validators.add(dv);
      validators.add(sv);
      validators.add(quantity);
    } else if (type.equals("decimal")) {
      DecimalFilter df = new DecimalFilter();
      DecimalValidator dv = new DecimalValidator();
      QuantityValidator quantity = new QuantityValidator();
      quantity.setMin(0);
      quantity.setMax(1000000000);
      validators.add(df);
      validators.add(dv);
      validators.add(quantity);
    } else if (type.equals("datetime")) {
      DateToFormatFilter filter = new DateToFormatFilter();
      filter.setFormat("yyyy-MM-dd HH:mm:ss");
      DateFormatValidator val = new DateFormatValidator();
      val.setFormat("yyyy-MM-dd HH:mm:ss");
      validators.add(filter);
      validators.add(val);
    } else if (type.equals("bool")) {
      DigitsFilter df = new DigitsFilter();
      DigitsValidator dv = new DigitsValidator();
      StringLenghtValidator sv = new StringLenghtValidator();
      sv.setMin(1);
      sv.setMax(1);
      validators.add(df);
      validators.add(dv);
      validators.add(sv);
    } else if (type.equals("text")) {
      StringLenghtValidator sv = new StringLenghtValidator();
      sv.setMin(1);
      sv.setMax(64000);
      validators.add(sv);
    }
    return validators;
  }

  private void refreshStructure() throws Exception {
    WarehouseSingleton.getInstance().getNewKeeper(app);
  }
}