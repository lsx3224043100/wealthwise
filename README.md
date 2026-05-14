# 潮汐拾贝 (WealthWise) — 开发文档

> 个人理财记账 Android 应用，极简玻璃态设计风格。

---

## 功能清单

### 1. 用户认证
- 注册：用户名 + 密码 + 确认密码
- 登录：JWT Token（7天有效期）
- 自动登录：启动时检查本地 Token，有效则直接进入主页
- Token 过期自动跳转登录页

### 2. 添加账单
- 方向选择：支出/收入 自定义按钮切换（收入时隐藏消费类型）
- 金额：大字居中输入
- 描述：单行文本输入
- 日期+时间：并排两列，点击弹出 DatePicker/TimePicker
- 分类：Spinner 加载，按 direction 筛选（支出只显示支出分类，收入只显示收入分类）
- 支付方式：Spinner 加载，按 direction 筛选
- 消费类型：三个按钮（必要/可省/冲动），仅支出时显示
- 提交到后端 `POST /api/bill/add`

### 3. 首页账单列表
- 按日期分组，倒序排列（最新在前）
- ChipGroup 筛选：全部 | 日 | 月 | 年
- ◀▶ 日期导航，点击日期弹出 DatePicker
- 日期头：`今天 周三` / `昨天 周二` / `2026-05-10 周日`
- 当日支出和收入总计
- 账单卡片（玻璃态圆角）：
  - 左侧竖条颜色指示（收入=红色，支出=绿色）
  - 分类名 + 金额（支出=绿色，收入=红色）
- 点击账单 → 详情弹窗（查看/编辑/删除）
- FAB 浮动按钮 → 添加账单弹窗
- 数据：`GET /api/bill/list?page=0&size=500`

### 4. 账单编辑
- 详情页编辑按钮 → EditBillDialog
- 预填现有数据，修改后 `PUT /api/bill/update`
- 前后端均有验证

### 5. 统计页面
- 带图标按钮切换：`↓ 支出`(绿) / `↑ 收入`(红)
- ChipGroup 时间筛选：全部/日/月/年
- ◀▶ 导航 + 点击日期弹出 DatePicker
- 饼图：按分类显示支出/收入占比
- 消费类型分布：正常/可省/冲动 百分比
- 底部：总金额 + 分类数量

### 6. 设置页面
- 用户信息：显示当前用户名
- 分类管理：按方向（支出/收入）切换，添加/删除
- 支付方式管理：按方向切换，添加/删除（本地存储）
- 退出登录

---

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.7 |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8 |
| 认证 | JWT（jjwt 0.9.1） |
| Android 网络 | Retrofit 2 + OkHttp 4 |
| Android 图表 | MPAndroidChart v3.1.0 |
| Android UI | Material 3，AndroidX |
| 构建工具 | Gradle（AGP 8.2） |
| 最低 SDK | API 24 (Android 7.0) |
| 目标 SDK | API 34 (Android 14) |

---

## 数据库设计

### user 表
```sql
id          BIGINT PRIMARY KEY AUTO_INCREMENT
username    VARCHAR(50) NOT NULL UNIQUE
password    VARCHAR(255) NOT NULL
create_time DATETIME DEFAULT CURRENT_TIMESTAMP
```

### category 表
```sql
id         BIGINT PRIMARY KEY AUTO_INCREMENT
user_id    BIGINT NOT NULL
name       VARCHAR(50) NOT NULL         -- 分类名称
type       TINYINT DEFAULT 1            -- 1=正常消费 2=可优化 3=冲动
icon       VARCHAR(50) DEFAULT ''       -- 图标标识
direction  TINYINT NOT NULL DEFAULT 2   -- 1=收入 2=支出
FOREIGN KEY (user_id) REFERENCES user(id)
```

### bill 表
```sql
id               BIGINT PRIMARY KEY AUTO_INCREMENT
user_id          BIGINT NOT NULL
category_id      BIGINT
amount           DECIMAL(10,2) NOT NULL
direction        TINYINT DEFAULT 2         -- 1=收入 2=支出
remark           VARCHAR(255) DEFAULT ''
bill_date        DATE NOT NULL
consumption_type TINYINT DEFAULT 1         -- 1=正常 2=可优化 3=冲动
payment_method   VARCHAR(20) DEFAULT '微信'
bill_time        VARCHAR(5) DEFAULT ''     -- HH:mm
create_time      DATETIME DEFAULT CURRENT_TIMESTAMP
FOREIGN KEY (user_id) REFERENCES user(id)
FOREIGN KEY (category_id) REFERENCES category(id)
```

---

## API 接口

### 用户
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/register` | 注册 |
| POST | `/api/user/login` | 登录，返回 JWT Token |

### 分类
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/category/list?userId=&direction=` | 查询分类列表 |
| POST | `/api/category/add` | 添加分类 |
| DELETE | `/api/category/delete/{id}` | 删除分类 |

### 账单
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/bill/list?page=&size=` | 账单列表（分页） |
| POST | `/api/bill/add` | 添加账单 |
| PUT | `/api/bill/update` | 更新账单 |
| DELETE | `/api/bill/delete/{id}` | 删除账单 |

### 统计
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/statistics/overview` | 总览统计 |
| GET | `/api/statistics/by-category` | 按分类统计 |
| GET | `/api/statistics/trend` | 趋势统计 |
| GET | `/api/statistics/by-consumption-type` | 按消费类型统计 |
| GET | `/api/statistics/by-payment` | 按支付方式统计 |

---

## Android 项目结构

```
com.wealthwise.app/
├── LoginActivity.java           # 登录/注册
├── MainActivity.java            # 主页容器（ViewPager2 + BottomNavigation）
├── BillListFragment.java        # 账单列表（ChipGroup 筛选）
├── BillAdapter.java             # 列表适配器
├── AddBillDialog.java           # 添加账单弹窗（DialogFragment）
├── EditBillDialog.java          # 编辑账单（继承 AddBillDialog）
├── BillDetailDialog.java        # 账单详情弹窗
├── StatisticsFragment.java      # 统计（饼图 + 消费类型分布）
├── SettingsFragment.java        # 设置（分类/支付方式管理）
├── SharedPreferencesManager.java # 本地存储（Token/支付方式）
└── api/
    ├── ApiService.java          # Retrofit 接口定义
    ├── RetrofitClient.java      # OkHttp + Retrofit 单例
    ├── Bill.java / Category.java # 数据模型
    ├── AddBillRequest.java / UpdateBillRequest.java / AddCategoryRequest.java
    └── Result.java / TokenResponse.java
```

---

## 后端项目结构

```
com.wealthwise.server/
├── controller/
│   ├── UserController.java
│   ├── CategoryController.java
│   ├── BillController.java
│   └── StatisticsController.java
├── service/
│   ├── UserService.java         # 含 DEFAULT_CATEGORIES 默认分类
│   ├── CategoryService.java
│   ├── BillService.java
│   └── StatisticsService.java
├── entity/
│   ├── User.java
│   ├── Category.java
│   └── Bill.java
├── mapper/                      # MyBatis-Plus Mapper
├── dto/                         # 请求/响应 DTO
├── common/
│   ├── JwtUtil.java             # JWT 工具
│   ├── Result.java              # 统一响应
│   ├── GlobalExceptionHandler.java
│   └── JwtInterceptor.java
└── config/
    └── WebMvcConfig.java        # 拦截器注册
```

---

## 设计规范

### 玻璃态（Glassmorphism）
- 卡片背景：半透明白色 `#F2FFFFFF`
- 边框：`0.5dp #33FFFFFF`
- 圆角：卡片 12dp，按钮 8dp，对话框 16dp
- 阴影：elevation 1-2dp
- 页面背景：暖灰 `#F9FAFB`

### 颜色系统
| 用途 | 颜色 |
|------|------|
| 主色 | `#0072F5` |
| 支出金额/绿色 | `#4CAF50` |
| 收入金额/红色 | `#F44336` |
| 可优化/橙色 | `#FF9800` |
| 微信支付 | `#1E90FF` |
| 支付宝 | `#00C853` |
| 现金 | `#FF6D00` |
| 银行卡 | `#9C27B0` |

### 消费类型颜色
| 类型 | 颜色 |
|------|------|
| 正常消费（必要） | `#4CAF50` 绿 |
| 可优化（可省） | `#FF9800` 橙 |
| 冲动消费 | `#F44336` 红 |

### 方向显示
| 方向 | 金额颜色 | 指示条颜色 |
|------|----------|-----------|
| 支出 (direction=2) | `#4CAF50` 绿 | `#4CAF50` 绿 |
| 收入 (direction=1) | `#F44336` 红 | `#F44336` 红 |

---

## 默认分类

注册时自动创建以下默认分类：

### 支出（direction=2）— 9个
| 名称 | type |
|------|------|
| 交通 | 1（正常） |
| 住房 | 1（正常） |
| 医疗 | 1（正常） |
| 教育 | 1（正常） |
| 零食 | 2（可优化） |
| 饮料 | 2（可优化） |
| 餐饮 | 1（正常） |
| 游戏充值 | 3（冲动） |
| 订阅会员 | 2（可优化） |

### 收入（direction=1）— 2个
| 名称 | type |
|------|------|
| 工资 | 1（正常） |
| 生活费 | 1（正常） |

---

## 部署指南

### 后端
```bash
# 1. 确保 MySQL 已创建数据库 wealthwise
# 2. 修改 application.yml 中的数据库密码
# 3. 打包并启动
mvn clean package -DskipTests
nohup java -jar target/wealth-wise-server-1.0.jar > app.log 2>&1 &
```

### Android
```bash
# 1. 修改 RetrofitClient.java 中的 BASE_URL 为服务器地址
# 2. 构建 APK
cd android
./gradlew assembleDebug
# APK 路径: android/app/build/outputs/apk/debug/
```

### 数据库迁移
如果是从旧版升级（缺少 direction/consumption_type 等字段）：
```bash
mysql -u root -p wealthwise --default-character-set=utf8mb4 < backend/src/main/resources/migration_v2.sql
```

---

## 开发环境
- JDK 17
- Android Studio (Gradle 8.2+)
- MySQL 8.0+
- 模拟器测试：BASE_URL = `http://10.0.2.2:8081/api/`
- 真机/部署：BASE_URL = `http://服务器IP:8081/api/`
