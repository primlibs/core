/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.controllerStructure;

import com.prim.support.ToXml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.prim.support.primXml;

/**
 *
 * @author Кот
 */
public class StructureController implements ToXml {

  public static final String ELEMENT_NAME = "StructureController";
  
  private Map<String, ControllerMethod> controllersMethods = new HashMap<String, ControllerMethod>();
  private String alias = "";
  private String description = "";
  private String name = "";

  public static StructureController getFromXml(Element elem) {
    String alias = elem.getElementsByTagName("alias").item(0).getChildNodes().item(0).getNodeValue();
    String description = elem.getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue();
    String name = elem.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();

    Map<String, ControllerMethod> methods = new HashMap();
    Element methodsElement = (Element) elem.getElementsByTagName("methods").item(0);
    NodeList methodNodeList = methodsElement.getElementsByTagName(ControllerMethod.ELEMENT_NAME);
    for (int i = 0; i < methodNodeList.getLength(); i++) {
      Element methodElement = (Element) methodNodeList.item(i);
      ControllerMethod method = ControllerMethod.getFromXml(methodElement);
      methods.put(method.getName(), method);
    }
    return new StructureController(alias, description, name, methods);

  }
  
  StructureController() {
  }
  
  StructureController(String alias, String description, String name, Map<String, ControllerMethod> controllersMethods) {
    this.alias = alias;
    this.description = description;
    this.controllersMethods = controllersMethods;
    this.name = name;
  }

  public ControllerMethod getMethod(String str) {
    return controllersMethods.get(str);
  }

  public void setMethod(String name, ControllerMethod cm) {
    controllersMethods.put(name, cm);
    cm.setName(name);
  }

  public void deleteMethod(String name) {
    //renders.config.Controller.out2.print("name");
    if (controllersMethods.containsKey(name)) {
      //renders.config.Controller.out2.print("exists2");
      controllersMethods.remove(name);
    }
  }

  public Map<String, ControllerMethod> getControllersMethods() {
    return controllersMethods;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void getSelfInXml(Document doc, Element controllerElement) throws Exception {
    primXml.createElement(doc, controllerElement, "alias", alias);
    primXml.createElement(doc, controllerElement, "description", description);
    primXml.createElement(doc, controllerElement, "name", name);
    Element sl=primXml.createEmptyElement(doc, controllerElement, "methods");
    for (ControllerMethod method : controllersMethods.values()) {
      Element methodElement =primXml.createEmptyElement(doc, sl, ControllerMethod.ELEMENT_NAME);
      method.getSelfInXml(doc, methodElement);
    }
  }
  
}
