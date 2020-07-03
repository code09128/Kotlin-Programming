package com.base.utilities;

import java.util.Comparator;

/*
 * Created by Eric on 2018/5/2.
 */
public class BaseSortUtil {
    private static BaseSortUtil baseSortUtil;

    /**資料排序模組*/
    public static BaseSortUtil getBaseSortUtil(){
        if(baseSortUtil == null){
            baseSortUtil = new BaseSortUtil();
        }

        return baseSortUtil;
    }

    /**行事曆月曆排序*/
    public Comparator<Integer> monthKey = new Comparator<Integer>(){
        @Override
        public int compare(Integer lhs, Integer rhs) {
            if(lhs > rhs){
                return 1;
            }

            if(lhs < rhs){
                return -1;
            }

            return 0;
        }
    };
}
