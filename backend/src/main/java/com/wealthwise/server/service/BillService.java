package com.wealthwise.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wealthwise.server.dto.UpdateBillRequest;
import com.wealthwise.server.entity.Bill;
import com.wealthwise.server.mapper.BillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class BillService {

    @Autowired
    private BillMapper billMapper;

    /** 添加账单 */
    public void add(Long userId, Integer categoryId, BigDecimal amount,
                    Integer direction, String remark, LocalDate billDate,
                    Integer consumptionType, String paymentMethod,
                    String billTime) {
        Bill bill = new Bill();
        bill.setUserId(userId);
        bill.setCategoryId(categoryId);
        bill.setAmount(amount);
        bill.setDirection(direction != null ? direction : 2);
        bill.setRemark(StringUtils.hasText(remark) ? remark : "");
        bill.setBillDate(billDate != null ? billDate : LocalDate.now());
        bill.setConsumptionType(consumptionType != null ? consumptionType : 1);
        bill.setPaymentMethod(StringUtils.hasText(paymentMethod) ? paymentMethod : "微信");
        bill.setBillTime(StringUtils.hasText(billTime) ? billTime : "");
        billMapper.insert(bill);
    }

    /** 分页查询账单列表 */
    public Page<Bill> list(Long userId, int page, int size,
                           String startDate, String endDate) {
        Page<Bill> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getUserId, userId);
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(Bill::getBillDate, startDate);
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(Bill::getBillDate, endDate);
        }
        wrapper.orderByDesc(Bill::getBillDate);
        return billMapper.selectPage(pageParam, wrapper);
    }

    /** 删除账单 */
    public void delete(Long userId, Long id) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getId, id).eq(Bill::getUserId, userId);
        int count = billMapper.delete(wrapper);
        if (count == 0) {
            throw new RuntimeException("账单不存在");
        }
    }

    /** 更新账单 */
    public void update(Long userId, UpdateBillRequest request) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getId, request.getId()).eq(Bill::getUserId, userId);
        Bill existing = billMapper.selectOne(wrapper);
        if (existing == null) {
            throw new RuntimeException("账单不存在");
        }
        Bill bill = new Bill();
        bill.setId(request.getId());
        bill.setCategoryId(request.getCategoryId());
        bill.setAmount(request.getAmount());
        bill.setDirection(request.getDirection());
        bill.setRemark(request.getRemark() != null ? request.getRemark() : "");
        bill.setBillDate(request.getBillDate());
        bill.setConsumptionType(request.getConsumptionType() != null ? request.getConsumptionType() : 1);
        bill.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "微信");
        bill.setBillTime(request.getBillTime() != null ? request.getBillTime() : "");
        billMapper.updateById(bill);
    }
}
