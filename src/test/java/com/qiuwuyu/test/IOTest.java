package com.qiuwuyu.test;

import com.qiuwuyu.algorithm.SchedulingAlgorithm;
import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.io.FileReader;
import com.qiuwuyu.util.Access;
import com.qiuwuyu.util.CalculateSpecularReflection;
import com.qiuwuyu.util.ECEFToNED;
import com.qiuwuyu.util.GetPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author paralog
 * @date 2022/10/8 9:58
 */

public class IOTest {

    @Test
    public void testCYGNSS() {
        String receiver = "D:\\Code\\Satellite\\src\\main\\resources\\receiver.txt";
        String transmitter = "D:\\Code\\Satellite\\src\\main\\resources\\transmitter.txt";
        List<List<Double>> pointByCYGNSS = CalculateSpecularReflection.getPointByCYGNSS(receiver, transmitter);
        for (List<Double> list : pointByCYGNSS) {
            System.out.println(list.toString());
        }
    }

    @Test
    public void testGetSvnAngle() {
        String path = "D:\\Code\\Satellite\\src\\main\\resources\\svn-ECEF.txt";
        List<List<Double>> point = GetPoint.getPointBySyn(path);
        for (List<Double> list : point) {
            System.out.println(list.toString());
        }
    }

    @Test
    public void testAlgo() {
        SchedulingAlgorithm.executeByTime();
    }

    @Test
    public void testGetAverage() {
        String path = "D:\\Code\\Satellite\\src\\main\\resources\\AER.txt";
    }

    @Test
    public void test() {
        double[] r = new double[]{-4069986.703, -3583236.963, 4527639.271};
        double[] t = new double[]{-11178791.991, -13160191.204, 20341528.127};
        double[] r2 = new double[]{335.492703, -6460.107963, -2813.071271};
        double[] t2 = new double[]{1366.322991, -23127.631204, -11982.698127};
        double v = GetPoint.dichotomyAlgo(r2, t2);
    }

    @Test
    public void testGetAngle() {
        String path = "D:\\Code\\Satellite\\src\\main\\resources\\simple ECEF.txt";
        List<List<Double>> point = GetPoint.getPoint(path);
        for (List<Double> list : point) {
            System.out.println(list.toString());
        }
    }

    @Test
    public void testRedLine() {
        String line = "                      12    17 Sep 2022 00:04:33.295    17 Sep 2022 02:17:09.789          7956.494";
        String[] split = line.split("\\s+");
        System.out.println(split.length);

    }

    @Test
    public void testIO() {
        FileReader fileReader = new FileReader();
        List<String> strings = fileReader.readTxtFile("D:\\Code\\Satellite\\src\\main\\resources\\Access.txt");
        for (String s : strings) {
            System.out.println(s);
        }
    }

    @Test
    public void testRead() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<Sat> sats = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\Access.txt");
        for (Sat sat : sats) {
            System.out.println(sat);
        }
    }
}
