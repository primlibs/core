/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.select;

/**
 *
 * @author кот
 */
public interface Parameter {
  /**
   * получить реальное имя параметра
   * @return 
   */
  public String getRealName();
   /**
   * получить реальное алиас модели
   * @return 
   */
  public String getModelAlias();

  /**
   * вернет ссылку на таблицу
   * @return 
   */
  public Table getTable();
  /**
   * вернет алиас селекта
   * @return 
   */
  public String getSelectAlias();
  
  public Parameter clone()throws CloneNotSupportedException;

  /**
   * получить условие >
   *
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  public Condition bigger(Object object);

  /**
   * получить условие <
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  public Condition lesser(Object object);

  /**
   * получить условие =
   *
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  public Condition eq(Object object);

  /**
   * получить условие <=
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  public Condition lesserEq(Object object);

  /**
   * получить условие >=
   *
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  public Condition biggerEq(Object object);

  /**
   * получить условие is null
   *
   * @return - объект условия
   */
  public Condition isNull();

  /**
   * получить условие is not null
   *
   * @return - объект условия
   */
  public Condition isNotNull();
  /**
   * получить условие like
   *
   * @return - объект условия
   */
  public Condition isLikeLower(Object object);
  /**
   * получить условие like, при этом строка object разбивается на части по
   * рег.выражению split, и между этими частями вставляется символ %
   *
   * @param object строка
   * @param split рег.выражение
   * @return
   */
  public Condition isLikeLower(Object object, String split);

  /**
   * получить условие like с пробелами с 2 сторон
   * рег.выражению split, и между этими частями вставляется символ %
   *
   * @param object строка
   * @param split рег.выражение
   * @return
   */
  public Condition isLikeLowerTwoSpace(Object object, String split);
  
  /**
   * получить условие like с пробелами слева
   * рег.выражению split, и между этими частями вставляется символ %
   *
   * @param object строка
   * @param split рег.выражение
   * @return
   */
  public Condition isLikeLowerLeftSpace(Object object, String split);
  
  /**
   * получить условие rlike(слово)
   * рег.выражению split, и между этими частями вставляется символ %
   *
   * @param object строка
   * @param split рег.выражение
   * @return
   */
  public Condition rlike(Object object, String split);
  
  
  /**
   * получить условие not like
   *
   * @param object
   * @return
   */
  public Condition isNotLikeLower(Object object);

  /**
   * получить условие like, при этом строка object разбивается на части по
   * рег.выражению split, и между этими частями вставляется символ %
   *
   * @param object строка
   * @param split рег.выражение
   * @return
   */
  public Condition isNotLikeLower(Object object, String split);

  /**
   * получить условие !=
   * @param object объект парметра, либо элементарное значение (число, строка)
   * @return 
   */
  public Condition notEq(Object object);
}
