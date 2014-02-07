package com.prim.core.warehouse.modelKeeper;

import com.prim.core.AbstractApplication;
import com.prim.core.db.ExecutorFabric;
import com.prim.core.db.QueryExecutor;
import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.FieldFabric;
import com.prim.core.modelStructure.Structure;
import com.prim.core.modelStructure.StructureFabric;
import com.prim.core.select.Select;
import com.prim.core.select.TableSelectFactory;
import com.prim.support.filterValidator.entity.ValidatorAbstract;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.StringReader;
import java.util.*;
import java.sql.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//import prim.warehouse.OptionsSingleton;
/**
 * класс,хранящий в себе структуру моделей
 *
 * @author Pavel Rice
 */
public class ModelStructureKeeper {

  /**
   * массив-карта, в котором хранятся объекты структуры. Ключи массива - это
   * алиасы таблиц.
   */
  final private Map<String, Structure> structureMap;
  private ArrayList<String> errors = new ArrayList<String>();
  final private AbstractApplication app;

  private ModelStructureKeeper(AbstractApplication application) throws Exception {
    app = application;
    Statement st = null;
    try {
      if (app.getProperty("factoryFilePath") == null) {
        st = application.getConnection().createStatement();
        st.executeQuery("set names " + app.getDbEncoding() + ";");
        Map<String, Structure> structMap = new LinkedHashMap();
        structMap.putAll(setUserStructure());
        structMap.putAll(setStructureFromXml(app.getAppUserDataConfigPath() + "/systemModel.xml"));
        structureMap = structMap;
      } else {
        st = application.getConnection().createStatement();
        st.executeQuery("set names " + app.getDbEncoding() + ";");
        Map<String, Structure> structMap = new LinkedHashMap();
        structMap.putAll(setStructureFromXml(app.getProperty("factoryFilePath")));
        structMap.putAll(setStructureFromXml(app.getAppUserDataConfigPath() + "/systemModel.xml"));
        structureMap = structMap;
      }
    } finally {
      if (st != null) {
        st.close();
      }
    }
  }

  public static ModelStructureKeeper getInstance(AbstractApplication application) throws Exception {
    return new ModelStructureKeeper(application);
  }

  /**
   * возвращает клон объекта структуры по имени модели
   *
   * @param name - имя модели
   * @return
   */
  public Structure getStructure(String name) throws CloneNotSupportedException {
    if (structureMap.get(name) != null) {
      return structureMap.get(name).clone();
    }
    return null;
  }

  /**
   * возвращает массив клонов всех объектов структур
   *
   * @return
   */
  public Map<String, Structure> getStructureMap() throws CloneNotSupportedException {
    Map<String, Structure> result = new LinkedHashMap();
    for (String name : structureMap.keySet()) {
      result.put(name, structureMap.get(name).clone());
    }
    return structureMap;
  }

  /**
   * существует ли структура с таким именем
   *
   * @param name
   * @return
   */
  public boolean hasStructure(String name) {
    return structureMap.containsKey(name);
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
   * удалить структуру по имени
   *
   * @param name
   * @return
   */
  ModelStructureKeeper removeStructure(String name) throws Exception {
    boolean status = false;
    errors.clear();
    if (structureMap.containsKey(name)) {
      Structure struct = structureMap.get(name);
      Select sel = new TableSelectFactory(app).getSelect();
      String query = "update user_data_types set active_to = now() where name = " + sel.validateParameter(name, true);
      QueryExecutor exec = ExecutorFabric.getExecutor(app.getConnection(), query);
      status = exec.update();
      errors.addAll(exec.getError());
    } else {
      errors.add("Ошибка: структуры с таким именем не существует");
    }

    if (status == true) {
      return new ModelStructureKeeper(app);
    }
    return this;
  }

  /**
   * добавить новую структуру
   *
   * @param name
   * @param struct
   * @return
   * @throws Exception
   */
  public ModelStructureKeeper addStructure(String name, Structure struct) throws Exception {
    boolean status = false;
    errors.clear();
    PreparedStatement st2 = null;
    CharArrayWriter ch = null;
    try {
      if (!structureMap.containsKey(name)) {
        if (!struct.isSystem()) {
          st2 = app.getConnection().prepareStatement("insert into user_data_types (name, active_from, struct_text) values (?, now(), ?)");
          st2.setString(1, name);

          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document doc = db.newDocument();
          Element root = doc.createElement("root");
          doc.appendChild(root);
          struct.getSelfInXml(doc, root);

          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          Transformer transformer = transformerFactory.newTransformer();

          DOMSource source = new DOMSource(doc);
          ch = new CharArrayWriter();
          StreamResult result = new StreamResult(ch);
          transformer.transform(source, result);
          st2.setString(2, ch.toString());
          int n = st2.executeUpdate();
          if (n != 0) {
            status = true;
          }
          if (status == false) {
            errors.add("Ошибка при попытке записать структуру в базу данных");
          }
        } else {
          errors.add("Ошибка: нельзя добавить системную модель");
        }
      } else {
        errors.add("Ошибка: структура с таким именем уже существует");
      }
    } finally {
      if (st2 != null) {
        st2.close();
      }
      if (ch != null) {
        ch.close();
      }
    }
    if (status == true) {
      return new ModelStructureKeeper(app);
    }

    return this;
  }

  /**
   * обновить структуру
   *
   * @param struct
   * @return
   * @throws Exception
   */
  ModelStructureKeeper updateStructure(Structure struct) throws Exception {
    boolean status = false;
    if (structureMap.containsValue(struct)) {
      return updateStructure(struct.getName());
    }
    return this;
  }

  /**
   * обновить структуру по имени
   *
   * @param name
   * @return
   * @throws Exception
   */
  ModelStructureKeeper updateStructure(String name) throws Exception {
    boolean status = false;
    errors.clear();

    PreparedStatement st = null;
    CharArrayWriter ch = null;
    try {

      if (structureMap.containsKey(name)) {
        Structure struct = structureMap.get(name);
        if (!struct.isSystem()) {
          st = app.getConnection().prepareStatement("update user_data_types set struct_text = ? where name = ?");
          st.setString(2, name);
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document doc = db.newDocument();
          Element root = doc.createElement("root");
          doc.appendChild(root);
          struct.getSelfInXml(doc, root);

          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          Transformer transformer = transformerFactory.newTransformer();
          DOMSource source = new DOMSource(doc);
          ch = new CharArrayWriter();
          StreamResult result = new StreamResult(ch);
          transformer.transform(source, result);
          st.setString(1, ch.toString());
          int n = st.executeUpdate();
          if (n != 0) {
            status = true;
          }
          if (status == false) {
            errors.add("Ошибка при попытке записать структуру в базу данных");
          }
          st.close();
        } else {
          errors.add("Ошибка: нельзя изменить системную модель");
        }
      } else {
        errors.add("Ошибка: структура с таким именем не существует");
      }

    } finally {
      if (st != null) {
        st.close();
      }
      if (ch != null) {
        ch.close();
      }
    }

    if (status == true) {
      return new ModelStructureKeeper(app);
    }
    return this;
  }

  /**
   * обновить структуру
   *
   * @param name имя
   * @param struct объект структуры
   * @return
   * @throws Exception
   */
  public ModelStructureKeeper updateStructure(String name, Structure struct) throws Exception {
    boolean status = false;
    errors.clear();

    PreparedStatement st = null;
    CharArrayWriter ch = null;
    try {

      if (structureMap.containsKey(name)) {
        if (!struct.isSystem()) {
          st = app.getConnection().prepareStatement("update user_data_types set struct_text = ? where name = ?");
          st.setString(2, name);
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document doc = db.newDocument();
          Element root = doc.createElement("root");
          doc.appendChild(root);
          struct.getSelfInXml(doc, root);

          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          Transformer transformer = transformerFactory.newTransformer();
          DOMSource source = new DOMSource(doc);
          ch = new CharArrayWriter();
          StreamResult result = new StreamResult(ch);
          transformer.transform(source, result);
          st.setString(1, ch.toString());
          int n = st.executeUpdate();
          if (n != 0) {
            status = true;
          }
          if (status == false) {
            errors.add("Ошибка при попытке записать структуру в базу данных");
          }
          st.close();
        } else {
          errors.add("Ошибка: нельзя изменить системную модель");
        }
      } else {
        errors.add("Ошибка: структура с таким именем не существует");
      }
      if (status == true) {
        return new ModelStructureKeeper(app);
      }

    } finally {
      if (st != null) {
        st.close();
      }
      if (ch != null) {
        ch.close();
      }
    }

    return this;
  }

  /**
   * делает запрос в БД и создает объекты структуры для моделей пользовательских
   * данных
   */
  private Map<String, Structure> setUserStructure() throws Exception {
    Map<String, Structure> structMap = new LinkedHashMap();
    // запрос в базу данных - получение информации о структуре
    String queryText = " select t.user_data_type_id, t.name, t.active_from, t.active_to, struct_text "
            + " from user_data_types t "
            + " where active_to is null "
            + "order by t.name";

    Statement st = null;
    ResultSet rs = null;
    try {
      st = app.getConnection().createStatement();
      rs = st.executeQuery(queryText);
      while (rs.next()) {
        // извлечь название 
        String name = rs.getString("name");
        // извлечь объект
        String str = rs.getString("struct_text");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(str));

        Document doc = builder.parse(is);

        NodeList list = doc.getChildNodes();

        Element structureElement = (Element) list.item(0);
        Structure structure = StructureFabric.getStructureFromXml(structureElement);
        int id = rs.getInt("user_data_type_id");

        Map<String, Field> fields = structure.getCloneFields();
        fields.putAll(getStandartFields(structure, id));
        structure = StructureFabric.getStructure(structure.getTableName(), structure.getName(), structure.getTableAlias(),
                structure.getPrimaryAlias(), structure.isSystem(), structure.isFileWork(), fields, structure.getUniqueList());

        structMap.put(name, structure);
      }
    } finally {
      if (st != null) {
        st.close();
      }
      if (rs != null) {
        rs.close();
      }
    }
    return structMap;
  }

  private Map<String, Structure> setStructureFromXml(String path) throws Exception {
    Map<String, Structure> structMap = new LinkedHashMap();
    // запрос в файл данных - получение информации о структуре
    String config = path;
    // прочитать файл конфига
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    File file = new File(config);
    Document doc = builder.parse(file);
    NodeList list = doc.getChildNodes();
    Element root = (Element) list.item(0);
    // получить все дочерние узлы типа model
    NodeList models = root.getElementsByTagName("structure");
    for (int i = 0; i < models.getLength(); i++) {
      Element modelNode = (Element) models.item(i);
      Structure str = StructureFabric.getStructureFromXml(modelNode);
      structMap.put(str.getName(), str);

    }
    return structMap;
  }

  /**
   * возвращает список полей, общий для всех пользовательских моделей
   *
   * @param structure структура
   * @param id ИД структуры
   * @return
   * @throws Exception
   */
  private Map<String, Field> getStandartFields(Structure structure, Integer id) throws Exception {

    Map<String, Field> listField = new LinkedHashMap();
    // удалить лишнее

    ValidatorAbstract StringValidator = ValidatorAbstract.getValidator("StringLenghtValidator");
    StringValidator.setParameter("min", 1);
    StringValidator.setParameter("max", 11);

    ValidatorAbstract DigitsValidator = ValidatorAbstract.getValidator("DigitsValidator");
    ValidatorAbstract QuantityValidator = ValidatorAbstract.getValidator("QuantityValidator");
    QuantityValidator.setParameter("min", 1);
    QuantityValidator.setParameter("max", "99999999999");


    ValidatorAbstract DateFilter = ValidatorAbstract.getValidator("DateToFormatFilter");
    DateFilter.setParameter("format", "yyyy-MM-dd HH:mm:ss");

    ValidatorAbstract DateValidator = ValidatorAbstract.getValidator("DateFormatValidator");
    DateValidator.setParameter("format", "yyyy-MM-dd HH:mm:ss");

    String primaryName = structure.getPrimaryAlias();

    Field dataType = FieldFabric.getField(
            "user_data_type_id",
            "user_data_type_id",
            "Тип данных",
            false,
            false,
            "int",
            "",
            id.toString(),
            getValidtorList(StringValidator, DigitsValidator, QuantityValidator),
            false);
    listField.put("user_data_type_id", dataType);

    Field primaryField = FieldFabric.getField(
            "id",
            primaryName,
            "ИД записи",
            true,
            false,
            "int",
            "",
            null,
            getValidtorList(StringValidator, QuantityValidator),
            false);
    listField.put(primaryName, primaryField);

    Field insertDate = FieldFabric.getField(
            "insert_date",
            "insert_date",
            "Дата добавления",
            true,
            false,
            "datetime",
            "",
            null,
            getValidtorList(DateFilter, DateValidator),
            false);

    listField.put("insert_date", insertDate);

    Field insertUser = FieldFabric.getField(
            "insert_user_id",
            "insert_user_id",
            "Добавивший пользователь",
            true,
            false,
            "int",
            "users",
            null,
            getValidtorList(StringValidator, DigitsValidator, QuantityValidator),
            false);
    listField.put("insert_user_id", insertUser);

    Field updateDate = FieldFabric.getField(
            "update_date",
            "update_date",
            "Дата обновления",
            false,
            true,
            "datetime",
            "",
            null,
            getValidtorList(DateFilter, DateValidator),
            false);
    listField.put("update_date", updateDate);

    Field updateUser = FieldFabric.getField(
            "update_user_id",
            "update_user_id",
            "Обновивший пользователь",
            false,
            true,
            "int",
            "users",
            null,
            getValidtorList(StringValidator, DigitsValidator, QuantityValidator),
            false);
    listField.put("update_user_id", updateUser);

    Field deleteDate = FieldFabric.getField(
            "delete_date",
            "delete_date",
            "Дата удаления",
            false,
            true,
            "datetime",
            "",
            null,
            getValidtorList(DateFilter, DateValidator),
            false);
    listField.put("delete_date", deleteDate);

    Field deleteUser = FieldFabric.getField(
            "delete_user_id",
            "delete_user_id",
            "Удаливший пользователь",
            false,
            true,
            "int",
            "users",
            null,
            getValidtorList(StringValidator, DigitsValidator, QuantityValidator),
            false);
    listField.put("delete_user_id", deleteUser);

    ValidatorAbstract StringValidatorForChar = ValidatorAbstract.getValidator("StringLenghtValidator");
    StringValidatorForChar.setParameter("min", 1);
    StringValidatorForChar.setParameter("max", 255);

    Field oldId = FieldFabric.getField(
            "old_id",
            "old_id",
            "ID в другом хранилище данных",
            false,
            true,
            "char",
            "",
            null,
            getValidtorList(StringValidatorForChar),
            false);
    listField.put("old_id", oldId);

    return listField;
  }

  /**
   * устанавливает структуру сиситемных моделей
   */
  /*
   private Map<String, Structure> setSystemStructure() throws Exception {
   Map<String, Structure> structMap = new LinkedHashMap();
   SystemModelStructure sys = new SystemModelStructure(app);
   structMap.putAll(sys.getSystemConfig());
   return structMap;
   }
   */
  private List<ValidatorAbstract> getValidtorList(ValidatorAbstract... abs) {
    List<ValidatorAbstract> li = new ArrayList();
    for (ValidatorAbstract va : abs) {
      li.add(va);
    }
    return li;
  }
}
