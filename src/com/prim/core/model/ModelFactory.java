/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.model;

import com.prim.core.AbstractApplication;
import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.FieldFabric;
import com.prim.core.modelStructure.Structure;
import com.prim.core.modelStructure.StructureFabric;
import com.prim.core.warehouse.DataTypes;
import java.util.ArrayList;
import java.util.Map;


/**
 * создает экземпляры моделей
 *
 * @author User1
 */
public class ModelFactory {

  //public static String message = "";
  
  /**
   * объект приложения
   */
  AbstractApplication app;

  /**
   *
   * @param app объект приложения
   */
  public ModelFactory(AbstractApplication app) {
    if (app != null) {
      this.app = app;
    }
  }
  
  public static Model getModel(AbstractApplication app, String tableAlias) throws Exception {
    ModelFactory mf = new ModelFactory(app);
    return mf.getModel(tableAlias);
  }

  /**
   * получить модель
   *
   * @param tableAlias название модели
   * @return
   * @throws Exception
   */
  public Model getModel(String tableAlias) throws Exception {
    //Structure structure = ModelStructureKeeper.getInstance(app).getStructure(tableAlias);
    Structure structure = app.getKeeper().getModelStructureKeeper().getStructure(tableAlias);
    if (structure == null) {
      throw new Exception("Model structure is null " + tableAlias);
    }
    return new ModelObject(structure, app);
  }

  public static DinamicModel getDinamicModel(Structure structure) {
    return DinamicModelObject.getInstance(structure);
  }

  public static DinamicModel getDinamicModel(Map<String, Field> structure, Map<String, Object> params) throws CloneNotSupportedException {
    Structure struct = StructureFabric.getStructure(null, null, null, null, false, false, structure, null);
    DinamicModel dm = DinamicModelObject.getInstance(struct);
    dm.set(params);
    return dm;
  }
  
  /**
   * возвращает копию переданной модели, к которой прибавлены новый поля
   * @param model модель
   * @param fieldsNames названия новых полей
   * @return
   * @throws CloneNotSupportedException 
   */
  public static DinamicModel copyModelWithNewFields(DinamicModel model, String ... fieldsNames) throws CloneNotSupportedException {
    Map<String, Field> fields = model.getStructure().getCloneFields();
    for (String fieldName: fieldsNames) {
      fields.put(fieldName, FieldFabric.getField(fieldName, fieldName, "", false, false, DataTypes.CHAR));
    }
    return getDinamicModel(fields, model.getParams());
  }
  
}
