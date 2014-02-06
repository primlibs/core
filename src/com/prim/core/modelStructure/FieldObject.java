package com.prim.core.modelStructure;

import com.prim.support.filterValidator.entity.ValidatorAbstract;
import java.io.Serializable;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.prim.support.MyString;
import com.prim.support.primXml;

/**
 * класс представляет собой структуру поля в модели
 *
 * @author Pavel Rice
 */
final class FieldObject implements Cloneable, Serializable, Field {

  /**
   * имя поля в хранилище данных
   */
  final private String name;
  /**
   * алиас поля
   */
  final private String alias;
  /**
   * имя поля в приложении
   */
  final private String appName;
  /**
   * является ли поле обязательным
   */
  final private Boolean mandatory;
  /**
   * разрешено ли обновление поля
   */
  final private Boolean updatable;
  /**
   * имя связи
   */
  final private String relations;
  /**
   * тип поля
   */
  final private String type;
  /**
   * значение по умолчанию
   */
  final private String def;
  /**
   * значение
   */
  private Object value;
  // редактируемое ли поле
  // то есть можно ли редактировать параметры поля через интерфейс админа
  final private Boolean editable;
  final private List<ValidatorAbstract> validatorList;
  static final long serialVersionUID = 12345L;

  private FieldObject(String name, String alias, String appName, Boolean mandatory, Boolean updatable, String type, String relations, String def, List<ValidatorAbstract> validators, Boolean editable) throws CloneNotSupportedException {
    this.name = name;
    this.alias = alias;
    this.appName = appName;
    this.mandatory = mandatory;
    this.updatable = updatable;
    if (validators == null) {
      validatorList = new ArrayList();
    } else {
      List<ValidatorAbstract> li = new ArrayList();
      for (ValidatorAbstract va : validators) {
        if (va != null) {
          li.add(va.clone());
        }
      }
      validatorList = li;
    }
    this.relations = (relations != null ? relations.toString() : null);
    this.type = type;
    this.def = def;
    this.editable = editable;
  }

  static FieldObject valueOf(String name, String alias, String appName, Boolean mandatory, Boolean updatable, String type, String relations, String def, List<ValidatorAbstract> validators, Boolean editable) throws CloneNotSupportedException {
    return new FieldObject(name, alias, appName, mandatory, updatable, type, relations, def, validators, editable);
  }

  @Override
  public Boolean isEditable() {
    return editable;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public Boolean isMandatory() {
    return mandatory;
  }

  @Override
  public String getAppName() {
    return appName;
  }

  @Override
  public Boolean isUpdatable() {
    return updatable;
  }

  @Override
  public String getRelations() {
    return relations;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public List<ValidatorAbstract> getCloneValidatorList() throws CloneNotSupportedException {
    List<ValidatorAbstract> li = new ArrayList();
    for (ValidatorAbstract va : validatorList) {
      li.add(va.clone());
    }
    return li;
  }

  public List<ValidatorAbstract> getValidatorList() {
    return validatorList;
  }

  @Override
  public String getDef() {
    return def;
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public void setValue(Object value) {
    if (def == null||updatable==true) {
      this.value = value;
    }else{
      this.value = def; 
    }
  }

  @Override
  public FieldObject clone() throws CloneNotSupportedException {
    FieldObject fd = new FieldObject(name, alias, appName, mandatory, updatable, type, relations, def, validatorList, editable);
    fd.setValue(value);
    return fd;
  }

  //@Override
  public void getSelfInXml(Document doc, Element field) throws Exception {
    primXml.createElement(doc, field, "name", name);
    primXml.createElement(doc, field, "alias", alias);
    primXml.createElement(doc, field, "appName", appName);   
    if (def != null) {
      primXml.createElement(doc, field, "def", def);
    }
    primXml.createElement(doc, field, "relations", (MyString.NotNull(relations) ? relations : null));  
    primXml.createElement(doc, field, "mandatory", mandatory);
    primXml.createElement(doc, field, "updatable", updatable);
    primXml.createElement(doc, field, "editable", editable);
    primXml.createElement(doc, field, "type", type);
    primXml.createElement(doc, field, "value", value);
    Element el=primXml.createEmptyElement(doc, field, "validatorList");
    for (ValidatorAbstract va : validatorList) {
      Element vl=primXml.createEmptyElement(doc, el, "validator");
      primXml.createElement(doc, vl, "name", va.getClass());
      Element params=primXml.createEmptyElement(doc, vl, "parameters");
      for (String name : va.getParameters().keySet()) {
        Element pr=primXml.createEmptyElement(doc, params, "parameter");
        primXml.createElement(doc, pr, "name", name);
        primXml.createElement(doc, pr, "value", va.getParameters().get(name));
      }
    }
  }
}
