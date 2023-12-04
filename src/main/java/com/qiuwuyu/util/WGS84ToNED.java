package com.qiuwuyu.util;

import com.qiuwuyu.entity.Ned;
import com.qiuwuyu.entity.WGS84;

/**
 * @author paralog
 * @date 2022/10/19 8:54
 * WGS84坐标转NED坐标
 */
public class WGS84ToNED {
    private static final double xianLatitude = 34.445;
    private static final double xianLongitude = 109.494;
    private static final double xianAltitude = 0.577;

    public static Ned WGS84toNed(WGS84 wgs84) {
        Ned ned = new Ned();
        double altitude = wgs84.getAltitude();
        double latitude = wgs84.getLatitude();
        double longitude = wgs84.getLongitude();
        double e = 0.0818191908426;
        double Rn = (6378.137) / Math.sqrt(1 - e * e * Math.sin(xianLatitude) * Math.sin(xianLatitude));
//        double Rn0 = (6378.137) / Math.sqrt(1 - e * e * Math.sin(xianLatitude) * Math.sin(xianLatitude));
//        double N = (Rn + altitude) * Math.cos(latitude) * Math.sin(longitude - xianLongitude);
//        double E = (Rn + altitude) * (Math.sin(latitude) * Math.cos(xianLatitude) - Math.cos(latitude) * Math.sin(xianLatitude) * Math.cos(longitude - xianLongitude)) + e * e * (Rn0 * Math.sin(xianLatitude) - Rn * Math.sin(latitude)) * Math.cos(xianLatitude);
//        double D = (Rn + altitude) * (Math.sin(latitude) * Math.sin(xianLatitude) + Math.cos(latitude) * Math.cos(xianLatitude) * Math.cos(longitude - xianLongitude)) + e * e * (Rn0 * Math.sin(xianLatitude) - Rn * Math.sin(latitude)) * Math.sin(xianLatitude) - (Rn0 - xianAltitude);
//        ned.setN(N);
//        ned.setE(E);
//        ned.setD(D);
        return ned;
    }
}
