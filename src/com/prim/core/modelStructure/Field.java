/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.modelStructure;

import com.prim.support.ToXml;
import java.util.List;

import com.prim.support.filterValidator.entity.ValidatorAbstract;

/**
 *
 * @author User
 */
public interface Field extends ToXml{
  /**
   * редактируемое ли поле то есть можно ли редактировать параметры поля через интерфейс админа
   */
  public Boolean isEditable() ;

  /**
   * получить имя поля в хранилище данных
   */
  public String getName();
  /**
   * алиас поля (название поля в модели)
   */
  public String getAlias();
    /**
   * обязательность поля
   */
  public Boolean isMandatory();

  public String getAppName();
  
  public Boolean isUpdatable();

  public String getRelations();

  public String getType();

  public List<ValidatorAbstract> getCloneValidatorList()throws CloneNotSupportedException ;

  public List<ValidatorAbstract> getValidatorList();
  
  public String getDef() ;

  public Object getValue();

  public void setValue(Object value);

  public Field clone()throws CloneNotSupportedException;
  
}
