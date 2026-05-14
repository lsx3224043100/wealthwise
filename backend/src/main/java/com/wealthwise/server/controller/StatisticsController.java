package com.wealthwise.server.controller;

import com.wealthwise.server.common.Result;
import com.wealthwise.server.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /** 总览：总收入/总支出 */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(HttpServletRequest request,
                                                  @RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate) {
        Long userId = getCurrentUserId(request);
        Map<String, Object> data = statisticsService.getOverview(userId, startDate, endDate);
        return Result.success(data);
    }

    /** 各类型占比（饼图数据） */
    @GetMapping("/byCategory")
    public Result<List<Map<String, Object>>> byCategory(HttpServletRequest request,
                                                          @RequestParam(required = false) String startDate,
                                                          @RequestParam(required = false) String endDate) {
        Long userId = getCurrentUserId(request);
        List<Map<String, Object>> data = statisticsService.getByCategory(userId, startDate, endDate);
        return Result.success(data);
    }

    /** 趋势数据（折线图） */
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend(HttpServletRequest request,
                                                      @RequestParam(required = false) String startDate,
                                                      @RequestParam(required = false) String endDate,
                                                      @RequestParam(defaultValue = "%Y-%m") String granularity) {
        Long userId = getCurrentUserId(request);
        List<Map<String, Object>> data = statisticsService.getTrend(userId, startDate, endDate, granularity);
        return Result.success(data);
    }

    /** 三类消费对比（柱状图） */
    @GetMapping("/categoryCompare")
    public Result<List<Map<String, Object>>> categoryCompare(HttpServletRequest request,
                                                               @RequestParam(required = false) String startDate,
                                                               @RequestParam(required = false) String endDate) {
        Long userId = getCurrentUserId(request);
        List<Map<String, Object>> data = statisticsService.getCategoryCompare(userId, startDate, endDate);
        return Result.success(data);
    }

    /** 按支付方式统计 */
    @GetMapping("/byPaymentMethod")
    public Result<List<Map<String, Object>>> byPaymentMethod(HttpServletRequest request,
                                                              @RequestParam(required = false) String startDate,
                                                              @RequestParam(required = false) String endDate) {
        Long userId = getCurrentUserId(request);
        List<Map<String, Object>> data = statisticsService.getByPaymentMethod(userId, startDate, endDate);
        return Result.success(data);
    }
}
