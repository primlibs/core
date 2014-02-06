/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author кот
 */
public interface QueryExecutor {

  /**
   * возвращает результат запроса в виде ArrayList
   *
   * @return - массив результатов запроса
   * @throws Exception
   */
  public List<Map<String, Object>> getResultList() throws Exception ;

  /*
   * Возвращает время самого длительного запроса в секундах
   */
  public Double getMaxQueryTime();
  /*
   * Возвращает текст самого длительного запроса 
   */
  public String getMaxQueryText();
  /*
   * Обнуляет данные о длительности запросов
   */
  public void resetMaxQueryInfo();
  
  
  /**
   * @return Boolean
   */
  public Boolean update();

  /**
   * @return Boolean
   */
  public Boolean select();

  public String checkParamByWords(String param);

  /**
   * Возвращает ошибки
   */
  public List<String> getError();

  /**
   * Возвращает текст запроса
   */
  public String getQueryText();
  
  public Double getQueryTime();

}
