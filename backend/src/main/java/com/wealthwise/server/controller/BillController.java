package com.wealthwise.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wealthwise.server.common.Result;
import com.wealthwise.server.dto.AddBillRequest;
import com.wealthwise.server.dto.UpdateBillRequest;
import com.wealthwise.server.entity.Bill;
import com.wealthwise.server.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/bill")
public class BillController {

    @Autowired
    private BillService billService;

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /** 获取账单列表 */
    @GetMapping("/list")
    public Result<Page<Bill>> list(HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    @RequestParam(required = false) String startDate,
                                    @RequestParam(required = false) String endDate) {
        Long userId = getCurrentUserId(request);
        Page<Bill> billPage = billService.list(userId, page, size, startDate, endDate);
        return Result.success(billPage);
    }

    /** 添加账单 */
    @PostMapping("/add")
    public Result<?> add(HttpServletRequest request, @RequestBody AddBillRequest requestDTO) {
        try {
            Long userId = getCurrentUserId(request);
            billService.add(userId, requestDTO.getCategoryId(), requestDTO.getAmount(),
                    requestDTO.getDirection(), requestDTO.getRemark(), requestDTO.getBillDate(),
                    requestDTO.getConsumptionType(), requestDTO.getPaymentMethod(),
                    requestDTO.getBillTime());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /** 删除账单 */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long userId = getCurrentUserId(request);
            billService.delete(userId, id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /** 更新账单 */
    @PutMapping("/update")
    public Result<?> update(HttpServletRequest request, @RequestBody UpdateBillRequest requestDTO) {
        try {
            Long userId = getCurrentUserId(request);
            billService.update(userId, requestDTO);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
