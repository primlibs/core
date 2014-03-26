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
import java.util.LinkedHashMap;


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
   * структуа модели
   */
  private Map<String, Object> params = new LinkedHashMap();
  List<Map<String, Object>> fileArray = new ArrayList();

  private DinamicModelObject() {

  }

  static DinamicModelObject getInstance() {
    return new DinamicModelObject();
  }

  @Override
  public DinamicModel clone() throws CloneNotSupportedException {
    DinamicModel dm = getInstance();
    dm.set(params);
    for (Map<String, Object> fileInfo : fileArray) {
      dm.addFileToArray(fileInfo);
    }
    for (DinamicModel din : innerDinamicModel) {
      dm.addInner(din.clone());
    }
    return dm;
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

  public void addInner(List<DinamicModel> list) throws CloneNotSupportedException {
    for (DinamicModel dm: list) {
      addInner(dm);
    }
  }
  
  @Override
  public void clearInner() {
    innerDinamicModel = new ArrayList();
  }

  @Override
  public Object get(String name) {
    return params.get(name);
  }

  @Override
  public void set(String name, Object value) {
    params.put(name, value);
  }

  @Override
  public void set(Map<String, Object> map) {
    for (String name : map.keySet()) {
      set(name, map.get(name));
    }
  }

  @Override
  public Map<String, Object> getParams() throws CloneNotSupportedException {
    HashMap<String, Object> obj = new HashMap<String, Object>();
    for (String name : params.keySet()) {
      obj.put(name, params.get(name));
    }
    return obj;
  }

  @Override
  public void getSelfInXml(Document doc, Element root) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  
}
