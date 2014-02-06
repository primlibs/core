/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.select;

import com.prim.core.modelStructure.Field;
import java.util.List;
import java.util.Map;

/**
 *
 * @author User
 */
public interface Table {

  public Boolean isSystem();

  public Field getField(String name) throws CloneNotSupportedException;

  public String getFieldAlias(String name) ;

  public String getFieldName(String name) throws Exception ;

  public String getRealName();
  public Map<String, Field> getStructure() throws CloneNotSupportedException;

  public String getModelTbAlias() ;

  public Parameter getPrimary();

  public List<Parameter> getParameters()throws CloneNotSupportedException;
  
  public Parameter get(String paramModelAlias) throws CloneNotSupportedException;
  public Table clone()throws CloneNotSupportedException;
}
