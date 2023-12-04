package com.qiuwuyu.util;

import com.qiuwuyu.io.FileReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paralog
 * @date 2023/1/22 15:30
 */
public class CalculateSpecularReflection {

    @Autowired
    private static FileReader fileReader;

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
            list.add(getAngle(receiver, stringTransmitters.get(i)));
        }
        res.add(list);
        return res;
    }

    public static double getAngle(double[] receiver, String tran) {
        String[] split = tran.split("\\s+");
        double[] transmitter = new double[3];
        transmitter[0] = Double.parseDouble(split[4]);
        transmitter[1] = Double.parseDouble(split[5]);
        transmitter[2] = Double.parseDouble(split[6]);
        double res = dichotomyAlgoByWGS84(receiver, transmitter);
        return res;
    }

    public static double dichotomyAlgoByWGS84(double[] receiver, double[] transmitter) {
        double[] O = new double[]{0 , 0 , 0};
        double[] m = new double[3];
        m[0] = 0.5 * (receiver[0] + transmitter[0]);
        m[1] = 0.5 * (receiver[1] + transmitter[1]);
        m[2] = 0.5 * (receiver[2] + transmitter[2]);
//        double OS = ECEFToNED.getOS(m);
//        double OS = ECEFToNED.getLength(new double[]{0,0,0} , m , new double[]{0,0,0});
        double OS = 6371393.00;
        double OM = getSegment(O , m);
        double k = OS / OM;
        double[] s = new double[]{k * m[0], k * m[1], k * m[2]};
        double OR = getSegment(O , receiver);
        double OT = getSegment(O , transmitter);
        double RS = getSegment(receiver , s);
        double TS = getSegment(transmitter , s);
        double ar = ((RS * RS + OS * OS - OR * OR) / (RS * 2 * OS));
        double at = (TS * TS + OS * OS - OT * OT) / (TS * 2 * OS);
//        double angleT = Math.acos(at) - (Math.PI / 2);
        double angleT = Math.abs(Math.toDegrees(1 / Math.cos(at) - (Math.PI / 2)));
//        double angleR = Math.acos(ar) - (Math.PI / 2);
        double angleR = Math.abs(Math.toDegrees(1 / Math.cos(ar) - (Math.PI / 2)));
        System.out.println("aR = " + angleR + " , " + "aT = " + angleT);
        if (Math.abs(angleR - angleT) <= 0.00001) {
            System.out.println(s[0] + " , " + s[1] + " , "  + s[2]);
            return angleT;
        } else {
            if (angleT > angleR) {
                return dichotomyAlgoByWGS84(receiver, m);
            } else {
                return dichotomyAlgoByWGS84(m, transmitter);
            }
        }
    }

    private static double getSegment(double[] s1 , double[] s2){
        return Math.sqrt((s1[0] - s2[0]) * (s1[0] - s2[0]) + (s1[1] - s2[1]) * (s1[1] - s2[1]) + (s1[2] - s2[2]) * (s1[2] - s2[2]));
    }
}
