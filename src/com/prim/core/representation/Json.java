/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.representation;

import com.prim.core.controller.ActionResult;

/**
 *
 * @author кот
 */
public class Json {
    
    public static String actionResultToJson(ActionResult ar) throws CloneNotSupportedException{
        ActionResultView arw = new ActionResultView(ar);
        return arw.getSelfInJson();
    }
    
}
