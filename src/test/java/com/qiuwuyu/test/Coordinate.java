package com.qiuwuyu.test;

import com.qiuwuyu.entity.Ned;
import com.qiuwuyu.entity.WGS84;
import com.qiuwuyu.util.WGS84ToNED;
import org.junit.jupiter.api.Test;

/**
 * @author paralog
 * @date 2022/10/19 9:38
 */
public class Coordinate {
    @Test
    public void testWGS84ToNED() {
        WGS84 wgs84 = new WGS84(-46.855,-72.462,20324.325853);
        Ned ned = WGS84ToNED.WGS84toNed(wgs84);
        System.out.println(ned);
    }
}
