package com.wealthwise.app.api;

import com.google.gson.annotations.SerializedName;

public class AddCategoryRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private Integer type;

    @SerializedName("icon")
    private String icon;

    @SerializedName("direction")
    private Integer direction;

    public AddCategoryRequest(String name, Integer type, String icon, Integer direction) {
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.direction = direction;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getDirection() { return direction; }
    public void setDirection(Integer direction) { this.direction = direction; }
}
