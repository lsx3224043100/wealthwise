package com.wealthwise.app.api;

public class Category {
    private Integer id;
    private Long userId;
    private String name;
    private Integer type;  // 1=正常消费，2=不必要消费，3=可优化消费
    private Integer direction; // 1=收入, 2=支出
    private String icon;

    public Category() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getDirection() { return direction; }
    public void setDirection(Integer direction) { this.direction = direction; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
