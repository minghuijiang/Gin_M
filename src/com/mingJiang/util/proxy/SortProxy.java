package com.mingJiang.util.proxy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortProxy {

    public static void sortByFailRate(List<MyProxy> items) {
        ProxyFailRateComparator com = new ProxyFailRateComparator();
        Collections.sort(items, com);
    }

    public static void sortByFailCount(List<MyProxy> items) {
        ProxyFailCountComparator com = new ProxyFailCountComparator();
        Collections.sort(items, com);
    }

    public static void sortByTotalFailRate(List<MyProxy> items) {
        Collections.sort(items, new Comparator<MyProxy>() {

            @Override
            public int compare(MyProxy o1, MyProxy o2) {
                double r1 = o1.getTotalFailRate();
                double r2 = o2.getTotalFailRate();
                if (r1 > r2) {
                    return -1;
                }
                if (r1 < r2) {
                    return +1;
                }
                
                int f1 = o1.getTotalFail();
                int f2 = o2.getTotalFail();
                if(f1> f2)
                	return -1;
                if(f2> f1)
                	return +1;
                return 0;
            }

        });
    }
}

/**
 * first compare fail rate, lowest rate will be on top, if the fail rate equals,
 * the higher submit count will be on top.
 *
 * @author Ming Jiang
 *
 */
class ProxyFailRateComparator implements Comparator<MyProxy> {

    public int compare(MyProxy p, MyProxy q) {
        double f1 = p.getFailRate();
        double f2 = q.getFailRate();

        if (f1 > f2) {
            return +1;
        } else if (f1 < f2) {
            return -1;
        } else {
            int s1 = p.getSubmit();
            int s2 = q.getSubmit();
            if (s1 > s2) {
                return -1;
            } else if (s1 < s2) {
                return +1;
            } else {
                return 0;
            }
        }
    }
}

/**
 * first compare fail rate, lowest rate will be on top, if the fail rate equals,
 * the higher submit count will be on top.
 *
 * @author Ming Jiang
 *
 */
class ProxyFailCountComparator implements Comparator<MyProxy> {

    public int compare(MyProxy p, MyProxy q) {
        int f1 = p.getFail();
        int f2 = q.getFail();
        if (f1 == f2) {
            int s1 = p.getSubmit();
            int s2 = q.getSubmit();
            if (s1 > s2) {
                return -1;
            } else if (s1 < s2) {
                return +1;
            } else {
                return 0;
            }
        } else if (f1 > f2) {
            return +1;
        } else if (f1 < f2) {
            return -1;
        } else {
            return 0;
        }
    }
}
