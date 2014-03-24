/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.controllerStructure;

import com.prim.core.warehouse.DataTypes;
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
  private boolean mandatory;
  private DataTypes dataType;
  

  public ServiceParameter(String name) {
        this.name = name;
  }
  
  ServiceParameter(String alias, ControllerOrigin origin,String name, boolean mandatory, DataTypes dataType) {
    this.alias = alias;
    this.origin = origin;
    this.name = name;
    this.mandatory = mandatory;
    this.dataType = dataType;
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
    
    boolean mandatory = false;
    String mandatoryString = primXml.getValue(elem, "mandatory");
    if (mandatoryString != null && mandatoryString.trim().equals("true")) {
      mandatory = true;
    }
    DataTypes dataType = null;
    String dataTypeString = primXml.getValue(elem, "dataType");
    if (dataTypeString != null) {
      for (DataTypes type: DataTypes.values()) {
        if (dataTypeString.equalsIgnoreCase(type.toString())) {
          dataType = type;
          break;
        }
      }
    }
    
    ControllerOrigin origin = ControllerOrigin.Request;
    for (ControllerOrigin org: ControllerOrigin.values()) {
      if (org.toString().equals(originString)) {
        origin = org;
        break;
      }
    }
    return new ServiceParameter(alias, origin, name, mandatory, dataType);    
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public DataTypes getDataType() {
    return dataType;
  }

  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }

  public void setDataType(DataTypes dataType) {
    this.dataType = dataType;
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
    primXml.createElement(doc, parameter, "mandatory", mandatory);
    primXml.createElement(doc, parameter, "dataType", dataType);
  }
  
}
