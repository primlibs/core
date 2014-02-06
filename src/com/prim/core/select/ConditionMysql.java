package com.prim.core.select;

import java.util.ArrayList;
import java.util.List;

/**
 * условие SQL-запроса к БД
 *
 * @author Pavel Rice
 */
class ConditionMysql implements Condition{

  final private Parameter param1;
  final private CondType cond;
  final private Object param2;

  static Condition getInstance(Parameter param1, CondType cond, Object param2){
    return new ConditionMysql(param1,cond,param2);
  }
  
  /**
   * создать объект условия
   * @param param1 - первый параметр
   * @param sign - знак сравнения, например >, =
   * @param param2 - второй парметр
   */
  private ConditionMysql(Parameter param1, CondType cond, Object param2) {
    this.param1 = param1;
    this.cond = cond;
    this.param2 = param2;
  }  
  
  public Parameter getParamFirst() {
    return param1;
  }

  public CondType getCondType() {
    return cond;
  }

  public Object getParamSecond() {
    return param2;
  }
}
