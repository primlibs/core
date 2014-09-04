/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.pair;

import com.prim.core.AbstractApplication;
import com.prim.core.pair.Pair;
import com.prim.core.pair.PairObject;
import com.prim.core.representation.Xml;
import com.prim.support.MyString;
import com.prim.support.primXml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Кот
 */
public class PairKeeper {

  /**
   * экземпляр класса
   */
  Pair pair;
  private AbstractApplication app;
  private List<String> errors = new ArrayList();

  public void setApp(AbstractApplication app) {
    this.app = app;
  }
  
  /**
   * получить экземпляр класса
   *
   * @return
   */
  public static PairKeeper getInstance(AbstractApplication app) {
    return new PairKeeper(app);
  }

  private PairKeeper(AbstractApplication app) {
    this.app = app;
    try {
      setPairsFromXml(app.getAppUserDataConfigPath() + "/pair.xml");
    } catch (Exception e) {
      reset();
    }
  }

  /**
   * возвращает главную пару
   *
   * @return
   */
  public Pair getPair() {
    return pair;
  }

  public void setPair(Pair pair) {
    this.pair = pair;
  }

  /**
   * существуте ли пара с такими objectName и actionName
   *
   * @param objectName
   * @param actionName
   * @return
   */
  public boolean containsPair(String objectName, String actionName) {
    boolean contains = false;
    if (pair != null) {
      contains = pair.containsPair(objectName, actionName);
    }
    return contains;
  }

  /**
   * найти одну пару
   *
   * @param object
   * @param action
   * @return
   */
  public Pair searchOnePair(String object, String action) {
    Pair p = null;
    if (pair != null) {
      p = pair.searchOne(object, action);
    }
    return p;
  }

  /**
   * удалить пару
   *
   * @param object
   * @param action
   */
  public void removePair(String object, String action) {
    pair.removePair(object, action);
  }

  /**
   * сохранить в файле коллекцию объектов
   *
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public Boolean SaveCollectionInFile() throws FileNotFoundException, IOException {
    Boolean res = false;
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      Element root = doc.createElement("root");
      doc.appendChild(root);
      Element pr = primXml.createEmptyElement(doc, root, "pair");
      //pair.getSelfInXml(doc, pr);
      Xml.pairToXml(doc, pr, pair); 

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);

      StreamResult result = new StreamResult(new File(app.getAppUserDataConfigPath() + "/pair.xml"));
      transformer.transform(source, result);
      res = true;
    } catch (FileNotFoundException e) {
      errors.add(MyString.getStackExeption(e));
      res = false;
    } catch (IOException e) {
      errors.add(MyString.getStackExeption(e));
      res = false;
    } catch (Exception e) {
      errors.add(MyString.getStackExeption(e));
      res = false;
    }
    return res;
  }

  /**
   * возвращает список всех пар, которые нужно вызвать для данных object, action
   *
   * @param object
   * @param action
   * @return
   */
  public List<Pair> getPairList(String object, String action) {
    pair.search(object, action);
    List<Pair> list = pair.getSearchListClone();
    if (list.isEmpty()) {
      list.add(pair);
    }
    return list;
  }

  /**
   * возвращает список пар, которые нужно вызвать для данных object, action. При
   * этом возвращаются только дочерние пары, а не родительские
   *
   * @param object
   * @param action
   * @return
   */
  public List<Pair> getChildrenPairList(String object, String action) {
    pair.searchChildren(object, action);
    List<Pair> list = pair.getSearchListClone();
    return list;
  }

  /**
   * добавить пару к главной паре
   *
   * @param object
   * @param action
   * @return
   */
  public void addPair(Pair pp) {
    pair.addPair(pp);
  }

  /**
   * рекурсивный метод, который возвращает список всех пар
   *
   * @return
   */
  public List<Pair> getAllPairs() {
    return pair.getAllPairsClone();
  }

  public void reset() {
    pair = PairObject.getInstance("app", "show", Boolean.TRUE, null, null, null, false, "");
  }

  private void setPairsFromXml(String path) throws Exception {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(path);

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbf.newDocumentBuilder();
      Document doc = builder.parse(fis);
      NodeList list = doc.getChildNodes();
      Element root = (Element) list.item(0);
      Element pairElement = (Element) root.getElementsByTagName("pair").item(0);
      //pair = PairObject.getPairFromXml(pairElement);
      pair = Xml.pairFromXml(pairElement);

    } catch (Exception e) {
      errors.add(MyString.getStackExeption(e));
      reset();
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
  }

  public List<String> getErrors() {
    return errors;
  }
}
