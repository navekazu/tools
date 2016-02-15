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
public class ComparatorConfig {
    private boolean showGUIProgress;
    private List<CompareTarget> targetList;
    private List<String> includeList;
    private List<String> excludeList;

    public ComparatorConfig() {
        showGUIProgress = false;
        targetList = new ArrayList<>();
        includeList = new ArrayList<>();
        excludeList = new ArrayList<>();
    }

    public boolean isShowGUIProgress() {
        return showGUIProgress;
    }

    public void setShowGUIProgress(boolean showGUIProgress) {
        this.showGUIProgress = showGUIProgress;
    }

    public List<CompareTarget> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<CompareTarget> targetList) {
        this.targetList = targetList;
    }

    public List<String> getIncludeList() {
        return includeList;
    }

    public void setIncludeList(List<String> includeList) {
        this.includeList = includeList;
    }
    
    public List<String> getExcludeList() {
        return excludeList;
    }

    public void setExcludeList(List<String> excludeList) {
        this.excludeList = excludeList;
    }
    
}
