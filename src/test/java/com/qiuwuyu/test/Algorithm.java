package com.qiuwuyu.test;

import com.qiuwuyu.algorithm.*;
import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.util.Access;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author paralog
 * @date 2023/1/30 10:15
 */
public class Algorithm {

    // 8
    @Test
    public void testAccess8ByHRRN() {
        AlgorithmByHNNR.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess8.txt");
    }

    @Test
    public void testAccess8ByFCFS() {
        AlgorithmByHNNR.executeByTimeForPictureByFCFS("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess8.txt");
    }

    @Test
    public void testAccess8BySJF() {
        SchedulingAlgorithm.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess8.txt");
    }

    // 16
    @Test
    public void testAccess16ByHRRN() {
        AlgorithmByHNNR.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess16.txt");
    }

    @Test
    public void testAccess16ByFCFS() {
        AlgorithmByHNNR.executeByTimeForPictureByFCFS("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess16.txt");
    }

    @Test
    public void testAccess16BySJF() {
        SchedulingAlgorithm.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess16.txt");
    }

    // 31
    @Test
    public void testAccess31ByHRRN() {
        AlgorithmByHNNR.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
    }

    @Test
    public void testAccess31ByFCFS() {
        AlgorithmByHNNR.executeByTimeForPictureByFCFS("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
    }

    @Test
    public void testAccess31BySJF() {
        SchedulingAlgorithm.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
    }

    // 16 2
    @Test
    public void testAccess162ByHRRN() {
        AlgorithmByHNNR.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess16.txt");
    }

    @Test
    public void testAccess162ByFCFS() {
        AlgorithmByHNNR.executeByTimeForPictureByFCFS("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess16.txt");
    }

    @Test
    public void testAccess162BySJF() {
        SchedulingAlgorithm.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess16.txt");
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        double[] res = new double[]{18.41 , 0.9288};
        for(int i = 0 ; i < 1000 ; ++i){
            List<Sat> sates = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
            double[] temp = RandomlySelectSatellites.selectSates(sates, 29);
            res[0] = 0.5 * (res[0] + temp[0]);
            res[1] = 0.5 * (res[1] + temp[1]);
        }
        System.out.println(res[0] + " , " + res[1]);
    }

    //Average
    @Test
    public void Average8ByHRRN() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<Sat> sates = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
        double[] temp = RandomlySelectSatellites.selectSates(sates, 8);
        System.out.println(temp[0] + " , " + temp[1]);
    }

    //NoMInTime
    @Test
    public void noMinTime(){
        NoMInTimeAlgo.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\NewRandomAccess312.txt");
    }

    //NoMInTime
    @Test
    public void noMinTimeByFCFS(){
        NoMInTimeAlgo.executeByTimeForPictureByFCFS("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\NewRandomAccess312.txt");
    }

    //NoMinTimeAverage 141.2210 56.46
    @Test
    public void noMinTimeAverageByHRRN() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        double[] res = new double[]{0 , 0};
        for(int i = 0 ; i < 1000 ; ++i){
            List<Sat> sates = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
            double[] temp = NoMinTimeRandomALgo.selectSates(sates, 24);
            res[0] += temp[0];
            res[1] += temp[1];
        }
        res[0] = res[0] / 1000.0;
        res[1] = res[1] / 1000.0;
        System.out.println(res[0] + " , " + res[1]);
    }
    //NoMinTimeAverage 140.11 56.99
    @Test
    public void noMinTimeAverageByFCFS() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        double[] res = new double[]{0 , 0};
        for(int i = 0 ; i < 1000 ; ++i){
            List<Sat> sates = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
            double[] temp = NoMinTimeRandomALgo.selectSates(sates, 24);
            res[0] += temp[0];
            res[1] += temp[1];
        }
        res[0] = res[0] / 1000.0;
        res[1] = res[1] / 1000.0;
        System.out.println(res[0] + " , " + res[1]);
    }
    //NoMinTimeAverage 141.41 56.79
    @Test
    public void noMinTimeAverageBySJF() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        double[] res = new double[]{0 , 0};
        for(int i = 0 ; i < 1000 ; ++i){
            List<Sat> sates = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
            double[] temp = NoMinTimeRandomALgo.selectSates(sates, 12);
            res[0] += temp[0];
            res[1] += temp[1];
        }
        res[0] = res[0] / 1000.0;
        res[1] = res[1] / 1000.0;
        System.out.println(res[0] + " , " + res[1]);
    }

    //NoMInTime
    @Test
    public void noMinTimeBySJF(){
        NoMInTimeAlgo.executeByTimeForPictureBySJF("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\NewRandomAccess312.txt");
    }

    //仰角20
    @Test
    public void noMinTimeBySJF20(){
        int i = NoMInTimeAlgo.executeByTimeForPictureBySJF("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\GPS31.txt");
        System.out.println("size: " + i);
    }

    //仰角20
    @Test
    public void noMinTimeByFCFS20(){
        NoMInTimeAlgo.executeByTimeForPictureByFCFS("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\GPS31.txt");
    }

    //仰角20
    @Test
    public void noMinTimeByHRRN20(){
        NoMInTimeAlgo.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\GPS31.txt");
    }
}
