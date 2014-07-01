/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.dao;

import com.prim.core.AbstractApplication;
import com.prim.core.model.Model;
import com.prim.core.select.Select;
import com.prim.core.select.Table;
import com.prim.core.select.TableSelectFactory;
import com.prim.support.FormatDate;
import java.sql.Connection;

/**
 *
 * родительский класс для объектов доступа к данным - Data Access Object
 *
 * @author Pavel Rice
 */
public class DAO {

  protected AbstractApplication app;

  public DAO(AbstractApplication app) {
    this.app = app;
  }

  /**
   *
   * @param name название сущности из БД
   * @return объект Table для запроса
   * @throws Exception
   */
  protected Table getTable(String name) throws Exception {
    return TableSelectFactory.getTable(app, name);
  }

  /**
   *
   * @param name название сущности из БД
   * @param alias новый алиас таблицы
   * @return объект Table для запроса
   * @throws Exception
   */
  protected Table getTable(String name, String alias) throws Exception {
    return TableSelectFactory.getTable(app, name, alias);
  }

  /**
   *
   * @return объект запроса Select
   * @throws Exception
   */
  protected Select getSelect() throws Exception {
    return TableSelectFactory.getSelect(app);
  }

  /**
   *
   * @return объект соединения с БД
   */
  protected Connection getConnection() {
    return app.getConnection();
  }

  /**
   * сделать запрос к БД. Если при выполнении запроса произошла ошибка - метод бросает Exception
   *
   * @param select объект запроса
   * @throws Exception если при выполнении запроса произошла ошибка
   */
  public void executeSelect(Select select) throws Exception {
    boolean ok = select.executeSelect(getConnection());
    if (!ok) {
      throw new Exception(select.getError().toString());
    }
  }

  

  /**
   * сохранить модель. Если при сохранении произошла ошибка - метод бросает Exception
   *
   * @param model
   * @throws Exception
   */
  public void saveModel(Model model) throws Exception {
    boolean ok = model.save();
    if (!ok) {
      throw new Exception(model.getError().toString());
    }
  }
}
