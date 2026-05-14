package com.wealthwise.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wealthwise.server.entity.Category;
import com.wealthwise.server.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /** 获取用户的类型列表 */
    public List<Category> listByUserId(Long userId, Integer type, Integer direction) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getUserId, userId);
        if (type != null) {
            wrapper.eq(Category::getType, type);
        }
        if (direction != null) {
            wrapper.eq(Category::getDirection, direction);
        }
        return categoryMapper.selectList(wrapper);
    }

    /** 添加类型 */
    public void add(Long userId, String name, Integer type, String icon) {
        add(userId, name, type, icon, null);
    }

    /** 添加类型（含方向） */
    public void add(Long userId, String name, Integer type, String icon, Integer direction) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getUserId, userId)
               .eq(Category::getName, name);
        if (direction != null) {
            wrapper.eq(Category::getDirection, direction);
        }
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该类型已存在");
        }
        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setType(type);
        category.setDirection(direction != null ? direction : 2);
        category.setIcon(icon != null ? icon : "");
        categoryMapper.insert(category);
    }

    /** 删除类型 */
    public void delete(Long userId, Integer id) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getId, id).eq(Category::getUserId, userId);
        int count = categoryMapper.delete(wrapper);
        if (count == 0) {
            throw new RuntimeException("类型不存在");
        }
    }
}
