/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.controller;

import java.util.ArrayList;
import java.util.List;
import com.prim.support.MyString;
/**
 * объект, который содержит в себе права пользователя и информацию о пользователе
 * @author Pavel Rice
 */
public class RightsObject{

  /**
   * ID пользователя
   */
  private final String userId;
  /**
   * логин
   */
  private String login="";
  /**
   * массив прав в форме controllerName+":"+methodName
   */
  private final List<String> rightList;

  public static RightsObject getInstance(String userId,List<String> rightList){
    return new RightsObject (userId,rightList);
  }
  
  public static RightsObject valueOf(RightsObject ro){
     RightsObject ro1=new RightsObject (ro.userId,ro.rightList);
     ro1.login=ro.login;
     return ro1;
  }
  
  /**
   * @param userId ИД пользователя
   * @param rightList массив прав в форме controller_name+":"+method_name
   */  
  private RightsObject (String userId,List<String> rightList){
    this.userId=userId;
    List<String> middleList=new ArrayList();
    if(rightList!=null){
      for(String s:rightList){
        if(MyString.NotNull(s)){
          middleList.add(s);
        }
      }
    }
    this.rightList=middleList;
  }
  
  /**
   * установить логин пользователя
   * @param login 
   */
  public void setLogin(String login) {
    if(login!=null){
      this.login = login;
    }
  }
  
  /**
   * получить userId
   * @return 
   */
  public String getUserId() {
    return userId;
  }

  public List<String> getRightListClone() {
    List<String> newRightsList = new ArrayList();
    for (String right: rightList) {
      newRightsList.add(right);
    }
    return newRightsList;
  }

  /**
   * получить логин
   * @return 
   */
  public String getLogin() {
    return login;
  }

  /**
   * есть ли у пользователя права на данный метод
   *
   * @param controllerName название контроллера
   * @param methodName название метода
   * @return
   */
  public boolean methodInRight(String controllerName, String methodName) {
    if(MyString.NotNull(controllerName)&&MyString.NotNull(methodName)){
      return rightList.contains(controllerName+":"+methodName);
    }
    return false;
  }
  
 
}
