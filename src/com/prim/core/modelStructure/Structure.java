/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.modelStructure;

import com.prim.support.ToXml;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author User
 */
public interface Structure {
  
  public static final String ELEMENT_NAME = "ModelStructure";

  public List<Unique> getUniqueList() throws CloneNotSupportedException;

  /**
   *
   * @return имя таблицы
   */
  public String getTableName() ;
  /**
   *
   * @return алиас таблицы
   */
  public String getTableAlias() ;

  /**
   *
   * @return алиас первичного поля первичного ключа
   */
  public String getPrimaryAlias();

  /**
   *
   * @return настоящее имя поля первичного ключа
   */
  public String getPrimaryRealName() ;

  /**
   *
   * @return является ли модель системной
   */
  public Boolean isSystem();

  /**
   *
   * @return разрешена ли работа с файлами для модели
   */
  public Boolean isFileWork();

  public Map<String, Field> getFields();
  
  /**
   *
   * @return клон массива полей
   */
  public Map<String, Field> getCloneFields() throws CloneNotSupportedException ;

  /**
   *
   * @return массив названия полей
   */
  public Set<String> getFieldsNames();

  /**
   * возвращает объект поля
   *
   * @param alias - алиас поля
   * @return
   */
  public Field getFieldClone(String alias) throws CloneNotSupportedException ;
  
  
  public Field getField(String alias);
  
  
  /**
   * существует ли поле под таким алиасом
   *
   * @param alias
   * @return
   */
  public boolean hasField(String alias);

  public String getName();
  /**
   *
   * @return описание структуры в виде строки
   */
  public String getInfo() ;
  
  public Structure clone()throws CloneNotSupportedException;
  
  
  
}
