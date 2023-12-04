package com.qiuwuyu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author paralog
 * @date 2022/10/19 8:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WGS84 {
    private double latitude;
    private double longitude;
    private double altitude;
}
