package com.qiuwuyu.util;

import com.qiuwuyu.io.FileReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author paralog
 * @date 2022/11/6 9:38
 * 线段二分法得到镜面反射点和卫星高度角
 * 注意没有使用WGS-84椭圆模型，所以精度大概差10km左右
 */
public class GetPoint {
    private static final double xianLatitude = 34.445;
    private static final double xianLongitude = 109.494;
    private static final double xianAltitude = 0.577;
    @Autowired
    private static FileReader fileReader;
    private static double[] receiver = new double[3];

    static {
        double e = 0.0818191908426;
        double N = (6378.137) / Math.sqrt(1 - e * e * Math.sin(xianLatitude) * Math.sin(xianLatitude));
        receiver[0] = (N + xianAltitude) * Math.cos(xianLatitude) * Math.cos(xianLongitude) * 2;
        receiver[1] = (N + xianAltitude) * Math.cos(xianLatitude) * Math.sin(xianLongitude) * 2;
        receiver[2] = (N * (1 - e * e) + xianAltitude) * Math.sin(xianLatitude) * 2;
    }

    public static List<List<Double>> getPoint(String filePath) {
        List<List<Double>> res = new ArrayList<>();
        ArrayList<Double> list = null;
        List<String> strings = fileReader.readTxtFile(filePath);
        for (int i = 0; i < strings.size(); ++i) {
            if (strings.get(i).contains("gps")) {
                if (list == null) {
                    list = new ArrayList<>();
                } else {
                    res.add(new ArrayList<>(list));
                    list = new ArrayList<>();
                }
            } else {
                if (strings.get(i).contains("2022")) {
                    list.add(Math.toDegrees(getAngle(receiver, strings.get(i))));
                }
            }
        }
        return res;
    }

    public static List<List<Double>> getPointBySyn(String filePath) {
        List<List<Double>> res = new ArrayList<>();
        ArrayList<Double> list = null;
        List<String> strings = fileReader.readTxtFile(filePath);
        for (int i = 0; i < strings.size(); ++i) {
            if (strings.get(i).contains("Svn")) {
                if (list == null) {
                    list = new ArrayList<>();
                } else {
                    res.add(new ArrayList<>(list));
                    list = new ArrayList<>();
                }
            } else {
                if (strings.get(i).contains("2022")) {
                    list.add(Math.toDegrees(getAngle(receiver, strings.get(i))));
                }
                if (i == strings.size() - 1) {
                    res.add(new ArrayList<>(list));
                    list = null;
                }
            }
        }
        return res;
    }

    public static double getAngle(double[] receiver, String tran) {
        String[] split = tran.split("\\s+");
        double[] transmitter = new double[3];
        transmitter[0] = Double.parseDouble(split[4]);
        transmitter[1] = Double.parseDouble(split[5]);
        transmitter[2] = Double.parseDouble(split[6]);
        double res = dichotomyAlgo(receiver, transmitter);
        return res;
    }

    public static double dichotomyAlgo(double[] receiver, double[] transmitter) {
        double[] m = new double[3];
        m[0] = 0.5 * (receiver[0] + transmitter[0]);
        m[1] = 0.5 * (receiver[1] + transmitter[1]);
        m[2] = 0.5 * (receiver[2] + transmitter[2]);
        //使用的是地球圆模型
        double OS = 6371.393;
        //地球原点指向线段重点M
        double OM = Math.sqrt(m[0] * m[0] + m[1] * m[1] + m[2] * m[2]);
        //k表示OS比OM
        double k = OS / OM;
        //s坐标
        double[] s = new double[]{k * m[0], k * m[1], k * m[2]};
        //地球原点指向接收机
        double OR = Math.sqrt(receiver[0] * receiver[0] + receiver[1] * receiver[1] + receiver[2] * receiver[2]);
        //地球原点指向发射机
        double OT = Math.sqrt(transmitter[0] * transmitter[0] + transmitter[1] * transmitter[1] + transmitter[2] * transmitter[2]);
        //RS长度(受地球模型影响)
        double RS = Math.sqrt((receiver[0] - s[0]) * (receiver[0] - s[0]) + (receiver[1] - s[1]) * (receiver[1] - s[1]) + (receiver[2] - s[2]) * (receiver[2] - s[2]));
        //TS长度(收地球模型影响)
        double TS = Math.sqrt((transmitter[0] - s[0]) * (transmitter[0] - s[0]) + (transmitter[1] - s[1]) * (transmitter[1] - s[1]) + (transmitter[2] - s[2]) * (transmitter[2] - s[2]));
        double ar = ((RS * RS + OS * OS - OR * OR) / (RS * 2 * OS));
        double at = (TS * TS + OS * OS - OT * OT) / (TS * 2 * OS);
        double angleR = Math.acos(ar) - Math.PI / 2;
        double angleT = Math.acos(at) - Math.PI / 2;
        if (Math.abs(angleR - angleT) <= 0.00000573) {
            System.out.println(s[0] + " , " + s[1] + " , "  + s[2]);
            return angleT;
        } else {
            if (angleT > angleR) {
                return dichotomyAlgo(receiver, m);
            } else {
                return dichotomyAlgo(m, transmitter);
            }
        }
    }

    public static List<List<Double>> getPointByCYGNSS(String receiverPath, String transmitterPath) {
        List<List<Double>> res = new ArrayList<>();
        ArrayList<Double> list = new ArrayList<>();
        List<String> stringReceivers = fileReader.readTxtFile(receiverPath);
        List<String> stringTransmitters = fileReader.readTxtFile(transmitterPath);
        for (int i = 0; i < stringTransmitters.size(); ++i) {
            String[] split = stringReceivers.get(i).split("\\s+");
            double[] receiver = new double[3];
            receiver[0] = Double.parseDouble(split[4]);
            receiver[1] = Double.parseDouble(split[5]);
            receiver[2] = Double.parseDouble(split[6]);
            list.add(Math.toDegrees(getAngle(receiver, stringTransmitters.get(i))));
        }
        res.add(list);
        return res;
    }

    public static double dichotomyAlgoByWGS84(double[] receiver, double[] transmitter) {
        double[] m = new double[3];
        m[0] = 0.5 * (receiver[0] + transmitter[0]);
        m[1] = 0.5 * (receiver[1] + transmitter[1]);
        m[2] = 0.5 * (receiver[2] + transmitter[2]);
        //根据WGS84计算，但是目前OS数值存在问题
        double OS = ECEFToNED.getOS(m);
        System.out.println(OS);
        //地球原点指向线段重点M
        double OM = Math.sqrt(m[0] * m[0] + m[1] * m[1] + m[2] * m[2]);
        //k表示OS比OM
        double k = OS / OM;
        //s坐标
        double[] s = new double[]{k * m[0], k * m[1], k * m[2]};
        //地球原点指向接收机
        double OR = Math.sqrt(receiver[0] * receiver[0] + receiver[1] * receiver[1] + receiver[2] * receiver[2]);
        //地球原点指向发射机
        double OT = Math.sqrt(transmitter[0] * transmitter[0] + transmitter[1] * transmitter[1] + transmitter[2] * transmitter[2]);
        //RS长度(受地球模型影响)
        double RS = Math.sqrt((receiver[0] - s[0]) * (receiver[0] - s[0]) + (receiver[1] - s[1]) * (receiver[1] - s[1]) + (receiver[2] - s[2]) * (receiver[2] - s[2]));
        //TS长度(收地球模型影响)
        double TS = Math.sqrt((transmitter[0] - s[0]) * (transmitter[0] - s[0]) + (transmitter[1] - s[1]) * (transmitter[1] - s[1]) + (transmitter[2] - s[2]) * (transmitter[2] - s[2]));
        double ar = ((RS * RS + OS * OS - OR * OR) / (RS * 2 * OS));
        double at = (TS * TS + OS * OS - OT * OT) / (TS * 2 * OS);
        double angleR = Math.toDegrees(Math.acos(ar)) - 90;
        double angleT = Math.toDegrees(Math.acos(at)) - 90;
        if (Math.abs(angleR - angleT) <= 0.00000573) {
            System.out.println(s[0] + " , " + s[1] + " , "  + s[2]);
            return angleT;
        } else {
            if (angleT > angleR) {
                return dichotomyAlgo(receiver, m);
            } else {
                return dichotomyAlgo(m, transmitter);
            }
        }
    }
}
