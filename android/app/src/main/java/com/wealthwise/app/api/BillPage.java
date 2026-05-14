package com.wealthwise.app.api;

import java.util.List;

public class BillPage {
    private Long current;
    private Long size;
    private Long total;
    private Integer pages;
    private List<Bill> records;

    public Long getCurrent() { return current; }
    public void setCurrent(Long current) { this.current = current; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }
    public List<Bill> getRecords() { return records; }
    public void setRecords(List<Bill> records) { this.records = records; }
}
