/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.controller;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * типы затрат
 * 
 * @author Rice Pavel
 */
public enum StatusCodes {
  
  TRUE(0, "Успешно"), AUTH(1, "Ошибка авторизации"),
  RIGHT(2, "Нет прав"),OBJECT(3, "Нет объекта"),ACTION(4, "Нет метода"),
  CONTROLLER(5, "Ошибка контроллера"),APPLICATION(5, "Ошибка приложения"),BIZ(9,"Ошибка бизнес логики");
  
  final private Integer id;
  
  final private String name;
  
  public Integer getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  private StatusCodes(int id, String name) {
    this.id = id;
    this.name = name;
  }
  
  public static Map<String, Object> all() {
    Map<String, Object> map = new LinkedHashMap();
    for (StatusCodes type: StatusCodes.values()) {
      map.put(type.id.toString(), type.name);
    }
    return map;
  }
  
  public static String getNameById(Object typeId) {
    String name = "";
    if (typeId != null) {
      Map<String, Object> all = all();
      String typeIdString = typeId.toString();
      if (all.get(typeIdString) != null) {
        name = all.get(typeIdString).toString();
      }
    }
    return name;
  }
  
}
