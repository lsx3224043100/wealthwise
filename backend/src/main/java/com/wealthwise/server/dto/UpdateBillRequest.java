package com.wealthwise.server.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateBillRequest {
    private Long id;
    private Integer categoryId;
    private BigDecimal amount;
    /** 方向: 1=收入, 2=支出 */
    private Integer direction;
    private String remark;
    private LocalDate billDate;
    /** 消费类型: 1=正常消费, 2=可优化消费, 3=冲动消费 */
    private Integer consumptionType;
    /** 支付方式: 微信/支付宝/现金/银行卡 */
    private String paymentMethod;
    /** 账单时间: HH:mm */
    private String billTime;
}
