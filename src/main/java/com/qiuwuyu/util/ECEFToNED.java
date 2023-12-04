package com.qiuwuyu.util;

import java.util.Arrays;

/**
 * @author paralog
 * @date 2023/1/21 9:17
 */
public class ECEFToNED {

    private static final double a = 6378137; // radius
    private static final double e = 8.1819190842622e-2;  // eccentricity
    private static final double b = 6356752.3142;
    private static final double c = 6399593.6258;

//    private static double[][] M = new double[][]{
//            {0 , 0 , 1},
//            {0 , 1 , 0},
//            {-1 , 0 , 0}
//    };

    private static final double asq = Math.pow(a, 2);
    private static final double esq = Math.pow(e, 2);

    private static double[] ecef2lla(double[] ecef) {
        double x = ecef[0];
        double y = ecef[1];
        double z = ecef[2];
        double b = Math.sqrt(asq * (1 - esq));
        double bsq = Math.pow(b, 2);
        double ep = Math.sqrt((asq - bsq) / bsq);
        double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double th = Math.atan2(a * z, b * p);
        double lon = Math.atan2(y, x);
        double lat = Math.atan2((z + Math.pow(ep, 2) * b * Math.pow(Math.sin(th), 3)), (p - esq * a * Math.pow(Math.cos(th), 3)));
        double N = a / (Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2)));
        double alt = p / Math.cos(lat) - N;
        lon = lon % (2 * Math.PI);
        double[] ret = {lat, lon, alt};

        return ret;
    }

    public static void main(String[] args) {
//        getLength(new double[]{0,0,0} , new double[]{-11178791.99129, -13160191.20499, 20341528.12754} , new double[]{0,0,0});
        double[] doubles = ecef2lla(new double[]{0, 0, 0});
        System.out.println(Arrays.toString(doubles));
    }

    public static int[][] matrixMultiplication(int[][] first, int[][] second) {
        int m = first.length;//第一个矩阵的行长度
        int n = first[0].length;//第一个矩阵的列长度，第二个矩阵的行长度
        int p = second[0].length;//第二个矩阵的列长度
        int[][] result = new int[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] = result[i][j] + first[i][k] * second[k][j];
                }
            }
        }
        return result;
    }

    public static double[] getTwoAngle(double[] sat) {
        double[] res = new double[2];
        double[] NED = new double[3];
        NED[0] = sat[2];
        NED[1] = sat[1];
        NED[2] = -sat[0];
        double Az = Math.toDegrees(Math.acos(NED[0] / Math.sqrt(NED[0] * NED[0] + NED[1] * NED[1])));
        if (Az < 0) {
            Az = 360 - Az;
        }
        double El = Math.toDegrees(Math.atan(NED[2] / Math.sqrt(NED[0] * NED[0] + NED[1] * NED[1])));
        //res[0]为方位角，res[1]为俯仰角
        res[0] = Az;
        res[1] = El;
        return res;
    }

    public static double getOS(double[] m) {
        double[] angles = getTwoAngle(m);
        double x = a * Math.sin(angles[1]) * Math.cos(angles[0]);
        double y = b * Math.sin(angles[1]) * Math.cos(angles[0]);
        double z = c * Math.cos(angles[1]);
        double OS = Math.sqrt(x * x + y * y + z * z);
        return OS;
    }

    public static double getLength(double[] start, double[] end, double[] center) {
        double x0 = start[0], y0 = start[1], z0 = start[2];
        double x1 = end[0], y1 = end[1], z1 = end[2];
        double cx = center[0], cy = center[1], cz = center[2];
        double m = x1 - x0, n = y1 - y0, p = z1 - z0;
        double A = (m*m + n*n) / (a*a) + p*p / (b*b);
        double B = 2 * ((m*(x0 - cx) + n*(y0 - cy)) / (a*a) + p*(z0 - cz) / (b*b));
        double C = ((x0 - cx)*(x0 - cx) + (y0 - cy)*(y0 - cy)) / (a*a) + (z0 - cz)*(z0 - cz) / (b*b) - 1;
        double test = B*B - 4.0*A*C;
        double length = 0;
        if (test >= 0.0) {
            double t0 = (-B - Math.sqrt(test)) / (2.0 * A);
            double t1 = (-B + Math.sqrt(test)) / (2.0 * A);
            double[] point = new double[]{m , n , p};
            point[0] = point[0] * t1;
            point[1] = point[1] * t1;
            point[2] = point[2] * t1;
            length = Math.sqrt(point[0] * point[0] + point[1] * point[1] + point[2] * point[2]);
        }
        System.out.println(length);
//        return osg::Vec3d(0,0,0);
        return length;
    }
}
