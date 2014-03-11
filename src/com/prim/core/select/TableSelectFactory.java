/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.select;

import com.prim.core.AbstractApplication;
import com.prim.core.model.DinamicModel;
import com.prim.core.modelStructure.Structure;
import com.prim.core.warehouse.Keeper;
import com.prim.core.warehouse.modelKeeper.ModelStructureKeeper;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author User
 */
public class TableSelectFactory {

  AbstractApplication app;

  public static Table getTable(AbstractApplication app, String modelAlias) throws Exception {
    TableSelectFactory tf = new TableSelectFactory(app);
    Table table = tf.getTable(modelAlias);
    return table;
  }
  
  public static Table getTable(AbstractApplication app, String modelAlias, String tableAlias) throws Exception {
    TableSelectFactory tf = new TableSelectFactory(app);
    Table table = tf.getTable(modelAlias, tableAlias);
    return table;
  }
  
  public static Select getSelect(AbstractApplication app, Table... tb) throws Exception {
    TableSelectFactory tf = new TableSelectFactory(app);
    return tf.getSelect(tb);
  }
  
  /**
   *
   * @param app объект приложения
   */
  public TableSelectFactory(AbstractApplication app) {
    if (app != null) {
      this.app = app;
    }
  }

  /**
   * получить объект Table
   *
   * @param modelAlias название модели
   * @return
   * @throws Exception
   */
  public Table getTable(String modelAlias) throws Exception {
    Keeper keeper = app.getKeeper();
    ModelStructureKeeper msk = keeper.getModelStructureKeeper();
    Structure structure = msk.getStructure(modelAlias);
    //Structure structure = ModelStructureKeeper.getInstance(app).getStructure(modelAlias);
    if (structure == null) {
      throw new Exception("Tables structure is null " + modelAlias);
    }
    if (app.getDbDriver().equals("mysql")) {
      return TableMysql.getInstance(structure);
    } else {
      return TableMysql.getInstance(structure);
    }
  }

  /**
   * получить объект Table с установленным псевдонимом
   *
   * @param modelAlias название модели
   * @param tableAlias псевдоним таблицы
   * @return
   * @throws Exception
   */
  public Table getTable(String modelAlias, String tableAlias) throws Exception {
    Structure structure = app.getKeeper().getModelStructureKeeper().getStructure(modelAlias);
    if (structure == null) {
      throw new Exception("Tables structure is null " + modelAlias);
    }
    if (app.getDbDriver().equals("mysql")) {
      return TableMysql.getInstance(structure, tableAlias);
    } else {
      return TableMysql.getInstance(structure, tableAlias);
    }
  }

  @Deprecated
  public List<DinamicModel> searchByParams(Table tb, Connection conn, Parameter sort, OrdTypes ty, Integer limitFrom, Integer limit, Condition... cnd) throws Exception {
    List<DinamicModel> result = new ArrayList<DinamicModel>();
    Select sel = getSelect(tb);
    if (limit != null) {
      sel.setLimitRange(limit);
    }
    if (limitFrom != null) {
      sel.setLimitFrom(limitFrom);
    }
    sel.from(tb);
    sel.and(tb.getPrimary().isNotNull());
    sel.and(cnd);
    if (sort != null && ty != null) {
      sel.order(sort, ty);
    }
    sel.executeSelect(conn);
    if (!sel.getError().isEmpty()) {
      throw new Exception(sel.getError().toString());
    }
    result = sel.getDinamicList();
    return result;
  }

  /**
   * возвращает одну модель. Если ни одной модели не найдено, возвращает null
   *
   * @param tb
   * @param conn
   * @param sort
   * @param ty
   * @param limitFrom
   * @param limit
   * @param cnd
   * @return
   * @throws Exception
   */
  @Deprecated
  public DinamicModel searchOneByParams(Table tb, Connection conn, Parameter sort, OrdTypes ty, Integer limitFrom, Integer limit, Condition... cnd) throws Exception {
    List<DinamicModel> result = searchByParams(tb, conn, sort, ty, limitFrom, limit, cnd);
    if (result.isEmpty()) {
      return null;
    } else {
      return result.get(0);
    }
  }

  /**
   * возвращает одну модель. Если ни одной модели не найдено, возвращает null
   *
   * @param tb
   * @param conn
   * @param cnd
   * @return
   * @throws Exception
   */
  @Deprecated
  public DinamicModel searchOneByParams(Table tb, Connection conn, Condition... cnd) throws Exception {
    return searchOneByParams(tb, conn, null, null, null, null, cnd);
  }

  public static List<Condition> getConditionList(Condition... cnd) {
    List<Condition> list = new ArrayList();
    list.addAll(Arrays.asList(cnd));
    return list;
  }

  public Select getSelect(Table... tb) throws CloneNotSupportedException {
    if (app.getDbDriver().equals("mysql")) {
      return SelectMysql.getInstance(tb);
    } else {
      return SelectMysql.getInstance(tb);
    }
  }
}
