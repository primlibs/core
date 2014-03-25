/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.controllerStructure;

import com.prim.support.ToXml;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.prim.support.primXml;

/**
 *
 * @author Кот
 */
public class ControllerMethod implements Serializable, ToXml {

  public static final String ELEMENT_NAME = "ControllerMethod";
  static final long serialVersionUID = 12345L;
  private List<ControllerService> serviceList = new ArrayList<ControllerService>();
  private String alias = "";
  private String description = "";
  private String name = "";
  private Boolean hidden = false;
  private boolean free = false;

  public ControllerMethod() {
  }

  ControllerMethod(String alias, String description, String name, Boolean hidden, List<ControllerService> serviceList, boolean free) {
    this.alias = alias;
    this.description = description;
    this.hidden = hidden;
    this.serviceList = serviceList;
    this.name = name;
    this.free = free;
  }

  public boolean isFree() {
    return free;
  }

  public void setFree(boolean free) {
    this.free = free;
  }

  
  
  public static ControllerMethod getFromXml(Element elem) {

    String alias = elem.getElementsByTagName("alias").item(0).getChildNodes().item(0).getNodeValue();
    String description = elem.getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue();
    String name = primXml.getValue(elem, "name");
    Boolean hidden = Boolean.parseBoolean(elem.getElementsByTagName("hidden").item(0).getChildNodes().item(0).getNodeValue());
    boolean free = false;
    String freeString = primXml.getValue(elem, "free");
    if (freeString != null & !freeString.isEmpty()) {
      free = Boolean.parseBoolean(freeString);
    }

    List<ControllerService> serviceList = new ArrayList();
    Element serviceListElement = (Element) elem.getElementsByTagName("serviceList").item(0);
    NodeList servicesNodeList = serviceListElement.getElementsByTagName("controllerService");
    for (int i = 0; i < servicesNodeList.getLength(); i++) {
      Element serviceElement = (Element) servicesNodeList.item(i);
      ControllerService cs = ControllerService.getFromXml(serviceElement);
      serviceList.add(cs);
    }
    return new ControllerMethod(alias, description, name, hidden, serviceList, free);

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getHidden() {
    return hidden;
  }

  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  public ControllerService getContollerService(Integer ind) {
    return serviceList.get(ind);
  }

  public ControllerService addControllerService() {
    ControllerService cs = new ControllerService();
    serviceList.add(cs);
    return cs;
  }

  public Boolean removeControllerService(Integer Ind) {
    Boolean result = false;
    serviceList.remove(Ind);
    return result;
  }

  public void up(Integer Ind) {
    if (Ind > 0) {
      ControllerService cs = serviceList.get(Ind - 1);
      serviceList.set(Ind - 1, serviceList.get(Ind));
      serviceList.set(Ind, cs);
    }
  }

  public void down(Integer Ind) {
    if (Ind < serviceList.size() - 1) {
      ControllerService cs = serviceList.get(Ind + 1);
      serviceList.set(Ind + 1, serviceList.get(Ind));
      serviceList.set(Ind, cs);
    }
  }

  public List<ControllerService> getServiceList() {
    return serviceList;
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
  public void getSelfInXml(Document doc, Element controllerMethod) throws Exception {
    primXml.createElement(doc, controllerMethod, "alias", alias);
    primXml.createElement(doc, controllerMethod, "description", description);
    primXml.createElement(doc, controllerMethod, "name", name);
    primXml.createElement(doc, controllerMethod, "hidden", hidden);
    primXml.createElement(doc, controllerMethod, "free", free);
    Element sl = primXml.createEmptyElement(doc, controllerMethod, "serviceList");
    for (ControllerService cs : serviceList) {
      Element cer = primXml.createEmptyElement(doc, sl, "controllerService");
      cs.getSelfInXml(doc, cer);
    }
  }
}
