package com.qiuwuyu.util;

import com.qiuwuyu.algorithm.SingleSatellite;
import com.qiuwuyu.io.FileReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author paralog
 * @date 2023/2/21 19:46
 */
public class EIRP {

    private static double PRN3 = 14.77;
    private static double latitude = 34.153217;
    private static double longitude = 108.899623;
    private static double altitude = 0.41275;
    static double a = 6378.137; // WGS84 椭球体长半轴，单位为米
    static double b = 6356.7523142; // WGS84 椭球体短半轴，单位为米

    static double f = (a - b) / a; // WGS84 椭球体扁率
    static double e = Math.sqrt(2 * f - f * f); // WGS84 椭球体第一偏心率
    static double N = a / Math.sqrt(1 - e * e * Math.sin(latitude) * Math.sin(latitude)); // 子午线曲率半径，单位为米


    @Autowired
    private static FileReader fileReader;

    public static List<Double> getList(String filePath) {
        List<Double> res = new ArrayList<>();
        List<String> strings = fileReader.readTxtFile(filePath);
        for (int i = 0; i < strings.size(); ++i) {
            String str = strings.get(i);
            String[] split = str.split("\\s+");
            for (String s : split) {
                double aDouble = Double.parseDouble(s);
                res.add(aDouble);
            }
        }
        return res;
    }

    //离轴角
    public static List<Double> getOffSights(String filePath) {
        List<Double> res = new ArrayList<>();
        List<String> strings = fileReader.readTxtFile(filePath);
        for (int i = 0; i < strings.size(); ++i) {
            String str = strings.get(i);
            if (str.contains("2022")) {
                double angle = getAngle(str);
                res.add(angle);
            }

        }
        return res;
    }

    public static double getAngle(String tran) {
        String[] split = tran.split("\\s+");
        double[] transmitter = new double[3];
        transmitter[0] = Double.parseDouble(split[4]);
        transmitter[1] = Double.parseDouble(split[5]);
        transmitter[2] = Double.parseDouble(split[6]);
        double res = calculateOffBoresightAngle(transmitter[0], transmitter[1], transmitter[2]);
//        double res = isTrue(2888.2133, -5107.2314, 2493.6722, transmitter[0], transmitter[1], transmitter[2]);
        return res;
    }


    //计算离轴角
    public static double calculateOffBoresightAngle(double satX, double satY, double satZ) {
        double X = (N + altitude) * Math.cos(latitude) * Math.cos(longitude);
        double Y = (N + altitude) * Math.cos(latitude) * Math.sin(longitude);
        double Z = (N * (1 - e * e) + altitude) * Math.sin(latitude);
        double[] a = new double[]{satX, satY, satZ};
        double[] ab = new double[]{a[0] - X, a[1] - Y, a[2] - Z};
        double aLength = Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
        double abLength = Math.sqrt(ab[0] * ab[0] + ab[1] * ab[1] + ab[2] * ab[2]);
        double aab = a[0] * ab[0] + a[1] * ab[1] + a[2] * ab[2];
        double acos = Math.acos(aab / (aLength * abLength));
        return Math.toDegrees(acos);
    }

    //time by off angle
    public static List<List<Double>> getEIRPByTime(String filePath) {
        List<List<Double>> res = new ArrayList<>();
        List<Double> zeniths = new ArrayList<>();
        List<Double> zenithNoises = new ArrayList<>();
        List<Double> angles = EIRP.getOffSights(filePath);
        System.out.println(angles);
//        angles = calAngles(angles);
//        System.out.println(angles);
        List<Double> zenith = getList("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRNZ.txt");
        List<Double> zenithNoise = getList("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRNZN.txt");
        LinkedHashMap<Double, Double> zenMap = new LinkedHashMap<>();
        double angle = 0;
        for (int i = 0; i < zenith.size(); ++i) {
            angle = -15.0 + 0.1 * i;
            angle = new BigDecimal(angle).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            zenMap.put(angle, zenith.get(i));
        }
        LinkedHashMap<Double, Double> zenNoiseMap = new LinkedHashMap<>();
        for (int i = 0; i < zenithNoise.size(); ++i) {
            angle = -15.0 + 0.1 * i;
            angle = new BigDecimal(angle).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            zenNoiseMap.put(angle, zenithNoise.get(i));
        }
        for (int i = 0; i < angles.size() - 1; ++i) {
            //真实角度
            Double offAngle = angles.get(i);
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            //保留一位数的角度
            Double newAngle = Double.parseDouble(df.format(offAngle));

            Double aDouble = zenMap.get(newAngle);
            Double bDouble = zenNoiseMap.get(newAngle);
            //补偿增益
            double aDiff = offAngle - newAngle;
            //判断接下来角度上升还是下降
            boolean isR = (angles.get(i + 1) - angles.get(i)) > 0;
            if (isR) {
                Double format = Double.parseDouble(df.format(newAngle + 0.1));
                double aOffset = (zenMap.get(format) - zenMap.get(newAngle)) * (aDiff / 0.1);
                double bOffset = (zenNoiseMap.get(format) - zenNoiseMap.get(newAngle)) * (aDiff / 0.1);
                zeniths.add(aDouble + PRN3 + aOffset);
                zenithNoises.add(bDouble + PRN3 + bOffset);
            } else {
                Double format = Double.parseDouble(df.format(newAngle + 0.1));
                double aOffset = (zenMap.get(format) - zenMap.get(newAngle)) * (aDiff / 0.1);
                double bOffset = (zenNoiseMap.get(format) - zenNoiseMap.get(newAngle)) * (aDiff / 0.1);
                zeniths.add(aDouble + PRN3 + aOffset);
                zenithNoises.add(bDouble + PRN3 + bOffset);
            }
        }
        res.add(zeniths);
        res.add(zenithNoises);
        return res;
    }

    public static void getIndex(List<Double> list) {
        for (int i = 0; i < list.size(); ++i) {

        }
    }

    public static void main(String[] args) {
        List<Double> offSights = getOffSights("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN3ECEF.txt");
        System.out.println(offSights);
    }

    //time by off angle
    public static List<List<Double>> getEIRPByTime(String filePath, boolean isFake) {
        List<List<Double>> res = new ArrayList<>();
        List<Double> zeniths = new ArrayList<>();
        List<Double> zenithNoises = new ArrayList<>();
        //得到离轴角
        List<Double> angles = SingleSatellite.getOffAngle(filePath);
        List<Double> zenith = getList("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRNZ.txt");
        List<Double> zenithNoise = getList("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRNZN.txt");
        LinkedHashMap<Double, Double> zenMap = new LinkedHashMap<>();
        double angle = 0;
        for (int i = 0; i < zenith.size(); ++i) {
            angle = -15.0 + 0.1 * i;
            angle = new BigDecimal(angle).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            zenMap.put(angle, zenith.get(i));
        }
        LinkedHashMap<Double, Double> zenNoiseMap = new LinkedHashMap<>();
        for (int i = 0; i < zenithNoise.size(); ++i) {
            angle = -15.0 + 0.1 * i;
            angle = new BigDecimal(angle).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            zenNoiseMap.put(angle, zenithNoise.get(i));
        }
        for (int i = 0; i < angles.size() - 1; ++i) {
            //真实角度
            Double offAngle = angles.get(i);
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            Double newAngle = Double.parseDouble(df.format(offAngle));
            Double aDouble = zenMap.get(newAngle);
            Double bDouble = zenNoiseMap.get(newAngle);
            double aDiff = offAngle - newAngle;
            boolean isR = (angles.get(i + 1) - angles.get(i)) > 0;
            if (isR) {
                Double format = Double.parseDouble(df.format(newAngle + 0.1));
                double aOffset = (zenMap.get(format) - zenMap.get(newAngle)) * (aDiff / 0.1);
                double bOffset = (zenNoiseMap.get(format) - zenNoiseMap.get(newAngle)) * (aDiff / 0.1);
                zeniths.add(aDouble + PRN3 + aOffset);
                zenithNoises.add(bDouble + PRN3 + bOffset);
            } else {
                Double format = Double.parseDouble(df.format(newAngle + 0.1));
                double aOffset = (zenMap.get(format) - zenMap.get(newAngle)) * (aDiff / 0.1);
                double bOffset = (zenNoiseMap.get(format) - zenNoiseMap.get(newAngle)) * (aDiff / 0.1);
                zeniths.add(aDouble + PRN3 + aOffset);
                zenithNoises.add(bDouble + PRN3 + bOffset);
            }
        }
        res.add(zeniths);
        res.add(zenithNoises);
        return res;
    }

    public static double calculateStandardDeviation() {
        List<Double> list1 = com.qiuwuyu.util.EIRP.getList("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRNZ.txt");
        List<Double> list2 = com.qiuwuyu.util.EIRP.getList("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRNZN.txt");
//        double[] result = new double[2];
//        result[0] = calculateSD(list1);
//        result[1] = calculateSD(list2);
        double result = calculateSD(list1, list2);
        return result;
    }

    private static double calculateSD(List<Double> list) {
        double sum = 0;
        for (double num : list) {
            sum += num;
        }
        double mean = sum / list.size();
        System.out.println(mean);
        double squareSum = 0;
        for (double num : list) {
            squareSum += (num - mean) * (num - mean);
        }
        return Math.sqrt((double) (squareSum / (list.size() - 1)));
    }

    private static double calculateSD(List<Double> list1, List<Double> list2) {
        double sum = 0;
        for (int i = 0; i < list1.size(); ++i) {
            sum += (list2.get(i) - list1.get(i)) * (list2.get(i) - list1.get(i));
        }
        return Math.sqrt(sum / (list1.size() - 1));
    }

}
