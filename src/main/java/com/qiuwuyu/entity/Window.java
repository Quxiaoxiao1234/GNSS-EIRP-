package com.qiuwuyu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * @author paralog
 * @date 2022/10/8 10:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Window {
    private int IDX;
    private DateTime start;
    private DateTime end;
    private long during;
}
