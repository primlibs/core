package com.prim.core.select;

import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.Structure;
import java.util.*;


/**
 * таблица, которая учавствует в SQL-запросе к БД
 *
 * @author Pavel Rice
 */
final class TableMysql implements Table {

  final private String realName;
  final private String modelTbAlias;
  final private Parameter primary;
  final private Boolean system;
  final private List<Parameter> parameters;
  final private Map<String, Field> structure;

  static TableMysql getInstance(Structure str) throws CloneNotSupportedException {
    return new TableMysql(str);
  }
  
  static TableMysql getInstance(Structure str,String tableAlias) throws CloneNotSupportedException {
    return new TableMysql(str, tableAlias);
  }

  private TableMysql(Structure str) throws CloneNotSupportedException {
    realName = str.getTableName();
    modelTbAlias = str.getTableAlias();
    system = str.isSystem();
    Map<String,Field> newStructure= new HashMap<String, Field>();
    Parameter newPrimary=null;
    List<Parameter> newParams=new ArrayList();
    for (Field fd : str.getCloneFields().values()) {
      newStructure.put(fd.getAlias(), fd);
      Parameter pr = ParameterMysql.getInstance(fd.getName(), fd.getAlias(), this, null);
      if (fd.getAlias().equals(str.getPrimaryAlias())) {
        newPrimary = pr;
      }
      newParams.add(pr);
    }
    this.parameters = newParams;
    this.structure = newStructure;
    this.primary = newPrimary;
    
  }
  
   private TableMysql(Structure str,String tableAlias) throws CloneNotSupportedException {
    realName = str.getTableName();
    modelTbAlias = tableAlias;
    system = str.isSystem();
    Map<String,Field> newStructure= new HashMap<String, Field>();
    Parameter newPrimary=null;
    List<Parameter> newParams=new ArrayList();
    for (Field fd : str.getCloneFields().values()) {
      newStructure.put(fd.getAlias(), fd);
      Parameter pr = ParameterMysql.getInstance(fd.getName(), fd.getAlias(), this, null);
      if (fd.getAlias().equals(str.getPrimaryAlias())) {
        newPrimary = pr;
      }
      newParams.add(pr);
    }
    this.parameters = newParams;
    this.structure = newStructure;
    this.primary = newPrimary;
    
  }

  private TableMysql(String realName, String modelTbAlias, Parameter primary, Boolean system, List<Parameter> parameters, Map<String, Field> struct) throws CloneNotSupportedException {
    this.realName = realName;
    this.modelTbAlias = modelTbAlias;
    this.primary = primary;
    this.system = system;
    if (parameters != null) {
      this.parameters = parameters;
    } else {
      this.parameters = new ArrayList();
    }
    if (struct != null) {
      this.structure = struct;
    } else {
      this.structure = new HashMap();
    }
  }

  @Override
  public Boolean isSystem() {
    return system;
  }

  @Override
  public Field getField(String name) throws CloneNotSupportedException {
    if(structure.get(name)!=null){
      return structure.get(name).clone();
    }else{
      return null;
    }
    
  }

  @Override
  public String getFieldAlias(String name) {
    if(structure.get(name)!=null){
      return structure.get(name).getAlias();
    }else{
      return null;
    }
  }

  @Override
  public String getFieldName(String name) throws Exception {
    if (!structure.containsKey(name)) {
      throw new Exception("Error: модель " + modelTbAlias + " не содержит поля " + name);
    }
    return structure.get(name).getName();
  }

  @Override
  public String getRealName() {
    return realName;
  }

  @Override
  public Map<String, Field> getStructure() throws CloneNotSupportedException {
    Map<String,Field> newStructure= new HashMap<String, Field>();
    for(String name:structure.keySet()){
      if(structure.get(name)!=null){
        newStructure.put(name, structure.get(name).clone());
      }
    }
    return newStructure;
  }

  @Override
  public String getModelTbAlias() {
    return modelTbAlias;
  }

  @Override
  public Parameter getPrimary() {
    return primary;
  }

  @Override
  public List<Parameter> getParameters() throws CloneNotSupportedException {
    List<Parameter> newList = new ArrayList();
    for (Parameter pr:parameters){
      newList.add(pr.clone());
    }
    return newList;
  }


  @Override
  public Parameter get(String paramModelAlias) throws CloneNotSupportedException {
    for (Parameter pr : parameters) {
      if (pr.getModelAlias().equals(paramModelAlias)) {
        return pr.clone();
      }
    }
    return null;
  }

  @Override
  public Table clone() throws CloneNotSupportedException {
    return new TableMysql(realName, modelTbAlias, primary.clone(), system, getParameters(), getStructure());
  }
}
