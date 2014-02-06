/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.pair;

import com.prim.support.primXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Кот
 */
final public class SequenceObject implements Sequence {

  /**
   * имя секвенции
   */
  final private String name;
  /**
   * название объекта
   */
  final private String appObjectName;
  /**
   * название метода
   */
  final private String appMethodName;
  /**
   * название рендера true
   */
  final private String trueRender;
  /**
   * название рендера false
   */
  final private String falseRender;
  /**
   * редирект true
   */
  final private String trueRedirect;
  /**
   * редирект false
   */
  final private String falseRedirect;
  /**
   * параметры true редиректа
   */
  final private String trueRedirectParams;
  /**
   * параметры false редиректа
   */
  final private String falseRedirectParams;

  private SequenceObject(String name, String appObjectName, String appMethodName, String trueRender, String falseRender, String trueRedirect, String falseRedirect, String trueRedirectParams, String falseRedirectParams) {
    this.appObjectName = (appObjectName == null ? "" : appObjectName);
    this.appMethodName = (appMethodName == null ? "" : appMethodName);
    this.trueRender = (trueRender == null ? "" : trueRender);
    this.falseRender = (falseRender == null ? "" : falseRender);
    this.trueRedirect = (trueRedirect == null ? "" : trueRedirect);
    this.falseRedirect = (falseRedirect == null ? "" : falseRedirect);
    this.trueRedirectParams = (trueRedirectParams == null ? "" : trueRedirectParams);
    this.falseRedirectParams = (falseRedirectParams == null ? "" : falseRedirectParams);
    this.name = (name == null ? "" : name);
  }

  public static SequenceObject getInstance(String name, String appObjectName, String appMethodName, String trueRender, String falseRender, String trueRedirect, String falseRedirect, String trueRedirectParams, String falseRedirectParams) {
    return new SequenceObject(name, appObjectName, appMethodName, trueRender, falseRender, trueRedirect, falseRedirect, trueRedirectParams, falseRedirectParams);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getTrueRender() {
    return trueRender;
  }

  @Override
  public String getFalseRender() {
    return falseRender;
  }

  @Override
  public String getAppObjectName() {
    return appObjectName;
  }

  @Override
  public String getAppMethodName() {
    return appMethodName;
  }

  @Override
  public String getTrueRedirect() {
    return trueRedirect;
  }

  @Override
  public String getFalseRedirect() {
    return falseRedirect;
  }

  @Override
  public String getTrueRedirectParams() {
    return (trueRedirectParams != null ? trueRedirectParams : "");
  }

  @Override
  public String getFalseRedirectParams() {
    return (falseRedirectParams != null ? falseRedirectParams : "");
  }

  @Override
  public void getSelfInXml(Document doc, Element sequense) throws Exception {
    
    primXml.createElement(doc, sequense, "name", name);
    
    if (appObjectName != null) {
      primXml.createElement(doc, sequense, "appObjectName", appObjectName);
    }
    if (appMethodName != null) {
      primXml.createElement(doc, sequense, "appMethodName", appMethodName);
    }
    if (trueRender != null) {
      primXml.createElement(doc, sequense, "trueRender", trueRender);
    }
    if (falseRender != null) {
      primXml.createElement(doc, sequense, "falseRender", falseRender);
    }
    if (trueRedirect != null) {
      primXml.createElement(doc, sequense, "trueRedirect", trueRedirect);
    }
    if (falseRedirect != null) {
      primXml.createElement(doc, sequense, "falseRedirect", falseRedirect);
    }
    if (trueRedirectParams != null) {
      primXml.createElement(doc, sequense, "trueRedirectParams", trueRedirectParams);
    }
    if (falseRedirectParams != null) {
      primXml.createElement(doc, sequense, "falseRedirectParams", falseRedirectParams);
    }
  }

  @Override
  public Sequence clone() {
    return getInstance(name, appObjectName, appMethodName, trueRender, falseRender, trueRedirect, falseRedirect, trueRedirectParams, falseRedirectParams);
  }

  public static Sequence getSequenseFromXml(Element elem) throws CloneNotSupportedException, Exception {
    String name = elem.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();

    String appObjectName = null;
    if (elem.getElementsByTagName("appObjectName").item(0) != null
            && elem.getElementsByTagName("appObjectName").item(0).getChildNodes().item(0) != null) {
      appObjectName = elem.getElementsByTagName("appObjectName").item(0).getChildNodes().item(0).getNodeValue();
    }

    String appMethodName = null;
    if (elem.getElementsByTagName("appMethodName").item(0) != null
            && elem.getElementsByTagName("appMethodName").item(0).getChildNodes().item(0) != null) {
      appMethodName = elem.getElementsByTagName("appMethodName").item(0).getChildNodes().item(0).getNodeValue();
    }

    String trueRender = null;
    if (elem.getElementsByTagName("trueRender").item(0) != null
            && elem.getElementsByTagName("trueRender").item(0).getChildNodes().item(0) != null) {
      trueRender = elem.getElementsByTagName("trueRender").item(0).getChildNodes().item(0).getNodeValue();
    }

    String falseRender = null;
    if (elem.getElementsByTagName("falseRender").item(0) != null
            && elem.getElementsByTagName("falseRender").item(0).getChildNodes().item(0) != null) {
      falseRender = elem.getElementsByTagName("falseRender").item(0).getChildNodes().item(0).getNodeValue();
    }

    String trueRedirect = null;
    if (elem.getElementsByTagName("trueRedirect").item(0) != null
            && elem.getElementsByTagName("trueRedirect").item(0).getChildNodes().item(0) != null) {
      trueRedirect = elem.getElementsByTagName("trueRedirect").item(0).getChildNodes().item(0).getNodeValue();
    }

    String falseRedirect = null;
    if (elem.getElementsByTagName("falseRedirect").item(0) != null
            && elem.getElementsByTagName("falseRedirect").item(0).getChildNodes().item(0) != null) {
      falseRedirect = elem.getElementsByTagName("falseRedirect").item(0).getChildNodes().item(0).getNodeValue();
    }

    String trueRedirectParams = null;
    if (elem.getElementsByTagName("trueRedirectParams").item(0) != null
            && elem.getElementsByTagName("trueRedirectParams").item(0).getChildNodes().item(0) != null) {
      trueRedirectParams = elem.getElementsByTagName("trueRedirectParams").item(0).getChildNodes().item(0).getNodeValue();
    }

    String falseRedirectParams = null;
    if (elem.getElementsByTagName("falseRedirectParams").item(0) != null
            && elem.getElementsByTagName("falseRedirectParams").item(0).getChildNodes().item(0) != null) {
      falseRedirectParams = elem.getElementsByTagName("falseRedirectParams").item(0).getChildNodes().item(0).getNodeValue();
    }

    return getInstance(name, appObjectName, appMethodName, trueRender, falseRender, trueRedirect, falseRedirect, trueRedirectParams, falseRedirectParams);
  }
}
