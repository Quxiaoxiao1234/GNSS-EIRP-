package com.qiuwuyu.test;

import com.qiuwuyu.algorithm.AlgorithmByHNNR;
import com.qiuwuyu.algorithm.SchedulingAlgorithm;
import com.qiuwuyu.util.Filter;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author paralog
 * @date 2022/11/28 20:11
 */
public class GPS {

    @Test
    public void testAccess24(){
        SchedulingAlgorithm.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
        double during = 4584;
        double total = 91.5 * 60;
        double duringAbsoluteTime = 4567;
        double totalAbsoluteTime = 91.5 * 60;
        System.out.println(during / total);
        System.out.println(duringAbsoluteTime / totalAbsoluteTime);
    }

    @Test
    public void testAccess8ByHRRN() {
        AlgorithmByHNNR.executeByTime("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess8.txt");
    }

    @Test
    public void testAccess16ByFCFS() {
        AlgorithmByHNNR.executeByTimeForPictureByFCFS("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess16.txt");
    }


    @Test
    public void testAccess16ByHRRN(){
        AlgorithmByHNNR.executeByTime("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess31.txt");
    }

    @Test
    public void testAccess8() {
        SchedulingAlgorithm.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess8.txt");
    }

    @Test
    public void testAccess16() {
        SchedulingAlgorithm.executeByTimeForPicture("D:\\Code\\Satellite\\src\\main\\resources\\GPS\\RandomAccess16.txt");
        double during = 2455;
        double total = 60.46 * 60;
        System.out.println(during / total);
    }

    @Test
    public void testGetPoints() {
        String tranPath = "D:\\Code\\Satellite\\src\\main\\resources\\GPS\\GPS-svn71-ECEF.txt";
        String recsPath = "D:\\Code\\Satellite\\src\\main\\resources\\GPS\\GPS-T.txt";
        List<List<Double>> lists = Filter.executeByTime(tranPath, recsPath);
        int better = 0;
        int lower = 0;
        for (List<Double> list : lists) {
            for (double point : list) {
                if (point > 45) {
                    better++;
                } else {
                    lower++;
                }
            }
            System.out.println(list.toString());
        }
        System.out.println("-------------------------------------------------");
        System.out.println(better + " , " + lower);
    }

    @Test
    public void getVisibleWindows() {
        SchedulingAlgorithm.executeByTime();
    }
}
