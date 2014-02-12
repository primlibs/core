/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.select;

import com.prim.core.AbstractApplication;
import com.prim.core.model.DinamicModel;
import com.prim.core.model.Model;
import com.prim.core.model.ModelFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * класс, в котором собраны статические методы для получения данных
 *
 * @author Rice Pavel
 */
public class DataAccess {

  private DataAccess() {
  }

  public static List<Model> getModelsByParams(AbstractApplication app, String modelName, Map<String, Object> params) throws Exception {
    if (params.isEmpty()) {
      return new ArrayList();
    } else {
      Table table = TableSelectFactory.getTable(app, modelName);
      Select sel = TableSelectFactory.getSelect(app);
      sel.select(table).from(table);
      for (String key : params.keySet()) {
        Object value = params.get(key);
        if (value == null) {
          sel.and(table.get(key).isNull());
        } else {
          sel.and(table.get(key).eq(value));
        }
      }
      boolean ok = sel.executeSelect(app.getConnection());
      if (!ok) {
        throw new Exception(sel.getError().toString());
      }
      List<DinamicModel> dmList = sel.getDinamicList();
      List<Model> modelList = new ArrayList();
      for (DinamicModel dm : dmList) {
        Model model = ModelFactory.getModel(app, modelName);
        model.set(dm.getParams());
        modelList.add(model);
      }
      return modelList;
    }
  }
}
