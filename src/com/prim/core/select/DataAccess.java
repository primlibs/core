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

    static public List<DinamicModel> searchByParams(AbstractApplication app, Table table, Parameter sort, OrdTypes ty, Integer limitFrom, Integer limit, Condition... cnd) throws Exception {
        List<DinamicModel> result = new ArrayList<DinamicModel>();
        Select sel = TableSelectFactory.getSelect(app, table);
        if (limit != null) {
            sel.setLimitRange(limit);
        }
        if (limitFrom != null) {
            sel.setLimitFrom(limitFrom);
        }
        sel.from(table);
        sel.and(table.getPrimary().isNotNull());
        sel.and(cnd);
        if (sort != null && ty != null) {
            sel.order(sort, ty);
        }
        sel.executeSelect(app.getConnection());
        if (!sel.getError().isEmpty()) {
            throw new Exception(sel.getError().toString());
        }
        result = sel.getDinamicList();
        return result;
    }

    static public List<DinamicModel> searchByParams(AbstractApplication app, Table table, Condition... cnd) throws Exception {
        return searchByParams(app, table, null, null, null, null, cnd);
    }
}
