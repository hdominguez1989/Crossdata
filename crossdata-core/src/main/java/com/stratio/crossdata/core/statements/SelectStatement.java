/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.crossdata.core.statements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stratio.crossdata.common.data.CatalogName;
import com.stratio.crossdata.common.data.ColumnName;
import com.stratio.crossdata.common.data.TableName;
import com.stratio.crossdata.common.statements.structures.OrderByClause;
import com.stratio.crossdata.common.statements.structures.Relation;
import com.stratio.crossdata.common.statements.structures.SelectExpression;
import com.stratio.crossdata.common.statements.structures.Selector;
import com.stratio.crossdata.common.statements.structures.window.Window;
import com.stratio.crossdata.common.utils.Constants;
import com.stratio.crossdata.common.utils.StringUtils;
import com.stratio.crossdata.core.structures.GroupByClause;
import com.stratio.crossdata.core.structures.InnerJoin;
import com.stratio.crossdata.core.validator.requirements.ValidationRequirements;
import com.stratio.crossdata.core.validator.requirements.ValidationTypes;

/**
 * Class that models a {@code SELECT} statement from the CROSSDATA language.
 */
public class SelectStatement extends CrossdataStatement implements Serializable {

    /**
     * The name of the target table.
     */
    private final TableName tableName;
    /**
     * The list of selectors to be retrieved.
     */
    private SelectExpression selectExpression = null;
    /**
     * Whether a time window has been specified in the Select statement.
     */
    private boolean windowInc = false;
    /**
     * The {@link com.stratio.crossdata.common.statements.structures.window.Window} specified in the Select
     * statement for streaming queries.
     */
    private Window window = null;
    /**
     * Whether a JOIN clause has been specified.
     */
    private boolean joinInc = false;
    /**
     * The List of {@link com.stratio.crossdata.core.structures.InnerJoin} for the statement.
     */
    private List<InnerJoin> joinList = new ArrayList<>();

    /**
     * Whether the Select contains a WHERE clause.
     */
    private boolean whereInc = false;
    /**
     * The list of {@link com.stratio.crossdata.common.statements.structures.Relation} found
     * in the WHERE clause.
     */
    private List<Relation> where = null;
    /**
     * Whether an ORDER BY clause has been specified.
     */
    private boolean orderInc = false;
    /**
     * Whether a GROUP BY clause has been specified.
     */
    private boolean groupInc = false;
    /**
     * The {@link com.stratio.crossdata.core.structures.GroupByClause} clause.
     */
    private GroupByClause groupByClause = null;
    private List<OrderByClause> orderByClauseClauses = new ArrayList<>();
    /**
     * Whether a LIMIT clause has been specified.
     */
    private boolean limitInc = false;
    /**
     * Whether a subquery clause has been specified.
     */
    private boolean subqueryInc = false;
    /**
     * The subquery.
     */
    private SelectStatement subquery= null;
    /**
     * The subquery alias.
     */
    private String subqueryAlias= null;


    /**
     * The LIMIT in terms of the number of rows to be retrieved in the result of the SELECT statement.
     */
    private int limit = 0;
    private Map<String, String> fieldsAliasesMap;

    private Map<String, String> tablesAliasesMap;

    /**
     * Class constructor.
     *
     * @param tableName The name of the target table.
     */
    public SelectStatement(TableName tableName) {
        this.command = false;
        this.tableName = tableName;
    }

    /**
     * Class constructor.
     *
     * @param selectExpression The
     *                         {@link com.stratio.crossdata.common.statements.structures.SelectExpression} of the
     *                         Select statement.
     * @param tableName        The name of the target table.
     */
    public SelectStatement(SelectExpression selectExpression, TableName tableName) {
        this(tableName);
        this.selectExpression = selectExpression;
    }

    /**
     * Get the catalog specified in the select statement.
     *
     * @return The catalog or null if not specified.
     */
    public CatalogName getCatalog() {
        return catalog;
    }

    /**
     * Set the catalog specified in the select statement.
     *
     * @param catalog The name of the catalog.
     */
    public void setCatalog(CatalogName catalog) {
        this.catalogInc = true;
        this.catalog = catalog;
    }

    /**
     * Get the name of the target table.
     *
     * @return The table name.
     */
    public TableName getTableName() {
        return tableName;
    }

    public SelectExpression getSelectExpression() {
        return selectExpression;
    }

    public void setSelectExpression(SelectExpression selectExpression) {
        this.selectExpression = selectExpression;
    }



    /**
     * Get the list of joins of the statement.
     * @return A list of {@link com.stratio.crossdata.core.structures.InnerJoin} .
     */
    public List<InnerJoin> getJoinList() {
        return joinList;
    }

    /**
     * Set the join list.
     * @param joinList The list of {@link com.stratio.crossdata.core.structures.InnerJoin} .
     */
    public void setJoinList(List<InnerJoin> joinList) {
        this.joinList = joinList;
        if (!joinList.isEmpty()){
            joinInc=true;
        }else{
            joinInc=false;
        }
    }

    /**
     * Add a new join to the list of joins.
     * @param join The join.
     */
    public void addJoin(InnerJoin join){
        joinList.add(join);
        joinInc=true;
    }

    /**
     * Remove the specified join of the list of joins.
     * @param join The join.
     */
    public void removeJoin(InnerJoin join){
        joinList.remove(join);
    }

    /**
     * Get the list of {@link Relation} in the where clause.
     *
     * @return The list of relations.
     */
    public List<Relation> getWhere() {
        return where;
    }

    /**
     * Set the list of {@link Relation} in the where clause.
     *
     * @param where The list of relations.
     */
    public void setWhere(List<Relation> where) {
        this.whereInc = true;
        this.where = where;
    }

    /**
     * Check if ORDER BY clause is included.
     *
     * @return {@code true} if is included.
     */
    public boolean isOrderInc() {
        return orderInc;
    }

    public boolean isLimitInc() {
        return limitInc;
    }

    /**
     * Return GROUP BY clause.
     *
     * @return list of {@link com.stratio.crossdata.core.structures.GroupByClause}.
     */
    public GroupByClause getGroupByClause() {
        return groupByClause;
    }

    /**
     * Set the {@link com.stratio.crossdata.core.structures.GroupByClause} clause.
     *
     * @param groupByClause The group by.
     */
    public void setGroupByClause(GroupByClause groupByClause) {
        this.groupInc = true;
        this.groupByClause = groupByClause;
    }

    /**
     * Check if GROUP BY clause is included.
     *
     * @return {@code true} if is included.
     */
    public boolean isGroupInc() {
        return groupInc;
    }

    /**
     * Check if a WHERE clause is included.
     *
     * @return Whether it is included.
     */
    public boolean isWhereInc() {
        return whereInc;
    }

    /**
     * Check if a subquery is included.
     *
     * @return Whether it is included.
     */
    public boolean isSubqueryInc() {
        return subqueryInc;
    }

    public int getLimit() {
        return limit;
    }

    /**
     * Set the LIMIT of the query.
     *
     * @param limit The maximum number of rows to be returned.
     */
    public void setLimit(int limit) {
        this.limitInc = true;
        this.limit = limit;
    }

    public Window getWindow() {
        return window;
    }

    /**
     * Set the {@link com.stratio.crossdata.common.statements.structures.window.Window} for streaming
     * queries.
     *
     * @param window The window.
     */
    public void setWindow(Window window) {
        this.windowInc = true;
        this.window = window;
    }

    public Map<String, String> getFieldsAliasesMap() {
        return fieldsAliasesMap;
    }

    public void setFieldsAliases(Map fieldsAliasesMap) {
        this.fieldsAliasesMap = fieldsAliasesMap;
    }

    public Map<String, String> getTablesAliasesMap() {
        return tablesAliasesMap;
    }

    public void setTablesAliases(Map tablesAliasesMap) {
        this.tablesAliasesMap = tablesAliasesMap;
    }

    /**
     * Creates a String representing the Statement with CROSSDATA syntax.
     *
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SELECT ");
        if (selectExpression != null) {
            sb.append(selectExpression.toString());
        }
        sb.append(" FROM ");

        if(subqueryInc){
            sb.append("( ").append(subquery.toString()).append(" ) " ).append(subqueryAlias);
        }else{
            if (catalogInc) {
                sb.append(catalog).append(".");
            }
            sb.append(tableName);
        }

        if (windowInc) {
            sb.append(" WITH WINDOW ").append(window.toString());
        }
        if (joinInc) {
            for (InnerJoin myJoin:joinList) {
                sb.append(" ").append(myJoin.getType().toString().replace("_"," ")).append(" JOIN ").append(myJoin
                        .toString());
            }
        }
        if (whereInc) {
            sb.append(" WHERE ");
            sb.append(StringUtils.stringList(where, " AND "));
        }
        if (orderInc) {
            sb.append(" ORDER BY ").append(orderByClauseClauses);
        }
        if (groupInc) {
            sb.append(" GROUP BY ").append(StringUtils.stringList(groupByClause.getSelectorIdentifier(), ", "));
        }
        if (limitInc) {
            sb.append(" LIMIT ").append(limit);
        }

        return sb.toString().replaceAll("  ", " ").replaceAll(Constants.VIRTUAL_CATALOG_NAME+"\\.", "");
    }


    /**
     * Creates a String representing the Statement with SQL_92 syntax.
     *
     * @return String
     */
    public String toSQLString() {
      return toString();
    }

    @Override
    public ValidationRequirements getValidationRequirements() {
        return new ValidationRequirements().add(
                ValidationTypes.VALIDATE_SELECT);
    }

    public List<OrderByClause> getOrderByClauses() {
        return orderByClauseClauses;
    }

    /**
     * Set the order by clause of the statement.
     * @param orderByClauseClauses The list of columns that are implicated in the order by.
     */
    public void setOrderByClauses(List<OrderByClause> orderByClauseClauses) {
        if ((orderByClauseClauses != null) && (!orderByClauseClauses.isEmpty())) {
            this.orderInc = true;
            this.orderByClauseClauses = orderByClauseClauses;
        }
    }

    /**
     * Set the subquery of the statement.
     * @param subquery The subquery statement,
     * @param alias    The alias.
     */
    public void setSubquery(SelectStatement subquery, String alias) {
        if (subquery != null && alias != null) {
            this.subqueryInc = true;
            this.subquery = subquery;
            this.subqueryAlias = alias;
        }
    }

    /**
     * Get the subquery of the statement.
     * @return the inner select statemnet.
     */
    public SelectStatement getSubquery() {
        return subquery;
    }

    /**
     * Get the subquery alias.
     * @return The alias of the inner statement.
     */
    public String getSubqueryAlias() {
        return subqueryAlias;
    }

    @Override
    public List<TableName> getFromTables() {
        ArrayList<TableName> tableNames = new ArrayList<>();
        tableNames.add(tableName);
        if (!joinList.isEmpty()) {
            for (InnerJoin myJoin:joinList) {
                tableNames.add(myJoin.getTablename());
            }
        }
        return tableNames;
    }

    @Override
    public List<ColumnName> getColumns() {
        List<Selector> selectors = selectExpression.getSelectorList();
        List<ColumnName> columns = new ArrayList<>();
        for(Selector selector: selectors){
            columns.add(selector.getColumnName());
        }
        return columns;
    }
}
