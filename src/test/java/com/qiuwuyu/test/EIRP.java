package com.qiuwuyu.test;

import com.qiuwuyu.algorithm.SingleSatellite;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author paralog
 * @date 2023/2/21 21:03
 */

public class EIRP {

    @Test
    public void testEirpByTime() {
        List<List<Double>> eirps = com.qiuwuyu.util.EIRP.getEIRPByTime("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN3ECEF.txt");
        System.out.println(eirps.get(0).size() + ";");
        System.out.println(eirps.get(0).size() + ";");
    }


    @Test
    public void testGetAngles() {
        List<Double> angles = com.qiuwuyu.util.EIRP.getOffSights("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN6ECEF.txt");
        System.out.println(angles.size());
        System.out.println(angles);
    }

    @Test
    public void testGetOffAngles() {
        List<List<Double>> angles = com.qiuwuyu.util.EIRP.getEIRPByTime("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN16M.txt" , false);
        System.out.println(angles.toString());
    }


    @Test
    public void testGetOffBoreSight() {
        List<List<Double>> eirps = com.qiuwuyu.util.EIRP.getEIRPByTime("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN6ECEF.txt");
        List<Double> zenDiff = new ArrayList<>();
        List<Double> zenNoiseDiff = new ArrayList<>();
        double zenLast = eirps.get(0).get(0);
        for (int i = 1; i < eirps.get(0).size(); ++i) {
            zenDiff.add(Math.abs(eirps.get(0).get(i) - zenLast));
            zenLast = eirps.get(0).get(i);
        }
        double zenNoiseLast = eirps.get(1).get(0);
        for (int i = 1; i < eirps.get(1).size(); ++i) {
            zenNoiseDiff.add(Math.abs(eirps.get(1).get(i) - zenNoiseLast));
            zenNoiseLast = eirps.get(1).get(i);
        }
        System.out.println(zenDiff.size() + ";");
        System.out.println(zenDiff + ";");
        System.out.println(zenNoiseDiff.size() + ";");
        System.out.println(zenNoiseDiff + ";");
    }

    //统计监测步长
    @Test
    public void testGetOff() {

//        List<Double> offAngle = SingleSatellite.getOffAngle("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN3M.txt");
//        System.out.println(offAngle);


        List<Double> zenEIRP = com.qiuwuyu.util.EIRP.getEIRPByTime("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN16S.txt", true).get(0);
        List<Double> res = new ArrayList<>();
        for(int i = 1 ; i < 500 ; ++i){
            double last = zenEIRP.get(0);
            List<Double> temp = new ArrayList<>();
            for(int j = i ; j < zenEIRP.size() ; j+=i){
                temp.add(Math.abs(zenEIRP.get(i) - last));
            }
            Optional<Double> max = temp.stream().max(Double::compareTo);
            if (max.get() > 0.035){
                System.out.println(i);
            }
            res.add(max.get());
        }
        System.out.println(res);

//        List<Double> angles = SingleSatellite.getOffAngle("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN3M.txt");
//        List<Double> res = new ArrayList<>();

//        List<Double> ans = new ArrayList<>();
//        for(double d : angles){
//            res.add(Math.abs(d));
//        }
//        int index = 0;
//        for(int i = 1 ; i < angles.size() ; ++i){
//            if(res.get(i) - res.get(i - 1) <= 0){
//                index++;
//            }
//        }
//        for(int i = 1 ; i < index ; ++i){
//            ans.add(res.get(i) - res.get(i - 1));
//        }
//        for(int i = index ; i < res.size(); ++i){
//        }
//        for(int i = 1 ; i < angles.size() ; ++i){
//            res.add(Math.abs(angles.get(i) - angles.get(i - 1)));
//        }
//        System.out.println(res);
    }

    @Test
    public void testGetEIRP() {
        List<Double> zenith = com.qiuwuyu.util.EIRP.getList("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRNZ.txt");
        List<Double> zenithNoise = com.qiuwuyu.util.EIRP.getList("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRNZN.txt");
        List<Double> zenithRes = new ArrayList<>();
        List<Double> zenithNoiseRes = new ArrayList<>();
        for (int i = 0; i < zenith.size(); ++i) {
            zenithRes.add(zenith.get(i) + 14.77);
            zenithNoiseRes.add(zenithNoise.get(i) + 14.77);
        }
        System.out.println(zenithRes);
        System.out.println(zenithNoiseRes);
        List<Double> angles = new ArrayList<>();
        double start = -15;
        angles.add(start);
        for (int i = 0; i < 300; ++i) {
            start = start + 0.1;
            start = new BigDecimal(start).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            angles.add(start);
        }
        System.out.println(angles);
    }

    @Test
    //量化新方法与传统方法提升的精度
    public void getPrecision(){
        double doubles = com.qiuwuyu.util.EIRP.calculateStandardDeviation();
        System.out.println(doubles);
    }

    @Test
    public void testDiff1() {
//        String[] average50 = com.qiuwuyu.util.Average.getAverage("D:\\Code\\Satellite\\src\\main\\resources\\ECEF\\AccessRandom8\\PRN16S.txt");
        List<Double> angles = SingleSatellite.getOffAngle("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN16S.txt");
        System.out.println(angles.toString());
    }
}
