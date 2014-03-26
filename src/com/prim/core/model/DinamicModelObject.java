/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.model;

import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.Structure;
import com.prim.core.representation.Xml;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.prim.support.primXml;


/**
 * экземпляр класса представляет собой одну модель, то есть одну строку из
 * результата запроса к БД
 *
 * @author Кот
 */
final class DinamicModelObject implements DinamicModel, Cloneable {

  /**
   * внутренний массив моделей
   */
  private List<DinamicModel> innerDinamicModel = new ArrayList<DinamicModel>();
  /**
   * массив ошибок моделей
   */
  private List<String> errors = new ArrayList();
  /**
   * структуа модели
   */
  final private Structure structure;
  List<Map<String, Object>> fileArray = new ArrayList();

  private DinamicModelObject(Structure structure) {
    this.structure = structure;
  }

  static DinamicModelObject getInstance(Structure structure) {
    return new DinamicModelObject(structure);
  }

  @Override
  public DinamicModel clone() throws CloneNotSupportedException {
    DinamicModel dm = getInstance(structure);
    dm.addError(errors);
    for (Map<String, Object> fileInfo : fileArray) {
      dm.addFileToArray(fileInfo);
    }
    for (DinamicModel din : innerDinamicModel) {
      dm.addInner(din.clone());
    }
    return dm;
  }

  @Override
  public List<String> getError() {
    List<String> err = new ArrayList();
    err.addAll(errors);
    return err;
  }

  @Override
  public void addError(String err) {
    errors.add(err);
  }

  @Override
  public void addError(String... err) {
    for (String er : err) {
      errors.add(er);
    }
  }

  @Override
  public void addError(List<String> err) {
    errors.addAll(err);
  }

  @Override
  public List<Map<String, Object>> getFileArray() {
    List<Map<String, Object>> result = new ArrayList();
    for (Map<String, Object> fileInfo : fileArray) {
      HashMap<String, Object> resMap = new HashMap<String, Object>();
      for (String name : fileInfo.keySet()) {
        resMap.put(name, fileInfo.get(name));
      }
      result.add(resMap);
    }
    return result;
  }

  @Override
  public void addFileToArray(Map<String, Object> fileInfo) {
    HashMap<String, Object> resMap = new HashMap<String, Object>();
    for (String name : fileInfo.keySet()) {
      resMap.put(name, fileInfo.get(name));
    }
    fileArray.add(resMap);
  }

  @Override
  public void addFileArray(List<Map<String, Object>> fileArray) {
    for (Map<String, Object> map : fileArray) {
      addFileToArray(map);
    }
  }

  @Override
  public void clearFileArray() {
    fileArray = new ArrayList();
  }

  @Override
  public List<DinamicModel> getInnerDinamicModel() throws CloneNotSupportedException {
    List<DinamicModel> result = new ArrayList();
    for (DinamicModel dm : innerDinamicModel) {
      result.add(dm.clone());
    }
    return result;
  }

  @Override
  public void addInner(DinamicModel dm) throws CloneNotSupportedException {
    innerDinamicModel.add(dm.clone());
  }

  @Override
  public void clearInner() {
    innerDinamicModel = new ArrayList();
  }

  @Override
  public Object get(String name) {
    Field fd = structure.getField(name);
    if (fd != null) {
      return fd.getValue();
    } else {
      errors.add("Structure has no paraeter" + name);
    }
    return null;
  }

  @Override
  public void set(String name, Object value) {
    Field fd = structure.getField(name);
    if (fd != null) {
      fd.setValue(value);
    } else {
      errors.add("Structure has no parameter" + name);
    }
  }

  @Override
  public void set(Map<String, Object> map) {
    for (String name : map.keySet()) {
      set(name, map.get(name));
    }
  }

  @Override
  public Structure getStructureClone() throws CloneNotSupportedException {
    return structure.clone();
  }

  @Override
  public Structure getStructure() {
    return structure;
  }

  @Override
  public Map<String, Object> getParams() throws CloneNotSupportedException {
    HashMap<String, Object> obj = new HashMap<String, Object>();
    for (String name : structure.getCloneFields().keySet()) {
      obj.put(name, structure.getField(name).getValue());
    }
    return obj;
  }

  @Override
  public void getSelfInXml(Document doc, Element root) throws Exception {
    Element er =  primXml.createEmptyElement(doc, root, "errors");
    for (String error : getError()) {
      primXml.createElement(doc, er, "error", error);
    }
    // параметры
    Element struct = primXml.createEmptyElement(doc, root,"structure");
    //structure.getSelfInXml(doc, struct);
    Xml.structureToXml(doc, struct, structure);
 

    Element innerParams = primXml.createEmptyElement(doc, root, "innerParams");
    for (DinamicModel model : getInnerDinamicModel()) {
      Element mod = primXml.createEmptyElement(doc, root, "model");
      model.getSelfInXml(doc, mod);
    }
  }
}
