package com.qiuwuyu.algorithm;

import com.qiuwuyu.io.FileReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paralog
 * @date 2023/2/6 18:48
 */
public class SingleSatellite {


    @Autowired
    private static FileReader fileReader;

    public static List<Double> getOffAngle(String filePath) {
        List<String> strings = fileReader.readTxtFile(filePath);
        List<Double> res = new ArrayList<>();
        for (int i = 0; i < strings.size(); ++i) {
            String str = strings.get(i);
            if (str.split("\\s+").length == 8 && str.contains("2022")) {
                double[] doubles = readString(str);
                res.add(90 - Math.abs(doubles[1]));
            }
        }
        return res;
    }


    private static double[] readString(String line) {
        String[] strings = line.split("\\s+");
        double[] res = new double[3];
        res[0] = Double.parseDouble(strings[5]);
        res[1] = Double.parseDouble(strings[6]);
        res[2] = Double.parseDouble(strings[7]);
        return res;
    }
}
