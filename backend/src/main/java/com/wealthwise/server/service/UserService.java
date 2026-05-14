package com.wealthwise.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wealthwise.server.entity.Category;
import com.wealthwise.server.entity.User;
import com.wealthwise.server.mapper.CategoryMapper;
import com.wealthwise.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    // 注册时自动创建的默认分类 (type: 1=正常消费(绿), 2=可优化消费(橙), 3=冲动消费(红); direction: 1=收入, 2=支出)
    private static final String[][] DEFAULT_CATEGORIES = {
        // 支出 (direction=2)
        {"交通", "1", "transport", "2"},
        {"住房", "1", "house", "2"},
        {"医疗", "1", "medical", "2"},
        {"教育", "1", "education", "2"},
        {"零食", "2", "snack", "2"},
        {"饮料", "2", "drink", "2"},
        {"餐饮", "1", "food", "2"},
        {"游戏充值", "3", "game", "2"},
        {"订阅会员", "2", "subscription", "2"},
        // 收入 (direction=1)
        {"工资", "1", "salary", "1"},
        {"生活费", "1", "living", "1"},
    };

    public void register(String username, String password) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        userMapper.insert(user);

        // 为新用户创建默认分类
        for (String[] cat : DEFAULT_CATEGORIES) {
            Category category = new Category();
            category.setUserId(user.getId());
            category.setName(cat[0]);
            category.setType(Integer.parseInt(cat[1]));
            category.setIcon(cat[2]);
            category.setDirection(Integer.parseInt(cat[3]));
            categoryMapper.insert(category);
        }
    }

    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!md5Password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
