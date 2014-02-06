/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.db;

import java.sql.Connection;

/**
 *
 * @author кот
 */
public class ExecutorFabric {
  private ExecutorFabric(){
    
  }
  
  public static QueryExecutor getExecutor(Connection con, String queryText){
    return MysqlExecutor.getInstance(con,queryText);
  }
}
