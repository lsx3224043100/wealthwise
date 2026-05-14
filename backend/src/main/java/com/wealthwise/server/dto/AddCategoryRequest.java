package com.wealthwise.server.dto;

import lombok.Data;

@Data
public class AddCategoryRequest {
    private String name;
    /** 类别: 1=正常消费(绿), 2=可优化消费(橙), 3=冲动消费(红) */
    private Integer type;
    private String icon;
    /** 方向: 1=收入, 2=支出（默认2=支出） */
    private Integer direction;
}
