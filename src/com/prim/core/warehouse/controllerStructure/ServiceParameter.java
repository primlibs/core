/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.controllerStructure;

import com.prim.support.enums.DataTypes;
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
  private DataTypes dataType = DataTypes.CHAR;
  boolean array = false;

  public ServiceParameter(String name) {
        this.name = name;
  }
  
  ServiceParameter(String alias, ControllerOrigin origin,String name, boolean mandatory, DataTypes dataType, boolean array) {
    this.alias = alias;
    this.origin = origin;
    this.name = name;
    this.mandatory = mandatory;
    this.dataType = dataType;
    this.array = array;
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
    if (mandatoryString != null && mandatoryString.trim().equalsIgnoreCase("true")) {
      mandatory = true;
    }
    
    boolean array = false;
    String arrayString = primXml.getValue(elem, "array");
    if (arrayString != null && arrayString.trim().equalsIgnoreCase("true")) {
      array = true;
    }
    
    DataTypes dataType = DataTypes.CHAR;
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
    return new ServiceParameter(alias, origin, name, mandatory, dataType, array);    
  }

  public boolean isArray() {
    return array;
  }

  public void setArray(boolean array) {
    this.array = array;
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
    primXml.createElement(doc, parameter, "array", array);
    primXml.createElement(doc, parameter, "dataType", dataType);
  }
  
}
