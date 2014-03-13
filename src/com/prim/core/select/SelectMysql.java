/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.select;

import com.prim.core.db.ExecutorFabric;
import com.prim.core.db.QueryExecutor;
import com.prim.core.model.DinamicModel;
import com.prim.core.model.ModelFactory;
import com.prim.core.modelStructure.Field;
import com.prim.core.modelStructure.FieldFabric;
import com.prim.support.MyString;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * объект SQL-запроса
 *
 * @author Кот
 */
class SelectMysql implements Select {

    private String resultSelect = "";
    private Integer limitFrom = null;
    private Integer limitRange = null;
    private HashSet<String> errors = new HashSet<String>();
    private ArrayList<DinamicModel> dinamicList = new ArrayList<DinamicModel>();
    private Boolean from = false;
    private Boolean where = false;
    private Boolean group = false;
    private Boolean select = false;
    private Boolean and = false;
    private Boolean order = false;
    private Map<String, Field> structure = new LinkedHashMap<String, Field>();
    private Map<String, Table> tables = new HashMap<String, Table>();
    private ArrayList<Table> allSelectTables = new ArrayList<Table>();
    private Boolean cashable = false;
    private Double queryTime = 0.00;

    static Select getInstance(Table... tb) throws CloneNotSupportedException {
        if (tb == null) {
            return new SelectMysql();
        } else {
            return new SelectMysql(tb);
        }
    }

    private SelectMysql(Table tb) throws CloneNotSupportedException {
        for (Parameter par : tb.getParameters()) {
            select(par, par.getSelectAlias());
        }
        structure.putAll(tb.getStructure());
    }

    private SelectMysql(Table... tb) throws CloneNotSupportedException {
        for (Table tbs : tb) {
            select(tbs);
        }
    }

    private SelectMysql() {
    }

    /**
     * добавить в блок Select все параметры из таблицы
     *
     * @param tb таблица
     */
    @Override
    public SelectMysql select(Table tb) throws CloneNotSupportedException {
        for (Parameter par : tb.getParameters()) {
            select(par, par.getSelectAlias());
        }
        structure.putAll(tb.getStructure());
        return this;
    }

    @Override
    public Double getQueryTime() {
        return queryTime;
    }

    /**
     * добавить в блок Select все параметры из таблиц
     *
     * @param tb таблица
     */
    @Override
    public SelectMysql select(Table... tb) throws CloneNotSupportedException {
        for (Table tbs : tb) {
            select(tbs);
        }
        return this;
    }

    /**
     * добавить в блок Select все параметры из таблиц, при этом к названию
     * каждого параметра добавить алиас таблицы
     *
     * @param tb таблица
     */
    @Override
    public SelectMysql selectWithAliases(Table tb) throws CloneNotSupportedException {
        for (Parameter parameter : tb.getParameters()) {
            String paramAlias = tb.getModelTbAlias() + "_" + parameter.getSelectAlias();
            select(parameter, paramAlias);
        }
        return this;
    }

    /**
     * добавить в блок Select один параметр
     *
     * @param par параметр
     */
    @Override
    public SelectMysql select(Parameter par) throws CloneNotSupportedException {
        String selAlias = par.getSelectAlias();
        if (select == false) {
            resultSelect += " select ";
            resultSelect += par.getTable().getModelTbAlias() + "." + par.getRealName() + " " + selAlias + " ";
            select = true;
        } else {
            resultSelect += " ," + par.getTable().getModelTbAlias() + "." + par.getRealName() + " " + selAlias + " ";
        }
        structure.put(selAlias, par.getTable().getStructure().get(par.getModelAlias()));
        return this;
    }

    /**
     * добавить в блок Select distinct + один параметр
     *
     * @param par параметр
     */
    @Override
    public SelectMysql selectDistinct(Parameter par) throws CloneNotSupportedException {
        String selAlias = par.getSelectAlias();
        if (select == false) {
            resultSelect += " select ";
            resultSelect += " distinct " + par.getTable().getModelTbAlias() + "." + par.getRealName() + " " + selAlias + " ";
            select = true;
        } else {
            resultSelect += " , distinct " + par.getTable().getModelTbAlias() + "." + par.getRealName() + " " + selAlias + " ";
        }
        structure.put(selAlias, par.getTable().getStructure().get(par.getModelAlias()));
        return this;
    }

    @Override
    public SelectMysql select(Parameter... params) throws CloneNotSupportedException {
        for (Parameter par : params) {
            select(par);
        }
        return this;
    }

    /**
     * добавить в блок Select один параметр c новым псевдонимом
     *
     * @param par параметр
     * @param newAlias новый псевдоним
     */
    @Override
    public SelectMysql select(Parameter par, String newAlias) throws CloneNotSupportedException {
        String selAlias = par.getSelectAlias();
        if (newAlias != null && !"".equals(newAlias)) {
            selAlias = newAlias;
        }
        if (select == false) {
            resultSelect += " select ";
            resultSelect += par.getTable().getModelTbAlias() + "." + par.getRealName() + " " + selAlias + " ";
            select = true;
        } else {
            resultSelect += " ," + par.getTable().getModelTbAlias() + "." + par.getRealName() + " " + selAlias + " ";
        }
        structure.put(selAlias, par.getTable().getStructure().get(par.getModelAlias()));
        return this;
    }

    /**
     * добавить в блок Select один параметр, применить к нему агрегатную функцию
     *
     * @param par параметр
     * @param newAlias новый алиас параметра
     * @param agr агрегатная функция, которая будет применена к параметру
     */
    @Override
    public SelectMysql select(Parameter par, String newAlias, AgrTypes agr) throws CloneNotSupportedException {
        String selAlias = par.getSelectAlias();
        if (newAlias != null && !"".equals(newAlias)) {
            selAlias = newAlias;
        }
        if (select == false) {
            resultSelect += " select ";
            resultSelect += agr + "(" + par.getTable().getModelTbAlias() + "." + par.getRealName() + ") " + selAlias + " ";
            select = true;
        } else {
            resultSelect += " ," + agr + "(" + par.getTable().getModelTbAlias() + "." + par.getRealName() + ") " + selAlias + " ";
        }
        structure.put(selAlias, par.getTable().getStructure().get(par.getModelAlias()));
        return this;
    }

    /**
     * добавить в блок Select один параметр, применить к этому параметру
     * SQL-функцию
     *
     * @param par параметр
     * @param newAlias новый алиас параметра
     * @param func SQL-функция, которая будет применена к параметру
     */
    @Override
    public SelectMysql select(Parameter par, String newAlias, Function func) throws CloneNotSupportedException {
        String selAlias = par.getSelectAlias();
        if (newAlias != null && !"".equals(newAlias)) {
            selAlias = newAlias;
        }
        if (select == false) {
            resultSelect += " select ";
            resultSelect += func + "(" + par.getTable().getModelTbAlias() + "." + par.getRealName() + ") " + selAlias + " ";
            select = true;
        } else {
            resultSelect += " ," + func + "(" + par.getTable().getModelTbAlias() + "." + par.getRealName() + ") " + selAlias + " ";
        }
        structure.put(selAlias, par.getTable().getStructure().get(par.getModelAlias()));
        return this;
    }

    /**
     * добавить в блок Select константу
     *
     * @param constant константа
     * @param alias псевдоним
     */
    @Override
    public SelectMysql select(String constant, String alias) throws CloneNotSupportedException {
        if (select == false) {
            resultSelect += " select ";
            resultSelect += constant + " " + alias;
            select = true;
        } else {
            resultSelect += " ," + constant + " " + alias;
        }
        Field fd = FieldFabric.getField(alias, alias, alias, false, false, "char", null, null, null, null);
        structure.put(alias, fd);
        return this;
    }

    @Override
    public SelectMysql selectDistinct(Parameter par, String newAlias, AgrTypes agr) throws CloneNotSupportedException {
        String selAlias = par.getSelectAlias();
        if (newAlias != null && !"".equals(newAlias)) {
            selAlias = newAlias;
        }
        if (select == false) {
            resultSelect += " select ";
            resultSelect += agr + "(distinct " + par.getTable().getModelTbAlias() + "." + par.getRealName() + ") " + selAlias + " ";
            select = true;
        } else {
            resultSelect += " ," + agr + "(distinct " + par.getTable().getModelTbAlias() + "." + par.getRealName() + ") " + selAlias + " ";
        }
        structure.put(selAlias, par.getTable().getStructure().get(par.getModelAlias()));
        return this;
    }

    /**
     * добавить таблицы в блок From
     *
     * @param tb таблицы
     */
    @Override
    public SelectMysql from(Table... tb) {
        for (Table tbs : tb) {
            from(tbs);
        }
        return this;
    }

    /**
     * добавить таблицу в блок From
     *
     * @param tb таблица
     */
    @Override
    public SelectMysql fromWithIndex(Table tb, String... indexes) {
        tables.put(tb.getModelTbAlias(), tb);
        allSelectTables.add(tb);
        String index = "";
        Integer cnt = 0;
        for (String ind : indexes) {
            if (MyString.NotNull(ind)) {
                if (cnt == 0) {
                    index += ind;
                } else {
                    index += ", " + ind;
                }
                cnt++;
            }
        }
        if (cnt > 0) {
            index = "use index (" + index + ")";
        }
        if (from == false) {
            resultSelect += " from ";
            resultSelect += " " + tb.getRealName() + " " + tb.getModelTbAlias() + " " + index + " ";
            from = true;
        } else {
            resultSelect += " inner join " + tb.getRealName() + " " + tb.getModelTbAlias() + " " + index + " ";
        }
        return this;
    }

    @Override
    public SelectMysql from(Table tb) {
        fromWithIndex(tb);
        return this;
    }

    @Override
    public SelectMysql from(String str) {
        if (from == false) {
            resultSelect += " from ";
            from = true;
        } else {
            resultSelect += " inner join ";
        }
        resultSelect += str + " ";
        return this;
    }

    /**
     * добавить в блок from ещё один SQL-запрос (то есть подапрос)
     *
     * @param sl запрос
     * @param alias алиас подзапроса
     */
    @Override
    public SelectMysql from(SelectMysql sl, String alias) {
        allSelectTables.addAll(sl.getAllSelectTables());
        if (from == false) {
            resultSelect += " from ";
            resultSelect += " (" + sl.getPrepareSelect() + ") " + alias + " ";
            from = true;
        } else {
            resultSelect += " inner join (" + sl.getPrepareSelect() + ") " + alias + " ";
        }
        return this;
    }

    /**
     * добавить соединение таблиц типа LEFT JOIN
     *
     * @param first параметр из первой таблицы, учавствующей в соединении
     * @param second параметр из второй таблицы, учавствующей в соединении
     */
    @Override
    public SelectMysql leftJoin(Parameter first, Parameter second) throws CloneNotSupportedException {
        if (from == false) {
            tables.put(first.getTable().getModelTbAlias(), first.getTable());
            allSelectTables.add(first.getTable());
            //tables.put(second.getTable().getModelTbAlias(), second.getTable());
            resultSelect += " from " + first.getTable().getRealName() + " " + first.getTable().getModelTbAlias();
            resultSelect += " LEFT JOIN " + second.getTable().getRealName() + " " + second.getTable().getModelTbAlias();
            if (!second.getRealName().equals("id") && !second.getTable().isSystem()) {
                resultSelect += " use index (" + second.getRealName() + ") ";
            }
            resultSelect += " ON " + second.getTable().getModelTbAlias() + "." + second.getRealName() + " "
                    + " =" + first.getTable().getModelTbAlias() + "." + first.getRealName() + " ";
            resultSelect += getDefaultCondition(second.getTable());
            from = true;
        } else {
            //tables.put(second.getTable().getModelTbAlias(), second.getTable());
            resultSelect += " LEFT JOIN " + second.getTable().getRealName() + " " + second.getTable().getModelTbAlias();
            if (!second.getRealName().equals("id") && !second.getTable().isSystem()) {
                resultSelect += " use index (" + second.getRealName() + ") ";
            }
            resultSelect += " ON " + second.getTable().getModelTbAlias() + "." + second.getRealName() + " "
                    + " =" + first.getTable().getModelTbAlias() + "." + first.getRealName() + " ";
            resultSelect += getDefaultCondition(second.getTable());
        }
        return this;
    }

    @Override
    public Select leftJoin(Table table) throws CloneNotSupportedException {
        resultSelect += " LEFT JOIN " + table.getRealName() + " " + table.getModelTbAlias() + " on 1 = 1 ";
        resultSelect += getDefaultCondition(table);
        return this;
    }

    /**
     * добавить условие and в блок where
     *
     * @param cond условие
     */
    @Override
    public SelectMysql and(Condition cond) throws CloneNotSupportedException {
        and(cond, true);
        return this;
    }

    /**
     * добавить условия and в блок where
     *
     * @param cond условия
     */
    @Override
    public SelectMysql and(Condition... cond) throws CloneNotSupportedException {
        for (Condition cnd : cond) {
            and(cnd, true);
        }
        return this;
    }

    /**
     * добавить условие в блок where либо в блок ON
     *
     * @param cond условие
     * @param checkWhere если true, то добавляется в блок where
     */
    @Override
    public SelectMysql and(Condition cond, Boolean checkWhere) throws CloneNotSupportedException {
        if (and == false && checkWhere == true) {
            resultSelect += " WHERE 1=1 ";
            resultSelect += getDefaultCondition();
            resultSelect += " and " + getOrAnd(cond);
            and = true;
        } else {
            resultSelect += " and " + getOrAnd(cond);
        }
        return this;
    }

    @Override
    public Select on(Condition cond) throws CloneNotSupportedException {
        return and(cond, false);
    }

    @Override
    public Select and(String cond) throws CloneNotSupportedException {
        return and(cond, true);
    }

    @Override
    public Select and(String cond, boolean checkWhere) throws CloneNotSupportedException {
        if (and == false && checkWhere == true) {
            resultSelect += " WHERE 1=1 ";
            resultSelect += getDefaultCondition();
            and = true;
        }
        resultSelect += " and (" + cond + ") ";
        return this;
    }

    /**
     * добавить несколько условий через OR. <br/><br/> к блоку where
     * добавляется: AND ( условие 1 OR условие 2 OR ... )
     *
     * @param cond условия
     */
    @Override
    public SelectMysql andOr(Condition... cond) throws CloneNotSupportedException {
        List<Condition> cnLi = new ArrayList<Condition>();
        cnLi.addAll(Arrays.asList(cond));
        andOr(cnLi);
        return this;
    }

    /**
     * добавить несколько условий через OR. <br/><br/> к блоку where
     * добавляется: AND ( условие 1 OR условие 2 OR ... )
     *
     * @param cond условия
     */
    @Override
    public SelectMysql andOr(List<Condition> cond) throws CloneNotSupportedException {
        if (and == false) {
            resultSelect += " WHERE 1=1 ";
            resultSelect += getDefaultCondition();
            resultSelect += "and ( ";
            for (Condition cnd : cond) {
                if (cond.indexOf(cnd) > 0) {
                    resultSelect += " or " + getOrAnd(cnd);
                } else {
                    resultSelect += " " + getOrAnd(cnd);
                }

            }
            resultSelect += ") ";
            and = true;
        } else {
            resultSelect += "and ( ";
            for (Condition cnd : cond) {
                if (cond.indexOf(cnd) > 0) {
                    resultSelect += " or " + getOrAnd(cnd);
                } else {
                    resultSelect += " " + getOrAnd(cnd);
                }
            }
            resultSelect += ") ";
        }
        return this;
    }

    /**
     * добавить несколько списков условий через OR. <br/><br/> к блоку where
     * добавляется: AND ( (условие 1 and условие 2) OR (условие 3 and условие 4)
     * OR ... )
     *
     * @param cond условия
     */
    @Override
    public SelectMysql andOrList(List<Condition>... cond) throws CloneNotSupportedException {
        if (and == false) {
            resultSelect += " WHERE 1=1 ";
            resultSelect += getDefaultCondition();
        }
        resultSelect += "and ( ";
        Integer cnt = 0;
        for (List<Condition> cnd : cond) {
            if (cnd.size() > 0) {
                if (cnt > 0) {
                    resultSelect += "or";
                }
                resultSelect += "(";
                for (Condition cnd1 : cnd) {
                    if (cnd.indexOf(cnd1) > 0) {
                        resultSelect += " and " + getOrAnd(cnd1);
                    } else {
                        resultSelect += " " + getOrAnd(cnd1);
                    }
                }
                resultSelect += ")";
                cnt++;
            }
        }
        resultSelect += ") ";
        return this;
    }

    /**
     * добавить условия and в блок where
     *
     * @param cond условия
     */
    @Override
    public SelectMysql and(List<Condition> cond) throws CloneNotSupportedException {
        for (Condition cn : cond) {
            and(cn);
        }
        return this;
    }

    /**
     * добавить условия or в блок where
     *
     * @param cond условия
     */
    @Override
    public SelectMysql or(List<Condition> cond) throws CloneNotSupportedException {
        or(cond, true);
        return this;
    }

    /**
     * добавить условие в блок where либо в блок ON
     *
     * @param cond условие
     * @param checkWhere если true, то добавляется в блок where
     */
    @Override
    public SelectMysql or(List<Condition> cond, Boolean checkWhere) throws CloneNotSupportedException {
        if (and == false && checkWhere == true) {
            and(cond);
        } else if (cond.size() > 0) {
            resultSelect += " or (";
            resultSelect += getDefaultCondition();
        }
        for (Condition cn : cond) {
            and(cn);
        }
        if (cond.size() > 0) {
            resultSelect += ") ";
        }
        return this;
    }

    /**
     * добавить параметр в блок ORDER
     *
     * @param param параметр
     * @param ot тип сортировки
     */
    @Override
    public SelectMysql order(Parameter param, OrdTypes ot) throws Exception {
        if (and == false) {
            resultSelect += " WHERE 1=1 ";
            resultSelect += getDefaultCondition();
            and = true;
        }
        if (order == false) {
            resultSelect += " ORDER BY ";
            resultSelect += " " + param.getTable().getModelTbAlias() + "." + param.getRealName() + " " + ot.toString();
            order = true;
        } else {
            resultSelect += " ," + param.getTable().getModelTbAlias() + "." + param.getRealName() + " " + ot.toString();
        }
        return this;
    }

    /**
     * добавить произвольный параметр в блок ORDER
     *
     * @param param параметр
     * @param ot тип сортировки
     */
    @Override
    public SelectMysql order(String param, OrdTypes ot) throws Exception{
        if (and == false) {
            resultSelect += " WHERE 1=1 ";
            resultSelect += getDefaultCondition();
            and = true;
        }
        if (order == false) {
            resultSelect += " ORDER BY ";
            resultSelect += " " + validateParameter(param, false) + " " + ot.toString();
            order = true;
        } else {
            resultSelect += " ," + validateParameter(param, false) + " " + ot.toString();
        }
        return this;
    }

    /**
     * добавить параметр в блок GROUP BY
     *
     * @param param параметр
     */
    @Override
    public SelectMysql group(Parameter param) throws CloneNotSupportedException {
        if (and == false) {
            resultSelect += " WHERE 1=1 ";
            resultSelect += getDefaultCondition();
            and = true;
        }
        if (group == false) {
            resultSelect += " GROUP BY ";
            resultSelect += " " + param.getTable().getModelTbAlias() + "." + param.getRealName() + " ";
            group = true;
        } else {
            resultSelect += " ," + param.getTable().getModelTbAlias() + "." + param.getRealName() + " ";;
        }
        return this;
    }

    /**
     * добавить параметр в блок GROUP BY, применить к этому параметру функцию
     *
     * @param param параметр
     * @param func функция
     */
    @Override
    public SelectMysql group(Parameter param, Function func) throws Exception{
        if (and == false) {
            resultSelect += " WHERE 1=1 ";
            resultSelect += getDefaultCondition();
            and = true;
        }
        if (group == false) {
            resultSelect += " GROUP BY ";
            resultSelect += " " + func + "(" + param.getTable().getModelTbAlias() + "." + param.getRealName() + ")";
            group = true;
        } else {
            resultSelect += " ," + func + "(" + param.getTable().getModelTbAlias() + "." + param.getRealName() + ")";
        }
        return this;
    }

    /**
     * добавить параметры в блок GROUP BY
     *
     * @param param параметры
     */
    @Override
    public SelectMysql group(List<Parameter> param) throws CloneNotSupportedException {
        for (Parameter par : param) {
            group(par);
        }
        return this;
    }

    /**
     * добавить выражение exists в блок where
     *
     * @param query подзапрос, который будет внутри выражения exists
     */
    @Override
    public SelectMysql exists(String query) {
        if (and == false) {
            resultSelect += " WHERE 1=1 ";
            and = true;
        }
        resultSelect += " and exists (" + query + ") ";
        return this;
    }

    /**
     * добавить выражение exists в блок where
     *
     * @param query подзапрос, который будет внутри выражения exists
     */
    @Override
    public SelectMysql notExists(String query) {
        if (and == false) {
            resultSelect += " WHERE 1=1 ";
            and = true;
        }
        resultSelect += " and not exists (" + query + ") ";
        return this;
    }

    @Override
    public Select having(String str) throws CloneNotSupportedException {
        resultSelect += " having " + str + " ";
        return this;
    }

    @Override
    public Integer getLimitFrom() {
        return limitFrom;
    }

    /**
     * добавить в блок limit - номер записи, начиная с которой производится
     * выборка
     *
     * @param limitFrom
     */
    @Override
    public SelectMysql setLimitFrom(Integer limitFrom) {
        this.limitFrom = limitFrom;
        return this;
    }

    @Override
    public Integer getLimitRange() {
        return limitRange;
    }

    /**
     * добавить в блок linit - количество записей в результате запроса
     *
     * @param limitRange
     */
    @Override
    public SelectMysql setLimitRange(Integer limitRange) {
        this.limitRange = limitRange;
        return this;
    }

    /**
     * выполнить запрос
     *
     * @param conn объект соединения с БД
     * @return успешно ли выполнен запрос
     * @throws Exception
     */
    @Override
    public Boolean executeSelect(Connection conn) throws Exception {
        Boolean res = false;
        ArrayList<DinamicModel> result = new ArrayList<DinamicModel>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (errors.isEmpty()) {
            QueryExecutor qe = ExecutorFabric.getExecutor(conn, getPrepareSelect());
            if (qe.select() != false) {
                list = qe.getResultList();
                queryTime = qe.getQueryTime();
            } else {
                errors.addAll(qe.getError());
            }
            if (errors.isEmpty()) {
                for (Map<String, Object> map : list) {
                    DinamicModel model = ModelFactory.getDinamicModel(getStructure(), map);
                    result.add(model);
                }
                res = true;
                dinamicList = result;
            }
        }
        return res;
    }

    /**
     *
     * @return ошибки
     */
    @Override
    public ArrayList<String> getError() {
        ArrayList<String> errorList = new ArrayList<String>();
        for (String error : errors) {
            errorList.add(error);
        }
        return errorList;
    }

    /**
     * возвращает результат запроса
     *
     * @return
     */
    @Override
    public List<DinamicModel> getDinamicList() {
        return dinamicList;
    }

    /**
     * возвращает строку SQL-запроса
     *
     * @return
     */
    @Override
    public String getPrepareSelect() {
        String result = resultSelect + getLimit();
        return result;
    }

    @Override
    public ArrayList<Table> getAllSelectTables() {
        return allSelectTables;
    }

    private String getOrAnd(Condition cond) {
        String res = " ";      
        if(cond.getParamFirst()!=null){
            res += cond.getParamFirst().getTable().getModelTbAlias() + "." + cond.getParamFirst().getRealName() + " ";
        }
        CondType ct = cond.getCondType();
        Object par2 = cond.getParamSecond();
        if (ct == CondType.bigger) {
            res += ">" + AnaliseParams(par2, true);
        } else if (ct == CondType.biggerEq) {
            res += ">=" + AnaliseParams(par2, true);
        } else if (ct == CondType.equals) {
            res += "=" + AnaliseParams(par2, true);
        } else if (ct == CondType.exists) {
            res += " exists (" + AnaliseParams(par2, false)+") ";
        } else if (ct == CondType.isNotNull) {
            res += "is not NULL";
        } else if (ct == CondType.isNull) {
            res += "is NULL";
        } else if (ct == CondType.lesser) {
            res += "<" + AnaliseParams(par2, true);
        } else if (ct == CondType.lesserEq) {
            res += "<=" + AnaliseParams(par2, true);
        } else if (ct == CondType.like) {
            res = "lower(" + res + ") like lower ('%" + AnaliseParams(par2, false) + "%')";
        } else if (ct == CondType._like_) {
            res = "lower(" + res + ") like lower ('% " + AnaliseParams(par2, false) + " %')";
        } else if (ct == CondType._like) {
            res = "lower(" + res + ") like lower ('% " + AnaliseParams(par2, false) + "%')";
        } else if (ct == CondType._like_strong) {
            res = "lower(" + res + ") like lower ('% " + AnaliseParams(par2, false) + "')";
        } else if (ct == CondType.rlike) {
            res = "lower(" + res + ") rlike lower ('[[:<:]]" + AnaliseParams(par2, false) + "[[:>:]]')";
        } else if (ct == CondType.notEquals) {
            res += "!=" + AnaliseParams(par2, true);
        } else if (ct == CondType.notExists) {
            res += " not exists (" + AnaliseParams(par2, false)+") ";
        } else if (ct == CondType.notLike) {
            res = "lower(" + res + ") not like lower ('%" + AnaliseParams(par2, false) + "%')";
        }
        res += " ";
        return res;
    }

    private String AnaliseParams(Object ob, Boolean quoted) {
        String res = " ";
        if (ob instanceof Parameter) {
            Parameter pr = (Parameter) ob;
            res += " " + pr.getTable().getModelTbAlias() + "." + pr.getRealName() + " ";
        } else {
            String param = (ob == null ? "" : ob.toString());
            res = validateParameter(param, quoted);
        }
        return res;
    }

    private String getLimit() {
        String st = "";
        if (limitFrom != null || limitRange != null) {
            st += " limit ";
            if (limitFrom == null) {
                st += limitRange;
            } else {
                st += limitFrom + " , " + limitRange;
            }
            st += " ";
        }
        return st;
    }

    private String getDefaultCondition() throws CloneNotSupportedException {
        String result = " ";
        for (Table atb : tables.values()) {
            for (Field fs : atb.getStructure().values()) {
                if (fs.getDef() != null && fs.isUpdatable() == false) {
                    Condition cnd = ConditionMysql.getInstance(atb.get(fs.getAlias()), CondType.equals, fs.getDef());
                    result += " and " + getOrAnd(cnd) + " ";
                }
            }
        }
        return result;
    }

    private String getDefaultCondition(Table table) throws CloneNotSupportedException {
        String result = " ";
        for (Field fs : table.getStructure().values()) {
            if (fs.getDef() != null) {
                Condition cnd = ConditionMysql.getInstance(table.get(fs.getAlias()), CondType.equals, fs.getDef());
                result += " and " + getOrAnd(cnd) + " ";
            }
        }
        return result;
    }

    @Override
    public Map<String, Field> getStructure() throws CloneNotSupportedException {
        Map<String, Field> newStructure = new LinkedHashMap<String, Field>();
        for (String name : structure.keySet()) {
            newStructure.put(name, structure.get(name).clone());
        }
        return newStructure;
    }

    @Override
    public SelectMysql setCashable(Boolean cashable) {
        //this.cashable = cashable;
        this.cashable = false;
        return this;
    }

    /**
     * экранирует спецсимволы в строке, которая будет параметром SQL-запроса
     *
     * @param param - параметр
     * @param quoted - добавлять ли кавычки
     * @return
     */
    @Override
    public String validateParameter(Object param, Boolean quoted) {
        String paramVal;
        if (param != null && !"null".equals(param) && !param.equals("")) {
            paramVal = param.toString();
            paramVal = paramVal.replace("'", "\\\'");
            paramVal = paramVal.replace("\"", "\\\"");
            paramVal = paramVal.replace("\\\\", "\\");
            if (quoted == true) {
                paramVal = "'" + paramVal + "'";
            }
        } else {
            paramVal = "NULL";
        }
        return paramVal;
    }

    @Override
    public Condition getExistCondition(String subquery) {
        return ConditionMysql.getInstance(null, CondType.exists, subquery);
    }

    @Override
    public Condition getNotExistCondition(String subquery) {
       return ConditionMysql.getInstance(null, CondType.notExists, subquery);
    }
}
