package com.prim.core.warehouse.modelKeeper;

import com.prim.core.AbstractApplication;
import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.FieldFabric;
import com.prim.core.modelStructure.Structure;
import com.prim.core.modelStructure.StructureFabric;
import com.prim.core.modelStructure.Unique;
import com.prim.core.representation.Xml;
import com.prim.support.enums.DataTypes;
import com.prim.support.filterValidator.entity.ValidatorAbstract;
import java.util.HashMap;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import prim.warehouse.OptionsSingleton;

/**
 * класс для получения структуры системных моделей
 *
 * @author Pavel Rice
 */
public class SystemModelStructure {

  /**
   * хранит структуру системных моделей
   */
  private Map<String, Structure> systemStructureMap = new HashMap<String, Structure>();
  

  /**
   * в конструкторе устанавливается структура системных моделей
   *
   * @param app объект приложения
   * @throws Exception
   */
  public SystemModelStructure(AbstractApplication app) throws Exception {
    /* try {  */
    //получить путь до конфига  
    String config = app.getAppUserDataConfigPath() + "/systemModel.xml";
    // прочитать файл конфига
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    File file = new File(config);
    Document doc = builder.parse(file);
    NodeList list = doc.getChildNodes();
    Element root = (Element) list.item(0);
    // получить все дочерние узлы типа model
    NodeList models = root.getElementsByTagName("model");
    for (int i = 0; i < models.getLength(); i++) {
      Element modelNode = (Element) models.item(i);
      NamedNodeMap modelAttr = modelNode.getAttributes();
      String modelAlias = modelAttr.getNamedItem("alias").getNodeValue();

      // получить все дочерние узлы типа field
      NodeList fieldsNodeList = modelNode.getElementsByTagName("field");
      Map<String,Field> mp = new HashMap();
      //message += "1111";
      for (int j = 0; j < fieldsNodeList.getLength(); j++) {
        
        Element fieldElement = (Element) fieldsNodeList.item(j);
        // создать поле, добавить к модели
        NamedNodeMap fieldAttr = fieldElement.getAttributes();
        String fieldAlias = fieldAttr.getNamedItem("name").getNodeValue();
        String def = (fieldAttr.getNamedItem("def") == null ? null : fieldAttr.getNamedItem("def").getNodeValue());

        
        // получить все дочерние узлы типа validator
        NodeList validators = fieldElement.getElementsByTagName("validator");
        List<ValidatorAbstract> valList= new ArrayList();
        for (int k = 0; k < validators.getLength(); k++) {
          Element validatorNode = (Element) validators.item(k);
          NamedNodeMap validatorsAttr = validatorNode.getAttributes();
          String validatorName = validatorsAttr.getNamedItem("name").getNodeValue();
          ValidatorAbstract validator = ValidatorAbstract.getValidator(validatorName);
          validator.setTerminate(Boolean.parseBoolean(validatorsAttr.getNamedItem("terminate").getNodeValue()));
          // создать валидатор, добавить к полю 
          // получить дочерние узлы типа parameter
          NodeList parameters = validatorNode.getChildNodes();
          for (int n = 0; n < parameters.getLength(); n++) {
            Node parameterNode = parameters.item(n);
            if (parameterNode.getNodeName().equals("parameter")) {
              // добавить параметр к валидатору
              NamedNodeMap parameterAttr = parameterNode.getAttributes();
              validator.setParameter(parameterAttr.getNamedItem("name").getNodeValue(), parameterAttr.getNamedItem("value").getNodeValue());
            }
          }
          valList.add(validator);
        }
        
        DataTypes dataType = DataTypes.getTypeByString(fieldAttr.getNamedItem("type").getNodeValue());
        
        Field field = FieldFabric.getField(
                fieldAlias,
                fieldAlias,
                fieldAttr.getNamedItem("appname").getNodeValue(),
                Boolean.parseBoolean(fieldAttr.getNamedItem("mandatory").getNodeValue()),
                Boolean.parseBoolean(fieldAttr.getNamedItem("updatable").getNodeValue()),
                dataType,
                fieldAttr.getNamedItem("relations").getNodeValue(),
                def,
                valList,
                false);
        mp.put(fieldAlias, field);
        //message += fieldAlias;
      }

      Structure structure = StructureFabric.getStructure(
              modelAttr.getNamedItem("name").getNodeValue(),
              modelAttr.getNamedItem("name").getNodeValue(),
              modelAlias,
              modelAttr.getNamedItem("primary").getNodeValue(),
              true,
              true,
              mp,
              new ArrayList<Unique>());
      //message += mp.toString() + "<br/>" + structure.getFields() + "<br/><br/>";
      
      systemStructureMap.put(modelAlias, structure);
      
    }
    /*
     } catch (Exception exc) {
     throw new Exception("исключение в классе SystemModelStructure при чтении конфигурационного файла");
     } */
  }

  /**
   * возвращает структуру системных моделей
   *
   * @return массив, в котором содержатс объекты структуры системных моделей
   */
  public Map<String, Structure> getSystemConfig() {
    return systemStructureMap;
  }
  
  /*
   * программная регистрация новой структуры без создания базы данных
   */
  
  
  public SystemModelStructure.Responce register(String name, Structure str,AbstractApplication app) throws Exception{
      SystemModelStructure.Responce res=Responce.SUCSESS;
      //проверить не содержится ли уже
      if(systemStructureMap.containsKey(name)){
          res=Responce.ERROR_MODEL_IS_REGISRERED;
      }else{
          String config = app.getAppUserDataConfigPath() + "/systemModel.xml";
            // прочитать файл конфига
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            File file = new File(config);
            Document doc = builder.parse(file);
            NodeList list = doc.getChildNodes();
            Element root = (Element) list.item(0);
            Element structure = doc.createElement("structure");
            root.appendChild(structure);
            Xml.structureToXml(doc,structure, str);
            Transformer t=TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(config)));
      }
      return res;
  }  
  
  
  public static enum Responce{
      SUCSESS,ERROR_MODEL_IS_REGISRERED
  }
  
}
