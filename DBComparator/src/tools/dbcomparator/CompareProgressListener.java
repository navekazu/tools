/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator;

/**
 *
 * @author k_watanabe
 */
public interface CompareProgressListener {
    public void searchPK(String table, int target, int current, int count);
    public void recordCount(String table, int count);
    public void progress(String table, int progress);
    public void finish(String table, int status, int errorCount);
}
