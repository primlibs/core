/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.select;

import com.prim.core.model.DinamicModel;
import com.prim.core.modelStructure.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author кот
 */
public interface Select {

  /**
   * добавить в блок Select все параметры из таблицы
   *
   * @param tb таблица
   */
  public Select select(Table tb) throws CloneNotSupportedException;

  public Double getQueryTime();

  /**
   * добавить в блок Select все параметры из таблиц
   *
   * @param tb таблица
   */
  public Select select(Table... tb) throws CloneNotSupportedException;

  /**
   * добавить в блок Select все параметры из таблиц, при этом к названию каждого
   * параметра добавить алиас таблицы
   *
   * @param tb таблица
   */
  public Select selectWithAliases(Table tb) throws CloneNotSupportedException;

  /**
   * добавить в блок Select один параметр
   *
   * @param par параметр
   */
  public Select select(Parameter par) throws CloneNotSupportedException;

  /**
   * добавить в блок Select один параметр Distinct
   *
   * @param par
   * @return
   * @throws CloneNotSupportedException
   */
  public SelectMysql selectDistinct(Parameter par) throws CloneNotSupportedException;

  public Select select(Parameter... params) throws CloneNotSupportedException;

  /**
   * добавить в блок Select один параметр c новым псевдонимом
   *
   * @param par параметр
   * @param newAlias новый псевдоним
   */
  public Select select(Parameter par, String newAlias) throws CloneNotSupportedException;

  public Select onOr(List<Condition> cond) throws CloneNotSupportedException;

  /**
   * добавить в блок Select один параметр, применить к нему агрегатную функцию
   *
   * @param par параметр
   * @param newAlias новый алиас параметра
   * @param agr агрегатная функция, которая будет применена к параметру
   */
  public Select select(Parameter par, String newAlias, AgrTypes agr) throws CloneNotSupportedException;

  /**
   * добавить в блок Select один параметр, применить к этому параметру
   * SQL-функцию
   *
   * @param par параметр
   * @param newAlias новый алиас параметра
   * @param func SQL-функция, которая будет применена к параметру
   */
  public Select select(Parameter par, String newAlias, Function func) throws CloneNotSupportedException;

  /**
   * добавить в блок Select константу
   *
   * @param constant константа
   * @param alias псевдоним
   */
  public Select select(String constant, String alias) throws CloneNotSupportedException;

  /**
   * добавить в блок Select выражение selectDistinct
   *
   * @param par параметр
   * @param newAlias новый алиас
   * @param agr функция агрегации
   * @throws CloneNotSupportedException
   */
  public Select selectDistinct(Parameter par, String newAlias, AgrTypes agr) throws CloneNotSupportedException;

  /**
   * добавить таблицы в блок From
   *
   * @param tb таблицы
   */
  public Select from(Table... tb);

  /**
   * добавить таблицу в блок From
   *
   * @param tb таблица
   */
  public Select from(Table tb);

  /**
   * добавить таблицу в блок From c отбором по индексам
   *
   * @param tb таблица
   */
  public Select fromWithIndex(Table tb, String... indexes);

  /**
   * добавить выражение в блок select
   *
   * @param str
   * @return
   */
  public Select from(String str);

  /**
   * добавить в блок from ещё один SQL-запрос (то есть подапрос)
   *
   * @param sl запрос
   * @param alias алиас подзапроса
   */
  public Select from(SelectMysql sl, String alias);

  /**
   * добавить соединение таблиц типа LEFT JOIN
   *
   * @param first параметр из первой таблицы, учавствующей в соединении
   * @param second параметр из второй таблицы, учавствующей в соединении
   */
  public Select leftJoin(Parameter first, Parameter second) throws CloneNotSupportedException;

  /**
   * добавить соединение таблиц типа LEFT JOIN, не указывая индексов, которые будут использоваться
   * @param first параметр из первой таблицы, учавствующей в соединении
   * @param second параметр из второй таблицы, учавствующей в соединении
   * @return
   * @throws CloneNotSupportedException 
   */
  public Select leftJoinDefaultIndex(Parameter first, Parameter second) throws CloneNotSupportedException;
  
  /**
   * добавить соединение таблиц типа LEFT JOIN
   *
   * @param table таблица, с которой происходит соединение без параметров по
   * умолчанию
   * @return
   */
  public Select leftJoin(Table table) throws CloneNotSupportedException;

  /**
   * добавить условие and в блок where
   *
   * @param cond условие
   */
  public Select and(Condition cond) throws CloneNotSupportedException;

  /**
   * добавить условие and в блок where
   *
   * @param cond условие в виде строки
   * @return
   * @throws CloneNotSupportedException
   */
  public Select and(String cond) throws CloneNotSupportedException;

  /**
   * добавить условие and в блок where
   *
   * @param cond условие в виде строки
   * @param checkWhere добавлять ли условие where
   * @return
   * @throws CloneNotSupportedException
   */
  public Select and(String cond, boolean checkWhere) throws CloneNotSupportedException;

  /**
   * добавить условия and в блок where
   *
   * @param cond условия
   */
  public Select and(Condition... cond) throws CloneNotSupportedException;

  /**
   * добавить условие в блок where либо в блок ON
   *
   * @param cond условие
   * @param checkWhere если true, то добавляется в блок where
   */
  public Select and(Condition cond, Boolean checkWhere) throws CloneNotSupportedException;

  /**
   * добавить условие в блок ON. Применяется после блока left join
   */
  public Select on(Condition cond) throws CloneNotSupportedException;

  /**
   * добавить несколько условий через OR. <br/><br/> к блоку where добавляется:
   * AND ( условие 1 OR условие 2 OR ... )
   *
   * @param cond условия
   */
  public Select andOr(Condition... cond) throws CloneNotSupportedException;

  /**
   * добавить несколько условий через OR. <br/><br/> к блоку where добавляется:
   * AND ( условие 1 OR условие 2 OR ... )
   *
   * @param cond условия
   */
  public Select andOr(List<Condition> cond) throws CloneNotSupportedException;

  /**
   * добавить несколько списков условий через OR. <br/><br/> к блоку where
   * добавляется: AND ( (условие 1 and условие 2) OR (условие 3 and условие 4)
   * OR ... )
   *
   * @param cond условия
   */
  public Select andOrList(List<Condition>... cond) throws CloneNotSupportedException;

  /**
   * добавить несколько списков условий через OR. <br/><br/> к блоку where
   * добавляется: AND ( (условие 1 and условие 2) OR (условие 3 and условие 4)
   * OR ... )
   *
   * @param cond условия
   */
  public Select andOrList(List<List<Condition>> cond) throws CloneNotSupportedException;

  /**
   * добавить условия and в блок where
   *
   * @param cond условия
   */
  public Select and(List<Condition> cond) throws CloneNotSupportedException;

  /**
   * добавить условия or в блок where
   *
   * @param cond условия
   */
  public Select or(List<Condition> cond) throws CloneNotSupportedException;

  /**
   * добавить условие в блок where либо в блок ON
   *
   * @param cond условие
   * @param checkWhere если true, то добавляется в блок where
   */
  public Select or(List<Condition> cond, Boolean checkWhere) throws CloneNotSupportedException;

  /**
   * добавить параметр в блок ORDER
   *
   * @param param параметр
   * @param ot тип сортировки
   */
  public Select order(Parameter param, OrdTypes ot) throws Exception;

  /**
   * добавить произвольный параметр в блок ORDER
   *
   * @param param параметр
   * @param ot тип сортировки
   */
  public Select order(String param, OrdTypes ot) throws Exception;

  /**
   * добавить параметр в блок GROUP BY
   *
   * @param param параметр
   */
  public Select group(Parameter param) throws CloneNotSupportedException;
  
  /**
   * добавить параметр в блок GROUP BY
   *
   * @param param параметр
   */
  public Select group(String str) throws CloneNotSupportedException;

  /**
   * добавить параметр в блок GROUP BY, применить к этому параметру функцию
   *
   * @param param параметр
   * @param func функция
   */
  public Select group(Parameter param, Function func) throws Exception;

  /**
   * добавить параметры в блок GROUP BY
   *
   * @param param параметры
   */
  public Select group(List<Parameter> param) throws CloneNotSupportedException;

  /**
   * добавить выражение having
   *
   * @param str
   * @return
   */
  public Select having(String str) throws CloneNotSupportedException;

  /**
   * добавить выражение exists в блок where
   *
   * @param query подзапрос, который будет внутри выражения exists
   */
  public Select exists(String query);

  /**
   * добавить выражение exists в блок where
   *
   * @param query подзапрос, который будет внутри выражения exists
   */
  public Select notExists(String query);

  public Integer getLimitFrom();

  /**
   * добавить в блок limit - номер записи, начиная с которой производится
   * выборка
   *
   * @param limitFrom
   */
  public Select setLimitFrom(Integer limitFrom);

  public Integer getLimitRange();

  /**
   * добавить в блок linit - количество записей в результате запроса
   *
   * @param limitRange
   */
  public Select setLimitRange(Integer limitRange);

  /**
   * выполнить запрос
   *
   * @param conn объект соединения с БД
   * @return успешно ли выполнен запрос
   * @throws Exception
   */
  public Boolean executeSelect(Connection conn) throws Exception;

  /**
   *
   * @return ошибки
   */
  public List<String> getError();

  /**
   * возвращает результат запроса
   *
   * @return
   */
  public List<DinamicModel> getDinamicList();

  /**
   * возвращает строку SQL-запроса
   *
   * @return
   */
  public String getPrepareSelect();

  public List<Table> getAllSelectTables();

  public Map<String, Field> getStructure() throws CloneNotSupportedException;

  public Select setCashable(Boolean cashable);

  public String validateParameter(Object param, Boolean quoted);

  public Condition getExistCondition(String subquery);

  public Condition getNotExistCondition(String subquery);
}
