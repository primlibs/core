/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.pair;

import com.prim.support.ToXml;
import java.util.List;
import java.util.Map;


/**
 *
 * @author кот
 */
public interface Pair extends Cloneable {

  public Sequence getSequence(String name);

  /**
   * существует ли Sequence с таким именем
   *
   * @param sequenceName
   * @return
   */
  public Boolean containsSequence(String sequenceName);

  public Boolean getDef();

  public String getObject();

  public String getAction();

  public Map<String, Sequence> getSequenceClone();

  public Pair getParent();
  /**
     * возвращает список пар одного уровня
     * @return 
     */
  public List<Pair> getPairsClone();
  
  public void setParent(Pair pair);

  /**
   * рекурсивный поиск вложенной пары по имени и методу
   *
   * @param objectName
   * @param actionName
   * @return
   */
  public Pair searchOne(String objectName, String actionName);

  /**
   * рекурсивный метод, который возвращает все вложенные пары
   *
   * @return
   */
  public List<Pair> getAllPairsClone();

  /**
   * возвращает всех родителей пары
   *
   * @return
   */
  public List<Pair> getAllParentСlone();

  /**
   * содержится ли в паре пара с таким именем и методом
   *
   * @param objectName
   * @param actionName
   * @return
   */
  public Boolean containsPair(String objectName, String actionName);

  /**
   * произвести поиск пары </br> результаты поиска можно получить методом
   * getList()
   *
   * @param object
   * @param action
   * @return
   */
  public Boolean search(String object, String action);
  
   /**
   * найти только дочерние пары, которые должны быть вызваны для пары с такими object и action
   * @param object
   * @param action
   * @return 
   */
  public Boolean searchChildren(String object, String action);

  /**
   * получить результат поиска - результат выполнения метода search()
   *
   * @return
   */
  public List<Pair> getSearchListClone();

  /**
   * рекурсивно найти все вложенные пары, у которых def == true
   *
   * @return
   */
  public List<Pair> getDefPairsRecursiveClone();

  public Pair clone();
  
  public void setSequence(Sequence cs);
  
  public void removeSequence(String name);
  
  public void addPair(Pair pp);
  
  /**
   * удалить вложенную пару с такими object и action. Проверяются все уровни вложенности.
   * @param object
   * @param action 
   */
  public void removePair(String object, String action);
  
   public void setDef(Boolean def);
}
