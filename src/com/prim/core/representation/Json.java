/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.representation;

import com.google.gson.Gson;
import com.prim.core.controller.ActionResult;

/**
 *
 * запись и чтение объектов в виде xml
 * 
 * @author Rice Pavel
 */
public class Json {
  
  private Json() {}
  
  public static String actionResultToJson(ActionResult result) throws CloneNotSupportedException {
    Gson gson = new Gson();
    ActionResultView resultView = new ActionResultView(result);
    return gson.toJson(resultView);
  }
  
}
