package com.prim.core.modelStructure;

import java.io.Serializable;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.prim.support.filterValidator.entity.ValidatorAbstract;
import com.prim.support.MyString;
import com.prim.support.primXml;

/**
 * класс представляет собой структуру модели
 *
 * @author Pavel Rice
 */
final class StructureObject implements Serializable, Structure, Cloneable {

  /**
   * имя таблицы
   */
  private final String tableName;
  /**
   * алиас таблицы
   */
  private final String tableAlias;
  /**
   * имя поля, которое является первичным ключом
   */
  private final String primaryAlias;
  /**
   * является ли класс системной моделью
   */
  private final boolean system;
  /**
   * разрешена ли работа с файлами
   */
  private final boolean fileWork;
  private final String name;
  /**
   * массив, в котором содержатся объекты полей
   */
  private final Map<String, Field> fields;
  static final long serialVersionUID = 12345L;
  private final List<Unique> uniqueList;

  /*
   Structure(String tableName, String alias, String primary, boolean system, boolean fileWork, List<Unique> uniqueList) {
   this.tableName = tableName;
   this.tableAlias = alias;
   this.primaryAlias = primary;
   this.system = system;
   this.fileWork = fileWork;
   fields = new HashMap<String, FieldObject>();
   } */
  private StructureObject(String tableName, String name, String alias, String primary, boolean system, boolean fileWork, Map<String, Field> fields, List<Unique> uniqueList) throws CloneNotSupportedException {
    this.tableName = tableName;
    this.tableAlias = alias;
    this.primaryAlias = primary;
    this.system = system;
    this.fileWork = fileWork;
    this.name = name;

    if (fields == null) {
      this.fields = new HashMap();
    } else {
      Map<String, Field> map = new HashMap();
      for (String fieldName : fields.keySet()) {
        if (MyString.NotNull(fieldName, fields.get(fieldName))) {
          map.put(fieldName, fields.get(fieldName).clone());
        }
      }
      this.fields = map;
    }

    if (uniqueList == null) {
      this.uniqueList = new ArrayList();
    } else {
      List<Unique> li = new ArrayList();
      for (Unique uL : uniqueList) {
        if (uL != null) {
          li.add(uL.clone());
        }
      }
      this.uniqueList = li;
    }
  }

  static StructureObject valueOf(String tableName, String name, String alias, String primary, boolean system, boolean fileWork, Map<String, Field> fields, List<Unique> uniqueList) throws CloneNotSupportedException {
    return new StructureObject(tableName, name, alias, primary, system, fileWork, fields, uniqueList);
  }

  public List<Unique> getUniqueList() throws CloneNotSupportedException {
    List<Unique> li = new ArrayList();
    for (Unique un : uniqueList) {
      li.add(un.clone());
    }
    return li;
  }

  /**
   *
   * @return имя таблицы
   */
  public String getTableName() {
    return tableName;
  }

  /**
   *
   * @return алиас таблицы
   */
  public String getTableAlias() {
    return tableAlias;
  }

  /**
   *
   * @return алиас первичного поля первичного ключа
   */
  public String getPrimaryAlias() {
    return primaryAlias;
  }

  /**
   *
   * @return настоящее имя поля первичного ключа
   */
  public String getPrimaryRealName() {
    if (fields.get(getPrimaryAlias()) != null) {
      return fields.get(getPrimaryAlias()).getName();
    } else {
      return null;
    }
  }

  /**
   *
   * @return является ли модель системной
   */
  public Boolean isSystem() {
    return system;
  }

  /**
   *
   * @return разрешена ли работа с файлами для модели
   */
  public Boolean isFileWork() {
    return fileWork;
  }

  /**
   *
   * @return клон массива полей
   */
  @Override
  public Map<String, Field> getCloneFields() throws CloneNotSupportedException {
    Map<String, Field> newFields = new LinkedHashMap();
    for (String name : fields.keySet()) {
      newFields.put(name, fields.get(name).clone());
    }
    return newFields;
  }

  public Map<String, Field> getFields() {
    return fields;
  }

  /**
   *
   * @return массив названия полей
   */
  @Override
  public Set<String> getFieldsNames() {
    return fields.keySet();
  }

  /**
   * возвращает объект поля
   *
   * @param alias - алиас поля
   * @return
   */
  @Override
  public Field getField(String alias) {
    if (fields.get(alias) != null) {
      return fields.get(alias);
    } else {
      return null;
    }
  }

  /**
   * существует ли поле под таким алиасом
   *
   * @param alias
   * @return
   */
  public boolean hasField(String alias) {
    return fields.containsKey(alias);
  }

  public String getName() {
    return name;
  }

  /**
   *
   * @return описание структуры в виде строки
   */
  public String getInfo() {
    String str = "название таблицы: " + tableName
            + ", алиас таблицы: " + tableAlias
            + ", первичный ключ: " + primaryAlias
            + ", системный класс: " + system
            + ", работа с файлами: " + fileWork
            + ", поля:";
    for (String name : fields.keySet()) {
      str += name + ", ";
    }
    return str;
  }

  @Override
  public Structure clone() throws CloneNotSupportedException {
    return new StructureObject(tableName, name, tableAlias, primaryAlias, system, fileWork, fields, uniqueList);
  }

  @Override
  public Field getFieldClone(String alias) throws CloneNotSupportedException {
    if (fields.get(alias) != null) {
      return fields.get(alias).clone();
    } else {
      return null;
    }
  }

  @Override
  public void getSelfInXml(Document doc, Element root) throws Exception {
    primXml.createElement(doc, root, "tableName", tableName);
    primXml.createElement(doc, root, "tableAlias", tableAlias);
    primXml.createElement(doc, root, "primaryAlias", primaryAlias);
    primXml.createElement(doc, root, "name", name);
    primXml.createElement(doc, root, "system", system);
    primXml.createElement(doc, root, "fileWork", fileWork);

    Element fiel = primXml.createEmptyElement(doc, root, "fields");
    for (Field fd : fields.values()) {
      Element fdd =primXml.createEmptyElement(doc, fiel, "field");
      fd.getSelfInXml(doc, fdd);
    }
    Element ul =primXml.createEmptyElement(doc, root, "uniqueList"); 
    for (Unique un : uniqueList) {
      Element unn = primXml.createEmptyElement(doc, ul, "unique");
      un.getSelfInXml(doc, unn);
    }
  }
}
