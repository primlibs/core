package com.prim.core.model;

import com.prim.support.ToXml;
import com.prim.core.modelStructure.Structure;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author User
 */
public interface ExpandedDinamicModel {

  
  public ExpandedDinamicModel clone() throws CloneNotSupportedException;

  /**
   * получить ошибки
   * @return 
   */
  public List<String> getError();

  /**
   * добавить сообщение к ошибкам
   * @param err 
   */
  void addError(String err);

  /**
   * добавить сообщение к ошибкам
   * @param err 
   */
  void addError(String... err);

  /**
   * добавить сообщения к ошибкам
   * @param err 
   */
  void addError(List<String> err);

  /**
   * получить информацию о файлах
   * @return 
   */
  public List<Map<String, Object>> getFileArray();

  /**
   * добавить файл к массиву файлов
   * @param fileInfo 
   */
  void addFileToArray(Map<String, Object> fileInfo);

  /**
   * добавить файлы к массиву файлов
   * @param fileArray 
   */
  public void addFileArray(List<Map<String, Object>> fileArray);
  
  /**
   * очистить массив файлов
   */
  void clearFileArray();

  /**
   * получить вложенные DinamicModel
   * @return
   * @throws CloneNotSupportedException 
   */
  public List<ExpandedDinamicModel> getInnerDinamicModel()throws CloneNotSupportedException;

  /**
   * добавить вложенную DinamicModel
   * @param dm
   * @throws CloneNotSupportedException 
   */
  void addInner(ExpandedDinamicModel dm)throws CloneNotSupportedException;

  /**
   * очистить внутреннме DinamicModel
   */
  void clearInner();
  
  /**
   * получить параметр
   * @param name название параметра
   * @return 
   */
  public Object get(String name);
  
  /**
   * получить все параметры
   * @return
   * @throws CloneNotSupportedException 
   */
  public Map<String, Object> getParams()throws CloneNotSupportedException;
  
  /**
   * установить параметр
   * @param name название параметра
   * @param value значение параметра
   */
  void set(String name,Object value);
  
  /**
   * установить параметры
   * @param map 
   */
  void set(Map<String, Object> map);
  
  /**
   * получить объект структуры
   * @return 
   */
  Structure getStructure();
  
  /**
   * получить клон объекта структуры
   * @return
   * @throws CloneNotSupportedException 
   */
  public Structure getStructureClone() throws CloneNotSupportedException;
  
  
}
