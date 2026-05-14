package com.wealthwise.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private com.wealthwise.server.mapper.BillMapper billMapper;

    /** 默认时间范围：当月 */
    private LocalDate getDefaultStart() {
        return LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate getDefaultEnd() {
        return LocalDate.now();
    }

    /** 总览统计 */
    public Map<String, Object> getOverview(Long userId, String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : getDefaultStart();
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : getDefaultEnd();
        Map<String, Object> result = billMapper.selectOverview(userId, start, end);
        return result;
    }

    /** 各类型占比（饼图数据） */
    public List<Map<String, Object>> getByCategory(Long userId, String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : getDefaultStart();
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : getDefaultEnd();
        return billMapper.selectByCategory(userId, start, end);
    }

    /** 趋势数据（折线图） */
    public List<Map<String, Object>> getTrend(Long userId, String startDate, String endDate, String granularity) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : getDefaultStart();
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : getDefaultEnd();
        String gran = granularity != null ? granularity : "%Y-%m";
        return billMapper.selectTrend(userId, start, end, gran);
    }

    /** 三类消费对比（柱状图） */
    public List<Map<String, Object>> getCategoryCompare(Long userId, String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : getDefaultStart();
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : getDefaultEnd();
        return billMapper.selectCategoryCompare(userId, start, end);
    }

    /** 按支付方式统计 */
    public List<Map<String, Object>> getByPaymentMethod(Long userId, String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : getDefaultStart();
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : getDefaultEnd();
        return billMapper.selectByPaymentMethod(userId, start, end);
    }
}
