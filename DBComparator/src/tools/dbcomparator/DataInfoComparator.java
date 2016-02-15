/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Comparator;
import tools.dbcomparator.entity.DataInfo;

/**
 *
 * @author k_watanabe
 */
public class DataInfoComparator<E> implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        if (!(o1 instanceof DataInfo && o2 instanceof DataInfo)) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        DataInfo pk1 = (DataInfo)o1;
        DataInfo pk2 = (DataInfo)o2;

        if (pk1.columnValues.length!=pk2.columnValues.length) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        for (int loop=0; loop<pk1.columnValues.length; loop++) {
            Object object1 = pk1.columnValues[loop];
            Object object2 = pk2.columnValues[loop];
            int compareTo;

            if (object1 instanceof String && object2 instanceof String) {
                String string1 = (String)object1;
                String string2 = (String)object2;
                compareTo = string1.compareTo(string2);
                if (compareTo!=0) {
                    return compareTo;
                }
            }
            else
            if (object1 instanceof Integer && object2 instanceof Integer) {
                Integer integer1 = (Integer)object1;
                Integer integer2 = (Integer)object2;
                compareTo = integer1.compareTo(integer2);
                if (compareTo!=0) {
                    return compareTo;
                }
            }
            else
            if (object1 instanceof Double && object2 instanceof Double) {
                Double double1 = (Double)object1;
                Double double2 = (Double)object2;
                compareTo = double1.compareTo(double2);
                if (compareTo!=0) {
                    return compareTo;
                }
            }
            else
            if (object1 instanceof Date && object2 instanceof Date) {
                Date date1 = (Date)object1;
                Date date2 = (Date)object2;
                compareTo = date1.compareTo(date2);
                if (compareTo!=0) {
                    return compareTo;
                }
            }
            else
            if (object1 instanceof BigDecimal && object2 instanceof BigDecimal) {
                BigDecimal bigDecimal1 = (BigDecimal)object1;
                BigDecimal bigDecimal2 = (BigDecimal)object2;
                compareTo = bigDecimal1.compareTo(bigDecimal2);
                if (compareTo!=0) {
                    return compareTo;
                }
            }
            else
            if (object1 instanceof Timestamp && object2 instanceof Timestamp) {
                Timestamp timestamp1 = (Timestamp)object1;
                Timestamp timestamp2 = (Timestamp)object2;
                compareTo = timestamp1.compareTo(timestamp2);
                if (compareTo!=0) {
                    return compareTo;
                }
            }
            else
            if (object1==null && object2==null) {
            }
            else
            if (object1!=null && object2==null) {
                return -1;
            }
            else
            if (object1==null && object2!=null) {
                return 1;
            }
            else {
                throw new UnsupportedOperationException("Not supported yet. object1->"+object1.getClass().getName()+" object2"+object2.getClass().getName());
            }
        }
        return 0;
    }
    
}
