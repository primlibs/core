/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.service;

import com.prim.core.AbstractApplication;
import com.prim.core.select.Select;
import com.prim.core.select.Table;
import java.sql.Connection;

/**
 *
 * @author User
 */
public interface DAOInterface {
  
  /**
   *
   * @param name название сущности из БД
   * @return объект Table для запроса
   * @throws Exception
   */
  public Table getTable(String name) throws Exception ;

  /**
   *
   * @param name название сущности из БД
   * @param alias новый алиас таблицы
   * @return объект Table для запроса
   * @throws Exception
   */
  public Table getTable(String name, String alias) throws Exception ;
  /**
   *
   * @return объект запроса Select
   * @throws Exception
   */
  public Select getSelect() throws Exception ;
  /**
   *
   * @return объект соединения с БД
   */
  public Connection getConnection();

  /**
   * сделать запрос к БД. Если при выполнении запроса произошла ошибка - метод бросает Exception
   *
   * @param select объект запроса
   * @throws Exception если при выполнении запроса произошла ошибка
   */
  public void executeSelect(Select select) throws Exception ;
  
  
  public AbstractApplication getApp();

}
