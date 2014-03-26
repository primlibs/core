/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.pair;

import com.prim.support.ToXml;


/**
 *
 * @author кот
 */
public interface Sequence extends Cloneable {
  public String getName();
  
  public String getTrueRender();

  public String getFalseRender();

  public String getAppObjectName();

  public String getAppMethodName();

  public String getTrueRedirect();

  public String getFalseRedirect();

  public String getTrueRedirectParams();

  public String getFalseRedirectParams();

  public Sequence clone();
}
