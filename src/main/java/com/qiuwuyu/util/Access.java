package com.qiuwuyu.util;

import com.qiuwuyu.entity.Month;
import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.entity.Window;
import com.qiuwuyu.io.FileReader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paralog
 * @date 2022/10/8 10:13
 * 根据地面站可见时间窗口集合创建卫星集合List<Sat>
 */

public class Access {


    @Autowired
    private static FileReader fileReader;

    public static List<Sat> StringToObject(String filePath) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<Sat> lists = new ArrayList();
        List<String> strings = fileReader.readTxtFile(filePath);
        Sat sat = null;
        for (int i = 0; i < strings.size(); ++i) {
            String str = strings.get(i);
            if (str.contains("gps")) {
                if (sat != null) {
                    lists.add(sat);
                }
                sat = getSat(str);
            } else {
                Window window = getWindow(strings.get(i));
                if (window == null) {
                    continue;
                } else {
                    sat.getWindows().add(window);
                }
            }
        }
        lists.add(sat);
        return lists;
    }


    private static Sat getSat(String str) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class clazz = Class.forName("com.qiuwuyu.entity.Sat");
        Object obj = clazz.newInstance();
        if (obj instanceof Sat) {
            Sat sat = (Sat) obj;
            sat.setId(str);
            return sat;
        } else {
            System.out.println("find error");
            return null;
        }
    }

    private static Window getWindow(String str) {
        if (str.split("\\s+").length != 11 || !str.contains("2022")) {
            return null;
        }
        Window window = new Window();
        String[] arr = str.split("\\s+");
        int anInt = Integer.parseInt(arr[1]);
        window.setIDX(anInt);
        String time = arr[5].substring(0, 8);
        String year = arr[4];
        int month = Month.getMonth(arr[3]);
        String day = arr[2];
        String start = year + ":" + month + ":" + day + " " + time;
        String endTime = "" + arr[8] + ":" + Month.getMonth(arr[7]) + ":" + arr[6] + " " + arr[9].substring(0, 8);
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy:MM:dd HH:mm:ss");
        DateTime startDateTime = DateTime.parse(start, format);
        DateTime endDateTime = DateTime.parse(endTime, format);
        window.setStart(startDateTime);
        window.setEnd(endDateTime);
        window.setDuring((long) (Double.parseDouble(arr[10]) * 1000));
        return window;
    }

}






