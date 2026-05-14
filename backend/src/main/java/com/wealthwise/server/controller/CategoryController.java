package com.wealthwise.server.controller;

import com.wealthwise.server.common.Result;
import com.wealthwise.server.dto.AddCategoryRequest;
import com.wealthwise.server.entity.Category;
import com.wealthwise.server.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /** 获取类型列表 */
    @GetMapping("/list")
    public Result<List<Category>> list(HttpServletRequest request,
                                        @RequestParam(required = false) Integer type,
                                        @RequestParam(required = false) Integer direction) {
        Long userId = getCurrentUserId(request);
        List<Category> list = categoryService.listByUserId(userId, type, direction);
        return Result.success(list);
    }

    /** 添加类型 */
    @PostMapping("/add")
    public Result<?> add(HttpServletRequest request, @RequestBody AddCategoryRequest requestDTO) {
        try {
            Long userId = getCurrentUserId(request);
            categoryService.add(userId, requestDTO.getName(), requestDTO.getType(), requestDTO.getIcon(), requestDTO.getDirection());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /** 删除类型 */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Integer id) {
        try {
            Long userId = getCurrentUserId(request);
            categoryService.delete(userId, id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
