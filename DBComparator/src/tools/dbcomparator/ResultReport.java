/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import tools.dbcomparator.entity.TableInfo;

/**
 *
 * @author k_watanabe
 */
public class ResultReport {
    public static final String TEMPLATE = "CompareResultTemplate.xls";
    private Workbook reportWorkbook;

    public void createReport() throws IOException, InvalidFormatException {
        BufferedInputStream in = new BufferedInputStream(getClass().getResourceAsStream("CompareResultTemplate.xls"));
        reportWorkbook = WorkbookFactory.create(in);
    }

    public void outputTableReport(Set<String> allTableList, List<DBSubject> dbSubjectList) throws IOException, InvalidFormatException {

        Sheet sheet = reportWorkbook.getSheet("Tables");

        int rowNum = 2;
        int colNum = 3;
        Row row;
        Cell cell;

        for (DBSubject dbSubject: dbSubjectList) {
            row = sheet.getRow(0);
            cell = row.createCell(colNum);
            cell.setCellValue(dbSubject.getSubjectName());
            row = sheet.getRow(1);
            cell = row.createCell(colNum);
            cell.setCellValue("Column size");
            cell = row.createCell(colNum+1);
            cell.setCellValue("Count");
            colNum+=2;
        }

        for (String table: allTableList) {
            row = sheet.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue(table);
            colNum = 3;
            for (DBSubject dbSubject: dbSubjectList) {
                TableInfo tableInfo = dbSubject.getTableInfo(table);
                if (tableInfo!=null) {
                    cell = row.createCell(colNum+1);
                    cell.setCellValue(tableInfo.getCount());
                    
                }
                colNum+=2;
            }
            rowNum++;
        }
    }

    public void write() throws FileNotFoundException, IOException {
        FileOutputStream out = new FileOutputStream(fileName());
        reportWorkbook.write(out);
        out.close();
        
    }
    private String fileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "CompareReport_"+sdf.format(new Date())+".xls";
    }
}
