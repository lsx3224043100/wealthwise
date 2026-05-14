package com.wealthwise.app.api;

import com.google.gson.annotations.SerializedName;

public class AddBillRequest {
    @SerializedName("categoryId")
    private Integer categoryId;

    @SerializedName("amount")
    private String amount;

    @SerializedName("direction")
    private Integer direction;

    @SerializedName("remark")
    private String remark;

    @SerializedName("billDate")
    private String billDate;

    @SerializedName("consumptionType")
    private Integer consumptionType;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("billTime")
    private String billTime;

    public AddBillRequest(Integer categoryId, String amount, Integer direction,
                          String remark, String billDate,
                          Integer consumptionType, String paymentMethod,
                          String billTime) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.direction = direction;
        this.remark = remark;
        this.billDate = billDate;
        this.consumptionType = consumptionType;
        this.paymentMethod = paymentMethod;
        this.billTime = billTime;
    }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public Integer getDirection() { return direction; }
    public void setDirection(Integer direction) { this.direction = direction; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getBillDate() { return billDate; }
    public void setBillDate(String billDate) { this.billDate = billDate; }
    public Integer getConsumptionType() { return consumptionType; }
    public void setConsumptionType(Integer consumptionType) { this.consumptionType = consumptionType; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBillTime() { return billTime; }
    public void setBillTime(String billTime) { this.billTime = billTime; }
}
