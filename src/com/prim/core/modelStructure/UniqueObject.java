/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.modelStructure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.prim.support.MyString;
import com.prim.support.primXml;

/**
 * поля модели, значения которых должны быть уникальны
 *
 * @author Pavel Rice
 */
final public class UniqueObject implements Serializable, Cloneable, Unique {

  /**
   * навания полей, значения которых должны быть уникальны
   */
  final private List<String> fieldNames;
  /**
   * проверять ли удаленные записи
   */
  private Boolean checkDeleted;
  static final long serialVersionUID = 12345L;

  private UniqueObject(List<String> fieldNames, Boolean checkDeleted) {
    if (fieldNames == null) {
      this.fieldNames = new ArrayList();
    } else {
      List<String> li = new ArrayList();
      for (String st : fieldNames) {
        if (MyString.NotNull(st)) {
          li.add(st);
        }
      }
      this.fieldNames = li;
    }
    this.checkDeleted = checkDeleted;
  }

  public static Unique valueOf(List<String> fieldNames, Boolean checkDeleted) {
    return new UniqueObject(fieldNames, checkDeleted);
  }

  public static Unique getUniqueFromXml(Element uniqueElement) throws CloneNotSupportedException, Exception {

    Boolean checkDeleted = Boolean.parseBoolean(uniqueElement.getElementsByTagName("checkDeleted").item(0).getChildNodes().item(0).getNodeValue());

    NodeList namesNodeList = uniqueElement.getElementsByTagName("name");
    List<String> names = new ArrayList();
    for (int i = 0; i < namesNodeList.getLength(); i++) {
      Element nameElement = (Element) namesNodeList.item(i);
      String name = nameElement.getChildNodes().item(0).getNodeValue();
      names.add(name);
    }

    return valueOf(names, checkDeleted);
  }

  /**
   *
   * @return названия полей модели, которые должны быть уникальны
   */
  public List<String> getFieldNames() {
    List<String> li = new ArrayList();
    li.addAll(fieldNames);
    return li;
  }

  /**
   * нужно ли проверять на уникальность закрытые (удаленные записи)
   *
   * @return
   */
  public Boolean isCheckDeleted() {
    return checkDeleted;
  }

  @Override
  public UniqueObject clone() throws CloneNotSupportedException {
    ObjectOutputStream oos = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(baos);
      oos.writeObject(this);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      return (UniqueObject) ois.readObject();
    } catch (Exception ex) {
      throw new CloneNotSupportedException(MyString.getStackExeption(ex));
    }
  }

  @Override
   public void getSelfInXml(Document doc, Element unique) throws Exception {
    primXml.createElement(doc, unique, "checkDeleted", checkDeleted); 
    for (String name : fieldNames) {
      primXml.createElement(doc, unique, "name", name); 
    }
  }
}
