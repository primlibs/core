/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse;

import com.prim.core.AbstractApplication;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author User
 */
public class WarehouseSingleton {
  
  private static WarehouseSingleton instance = new WarehouseSingleton();
  
  private Map<String, Keeper> warehouse = new HashMap();
  
  private WarehouseSingleton() {
  }
  
  synchronized public static WarehouseSingleton getInstance() {
    return instance;
  }
  
  synchronized public Keeper getKeeper(AbstractApplication app) throws Exception {
    String name = app.getAppName();
    if (warehouse.get(name) == null) {
      warehouse.put(name, new Keeper(app.getOptionSingletonPath()));
    }
    return warehouse.get(name);
  }
  
  synchronized public Keeper getNewKeeper(AbstractApplication app) throws Exception {
    String name = app.getAppName();
    warehouse.put(name, new Keeper(app.getOptionSingletonPath()));
    return warehouse.get(name);
  }
  
  synchronized public Keeper getNewKeeper(String name, String optionSingletonPath) throws Exception {
    warehouse.put(name, new Keeper(optionSingletonPath));
    return warehouse.get(name);
  }
 
  
}
