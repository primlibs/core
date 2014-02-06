/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.model;

import com.prim.core.modelStructure.Structure;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *
 * @author User
 */
public interface Model {

  /**
   * сохранение записи. то есть, добавление в таблицу новой записи, либо
   * обновление существующей.
   *
   * @return
   */
  public Boolean save() throws Exception;
  
  /**
   * возвращает текст последнего sql-запроса
   * @return 
   */
  public String getLastQueryText();

  /**
   *
   * @return разрешена ли работа с файлами
   */
  public Boolean getFileWork();

  /**
   *
   * @return алиас первичного ключа
   */
  public String getPrimaryAlias();

  /**
   * возвращает значение первичного ключа
   *
   * @return
   */
  public Object getPrimary();

  /**
   * выполняет поиск в БД по первичному ключу. устанавливает параметры модели. Не загружается информация о файлах модели
   *
   * @return
   * @throws Exception
   */
  Boolean findByPrimary() throws Exception;

  /**
   * выполняет поиск в БД по первичному ключу. устанавливает параметры модели. Не загружается информация о файлах модели
   *
   * @param onlyActive искать только активные
   * @return - успешно ли выполнился поиск
   */
  public Boolean findByPrimary(Boolean onlyActive) throws Exception;

  /**
   * валидировать все параметры
   *
   * @param validatePrimary - валидировать ли первичный ключ
   * @param update - если true - то это запрос на обновление (update), если
   * false - то это запрос на вставку (insert)
   * @return - валидны ли все параметры
   */
  public Boolean validateAllFields(Boolean validatePrimary, boolean update) throws Exception;

  /**
   * возвращает часть строки запроса на update или insert
   *
   * @return
   */
  String prepareSaveParams() throws CloneNotSupportedException;

  /**
   * возвращает ошибки
   *
   * @return
   */
  public List<String> getError();

  /**
   * возвращает массив с информацией о файлах
   *
   * @return
   */
  public List<Map<String, Object>> getFileArray();

  /**
   * получить параметр модели
   *
   * @param name название параметра
   * @return значение параметра
   */
  public Object get(String name);

  /**
   *
   * @return все параметры модели
   * @throws CloneNotSupportedException
   */
  public Map<String, Object> getParams() throws CloneNotSupportedException;

  /**
   * установить параметр модели
   *
   * @param name название параметра
   * @param value значение параметра
   */
  public void set(String name, Object value);

  /**
   * установить все параметры модели
   *
   * @param map
   */
  public void set(Map<String, Object> map);

  /**
   * загружает информацию о файлах модели
   *
   * @return
   * @throws Exception
   */
  public void createFillesInfo() throws Exception;

  /**
   * получить объект dinamicModel
   * @return
   * @throws CloneNotSupportedException 
   */
  public DinamicModel getDinamicModel() throws CloneNotSupportedException;

  /**
   * удаление записи
   *
   * @return
   */
  public Boolean delete() throws Exception;

  /**
   * определяет, системная ли модель
   *
   * @return
   */
  public Boolean isModelSystem();

  /**
   * удалить все файлы данной модели
   *
   * @return
   */
  public Boolean deleteAllFiles() throws Exception;

  /**
   * удалить файл по его ИД
   *
   * @param fileId - ИД файла
   * @return - удален ли файл
   * @throws Exception
   */
  public Boolean deleteFile(Integer fileId) throws Exception;

  /**
   * сохранить новый файл, который является копией файла, расположенного по
   * указанному пути
   *
   * @param path - полный путь к файлу
   * @param fileName - название, под которым файл был загружен
   * @param userId - ИД пользователя, загрузившего файл
   * @param date - дата загрузки файла
   * @return - удалось ли сохранение
   * @throws Exception
   */
  public Boolean copyFile(String path, String fileName, Integer userId, Date date) throws Exception;

  /**
   * получить файл
   *
   * @param fileId ид файла
   * @return
   */
  public File getFile(String fileId);

  /**
   * сохранение нового файла с заданным контентом в виде строки
   *
   * @param name - имя файла
   * @param content - содержимое файла
   * @return - успешно ли добавился файл
   * @throws Exception
   */
  public Boolean saveFile(String name, String content, Integer userId, Date date) throws Exception;

  /**
   * сохранение нового файла с заданным контентом в виде массива байтов
   *
   * @param name - имя файла
   * @param content - содержимое файла
   * @return - успешно ли добавился файл
   * @throws Exception
   */
  public Boolean saveFile(String name, byte[] content, Integer userId, Date date) throws Exception;

   /**
   * получить все файлы модели в архиве zip
   *
   * @return
   * @throws Exception
   */
  public byte[] doZip() throws Exception;

  /**
   * применить валидацию и фильтрацию к значению одного поля
   *
   * @param name название поля
   * @return успешно ли прошла валидация
   */
  public Boolean validateField(String name);

  /**
   * возвращает клон ассоциированной структуры
   *
   * @return
   */
  public Structure getStructure() throws CloneNotSupportedException;

  /**
   * Отдает контент файла в виде байт потока
   */
  public byte[] getFileContentByte(String fileId);
  
  /**
   * id последнего сохраненного файла
   *
   * @return
   */
  public int getLastFileId();
}
