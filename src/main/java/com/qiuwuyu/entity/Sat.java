package com.qiuwuyu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paralog
 * @date 2022/10/7 9:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sat {
    //卫星编号
    private String id;
    //卫星类型
    private String type;
    private double power;
    //优先级
    private int priority;
    private boolean isVisible;
    //调度算法中当前时间执行到的窗口编号
    private int nowIdx;
    private List<Window> windows = new ArrayList<>();
}
