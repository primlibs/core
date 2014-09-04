/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.prim.support.primXml;

/**
 *
 * @author Кот
 */
public final class PairObject implements Pair {

  public static String message = "";
  
  final private String object;
  final private String action;
  private Boolean def;
  final private Map<String, Sequence> sequence;
  private List<Pair> pairArray;
  // ссылка на родительскую пару
  private Pair parent;
  //используется для поиска
  private List<Pair> searchList = new ArrayList();
  
  private String controllerName = "";
  
  private boolean byWebController = false;

  private PairObject(String object, String action, Boolean def, Map<String, Sequence> sequence, List<Pair> pairArray, Pair parent, boolean byWebController, String controllerName) {
    this.object = (object == null ? "" : object);
    this.action = (action == null ? "" : action);
    this.byWebController = byWebController;
    this.controllerName = controllerName;
    this.def = def;
    if (parent != null) {
      this.parent = parent;
    }

    Map<String, Sequence> newSequence = new HashMap();
    if (sequence != null) {
      for (String name : sequence.keySet()) {
        if (sequence.get(name) != null) {
          newSequence.put(name, sequence.get(name).clone());
        }
      }
    }
    this.sequence = newSequence;
    List<Pair> newPairList = new ArrayList();
    if (pairArray != null) {
      for (Pair pp : pairArray) {
        if (pp != null) {
          newPairList.add(pp.clone());
        }
      }
    }
    this.pairArray = newPairList;

  }

  public static PairObject getInstance(String object, String action, Boolean def, Map<String, Sequence> sequence, List<Pair> pairArray, Pair parent, boolean byWebController, String controllerName) {
    return new PairObject(object, action, def, sequence, pairArray, parent, byWebController, controllerName);
  }

  public void setParent(Pair pair) {
    this.parent = pair;
  }

  @Override
  public Sequence getSequence(String name) {
    if(sequence.get(name)!=null){
      return sequence.get(name).clone();
    }
    return null;
  }

  /**
   * существует ли Sequence с таким именем
   *
   * @param sequenceName
   * @return
   */
  @Override
  public Boolean containsSequence(String sequenceName) {
    return sequence.containsKey(sequenceName);
  }

  @Override
  public Boolean getDef() {
    return def;
  }

  @Override
  public String getObject() {
    return object;
  }

  @Override
  public String getAction() {
    return action;
  }

  @Override
  public Map<String, Sequence> getSequenceClone() {
    Map<String, Sequence> result = new HashMap();
    for (String name : sequence.keySet()) {
      result.put(name, (sequence.get(name) != null ? sequence.get(name).clone() : null));
    }
    return result;
  }

  @Override
  public Pair getParent() {
    if (parent != null) {
      return parent.clone();
    } else {
      return null;
    }
  }

  /**
   * возвращает список пар одного уровня
   *
   * @return
   */
  @Override
  public List<Pair> getPairsClone() {
    List<Pair> result = new ArrayList();
    for (Pair pp : pairArray) {
      if (pp != null) {
        result.add(pp.clone());
      }
    }
    return result;
  }

  /**
   * рекурсивный поиск вложенной пары по имени и методу
   *
   * @param objectName
   * @param actionName
   * @return
   */
  @Override
  public Pair searchOne(String objectName, String actionName) {
    Pair pair = null;
    if (objectName.equals(object) & actionName.equals(action)) {
      pair = this;
    } else {
      for (Pair p : pairArray) {
        if (p.searchOne(objectName, actionName) != null) {
          pair = p.searchOne(objectName, actionName);
          break;
        }
      }
    }
    if (pair != null) {
      return pair;
    }
    return null;
  }

  /**
   * рекурсивный метод, который возвращает все вложенные пары
   *
   * @return
   */
  @Override
  public List<Pair> getAllPairsClone() {
    List<Pair> allPairs = new ArrayList();
    for (Pair pair : pairArray) {
      if (pair != null) {
        allPairs.add(pair.clone());
        allPairs.addAll(pair.getAllPairsClone());
      }
    }
    return allPairs;
  }

  /**
   * возвращает всех родителей пары
   *
   * @return
   */
  @Override
  public List<Pair> getAllParentСlone() {
    List<Pair> pairs = new ArrayList();
    if (parent != null) {
      pairs.add(parent.clone());
      pairs.addAll(parent.getAllParentСlone());
    }
    return pairs;
  }

  /**
   * содержится ли в паре пара с таким именем и методом
   *
   * @param objectName
   * @param actionName
   * @return
   */
  @Override
  public Boolean containsPair(String objectName, String actionName) {
    Boolean contains;
    if (this.searchOne(objectName, actionName) != null) {
      contains = true;
    } else {
      contains = false;
    }
    return contains;
  }

  /**
   * произвести поиск пары </br> результаты поиска можно получить методом
   * getList()
   *
   * @param object
   * @param action
   * @return
   */
  @Override
  public Boolean search(String object, String action) {
    searchList = new ArrayList();
    Boolean result = false;
    // если текщий объект прошел проверку, то нужно добавить в массив все дочерние пары, у которых def==true
    if (object.equals(this.object) & action.equals(this.action)) {
      for (Pair p : getDefPairsRecursiveClone()) {
        if (!isObjectInList(p.getObject(), searchList)) {
          searchList.add(p.clone());
        }
      }
      result = true;
      // иначе - проверить все вложенные объекты
    } else {
      for (Pair pair : pairArray) {
        // если один из вложенных оъектов прошел проверку
        if (pair.search(object, action) == true) {
          // добавить все накопленные объекты
          searchList.addAll(pair.getSearchListClone());
          // добавить все вложенные объекты, у которых def==true
          for (Pair p : pairArray) {
            if (p.getDef() == true & p != pair) {
              if (!isObjectInList(p.getObject(), searchList)) {
                searchList.add(p);
              }
            }
          }
          result = true;
          break;
        }
      }
    }
    // добавить сам объект
    if (result) {
      searchList.add(this);
    }
    return result;
  }
  
  /**
   * найти только дочерние пары, которые должны быть вызваны для пары с такими object и action
   * @param object
   * @param action
   * @return 
   */
  @Override
  public Boolean searchChildren(String object, String action) {
    searchList = new ArrayList();
    Boolean result = false;
    // если текщий объект прошел проверку, то нужно добавить в массив все дочерние пары, у которых def==true
    if (object.equals(this.object) & action.equals(this.action)) {
      for (Pair p : getDefPairsRecursiveClone()) {
        if (!isObjectInList(p.getObject(), searchList)) {
          searchList.add(p.clone());
        }
      }
      searchList.add(this);
      result = true;
      // иначе - проверить все вложенные объекты
    } else {
      for (Pair pair : pairArray) {
        // если один из вложенных оъектов прошел проверку
        if (pair.searchChildren(object, action) == true) {
          // добавить все накопленные объекты
          searchList.addAll(pair.getSearchListClone());
          result = true;
          break;
        }
      }
    }
    return result;
  }
  

  /**
   * получить результат поиска - результат выполнения метода search()
   *
   * @return
   */
  public List<Pair> getSearchListClone() {
    return searchList;
  }

  @Override
  public String toString() {
    return object + ":" + action;
  }

  /**
   * возврат ссылок на пары
   */
  public List<Pair> getAllPairs() {
    return pairArray;
  }

  /**
   * рекурсивно найти все вложенные пары, у которых def == true
   *
   * @return
   */
  @Override
  public List<Pair> getDefPairsRecursiveClone() {
    List<Pair> list = new ArrayList();
    for (Pair pair : pairArray) {
      if (pair.getDef() == true) {
        list.add(pair);
        list.addAll(pair.getDefPairsRecursiveClone());
      }
    }
    return list;
  }

  /**
   * Проверить наличие объекта в списке
   */
  private Boolean isObjectInList(String object, List<Pair> list) {
    Boolean res = false;
    for (Pair p : list) {
      if (p.getObject().equals(object)) {
        res = true;
      }
    }
    return res;
  }

  @Override
  public Pair clone() {
    return getInstance(object, action, def, sequence, pairArray, parent, byWebController, controllerName);
  }

  /*
  @Override
  public void getSelfInXml(Document doc, Element pair) throws Exception {
    primXml.createElement(doc, pair, "object", object);
    primXml.createElement(doc, pair, "action", action);
    primXml.createElement(doc, pair, "def", def);
    Element seqMap=primXml.createEmptyElement(doc, pair, "sequenceMap");
    for (Sequence ss : sequence.values()) {
      Element seq=primXml.createEmptyElement(doc, seqMap, "sequense");
      ss.getSelfInXml(doc, seq);
    }

    Element paArr=primXml.createEmptyElement(doc, pair, "pairArray");
    for (Pair pp : pairArray) {
      Element pr=primXml.createEmptyElement(doc, paArr, "pair");
      pp.getSelfInXml(doc, pr);
    }


  }

  public static PairObject getPairFromXml(Element elem) throws CloneNotSupportedException, Exception {

    String object = elem.getElementsByTagName("object").item(0).getChildNodes().item(0).getNodeValue();
    String action = elem.getElementsByTagName("action").item(0).getChildNodes().item(0).getNodeValue();
    Boolean def = Boolean.parseBoolean(elem.getElementsByTagName("def").item(0).getChildNodes().item(0).getNodeValue());

    Element sequenceMapElement = (Element) elem.getElementsByTagName("sequenceMap").item(0);
    NodeList sequenceNodeList = sequenceMapElement.getElementsByTagName("sequense");
    Map<String, Sequence> sequence = new HashMap();
    for (int i = 0; i < sequenceNodeList.getLength(); i++) {
      Element sequenceElement = (Element) sequenceNodeList.item(i);
      Sequence seq = SequenceObject.getSequenseFromXml(sequenceElement);
      sequence.put(seq.getName(), seq);
    }

    Element pairArrayElement = (Element) elem.getElementsByTagName("pairArray").item(0);
    NodeList pairNodeList = pairArrayElement.getChildNodes();
    List<Pair> pairs = new ArrayList();
    for (int i = 0; i < pairNodeList.getLength(); i++) {
      Element pairElement = (Element) pairNodeList.item(i);
      if (pairElement.getTagName().equals("pair")) {
        PairObject pr = PairObject.getPairFromXml(pairElement);
        pairs.add(pr);
      }
    }

    PairObject self = getInstance(object, action, def, sequence, pairs, null);
    for (Pair pp : self.getAllParentСlone()) {
      pp.setParent(self);
    }
    return self;

  }
  */

  @Override
  public void setSequence(Sequence cs) {
    sequence.put(cs.getName(), cs.clone());
  }

  @Override
  public void removeSequence(String name) {
    sequence.remove(name);
  }

  @Override
  public void addPair(Pair pp) {
    pairArray.add(pp);
  }

  @Override
  public void removePair(String object, String action) {
    /*
    List<Pair> newPair= new ArrayList();
    for (Pair pp : pairArray) {
      if (pp.getAction().equals(action) && pp.getObject().equals(object)) {
 
      }else{
        newPair.add(pp.clone());
      }
    }
    pairArray=newPair;
    */
    for (Pair pair: pairArray) {
      if (pair.getAction().equals(action) && pair.getObject().equals(object)) {
        pairArray.remove(pair);
        break;
      } else {
        pair.removePair(object, action);
      }
    }
  }

  @Override
  public void setDef(Boolean def) {
    this.def=def;
  }

  @Override
  public void setControllerName(String controllerName) {
    this.controllerName = controllerName;
  }

  

  @Override
  public String getControllerName() {
    return controllerName;
  }

  @Override
  public boolean isByWebController() {
    return byWebController;
  }
}
