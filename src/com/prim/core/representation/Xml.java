/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.representation;

import com.prim.core.controller.ActionResult;
import com.prim.core.model.DinamicModel;
import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.FieldFabric;
import com.prim.core.modelStructure.Structure;
import com.prim.core.modelStructure.StructureFabric;
import com.prim.core.modelStructure.Unique;
import com.prim.core.modelStructure.UniqueObject;
import com.prim.core.pair.Pair;
import com.prim.core.pair.PairObject;
import com.prim.core.pair.Sequence;
import com.prim.core.pair.SequenceObject;
import com.prim.support.MyString;
import com.prim.support.enums.DataTypes;
import com.prim.support.filterValidator.entity.ValidatorAbstract;
import com.prim.support.primXml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * запись и чтение объектов в виде xml
 *
 * @author Rice Pavel
 */
public class Xml {

  private Xml() {
  }

  public static void fieldToXml(Document doc, Element fieldElement, Field field) throws Exception {
    primXml.createElement(doc, fieldElement, "name", field.getName());
    primXml.createElement(doc, fieldElement, "alias", field.getAlias());
    primXml.createElement(doc, fieldElement, "appName", field.getAppName());
    if (field.getDef() != null) {
      primXml.createElement(doc, fieldElement, "def", field.getDef());
    }
    primXml.createElement(doc, fieldElement, "relations", (MyString.NotNull(field.getRelations()) ? field.getRelations() : null));
    primXml.createElement(doc, fieldElement, "mandatory", field.isMandatory());
    primXml.createElement(doc, fieldElement, "updatable", field.isUpdatable());
    primXml.createElement(doc, fieldElement, "editable", field.isEditable());
    primXml.createElement(doc, fieldElement, "type", field.getType());
    primXml.createElement(doc, fieldElement, "value", field.getValue());
    Element el = primXml.createEmptyElement(doc, fieldElement, "validatorList");
    for (ValidatorAbstract va : field.getValidatorList()) {
      Element vl = primXml.createEmptyElement(doc, el, "validator");
      primXml.createElement(doc, vl, "name", va.getClass());
      Element params = primXml.createEmptyElement(doc, vl, "parameters");
      for (String name : va.getParameters().keySet()) {
        Element pr = primXml.createEmptyElement(doc, params, "parameter");
        primXml.createElement(doc, pr, "name", name);
        primXml.createElement(doc, pr, "value", va.getParameters().get(name));
      }
    }
  }

  public static Field fieldFromXml(Element fieldElement) throws CloneNotSupportedException, Exception {

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
      for (DataTypes t : DataTypes.values()) {
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
      if (relations.equals("null")) {
        relations = null;
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

    return FieldFabric.getField(name, alias, appName, mandatory, updatable, dataType, relations, def, validators, editable);
  }

  public static void structureToXml(Document doc, Element root, Structure struct) throws Exception {
    primXml.createElement(doc, root, "tableName", struct.getTableName());
    primXml.createElement(doc, root, "tableAlias", struct.getTableAlias());
    primXml.createElement(doc, root, "primaryAlias", struct.getPrimaryAlias());
    primXml.createElement(doc, root, "name", struct.getName());
    primXml.createElement(doc, root, "system", struct.isSystem());
    primXml.createElement(doc, root, "fileWork", struct.isFileWork());

    Element fiel = primXml.createEmptyElement(doc, root, "fields");
    for (Field fd : struct.getFields().values()) {
      Element fdd = primXml.createEmptyElement(doc, fiel, "field");
      //fd.getSelfInXml(doc, fdd);
      Xml.fieldToXml(doc, fdd, fd);
    }
    Element ul = primXml.createEmptyElement(doc, root, "uniqueList");
    for (Unique un : struct.getUniqueList()) {
      Element unn = primXml.createEmptyElement(doc, ul, "unique");
      Xml.uniqueToXml(doc, unn, un);
      //un.getSelfInXml(doc, unn);
    }
  }

  public static Structure structureFromXml(Element structureElement) throws CloneNotSupportedException, Exception {

    String tableName = structureElement.getElementsByTagName("tableName").item(0).getChildNodes().item(0).getNodeValue();
    String name = structureElement.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
    String alias = structureElement.getElementsByTagName("tableAlias").item(0).getChildNodes().item(0).getNodeValue();
    String primary = structureElement.getElementsByTagName("primaryAlias").item(0).getChildNodes().item(0).getNodeValue();
    Boolean system = Boolean.parseBoolean(structureElement.getElementsByTagName("system").item(0).getChildNodes().item(0).getNodeValue());
    Boolean fileWork = Boolean.parseBoolean(structureElement.getElementsByTagName("fileWork").item(0).getChildNodes().item(0).getNodeValue());

    Element fieldsElement = (Element) structureElement.getElementsByTagName("fields").item(0);
    NodeList fieldNodeList = fieldsElement.getElementsByTagName("field");
    Map<String, Field> fields = new HashMap();
    for (int i = 0; i < fieldNodeList.getLength(); i++) {
      Element fieldElement = (Element) fieldNodeList.item(i);
      //Field field = FieldFabric.getFieldFromXml(fieldElement);
      Field field = Xml.fieldFromXml(fieldElement);
      fields.put(field.getAlias(), field);
    }

    Element uniquesElement = (Element) structureElement.getElementsByTagName("uniqueList").item(0);
    List<Unique> uniqueList = new ArrayList();
    NodeList uniqueNodeList = uniquesElement.getElementsByTagName("unique");
    for (int i = 0; i < uniqueNodeList.getLength(); i++) {
      Element uniqueElement = (Element) uniqueNodeList.item(i);
      try {
        //Unique unique = UniqueObject.getUniqueFromXml(uniqueElement);
        Unique unique = Xml.uniqueFromXml(uniqueElement);
        uniqueList.add(unique);
      } catch (Exception e) {
        throw new Exception("Ошибка чтения структуры уникальных полей в " + name);
      }

    }

    return StructureFabric.getStructure(tableName, name, alias, primary, system, fileWork, fields, uniqueList);
  }

  public static void uniqueToXml(Document doc, Element uniqueElement, Unique unique) throws Exception {
    primXml.createElement(doc, uniqueElement, "checkDeleted", unique.isCheckDeleted());
    for (String name : unique.getFieldNames()) {
      primXml.createElement(doc, uniqueElement, "name", name);
    }
  }

  public static Unique uniqueFromXml(Element uniqueElement) throws CloneNotSupportedException, Exception {

    Boolean checkDeleted = Boolean.parseBoolean(uniqueElement.getElementsByTagName("checkDeleted").item(0).getChildNodes().item(0).getNodeValue());

    NodeList namesNodeList = uniqueElement.getElementsByTagName("name");
    List<String> names = new ArrayList();
    for (int i = 0; i < namesNodeList.getLength(); i++) {
      Element nameElement = (Element) namesNodeList.item(i);
      String name = nameElement.getChildNodes().item(0).getNodeValue();
      names.add(name);
    }

    return UniqueObject.valueOf(names, checkDeleted);
  }

  public static void pairToXml(Document doc, Element pairElement, Pair pair) throws Exception {
    primXml.createElement(doc, pairElement, "object", pair.getObject());
    primXml.createElement(doc, pairElement, "action", pair.getAction());
    primXml.createElement(doc, pairElement, "def", pair.getDef());

    primXml.createElement(doc, pairElement, "controllerName", pair.getControllerName());
    primXml.createElement(doc, pairElement, "byWebController", pair.isByWebController());

    Element seqMap = primXml.createEmptyElement(doc, pairElement, "sequenceMap");
    for (Sequence ss : pair.getSequenceClone().values()) {
      Element seq = primXml.createEmptyElement(doc, seqMap, "sequense");
      //ss.getSelfInXml(doc, seq);
      Xml.sequenceToXml(doc, seq, ss);
    }

    Element paArr = primXml.createEmptyElement(doc, pairElement, "pairArray");
    for (Pair pp : pair.getPairsClone()) {
      Element pr = primXml.createEmptyElement(doc, paArr, "pair");
      //pp.getSelfInXml(doc, pr);
      pairToXml(doc, pr, pp);
    }

  }

  public static PairObject pairFromXml(Element elem) throws CloneNotSupportedException, Exception {

    NodeList list = elem.getElementsByTagName("controllerName");
    String controllerName = "";
    if (list.getLength() > 0) {
      NodeList child = list.item(0).getChildNodes();
      if (child.getLength() > 0) {
        controllerName = child.item(0).getNodeValue();
      }
    }

    list = elem.getElementsByTagName("byWebController");
    boolean byWebController = false;
    if (list.getLength() > 0) {
      NodeList child = list.item(0).getChildNodes();
      if (child.getLength() > 0) {
        byWebController = Boolean.parseBoolean(child.item(0).getNodeValue());
      }
    }

    String object = elem.getElementsByTagName("object").item(0).getChildNodes().item(0).getNodeValue();
    String action = elem.getElementsByTagName("action").item(0).getChildNodes().item(0).getNodeValue();
    Boolean def = Boolean.parseBoolean(elem.getElementsByTagName("def").item(0).getChildNodes().item(0).getNodeValue());

    Element sequenceMapElement = (Element) elem.getElementsByTagName("sequenceMap").item(0);
    NodeList sequenceNodeList = sequenceMapElement.getElementsByTagName("sequense");
    Map<String, Sequence> sequence = new HashMap();
    for (int i = 0; i < sequenceNodeList.getLength(); i++) {
      Element sequenceElement = (Element) sequenceNodeList.item(i);
      //Sequence seq = SequenceObject.getSequenseFromXml(sequenceElement);
      Sequence seq = Xml.sequenseFromXml(sequenceElement);
      sequence.put(seq.getName(), seq);
    }

    Element pairArrayElement = (Element) elem.getElementsByTagName("pairArray").item(0);
    NodeList pairNodeList = pairArrayElement.getChildNodes();
    List<Pair> pairs = new ArrayList();
    for (int i = 0; i < pairNodeList.getLength(); i++) {
      Element pairElement = (Element) pairNodeList.item(i);
      if (pairElement.getTagName().equals("pair")) {
        //PairObject pr = PairObject.getPairFromXml(pairElement);
        PairObject pr = Xml.pairFromXml(pairElement);
        pairs.add(pr);
      }
    }

    PairObject self = PairObject.getInstance(object, action, def, sequence, pairs, null, byWebController, controllerName);

    for (Pair pp : self.getAllParentСlone()) {
      pp.setParent(self);
    }
    return self;

  }

  public static void sequenceToXml(Document doc, Element sequenseElement, Sequence seq) throws Exception {

    primXml.createElement(doc, sequenseElement, "name", seq.getName());

    if (seq.getAppObjectName() != null) {
      primXml.createElement(doc, sequenseElement, "appObjectName", seq.getAppObjectName());
    }
    if (seq.getAppMethodName() != null) {
      primXml.createElement(doc, sequenseElement, "appMethodName", seq.getAppMethodName());
    }
    if (seq.getTrueRender() != null) {
      primXml.createElement(doc, sequenseElement, "trueRender", seq.getTrueRender());
    }
    if (seq.getFalseRender() != null) {
      primXml.createElement(doc, sequenseElement, "falseRender", seq.getFalseRender());
    }
    if (seq.getTrueRedirect() != null) {
      primXml.createElement(doc, sequenseElement, "trueRedirect", seq.getTrueRedirect());
    }
    if (seq.getFalseRedirect() != null) {
      primXml.createElement(doc, sequenseElement, "falseRedirect", seq.getFalseRedirect());
    }
    if (seq.getTrueRedirectParams() != null) {
      primXml.createElement(doc, sequenseElement, "trueRedirectParams", seq.getTrueRedirectParams());
    }
    if (seq.getFalseRedirectParams() != null) {
      primXml.createElement(doc, sequenseElement, "falseRedirectParams", seq.getFalseRedirectParams());
    }
  }

  public static Sequence sequenseFromXml(Element elem) throws CloneNotSupportedException, Exception {
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

    return SequenceObject.getInstance(name, appObjectName, appMethodName, trueRender, falseRender, trueRedirect, falseRedirect, trueRedirectParams, falseRedirectParams);
  }

  public static void dinamicModelToXml(Document doc, Element rootElement, DinamicModel dm) throws Exception {

    Element paramsElement = primXml.createEmptyElement(doc, rootElement, "params");
    for (String name : dm.getParams().keySet()) {
      Object value = dm.getParams().get(name);
      Element parameterElement = primXml.createEmptyElement(doc, paramsElement, "parameter");
      primXml.createElement(doc, parameterElement, "name", name);
      primXml.createElement(doc, parameterElement, "value", value);
    }

    Element innerModelsElement = primXml.createEmptyElement(doc, rootElement, "innerParams");
    for (DinamicModel innerDm : dm.getInnerDinamicModel()) {
      Element modElement = primXml.createEmptyElement(doc, innerModelsElement, "model");
      //model.getSelfInXml(doc, mod);
      dinamicModelToXml(doc, modElement, innerDm);
    }
  }

  public static void actionResultToXml(Document doc, Element root, ActionResult ar) throws Exception {
    primXml.createElement(doc, root, "status", ar.getStatus());

    Element er = primXml.createEmptyElement(doc, root, "errors");
    for (String error : ar.getErrors()) {
      primXml.createElement(doc, er, "error", error);
    }
    // сообщения
    Element msg = primXml.createEmptyElement(doc, root, "messages");
    for (String message : ar.getMessageList()) {
      primXml.createElement(doc, msg, "message", message);
    }

    // параметры
    Element selfParams = primXml.createEmptyElement(doc, root, "selfParams");
    for (String key : ar.getParams().keySet()) {
      Object parameter = ar.getParams().get(key);
      Element paramet = primXml.createEmptyElement(doc, selfParams, "parameter");
      primXml.createElement(doc, msg, "paramName", key);
      primXml.createElement(doc, msg, "value", (parameter != null ? parameter.toString() : "null"));
    }

    // параметры DinamicModelList
    Element innerParams = primXml.createEmptyElement(doc, root, "innerParams");
    for (DinamicModel model : ar.getDinamicArrayList()) {
      Element mod = primXml.createEmptyElement(doc, selfParams, "model");
      //model.getSelfInXml(doc, mod);
      Xml.dinamicModelToXml(doc, mod, model);
    }
  }
}
