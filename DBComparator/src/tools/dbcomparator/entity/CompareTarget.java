/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator.entity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author k_watanabe
 */
public class CompareTarget {
    private List<DBInfo> dbInfoList;

    public CompareTarget() {
    }

    public void addDbInfoList(DBInfo dbInfo) {
        if (this.dbInfoList==null) {
            this.dbInfoList = new ArrayList<>();
        }
        this.dbInfoList.add(dbInfo);
    }

    public List<DBInfo> getDbInfoList() {
        return dbInfoList;
    }

    public void setDbInfoList(List<DBInfo> dbInfoList) {
        this.dbInfoList = dbInfoList;
    }
    
    
}
