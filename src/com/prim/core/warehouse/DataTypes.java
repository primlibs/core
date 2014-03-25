/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * типы данных в полях модели
 * 
 * @author Rice Pavel
 */
public enum DataTypes {
  
  NONE, INT, BOOL, CHAR, DATETIME, DECIMAL, TEXT, REF; 
  
 public static Map<String, Object> all() {
    Map<String, Object> map = new LinkedHashMap();
    for (DataTypes type : DataTypes.values()) {
      map.put(type.toString(), type.toString());
    }
    return map;
  }
  
  public static DataTypes getTypeByString(String name) {
    DataTypes dataType = DataTypes.NONE;
    if (name != null) {
      for (DataTypes t: DataTypes.values()) {
        if (name.equalsIgnoreCase(t.toString())) {
          dataType = t;
          break;
        }
      }
    }
    return dataType;
  }
 
}
