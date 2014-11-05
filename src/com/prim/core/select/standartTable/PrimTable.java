/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.select.standartTable;

import com.prim.core.modelStructure.Field;
import com.prim.core.select.Parameter;
import com.prim.core.select.Table;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pavel Rice
 */
public class PrimTable implements Table {

  /**
   * Дата удаления
   */
  public static final String DELETE_DATE = "delete_date";
  
  public static final String INSERT_DATE = "insert_date";

  protected Table table;

  @Override
  public Boolean isSystem() {
    return table.isSystem();
  }

  @Override
  public Field getField(String name) throws CloneNotSupportedException {
    return table.getField(name);
  }

  @Override
  public String getFieldAlias(String name) {
    return table.getFieldAlias(name);
  }

  @Override
  public String getFieldName(String name) throws Exception {
    return table.getFieldName(name);
  }

  @Override
  public String getRealName() {
    return table.getRealName();
  }

  @Override
  public Map<String, Field> getStructure() throws CloneNotSupportedException {
    return table.getStructure();
  }

  @Override
  public String getModelTbAlias() {
    return table.getModelTbAlias();
  }

  @Override
  public Parameter getPrimary() {
    return table.getPrimary();
  }

  @Override
  public List<Parameter> getParameters() throws CloneNotSupportedException {
    return table.getParameters();
  }

  @Override
  public Parameter get(String paramModelAlias) throws CloneNotSupportedException {
    return table.get(paramModelAlias);
  }

  @Override
  public Table clone() throws CloneNotSupportedException {
    return table.clone();
  }

  public Parameter DELETE_DATE() throws CloneNotSupportedException {
    return get(DELETE_DATE);
  }
  
  public Parameter INSERT_DATE() throws CloneNotSupportedException {
    return get(INSERT_DATE);
  }
  
}
