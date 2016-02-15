/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.tools.dbcomparator;

import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import tools.dbcomparator.TableCompareThread;

/**
 *
 * @author k_watanabe
 */
public class TableCompareThreadTest {
    
    public TableCompareThreadTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void mergeColumnList() {
        List<String> table1 = new ArrayList<>();
        table1.add("col1");
        table1.add("col2");
//        table1.add("col3");
        table1.add("col4");
        table1.add("col5");

        List<String> table2 = new ArrayList<>();
        table2.add("col1");
        table2.add("col2");
        table2.add("col3");
        table2.add("col4");
        table2.add("col5");

        List<List<String>> tableList = new ArrayList<>();
        tableList.add(table1);
        tableList.add(table2);

//        List<String> result = TableCompareThread.mergeColumnList(tableList);
//        for (String col: result) {
//            System.out.println(col);
//        }
    }
}
