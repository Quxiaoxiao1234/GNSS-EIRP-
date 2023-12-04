package com.qiuwuyu.test;

import com.qiuwuyu.algorithm.SingleSatellite;
import com.qiuwuyu.util.Average;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author paralog
 * @date 2023/2/6 19:05
 */
public class SingleSat {

    @Test
    public void test(){
        List<Double> offAngle = SingleSatellite.getOffAngle("D:\\Code\\Satellite\\src\\main\\resources\\SingleSat\\PRN16M.txt");
        System.out.println(offAngle.toString());
        List<Double> diff = Average.getDiff(offAngle);
        Collections.sort(diff);
        System.out.println(diff);
        List<Double> absDiff = new ArrayList<>();
        double max = 0;
        for(Double d : diff){
            if(max < Math.abs(d)){
                max = d;
            }
            absDiff.add(Math.abs(d));
        }
        System.out.println(max);
    }
}
