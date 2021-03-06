/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse.cron;

import com.prim.core.AbstractApplication;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;


/**
 *
 * @author User
 */
public class CronSingleton {

  /**
   * экземпляр класса
   */
  static private volatile CronSingleton instance;
  private ArrayList<CronObject> cronlist = new ArrayList<CronObject>();
  private String configFilePath;
  private AbstractApplication app;
  
  /**
   * получить экземпляр класса
   *
   * @return
   */
  public static CronSingleton getInstance(AbstractApplication app) {
    if (instance == null) {
      synchronized (CronSingleton.class) {
        if (instance == null) {
          instance = new CronSingleton(app);
        }
      }
    }
    return instance;
  }

  public static CronSingleton getInstanceNew(AbstractApplication app) {
    instance = new CronSingleton(app);
    return instance;
  }
  
  private CronSingleton(AbstractApplication app) {
    FileInputStream fis = null;
    ObjectInputStream oin = null;
    try {
      this.app = app;
      configFilePath = this.app.getAppUserDataConfigPath() + "/cron.out";
      fis = new FileInputStream(configFilePath);
      oin = new ObjectInputStream(fis);
      cronlist = (ArrayList<CronObject>) oin.readObject();
    } catch (Exception e) {
      cronlist = new ArrayList<CronObject>();
    } finally {
      try {
      if (fis != null) {
        fis.close();
      } 
      } catch (Exception e) { }
      
      try {
      if (oin != null) {
        oin.close();
      } 
      } catch (Exception e) { }
      
    }
  }

  public ArrayList<CronObject> getCronlist() {
    return cronlist;
  }

  public CronObject getCronObject(Integer index) {
    return cronlist.get(index);
  }

  public Integer setCronObject() {
    CronObject co = new CronObject();
    cronlist.add(co);
    return cronlist.indexOf(co);
  }

  public ArrayList<CronObject> getListOnDate(Date date) {
    ArrayList<CronObject> res = new ArrayList<CronObject>();
    for (CronObject co : cronlist) {
      if (co.checkDate(date) == true) {
        res.add(co);
      }
    }
    return res;
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
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    try {
      fos = new FileOutputStream(configFilePath);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(cronlist);
      res = true;
    } catch (FileNotFoundException e) {
      res = false;
    } catch (IOException e) {
      res = false;
    } finally {
      if (fos != null) {
        fos.close();
      }
      if (oos != null) {
        oos.flush();
        oos.close();
      }
    }
    return res;
  }
}
