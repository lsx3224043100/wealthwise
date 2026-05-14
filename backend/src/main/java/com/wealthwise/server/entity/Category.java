package com.wealthwise.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long userId;
    private String name;
    /** 类别: 1=正常消费(绿), 2=可优化消费(橙), 3=冲动消费(红) */
    private Integer type;
    /** 方向: 1=收入, 2=支出（默认2=支出） */
    private Integer direction;
    private String icon;
}
