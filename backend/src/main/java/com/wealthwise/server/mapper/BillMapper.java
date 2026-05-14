package com.wealthwise.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wealthwise.server.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    /** 统计时间段内收入和支出 */
    Map<String, Object> selectOverview(@Param("userId") Long userId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    /** 按类型分组统计 */
    List<Map<String, Object>> selectByCategory(@Param("userId") Long userId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /** 趋势数据（按天/月聚合） */
    List<Map<String, Object>> selectTrend(@Param("userId") Long userId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("granularity") String granularity);

    /** 三类消费对比 */
    List<Map<String, Object>> selectCategoryCompare(@Param("userId") Long userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /** 按支付方式统计 */
    List<Map<String, Object>> selectByPaymentMethod(@Param("userId") Long userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
}
