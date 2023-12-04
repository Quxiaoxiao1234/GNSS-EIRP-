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
 * @date 2022/11/28 15:47
 * 根据可见时间窗口Access.txt以及发射(接收)机在同一期间的tranECEF(recsECEF).txt得到高度角的变化规律
 */
public class Filter {
    @Autowired
    private static FileReader fileReader;

    private static List<Sat> sates;
    private static int transmitterIndex = 0;
    private static int receiverIndex = 0;
    private static List<String> trans = new ArrayList<>();
    private static List<String> recs = new ArrayList<>();

    static {
        try {
            sates = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\AccessTest.txt");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static List<List<Double>> executeByTime(String transPath, String recsPath) {
        System.out.println("*****************");
        List<List<Double>> res = new ArrayList<>();
        trans = fileReader.readTxtFile(transPath);
        recs = fileReader.readTxtFile(recsPath);
        List<Window> windows = sates.get(0).getWindows();
        for (int i = 0; i < windows.size(); ++i) {
            res.add(new ArrayList<>(getPoints(windows.get(i))));
        }
        System.out.println("*****************");
        System.out.println("end");
        return res;
    }

    private static List<Double> getPoints(Window window) {
        long start = window.getStart().getMillis();
        long end = window.getEnd().getMillis();
        while (transmitterIndex < trans.size() && (test(trans.get(transmitterIndex)) < start)) {
            transmitterIndex++;
        }
        while (receiverIndex < recs.size() && (test(recs.get(receiverIndex)) < start)) {
            receiverIndex++;
        }
        List<Double> list = new ArrayList<>();
        while (receiverIndex < recs.size() && test(recs.get(receiverIndex)) <= end) {
            String tranStr = trans.get(transmitterIndex);
            String recsStr = recs.get(receiverIndex);
            if(isE(tranStr)){
                transmitterIndex++;
            }
            if(isE(recsStr)){
                receiverIndex++;
            }
            if(test(recs.get(receiverIndex)) != test(trans.get(transmitterIndex))){
                String a1 = recs.get(receiverIndex);
                String t1 = trans.get(transmitterIndex);
                System.out.println("^^^^^^^^^");
                break;
            }
            String[] split = trans.get(transmitterIndex).split("\\s+");
            double[] transmitter = new double[3];
            transmitter[0] = Double.parseDouble(split[4]);
            transmitter[1] = Double.parseDouble(split[5]);
            transmitter[2] = Double.parseDouble(split[6]);
            split = recs.get(receiverIndex).split("\\s+");
            double[] receiver = new double[3];
            receiver[0] = Double.parseDouble(split[4]);
            receiver[1] = Double.parseDouble(split[5]);
            receiver[2] = Double.parseDouble(split[6]);
            double point = GetPoint.dichotomyAlgo(receiver, transmitter);
            list.add(Math.toDegrees(point));
            transmitterIndex++;
            receiverIndex++;
        }
        return list;
    }

    private static long test(String str) {
        String[] arr = str.split("\\s+");
        int len = str.split("\\s+").length;
        if (str.contains("Sep") && len == 7) {
            String time = arr[3].substring(0, 8);
            String year = arr[2];
            int month = Month.getMonth(arr[1]);
            String day = arr[0];
            String start = year + ":" + month + ":" + day + " " + time;
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy:MM:dd HH:mm:ss");
            return DateTime.parse(start, format).getMillis();
        } else {
            return -1;
        }
    }

    //有可能出现特殊情况
    public static boolean isE(String str){
        String[] split = str.split("\\s+");
        String substring = split[3].substring(6, 8);
        if(substring .equals("00")){
            return true;
        }else{
            return false;
        }
    }

}
