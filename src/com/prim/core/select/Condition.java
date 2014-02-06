/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.select;

/**
 *
 * @author кот
 */
public interface Condition {

  public Parameter getParamFirst();

  public CondType getCondType();

  public Object getParamSecond();
}
