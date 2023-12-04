package com.qiuwuyu.entity;

import java.util.HashMap;
import java.util.Set;

/**
 * @author paralog
 * @date 2022/10/8 14:46
 */
public class Month {
    public static HashMap<String , Integer> map = new HashMap<>();

    static {
        map.put("Jan" , 1);
        map.put("Feb" , 2);
        map.put("Mar" , 3);
        map.put("Apr" , 4);
        map.put("May" , 5);
        map.put("Jun" , 6);
        map.put("Jul" , 7);
        map.put("Aug" , 8);
        map.put("Sep" , 9);
        map.put("Oct" , 10);
        map.put("Nov" , 11);
        map.put("Dec" , 12);
    }

    public static int getMonth(String mon){
        Set<String> strings = map.keySet();
        return map.get(mon);
    }


}
