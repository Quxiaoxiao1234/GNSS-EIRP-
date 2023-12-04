package com.qiuwuyu.test;

import com.qiuwuyu.util.CalculateSpecularReflection;
import com.qiuwuyu.util.ECEFToNED;
import com.qiuwuyu.util.GetPoint;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author paralog
 * @date 2022/12/5 16:13
 */
public class Point {

    @Test
    public void testECEFByWGS843() {
        double[] recs = new double[]{-3469688.021 ,  5826237.147   , 4527639.27175};
        double[] trans = new double[]{-11178791.99129, -13160191.20499, 20341528.12754};
        System.out.println(CalculateSpecularReflection.dichotomyAlgoByWGS84(recs, trans));
    }

    @Test
    public void test(){
        double down = Math.sqrt(-11982698.916 * -11982698.916 + -23127631.879 * -23127631.879);
        double x1 = -23127631.879;
        double z1 = -1366322.127;
        System.out.println(360 - Math.toDegrees(Math.acos(x1 / down)));
        System.out.println(Math.toDegrees(Math.acos(z1 / down)));
    }

    @Test
    public void testECEFByWGS841() {
        double[] recs = new double[]{-4069896.70338, -3583236.96373, 4527639.27175};
        double[] trans = new double[]{-11178791.99129, -13160191.20499, 20341528.12754};
        System.out.println(CalculateSpecularReflection.dichotomyAlgoByWGS84(recs, trans));
    }

    @Test
    public void testECEFByWGS842() {
        double[] recs = new double[]{-335492.491, -6460107.391, -2813071.858};
        double[] trans = new double[]{1366322.127, -23127631.879, -11982698.916};
        System.out.println(CalculateSpecularReflection.dichotomyAlgoByWGS84(recs, trans));
    }

    @Test
    public void testMatrix() {
        int[][] first = new int[][]{
                {-1, 0, 0},
                {0, -1, 0},
                {0, 0, 1}
        };
        int[][] second = new int[][]{
                {0, 0, -1},
                {0, 1, 0},
                {1, 0, 0}
        };
        int[][] third = new int[][]{
                {-1, 0, 0},
                {0, -1, 0},
                {0, 0, 1}
        };
        int[][] fourth = new int[][]{{2}, {3}, {4}};
        int[][] ints = ECEFToNED.matrixMultiplication(first, second);
        int[][] temp = ECEFToNED.matrixMultiplication(ints, third);
        int[][] res = ECEFToNED.matrixMultiplication(temp, fourth);
        for (int[] is : res) {
            System.out.println(Arrays.toString(is));
        }
    }

    @Test
    public void testTable1() {
        double[] recs = new double[]{-4069.89670338, -3583.23696373, 4527.63927175};
        double[] trans = new double[]{-11178.79199129, -13160.19120499, 20341.52812754};
        System.out.println(Math.toDegrees(GetPoint.dichotomyAlgo(recs, trans)));
    }

    @Test
    public void testTable2() {
        double[] recs = new double[]{335.492, -6460.107, -2813.071};
        double[] trans = new double[]{1366.322, -23127.631, -11982.69891685};
        System.out.println(Math.toDegrees(GetPoint.dichotomyAlgo(recs, trans)));
    }
}
