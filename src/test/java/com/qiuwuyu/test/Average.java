package com.qiuwuyu.test;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paralog
 * @date 2022/12/15 17:16
 */
public class Average {

    @Test
    public void testXianToPRN16LatAndLon(){
        List<Double> lat = com.qiuwuyu.util.Average.getLat("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\XianToPRN16.txt");
        List<Double> lon = com.qiuwuyu.util.Average.getLon("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\XianToPRN16.txt");
        System.out.println(lat.toString());
        System.out.println(lon.toString());
        double max = 0;
        for(int i = 0 ; i < lat.size() ; ++i){
            if(max < lat.get(i)){
                max = lat.get(i);
            }
        }
        System.out.println(max);
        max = 0;
        for(int i = 0 ; i < lon.size() ; ++i){
            if(max < lon.get(i)){
                max = lon.get(i);
            }
        }
        System.out.println(max);
    }

    @Test
    public void testAccessRandom8() {
        String[] average50 = com.qiuwuyu.util.Average.getAverage("D:\\Code\\Satellite\\src\\main\\resources\\ECEF\\AccessRandom8\\svn-50AER.txt");
    }

    @Test
    public void test() {
        String[] average41 = com.qiuwuyu.util.Average.getAverage("D:\\Code\\Satellite\\src\\main\\resources\\ECEF\\AccessRandom8\\svn-TestAER.txt");
    }

    @Test
    public void testDiff1() {
        List<Double> average1 = com.qiuwuyu.util.Average.getDiffByL1("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\XianToPRN16.txt");
        System.out.println(average1.toString());
        double max = 0;
        for (double d : average1) {
            if (d > max) {
                max = d;
            }
        }
        System.out.println("max = " + max);
    }

    @Test
    public void testDiff2() {
        List<Double> average2 = com.qiuwuyu.util.Average.getDiffByL2("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\XianToPRN16.txt");
        System.out.println(average2.toString());
        double max = 0;
        for (double d : average2) {
            if (d > max) {
                max = d;
            }
        }
        System.out.println("max = " + max);
    }

    @Test
    public void testAllSates() {
        List<List<Double>> sates = com.qiuwuyu.util.Average.getLists("D:\\Code\\Satellite\\src\\main\\resources\\ECEF\\AccessRandom8\\allsats.txt");
        for (int i = 0; i < 30; ++i) {
            double max = 0;
            for (int j = 0; j < sates.get(i).size(); ++j) {
                if (max < sates.get(i).get(j)) {
                    max = sates.get(i).get(j);
                }
            }
            System.out.println(max);
        }
    }

    @Test
    public void testAllSates1() {
        List<List<Double>> sates = com.qiuwuyu.util.Average.getLists("D:\\Code\\Satellite\\src\\main\\resources\\ECEF\\AccessRandom8\\allsats.txt");
        for (int i = 20; i < 30; ++i) {
            List<Double> list = new ArrayList<>();
            for (int j = 6; j < sates.get(i).size() - 6; ++j) {
                list.add(sates.get(i).get(j));
            }
            System.out.println(list.toString() + ";");
        }
    }

    @Test
    public void testAllSates2() {
        List<List<Double>> sates = com.qiuwuyu.util.Average.getLists("D:\\Code\\Satellite\\src\\main\\resources\\ECEF\\AccessRandom8\\allsats.txt");
        for (int i = 0; i < 30; ++i) {
            for (int j = 0; j < sates.get(i).size(); ++j) {
                if (sates.get(i).get(j) > 1) {
                    System.out.println(i);
                }
            }
        }
        List<Double> list26 = sates.get(26);
        List<Double> list25 = sates.get(25);
        List<Double> list1 = new ArrayList<>();
        List<Double> list2 = new ArrayList<>();
        List<Double> list3 = new ArrayList<>();
        List<Double> list27 = sates.get(27);
        for (int i = 6; i < list26.size() - 6; ++i) {
            if(list26.get(i) < 0.5){
                list1.add(list26.get(i));
            }
        }
        for (int i = 6; i < list27.size() - 6; ++i) {
            if(list27.get(i) < 0.5){
                list2.add(list27.get(i));
            }
        }
        System.out.println(list1.toString());
        System.out.println(list2.toString());
        double max = 0;
        for (int i = 0 ; i < list1.size() ; ++i){
            if(max < list1.get(i)){
                max = list1.get(i);
            }
        }
        System.out.println(max);
        max = 0;
        for (int i = 0 ; i < list2.size() ; ++i){
            if(max < list2.get(i)){
                max = list2.get(i);
            }
        }
        System.out.println(max);
        for (int i = 6; i < list25.size() - 6; ++i) {
            if(list25.get(i) < 0.5){
                list3.add(list25.get(i));
            }
        }
        max = 0;
        for (int i = 0 ; i < list3.size() ; ++i){
            if(max < list3.get(i)){
                max = list3.get(i);
            }
        }
        System.out.println(max);
    }

    @Test
    public void getPrn16s(){
        List<Double> doubles = com.qiuwuyu.util.Average.getLat("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN16S.txt");
        List<Double> diff = com.qiuwuyu.util.Average.getDiff(doubles);
        System.out.println(diff + ";");
    }
}
