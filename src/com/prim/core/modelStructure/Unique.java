/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.modelStructure;

import com.prim.support.ToXml;
import java.util.List;

/**
 *
 * @author User
 */
public interface Unique {

  public List<String> getFieldNames();

  /**
   * нужно ли проверять на уникальность закрытые (удаленные записи)
   *
   * @return
   */
  public Boolean isCheckDeleted();

  /**
   * клонирует объект
   * @return
   * @throws CloneNotSupportedException 
   */
  public UniqueObject clone() throws CloneNotSupportedException;
  
}
