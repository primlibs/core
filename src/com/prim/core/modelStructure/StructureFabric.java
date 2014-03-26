/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.modelStructure;

import com.prim.core.representation.Xml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.prim.support.filterValidator.entity.ValidatorAbstract;

/**
 *
 * @author User
 */
final public class StructureFabric {

  public static Structure getStructure(String tableName, String name, String alias, String primary, boolean system, boolean fileWork, Map<String, Field> fields, List<Unique> uniqueList) throws CloneNotSupportedException {
    return StructureObject.valueOf(tableName, name, alias, primary, system, fileWork, fields, uniqueList);
  }

  private StructureFabric() {
  }

  /*
  public static Structure getStructureFromXml(Element structureElement) throws CloneNotSupportedException, Exception {

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
      try{
        Unique unique = UniqueObject.getUniqueFromXml(uniqueElement);
        uniqueList.add(unique);
      }catch(Exception e){
        throw new Exception("Ошибка чтения структуры уникальных полей в "+name);
      }
      
    }

    return getStructure(tableName, name, alias, primary, system, fileWork, fields, uniqueList);
  }
  */
}
