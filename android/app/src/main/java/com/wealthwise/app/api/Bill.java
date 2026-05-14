package com.wealthwise.app.api;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Bill {
    // 后端映射字段
    private Long id;
    private Long userId;
    private Integer categoryId;
    private BigDecimal amount;
    private Integer direction;
    private String remark;
    private String billDate;
    private String createdAt;

    // 消费类型: 1=正常消费, 2=可优化消费, 3=冲动消费
    private Integer consumptionType;
    // 支付方式: 微信/支付宝/现金/银行卡
    private String paymentMethod;

    // 账单时间: HH:mm
    private String billTime;

    // UI 显示字段（从后端响应转换或用于本地演示）
    private String category;
    private String description;
    private String date;

    public Bill() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getDirection() { return direction; }
    public void setDirection(Integer direction) { this.direction = direction; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getBillDate() { return billDate; }
    public void setBillDate(String billDate) { this.billDate = billDate; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public Integer getConsumptionType() { return consumptionType; }
    public void setConsumptionType(Integer consumptionType) { this.consumptionType = consumptionType; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBillTime() { return billTime; }
    public void setBillTime(String billTime) { this.billTime = billTime; }

    // UI 字段
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
