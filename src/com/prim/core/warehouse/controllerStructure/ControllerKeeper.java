/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.controllerStructure;

import com.prim.core.AbstractApplication;
import com.prim.core.db.ExecutorFabric;
import com.prim.core.db.QueryExecutor;
import com.prim.core.select.Select;
import com.prim.core.select.TableSelectFactory;
import com.prim.support.MyString;
import com.prim.support.primXml;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//import prim.warehouse.OptionsSingleton;

/**
 * Класс определяющий набор контроллеров
 *
 * @author Кот
 */
public final class ControllerKeeper {

  private Map<String, StructureController> controllers = new HashMap<String, StructureController>();
  private AbstractApplication app;
  private List<String> errors = new ArrayList();

  private ControllerKeeper(AbstractApplication app) throws SQLException, Exception {
    this.app = app;
    setDataFromBase();
  }

  static public ControllerKeeper getInstance(AbstractApplication app) throws Exception {
    return new ControllerKeeper(app);
  }

  public List<String> getErrors() {
    return errors;
  }

  /**
   * Вернет список контроллеров
   */
  public Map<String, StructureController> getControllers() {
    return controllers;
  }

  /**
   * возвращает контроллер
   *
   * @param cs
   * @return
   */
  public StructureController getController(String cs) {
    return controllers.get(cs);
  }

  /**
   * Вернет 1 метод
   */
  public ControllerMethod getOneControllerMethod(String controllerName, String controllerAction) {
    ControllerMethod method = null;
    StructureController rs = controllers.get(controllerName);
    if (rs != null) {
      method = rs.getMethod(controllerAction);
    }
    return method;
  }

  /**
   * Добавит метод в синглтон, вернет boolean
   */
  public Boolean addMethod(String controllerName, String controllerAction) throws Exception {
    ControllerMethod cm = new ControllerMethod();
    Boolean result = false;
    if (controllers.containsKey(controllerName)) {
      if (controllers.get(controllerName).getMethod(controllerAction) == null) {
        controllers.get(controllerName).setMethod(controllerAction, cm);
        result = true;
      }
    } else {
      StructureController clr = new StructureController();
      clr.setMethod(controllerAction, cm);
      controllers.put(controllerName, clr);
      result = true;
    }
    return result;
  }

  /**
   * если в структуре нет контроллера с таким именем - добавляет новый
   * контроллер
   *
   * @param controllerName
   */
  public void addController(String controllerName) {
    if (!controllers.containsKey(controllerName)) {
      controllers.put(controllerName, new StructureController());
    }
  }

  /**
   * есть ли такой метод в контроллере
   *
   * @param controllerName
   * @param methodName
   * @return
   */
  public boolean hasMethod(String controllerName, String methodName) {
    StructureController cnt = controllers.get(controllerName);
    if (cnt != null) {
      if (cnt.getControllersMethods().containsKey(methodName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * установить метод
   *
   * @param controllerName
   * @param methodName
   * @param method
   */
  public void addMethod(String controllerName, String methodName, ControllerMethod method) {
    StructureController cnt = controllers.get(controllerName);
    if (cnt != null) {
      cnt.setMethod(methodName, method);
    }
  }

  /**
   * удаляет контроллер по имени
   *
   * @param controllerName
   * @return
   * @throws Exception
   */
  public Boolean deleteController(String controllerName) throws Exception {
    Boolean result = false;
    if (controllers.containsKey(controllerName)) {
      controllers.remove(controllerName);
      result = deleteControllerFromBase(controllerName);
    }
    return result;
  }

  /**
   * Удалить метод контроллера
   */
  public Boolean deleteMethod(String controllerName, String controllerAction) throws Exception {
    Boolean result = false;
    //renders.config.Controller.out2.print(controllerName);
    StructureController clr = controllers.get(controllerName);
    if (clr != null) {
      //renders.config.Controller.out2.print("exists");
      clr.deleteMethod(controllerAction);
      result = true;
    }
    return result;
  }


  /**
   * Обновить controllersMethod из базы данных
   */
  public void setDataFromBase() throws SQLException, Exception {
    controllers = new HashMap<String, StructureController>();
    Connection connection = getConnection();
    String queryText = " select c.controller_id, c.controller_name, c.controller_alias, c.controller_description, "
            + " m.method_name, m.method_id, m.obj_text "
            + " from controllers c "
            + " LEFT JOIN methods m on m.controller_id=c.controller_id "
            + " where c.active_to is null"
            + " and m.active_to is null";
    Statement st = connection.createStatement();
    ResultSet rs = st.executeQuery(queryText);
    while (rs.next()) {
      // извлечь название контроллера
      String cName = rs.getString("controller_name");
      // извлечь название метода
      try {
        String mName = rs.getString("method_name");
        // извлечь объект
        String xml = rs.getString("obj_text");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = builder.parse(is);
        NodeList list = doc.getChildNodes();
        Element methodElement = (Element) list.item(0);
        ControllerMethod cMethod = ControllerMethod.getFromXml(methodElement);

        if (controllers.containsKey(cName)) {
          controllers.get(cName).setMethod(mName, cMethod);
        } else {
          StructureController clr = new StructureController();
          clr.setAlias(rs.getString("controller_alias"));
          clr.setDescription(rs.getString("controller_description"));
          clr.setName(cName); 
          clr.setMethod(mName, cMethod);
          controllers.put(cName, clr);
        }
      } catch (Exception e) {
        //controllers = new HashMap<String, StructureController>();
        errors.add(MyString.getStackExeption(e));
      }
    }
    connection.close();
  }

 
  /**
   * Сохранение контроллера и всей вложенной структуры
   *
   * @param controllerName
   * @return
   * @throws Exception
   */
  public Boolean saveController(String controllerName) throws Exception {
    Boolean result = false;
    Connection connection = getConnection();
    Select sel = new TableSelectFactory(app).getSelect();
    String qt = "select controller_id from controllers where controller_name="
            + " " + sel.validateParameter(controllerName, true) + " and active_to is null";
    QueryExecutor qe = ExecutorFabric.getExecutor(connection, qt);
    qe.select();
    List<Map<String, Object>> hs = qe.getResultList();
    if (hs.isEmpty()) {
      qt = "insert into controllers (controller_name,controller_alias,controller_description,active_from) "
              + "values (" + sel.validateParameter(controllerName, true) + ", "
              + " " + sel.validateParameter(controllerName, true) + ", "
              + " " + sel.validateParameter(controllerName, true) + " "
              + ",now())";
      qe = ExecutorFabric.getExecutor(connection, qt);
      result = qe.update();
    } else {
      Integer i = Integer.parseInt(hs.get(0).get("controller_id").toString());
      qt = "update controllers set controller_name=" + sel.validateParameter(controllerName, true) + ", "
              + " controller_alias=" + sel.validateParameter((!controllers.get(controllerName).getAlias().equals("") ? controllers.get(controllerName).getAlias() : controllerName), true) + ","
              + " controller_description=" + sel.validateParameter((!controllers.get(controllerName).getDescription().equals("") ? controllers.get(controllerName).getDescription() : controllerName), true) + ""
              + " where controller_id=" + sel.validateParameter(i, true) + " ";
      qe = ExecutorFabric.getExecutor(connection, qt);
      qe.update();
    }
    qt = "select controller_id from controllers where controller_name=" + sel.validateParameter(controllerName, true) + " and active_to is null";
    qe = ExecutorFabric.getExecutor(connection, qt);
    qe.select();
    hs = qe.getResultList();
    if (!hs.isEmpty()) {
      Integer i = Integer.parseInt(hs.get(0).get("controller_id").toString());
      for (String MethodName : controllers.get(controllerName).getControllersMethods().keySet()) {
        ControllerMethod cm = controllers.get(controllerName).getMethod(MethodName);
        result = saveMethodsInController(i, MethodName, cm);
      }
      qt = "select method_id,method_name from methods where controller_id=" + sel.validateParameter(i, true) + " and active_to is null";
      qe = ExecutorFabric.getExecutor(connection, qt);
      qe.select();
      for (Map<String, Object> hsb : qe.getResultList()) {
        if (controllers.get(controllerName).getMethod(hsb.get("method_name").toString()) == null) {
          Boolean res = deleteMethodFromBase(Integer.parseInt(hsb.get("method_id").toString()));
        } else {
        }
      }
    }
    connection.close();
    return result;
  }

 
  private Boolean saveMethodsInController(int controllerId, String methodName, ControllerMethod cm) throws Exception {
    Boolean result = false;
    Connection connection = getConnection();
    Select sel = new TableSelectFactory(app).getSelect();
    String qt = "select m.method_id from controllers c, methods m "
            + "where c.controller_id=" + sel.validateParameter(controllerId, true) + " "
            + " and c.active_to is null "
            + " and c.controller_id=m.controller_id "
            + " and m.active_to is null "
            + " and m.method_name=" + sel.validateParameter(methodName, true) + "";
    QueryExecutor qe = ExecutorFabric.getExecutor(connection, qt);
    qe.select();
    List<Map<String, Object>> hs = qe.getResultList();
    if (hs.isEmpty()) {
      PreparedStatement st2 = connection.prepareStatement("insert into methods (method_name, controller_id, active_from, obj_text, hidden) values (?,?,now(), ?,?)");
      st2.setString(1, methodName);
      st2.setInt(2, controllerId);

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      Element root = doc.createElement("root");
      doc.appendChild(root);
      Element cmet = primXml.createEmptyElement(doc, root, "ControllerMethod");
      cm.getSelfInXml(doc, cmet);

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      CharArrayWriter ch=new CharArrayWriter();
      StreamResult res = new StreamResult(ch);
      transformer.transform(source, res);

      st2.setString(3, ch.toString());

      if (cm.getHidden() != null && cm.getHidden() == true) {
        st2.setInt(4, 1);
      } else {
        st2.setNull(4, Types.NULL);
      }
      int n = st2.executeUpdate();
      if (n != 0) {
        result = true;
      }
    } else {
      PreparedStatement st2 = connection.prepareStatement("update methods set obj_text =?, hidden=? where method_id=?");

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      Element root = doc.createElement("root");
      doc.appendChild(root);
      Element cmet = primXml.createEmptyElement(doc, root, "ControllerMethod");
      cm.getSelfInXml(doc, cmet);

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      CharArrayWriter ch=new CharArrayWriter();
      StreamResult res = new StreamResult(ch);
      transformer.transform(source, res);
      st2.setString(1, ch.toString());

      if (cm.getHidden() != null && cm.getHidden() == true) {
        st2.setInt(2, 1);
      } else {
        st2.setNull(2, Types.NULL);
      }
      Integer i = Integer.parseInt(hs.get(0).get("method_id").toString());
      st2.setInt(3, i);
      int n = st2.executeUpdate();
      if (n != 0) {
        result = true;
      }
    }
    connection.close();
    return result;
  }

  private Boolean deleteControllerFromBase(String controllerName) throws Exception {
    Select sel = new TableSelectFactory(app).getSelect();
    Connection connection = getConnection();
    String qt = "update controllers set active_to =now() where active_to is null and controller_name=" + sel.validateParameter(controllerName, true) + "";
    QueryExecutor qe = ExecutorFabric.getExecutor(connection, qt);
    Boolean result = qe.update();
    connection.close();
    return result;
  }

  private Boolean deleteMethodFromBase(Integer methodId) throws Exception {
    Connection connection = getConnection();
    Select sel = new TableSelectFactory(app).getSelect();
    String qt = "update methods set active_to =now() where method_id=" + sel.validateParameter(methodId, true) + "";
    QueryExecutor qe = ExecutorFabric.getExecutor(connection, qt);
    Boolean result = qe.update();
    connection.close();
    return result;
  }

  private Connection getConnection() throws Exception, ClassNotFoundException {
    app.getDbDriver();
    String url = app.getDbDriverUrl() + "://" + app.getDbHost() + "/" + app.getDbName();
    Properties properties = new Properties();
    properties.setProperty("user", app.getDbUser());
    properties.setProperty("password", app.getDbPass());
    properties.setProperty("useUnicode", "true");
    properties.setProperty("characterEncoding", app.getDbEncoding());
    return DriverManager.getConnection(url, properties);
  }
}
