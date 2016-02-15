/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author k_watanabe
 */
public class CompareResultRecord {
    private List<String> columnNameList;
    private List<Map<String, String>> subjectValueList;

    public CompareResultRecord(int subjectCount) {
        columnNameList = new ArrayList<>();
        subjectValueList = new ArrayList<>();

        for (int loop=0; loop<subjectCount; loop++) {
            subjectValueList.add(new HashMap<String, String>());
        }
    }

    public void addColumn(String name) {
        columnNameList.add(name);

        for (Map valueMap: subjectValueList) {
            valueMap.put(name, null);
        }
    }

    public void addSubjectValue(String columnName, int subjectPosition, String value) {
        if (subjectValueList.size()<=subjectPosition) {
            throw new IllegalArgumentException();
        }

        Map valueMap = subjectValueList.get(subjectPosition);
        valueMap.put(columnName, value);
    }

    public boolean existSubjectValue(int subjectPosition, String columnName) {
        if (subjectValueList.size()<=subjectPosition) {
            throw new IllegalArgumentException();
        }

        return subjectValueList.get(subjectPosition).containsKey(columnName);
    }

    public String getSubjectValue(int subjectPosition, String columnName) {
        if (subjectValueList.size()<=subjectPosition) {
            throw new IllegalArgumentException();
        }

        if (!existSubjectValue(subjectPosition, columnName)) {
//            throw new IllegalArgumentException();
            return null;
        }

        return subjectValueList.get(subjectPosition).get(columnName);
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this.columnNameList = columnNameList;
    }

    public List<Map<String, String>> getSubjectValueList() {
        return subjectValueList;
    }
}
