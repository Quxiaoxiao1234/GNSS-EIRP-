package com.qiuwuyu.io;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author paralog
 * @date 2022/10/8 9:48
 */
@Data
@Component
public class FileReader {

    public static List<String> readTxtFile(String filePath) {
        List<String> lists = new ArrayList();
        File file = new File(filePath);
        String encoding = "utf-8";
        try (InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
             BufferedReader bufferedReader = new BufferedReader(read)) {
            //判断文件是否存在
            if (file.isFile() && file.exists()) {
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    lists.add(lineTxt);
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
        }
        return lists;
    }
}
