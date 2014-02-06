/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.controllerStructure;

import com.prim.support.ToXml;
import com.prim.support.primXml;
import java.io.Serializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Кот
 */
public class ServiceParameter implements Serializable, ToXml {

  static final long serialVersionUID = 12345L;
  private String alias;
  private String name;
  private ControllerOrigin origin = ControllerOrigin.Request;
  

  public ServiceParameter(String name) {
        this.name = name;
  }
  
  ServiceParameter(String alias, ControllerOrigin origin,String name) {
    this.alias = alias;
    this.origin = origin;
    this.name = name;
  }

  public static ServiceParameter getFromXml(Element elem) {
    
    String alias = elem.getElementsByTagName("alias").item(0).getChildNodes().item(0).getNodeValue();
    String originString = elem.getElementsByTagName("origin").item(0).getChildNodes().item(0).getNodeValue();
    String name=alias;
    try{
      name=elem.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
    }catch(Exception e){
      name=alias;
    }
    
    ControllerOrigin origin = ControllerOrigin.Request;
    for (ControllerOrigin org: ControllerOrigin.values()) {
      if (org.toString().equals(originString)) {
        origin = org;
        break;
      }
    }
    return new ServiceParameter(alias, origin,name);    
  }
  
  public String getAlias() {
    return alias;
  }
  
  public String getName() {
    return name;
  }

  public ControllerOrigin getOrigin() {
    return origin;
  }

  public void setOrigin(ControllerOrigin origin) {
    this.origin = origin;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public void getSelfInXml(Document doc, Element parameter) throws Exception {
    primXml.createElement(doc, parameter, "alias", alias);
    primXml.createElement(doc, parameter, "name", name);
    primXml.createElement(doc, parameter, "origin", origin);
  }
  
}
