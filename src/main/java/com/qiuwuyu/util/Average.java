package com.qiuwuyu.util;

import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.io.FileReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paralog
 * @date 2022/11/8 10:09
 * 根据STK的AER功能生成的NED.txt计算平均方位角，俯仰角变化情况，最大方位角俯仰角变化情况
 */
public class Average {

    @Autowired
    private static FileReader fileReader;

    public static List<Sat> svnToPrn(List<Sat> sates) {
        String[][] mappingMap = new String[][]{
                {"svn41", "prn14"}, {"svn43", "prn13"}, {"svn45", "prn21"}, {"svn48", "prn07"}, {"svn50", "prn05"}, {"svn51", "prn20"}, {"svn52", "prn31"}, {"svn53", "prn17"},
                {"svn55", "prn15"}, {"svn56", "prn16"}, {"svn57", "prn29"}, {"svn58", "prn12"}, {"svn59", "prn19"}, {"svn61", "prn02"}, {"svn62", "prn25"}, {"svn63", "prn01"},
                {"svn64", "prn30"}, {"svn65", "prn24"}, {"svn66", "prn27"}, {"svn67", "prn06"}, {"svn68", "prn09"}, {"svn69", "prn03"}, {"svn70", "prn32"}, {"svn71", "prn26"},
                {"svn72", "prn08"}, {"svn73", "prn10"}, {"svn74", "prn04"}, {"svn75", "prn18"}, {"svn76", "prn23"}, {"svn77", "prn22"}, {"svn78", "prn11"}
        };
        for (int i = 0; i < sates.size(); ++i) {
            for (int j = 0; j < mappingMap.length; ++j) {
                String id = sates.get(i).getId();
                if (id.contains(mappingMap[j][0])) {
                    String newId = id.replaceAll(mappingMap[j][0], mappingMap[j][1]);
                    sates.get(i).setId(newId);
                    break;
                }
            }
        }
        return sates;
    }

    public static String[] getAverage(String filePath) {
        double[] res = new double[3];
        List<Double> listA = new ArrayList<>();
        List<Double> listB = new ArrayList<>();
        List<String> strings = fileReader.readTxtFile(filePath);
        double preA = 0;
        double preB = 0;
        for (int i = 0; i < strings.size(); ++i) {
            //符合AER格式
            if (strings.get(i).split("\\s+").length == 8 && strings.get(i).contains("2022")) {
                //分为AER三要素
                double[] doubles = readString(strings.get(i));
                if (preA == 0 && preB == 0) {
                    preA = doubles[0];
                    preB = doubles[1];
                } else {
                    if (res[0] == 0 && res[1] == 0) {
                        double a = Math.abs(doubles[0] - preA);
                        if (a > 180) {
                            a = 360 - a;
                        }
                        double b = Math.abs(doubles[1] - preB);
                        if (b > 180) {
                            b = 360 - b;
                        }
                        preA = doubles[0];
                        preB = doubles[1];
                        listA.add(a);
                        listB.add(b);
                        res[0] = a;
                        res[1] = b;
                        res[2] = doubles[2];
                    } else {
                        double a = Math.abs(doubles[0] - preA);
                        if (a > 180) {
                            a = 360 - a;
                        }
                        double b = Math.abs(doubles[1] - preB);
                        if (b > 180) {
                            b = 360 - b;
                        }
                        preA = doubles[0];
                        preB = doubles[1];
                        listA.add(a);
                        listB.add(b);
                        res[0] = 0.5 * (a + res[0]);
                        res[1] = 0.5 * (b + res[1]);
                        res[2] = 0.5 * (doubles[2] + res[2]);
                    }
                }
            } else {
                preA = 0;
                preB = 0;
            }
        }
        List<Double> newListB = new ArrayList<>(listB.size());
        for(double d : listB){
            newListB.add(d + 0.65);
        }
        System.out.println(listA);
        System.out.println(newListB);
        double maxA = 0;
        double maxB = 0;
        double resA = 0;
        double resB = 0;
        for (double d : listA) {
            if (d > maxA) {
                maxA = d;
            }
            resA += d;
        }
        resA = resA / listA.size();
        for (double d : listB) {
            if (d > maxB) {
                maxB = d;
            }
            resB += d;
        }
        resB = resB / listB.size();
        String[] strs = new String[2];
        strs[0] = "maxA: " + maxA + " , maxB: " + maxB;
        strs[1] = "resA: " + resA + " , resB: " + resB;
        return strs;
    }

    public static List<List<Double>> getLists(String filePath) {
        List<List<Double>> res = new ArrayList<>();
        List<String> strings = fileReader.readTxtFile(filePath);
        List<String> list = new ArrayList<>();
        String str = "";
        for (int i = 0; i < strings.size(); ++i) {
            if (str.contains("svn") && strings.get(i).contains("svn")) {
                List<Double> diff = getDiffByL1(list);
                res.add(new ArrayList<>(diff));
                list = new ArrayList<>();
            }
            if (str == "" && strings.get(i).contains("svn")) {
                str = strings.get(i);
            }
            list.add(strings.get(i));
        }
        List<Double> diff = getDiffByL1(list);
        res.add(new ArrayList<>(diff));
        return res;
    }

    //方位角
    public static List<Double> getDiffByL1(List<String> strings) {
        double[] res = new double[3];
        List<Double> listA = new ArrayList<>();
        List<Double> listB = new ArrayList<>();
        double preA = 0;
        double preB = 0;
        for (int i = 0; i < strings.size(); ++i) {
            //符合AER格式
            if (strings.get(i).split("\\s+").length == 8 && strings.get(i).contains("2022")) {
                //分为AER三要素
                double[] doubles = readString(strings.get(i));
                if (preA == 0 && preB == 0) {
                    preA = doubles[0];
                    preB = doubles[1];
                } else {
                    if (res[0] == 0 && res[1] == 0) {
                        double a = Math.abs(doubles[0] - preA);
                        if (a > 180) {
                            a = 360 - a;
                        }
                        double b = Math.abs(doubles[1] - preB);
                        if (b > 180) {
                            b = 360 - b;
                        }
                        preA = doubles[0];
                        preB = doubles[1];
                        listA.add(a);
                        listB.add(b);
                        res[0] = a;
                        res[1] = b;
                        res[2] = doubles[2];
                    } else {
                        double a = Math.abs(doubles[0] - preA);
                        if (a > 180) {
                            a = 360 - a;
                        }
                        double b = Math.abs(doubles[1] - preB);
                        if (b > 180) {
                            b = 360 - b;
                        }
                        preA = doubles[0];
                        preB = doubles[1];
                        if (a < 1.4) {
                            listA.add(a);
                        }
                        listB.add(b);
                        res[0] = 0.5 * (a + res[0]);
                        res[1] = 0.5 * (b + res[1]);
                        res[2] = 0.5 * (doubles[2] + res[2]);
                    }
                }
            } else {
                preA = 0;
                preB = 0;
            }
        }
        return listB;
    }

    //方位角
    public static List<Double> getDiffByL1(String filePath) {
        double[] res = new double[3];
        List<Double> listA = new ArrayList<>();
        List<String> strings = fileReader.readTxtFile(filePath);
        double preA = 0;
        double preB = 0;
        for (int i = 0; i < strings.size(); ++i) {
            //符合AER格式
            if (strings.get(i).split("\\s+").length == 8 && strings.get(i).contains("2022")) {
                //分为AER三要素
                double[] doubles = readString(strings.get(i));
                if (preA == 0 && preB == 0) {
                    preA = doubles[0];
                } else {
                    if (res[0] == 0 && res[1] == 0) {
                        double a = Math.abs(doubles[0] - preA);
                        if (a > 180) {
                            a = 360 - a;
                        }
                        double b = Math.abs(doubles[1] - preB);
                        if (b > 180) {
                            b = 360 - b;
                        }
                        preA = doubles[0];
                        preB = doubles[1];
                        listA.add(a);
                        res[0] = a;
                        res[1] = b;
                        res[2] = doubles[2];
                    } else {
                        double a = Math.abs(doubles[0] - preA);
                        if (a > 180) {
                            a = 360 - a;
                        }
                        preA = doubles[0];
                        preB = doubles[1];
                        listA.add(a);
                        res[0] = 0.5 * (a + res[0]);
                    }
                }
            } else {
                preA = 0;
                preB = 0;
            }
        }
        return  listA;
    }
    //俯仰角
    public static List<Double> getDiffByL2(String filePath) {
        double[] res = new double[3];
        List<Double> listA = new ArrayList<>();
        List<Double> listB = new ArrayList<>();
        List<String> strings = fileReader.readTxtFile(filePath);
        double preA = 0;
        double preB = 0;
        for (int i = 0; i < strings.size(); ++i) {
            //符合AER格式
            if (strings.get(i).split("\\s+").length == 8 && strings.get(i).contains("2022")) {
                //分为AER三要素
                double[] doubles = readString(strings.get(i));
                if (preA == 0 && preB == 0) {
                    preA = doubles[0];
                    preB = doubles[1];
                } else {
                    if (res[0] == 0 && res[1] == 0) {
                        double a = Math.abs(doubles[0] - preA);
                        if (a > 180) {
                            a = 360 - a;
                        }
                        double b = Math.abs(doubles[1] - preB);
                        if (b > 180) {
                            b = 360 - b;
                        }
                        preA = doubles[0];
                        preB = doubles[1];
                        if (a < 2) {
                            listA.add(a);
                        }
                        listB.add(b);
                        res[0] = a;
                        res[1] = b;
                        res[2] = doubles[2];
                    } else {
                        double a = Math.abs(doubles[0] - preA);
                        if (a > 180) {
                            a = 360 - a;
                        }
                        double b = Math.abs(doubles[1] - preB);
                        if (b > 180) {
                            b = 360 - b;
                        }
                        preA = doubles[0];
                        preB = doubles[1];
                        listA.add(a);
                        listB.add(b);
                        res[0] = 0.5 * (a + res[0]);
                        res[1] = 0.5 * (b + res[1]);
                        res[2] = 0.5 * (doubles[2] + res[2]);
                    }
                }
            } else {
                preA = 0;
                preB = 0;
            }
        }
        return listB;
    }

    public static double[] readString(String line) {
        String[] strings = line.split("\\s+");
        double[] res = new double[3];
        res[0] = Double.parseDouble(strings[5]);
        res[1] = Double.parseDouble(strings[6]);
        res[2] = Double.parseDouble(strings[7]);
        return res;
    }


    public static List<Double> getLat(String filePath){
        List<Double> res = new ArrayList<>();
        List<String> strings = fileReader.readTxtFile(filePath);
        for(int i = 0 ; i < strings.size() ; ++i){
            String str = strings.get(i);
            if (strings.get(i).split("\\s+").length == 8 && strings.get(i).contains("2022")) {
                double[] doubles = readString(str);
                res.add(doubles[0]);
            }
        }
        return res;
    }

    public static List<Double> getLon(String filePath){
        List<Double> res = new ArrayList<>();
        List<String> strings = fileReader.readTxtFile(filePath);
        for(int i = 0 ; i < strings.size() ; ++i){
            String str = strings.get(i);
            if (strings.get(i).split("\\s+").length == 8 && strings.get(i).contains("2022")) {
                double[] doubles = readString(str);
                res.add(doubles[1]);
            }
        }
        return res;
    }

    public static List<Double> getDiff(List<Double> list){
        List<Double> res = new ArrayList<>();
        int length = list.size();
        double last = list.get(0);
        for(int i = 1 ; i < length ; ++i){
            res.add(list.get(i) - last);
            last = list.get(i);
        }
        return res;
    }
}
