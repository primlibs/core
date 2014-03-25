/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.modelStructure;

import com.prim.support.enums.DataTypes;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.prim.support.filterValidator.entity.ValidatorAbstract;
import com.prim.support.primXml;

/**
 *
 * @author User
 */
final public class FieldFabric {

  public static Field getField(String name, String alias, String appName, Boolean mandatory, Boolean updatable, DataTypes type,
          String relations, String def, List<ValidatorAbstract> validators, Boolean editable) throws CloneNotSupportedException {
    return FieldObject.valueOf(name, alias, appName, mandatory, updatable, type, relations, def, validators, editable);
  }
  
  public static Field getField(String name, String alias, String appName, Boolean mandatory, Boolean updatable, DataTypes type) throws CloneNotSupportedException {
    return FieldObject.valueOf(name, alias, appName, mandatory, updatable, type, null, null, null, null);
  }
  

  public static Field getFieldFromXml(Element fieldElement) throws CloneNotSupportedException, Exception {

    String name = fieldElement.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();

    String alias = fieldElement.getElementsByTagName("alias").item(0).getChildNodes().item(0).getNodeValue();
    
    String appName = primXml.getValue(fieldElement, "appName");

    Boolean mandatory = false;
    if (fieldElement.getElementsByTagName("mandatory").item(0) != null) {
      mandatory = Boolean.parseBoolean(fieldElement.getElementsByTagName("mandatory").item(0).getChildNodes().item(0).getNodeValue());
    }

    Boolean updatable = false;
    if (fieldElement.getElementsByTagName("updatable").item(0) != null) {
      updatable = Boolean.parseBoolean(fieldElement.getElementsByTagName("updatable").item(0).getChildNodes().item(0).getNodeValue());
    }

    DataTypes dataType = DataTypes.CHAR;
    String type = fieldElement.getElementsByTagName("type").item(0).getChildNodes().item(0).getNodeValue();
    if (type != null) {
      for (DataTypes t: DataTypes.values()) {
        if (type.equalsIgnoreCase(t.toString())) {
          dataType = t;
          break;
        }
      }
    }
    String relations = null;
    if (fieldElement.getElementsByTagName("relations").item(0) != null
            && fieldElement.getElementsByTagName("relations").item(0).getChildNodes() != null
            && fieldElement.getElementsByTagName("relations").item(0).getChildNodes().item(0) != null) {
      relations = fieldElement.getElementsByTagName("relations").item(0).getChildNodes().item(0).getNodeValue();
      if(relations.equals("null")){
        relations=null;
      }
    }

    String def = null;    
    if (fieldElement.getElementsByTagName("def").item(0) != null && fieldElement.getElementsByTagName("def").item(0).getChildNodes().item(0) != null) {
      def = fieldElement.getElementsByTagName("def").item(0).getChildNodes().item(0).getNodeValue();
    }

    Element validatorListElement = (Element) fieldElement.getElementsByTagName("validatorList").item(0);

    NodeList validatorNodeList = validatorListElement.getElementsByTagName("validator");
    List<ValidatorAbstract> validators = new ArrayList();
    for (int i = 0; i < validatorNodeList.getLength(); i++) {
      Element validatorElement = (Element) validatorNodeList.item(i);
      String validatorName = validatorElement.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
      String[] s = validatorName.split("\\.");
      validatorName = s[s.length - 1];
      ValidatorAbstract validator = ValidatorAbstract.getValidator(validatorName);

      Element parametersElement = (Element) validatorElement.getElementsByTagName("parameters").item(0);
      NodeList parameterNodeList = parametersElement.getElementsByTagName("parameter");
      for (int j = 0; j < parameterNodeList.getLength(); j++) {
        Element parameterElement = (Element) parameterNodeList.item(j);
        String paramName = parameterElement.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
        String value = parameterElement.getElementsByTagName("value").item(0).getChildNodes().item(0).getNodeValue();
        validator.setParameter(paramName, value);
      }
      validators.add(validator);
    }

    Boolean editable = Boolean.parseBoolean(fieldElement.getElementsByTagName("editable").item(0).getChildNodes().item(0).getNodeValue());

    return getField(name, alias, appName, mandatory, updatable, dataType, relations, def, validators, editable);
  }

  private FieldFabric() {
  }
}
