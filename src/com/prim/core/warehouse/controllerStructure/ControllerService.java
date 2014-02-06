/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.controllerStructure;

import com.prim.support.ToXml;
import java.io.Serializable;
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
public class ControllerService implements Serializable, ToXml {

  static final long serialVersionUID = 12345L;
  private String serviceName;
  private String serviceAction;
  private Map<String, ServiceParameter> innerParams = new HashMap<String, ServiceParameter>();
  private Map<String, ServiceParameter> outerParams = new HashMap<String, ServiceParameter>();

  

  
  public ControllerService() {
  }

  ControllerService(String serviceName, String serviceAction,
          Map<String, ServiceParameter> innerParams, Map<String, ServiceParameter> outerParams) {
    this.serviceName = serviceName;
    this.serviceAction = serviceAction;
    this.innerParams = innerParams;
    this.outerParams = outerParams;
  }

  public static ControllerService getFromXml(Element elem) {
    String serviceName = elem.getElementsByTagName("serviceName").item(0).getChildNodes().item(0).getNodeValue();
    String serviceAction = elem.getElementsByTagName("serviceAction").item(0).getChildNodes().item(0).getNodeValue();
    
    Map<String, ServiceParameter> innerParams = new HashMap();
    Element innerParamsElement = (Element) elem.getElementsByTagName("innerParams").item(0);
    NodeList innerParamsNodeList = innerParamsElement.getElementsByTagName("parameter");
    for (int i = 0; i < innerParamsNodeList.getLength(); i++) {
      Element parameterElement = (Element) innerParamsNodeList.item(i);
      ServiceParameter parameter = ServiceParameter.getFromXml(parameterElement);
      innerParams.put(parameter.getName(), parameter);
    }
    
    Map<String, ServiceParameter> outerParams = new HashMap();
    Element outerParamsElement = (Element) elem.getElementsByTagName("outerParams").item(0);
    NodeList outerParamsNodeList = outerParamsElement.getElementsByTagName("parameter");
    for (int i = 0; i < outerParamsNodeList.getLength(); i++) {
      Element parameterElement = (Element) outerParamsNodeList.item(i);
      ServiceParameter parameter = ServiceParameter.getFromXml(parameterElement);
      outerParams.put(parameter.getName(), parameter);
    }
    
    return new ControllerService(serviceName, serviceAction, innerParams, outerParams);
  }

  public String getServiceName() {
    return serviceName;
  }

  public String getServiceAction() {
    return serviceAction;
  }

  public Map<String, ServiceParameter> getInnerParams() {
    return innerParams;
  }

  public Map<String, ServiceParameter> getOuterParams() {
    return outerParams;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public void setServiceAction(String serviceAction) {
    this.serviceAction = serviceAction;
  }

  public ServiceParameter addInnerParams(String name) {
    ServiceParameter cp = new ServiceParameter(name);
    innerParams.put(name, cp);
    return cp;
  }

  public ServiceParameter addOuterParams(String name) {
    ServiceParameter cp = new ServiceParameter(name);
    outerParams.put(name, cp);
    return cp;
  }

  public void deleteInnerParams(String Name) {
    innerParams.remove(Name);
  }

  public void deleteOuterParams(String Name) {
    outerParams.remove(Name);
  }

  @Override
  public void getSelfInXml(Document doc, Element controllerService) throws Exception {
    primXml.createElement(doc, controllerService, "serviceName", serviceName);
    primXml.createElement(doc, controllerService, "serviceAction", serviceAction);
    Element innerP=primXml.createEmptyElement(doc, controllerService, "innerParams");
    for (ServiceParameter innerParameter : innerParams.values()) {
      Element parameter=primXml.createEmptyElement(doc, innerP, "parameter");
      innerParameter.getSelfInXml(doc, parameter);
    }
    
    Element outerP=primXml.createEmptyElement(doc, controllerService, "outerParams");
    for (ServiceParameter outerParameter : outerParams.values()) {
      Element parameter=primXml.createEmptyElement(doc, outerP, "parameter");
      outerParameter.getSelfInXml(doc, parameter);
    }
  }
}
