package com.prim.core.select;

import java.util.*;
import com.prim.support.MyString;

/**
 * параметр SQL-запроса к БД
 *
 * @author Pavel Rice
 */
class ParameterMysql implements Parameter{
  /*
   * реальное имя в бд
   */

  final private String realName;
  /*
   * алиас в таблице(название в структуре моделей)
   */
  final private String modelAlias;
  /*
   * ссылка на таблицу
   */
  final private Table table;
  /*
   * название в результатах запроса
   */
  final private String selectAlias;
  
  static ParameterMysql getInstance(String realName, String modelAlias, Table table,String selectAlias){
    return new ParameterMysql(realName, modelAlias, table, selectAlias);
  }

  private ParameterMysql(String realName, String modelAlias, Table table,String selectAlias) {
    this.realName = realName;
    this.modelAlias = modelAlias;
    if(selectAlias==null){
      this.selectAlias = modelAlias;
    }else{
      this.selectAlias = selectAlias;
    }
    this.table = table;
  }


  @Override
  public String getRealName() {
    return realName;
  }

  @Override
  public String getModelAlias() {
    return modelAlias;
  }

  @Override
  public Table getTable() {
    return table;
  }

  @Override
  public String getSelectAlias() {
    return selectAlias;
  }

  /**
   * получить условие >
   *
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  @Override
  public Condition bigger(Object object) {
    return ConditionMysql.getInstance(this, CondType.bigger, object);
  }

  /**
   * получить условие <
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  @Override
  public Condition lesser(Object object) {
    return ConditionMysql.getInstance(this, CondType.lesser, object);
  }

  /**
   * получить условие =
   *
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  @Override
  public Condition eq(Object object) {
    return ConditionMysql.getInstance(this, CondType.equals, object);
  }

  /**
   * получить условие <=
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  @Override
  public Condition lesserEq(Object object) {
    return ConditionMysql.getInstance(this, CondType.lesserEq, object);
  }

  /**
   * получить условие >=
   *
   * @param object - объект парметра, либо элементарное значение (число, строка)
   * @return - объект условия
   */
  @Override
  public Condition biggerEq(Object object) {
    return ConditionMysql.getInstance(this, CondType.biggerEq, object);
  }

  /**
   * получить условие is null
   *
   * @return - объект условия
   */
  @Override
  public Condition isNull() {
    return ConditionMysql.getInstance(this, CondType.isNull, null);
  }

  /**
   * получить условие is not null
   *
   * @return - объект условия
   */
  @Override
  public Condition isNotNull() {
    return ConditionMysql.getInstance(this, CondType.isNotNull, null);
  }

  /**
   * получить условие like
   *
   * @return - объект условия
   */
  @Override
  public Condition isLikeLower(Object object) {
    return ConditionMysql.getInstance(this, CondType.like, object);
  }

  /**
   * получить условие like, при этом строка object разбивается на части по
   * рег.выражению split, и между этими частями вставляется символ %
   *
   * @param object строка
   * @param split рег.выражение
   * @return
   */
  @Override
  public Condition isLikeLower(Object object, String split) {
    if (MyString.NotNull(split)&& MyString.NotNull(object)) {
      String splObj = object.toString();
      String[] rss = splObj.split(split);
      String rs = null;
      for (String ress : rss) {
        if (rs == null) {
          rs = ress;
        } else {
          rs = rs + "%" + ress;
        }
      }
      return ConditionMysql.getInstance(this, CondType.like, rs);
    }
    return ConditionMysql.getInstance(this, CondType.like, object);
  }

  /**
   * получить условие not like
   *
   * @param object
   * @return
   */
  @Override
  public Condition isNotLikeLower(Object object) {
    return ConditionMysql.getInstance(this, CondType.notLike, object);
  }

  /**
   * получить условие like, при этом строка object разбивается на части по
   * рег.выражению split, и между этими частями вставляется символ %
   *
   * @param object строка
   * @param split рег.выражение
   * @return
   */
  @Override
  public Condition isNotLikeLower(Object object, String split) {
    if (split != null && !split.equals("") && !object.equals("") && object != null) {
      String splObj = object.toString();
      String[] rss = splObj.split(split);
      String rs = null;
      for (String ress : rss) {
        if (rs == null) {
          rs = ress;
        } else {
          rs = rs + "%" + ress;
        }
      }
      return ConditionMysql.getInstance(this, CondType.notLike, rs);
    }
    return ConditionMysql.getInstance(this, CondType.notLike, object);
  }
  
  
  

  /**
   * получить условие !=
   * @param object объект парметра, либо элементарное значение (число, строка)
   * @return 
   */
  @Override
  public Condition notEq(Object object) {
    return ConditionMysql.getInstance(this, CondType.notEquals, object);
  }

  @Override
  public Parameter clone() throws CloneNotSupportedException {
    return getInstance(realName, modelAlias, table, selectAlias);
  }

  @Override
  public Condition isLikeLowerTwoSpace(Object object, String split) {
    if (MyString.NotNull(split)&& MyString.NotNull(object)) {
      String splObj = object.toString();
      String[] rss = splObj.split(split);
      String rs = null;
      for (String ress : rss) {
        if (rs == null) {
          rs = ress;
        } else {
          rs = rs + "%" + ress;
        }
      }
      return ConditionMysql.getInstance(this, CondType._like_, rs);
    }
    return ConditionMysql.getInstance(this, CondType._like_, object);
  }

  @Override
  public Condition isLikeLowerLeftSpace(Object object, String split) {
       if (MyString.NotNull(split)&& MyString.NotNull(object)) {
      String splObj = object.toString();
      String[] rss = splObj.split(split);
      String rs = null;
      for (String ress : rss) {
        if (rs == null) {
          rs = ress;
        } else {
          rs = rs + "%" + ress;
        }
      }
      return ConditionMysql.getInstance(this, CondType._like, rs);
    }
    return ConditionMysql.getInstance(this, CondType._like, object);
  }

  @Override
  public Condition rlike(Object object, String split) {
    if (MyString.NotNull(split)&& MyString.NotNull(object)) {
      String splObj = object.toString();
      String[] rss = splObj.split(split);
      String rs = null;
      for (String ress : rss) {
        if (rs == null) {
          rs = ress;
        } else {
          rs = rs + "%" + ress;
        }
      }
      return ConditionMysql.getInstance(this, CondType.rlike, rs);
    }
    return ConditionMysql.getInstance(this, CondType.rlike, object);
  }

}
