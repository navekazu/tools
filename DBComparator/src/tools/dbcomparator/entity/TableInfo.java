/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator.entity;

import java.util.List;

/**
 *
 * @author k_watanabe
 */
public class TableInfo {
    private String tableName;
    private List<String> primaryKeyColumnName;
    private List<Integer> primaryKeyColumnType;
    private int count;

    public List<String> getPrimaryKeyColumnName() {
        return primaryKeyColumnName;
    }

    public void setPrimaryKeyColumnName(List<String> primaryKeyColumnName) {
        this.primaryKeyColumnName = primaryKeyColumnName;
    }

    public List<Integer> getPrimaryKeyColumnType() {
        return primaryKeyColumnType;
    }

    public void setPrimaryKeyColumnType(List<Integer> primaryKeyColumnType) {
        this.primaryKeyColumnType = primaryKeyColumnType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    
}
