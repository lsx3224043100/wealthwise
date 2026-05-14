package com.wealthwise.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("bill")
public class Bill {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer categoryId;
    private BigDecimal amount;
    /** 方向: 1=收入, 2=支出 */
    private Integer direction;
    private String remark;
    private LocalDate billDate;
    private LocalDateTime createdAt;
    /** 消费类型: 1=正常消费, 2=可优化消费, 3=冲动消费 */
    private Integer consumptionType;
    /** 支付方式: 微信/支付宝/现金/银行卡 */
    private String paymentMethod;
    /** 账单时间: HH:mm */
    private String billTime;
}
