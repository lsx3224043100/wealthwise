# WealthWise - Android 理财记账 App

## 项目概述
个人理财记账应用，支持日常消费记录、多维度统计图表。极简玻璃态设计风格。

---

## 功能需求

### 1. 添加账单 (AddBillDialog — BottomSheet)
- **方向选择**：两个自定义按钮切换 支出/收入（收入时隐藏消费类型）
- **金额**：大字居中数字输入
- **描述**：单行文本输入
- **日期+时间**：并排两列，点击弹出 DatePicker/TimePicker，默认当天+当前时间
- **分类+支付方式**：并排两列 Spinner
  - 分类从 API 加载（按 direction 筛选：支出只显示支出分类，收入只显示收入分类）
  - 支付方式从 SharedPreferences 加载
- **消费类型**：三个按钮选择（必要/可省/冲动），仅支出时显示
  - 必要（正常消费）→ 绿色 `#4CAF50`
  - 可省（可优化消费）→ 橙色 `#FF9800`
  - 冲动（冲动消费）→ 红色 `#F44336`
- 提交到后端 `POST /api/bill/add`

### 2. 首页账单列表 (BillListFragment)
- 按日期分组，倒序排列（最新在最前）
- **筛选器**：ChipGroup（全部 | 日 | 月 | 年），支持 ◀▶ 日期导航，点击日期弹出 DatePicker，「今天」快速回到今天
- 每个日期组显示：
  - 日期头：`今天 周三` / `昨天 周二` / `2026-05-10 周日`
  - 当日支出和收入总计
  - 该日所有账单
- 每条账单卡片（玻璃态圆角卡片）：
  - 左侧竖条颜色指示器（收入=红色，支出=绿色）
  - 分类名 + 描述
  - 金额（支出=绿色，收入=红色）
- 点击账单 → 弹出详情对话框（查看/编辑/删除）
- FAB 浮动按钮 → 添加账单 BottomSheet
- 数据来源：`GET /api/bill/list?page=0&size=500`

### 3. 统计页面 (StatisticsFragment)
单个饼图，通过带图标按钮切换查看：

- **顶部按钮切换**：`↓ 支出`(绿) / `↑ 收入`(红)
- **时间筛选**：ChipGroup（全部/日/月/年）+ ◀▶ 导航 + 点击日期弹出 DatePicker + 「今天」按钮
- **饼图**：按分类显示当前时间范围内的支出或收入占比
- **底部**：总支出（绿色）/ 总收入（红色）+ 分类数量

### 4. 设置页面 (SettingsFragment)
玻璃态卡片布局，分为三块：
- **用户信息**：显示当前登录用户名
- **分类管理**：支持按方向（支出/收入）切换显示，添加时自动关联当前方向
  - 支出/收入标签切换
  - 输入框 + 添加按钮
  - 分类列表可点击删除
- **支付方式管理**：支持添加和删除支付方式（本地存储）
  - 输入框 + 添加按钮
  - 支付方式列表可点击删除
- **退出登录**：清除 token 跳转登录页

### 5. 消费类型体系（Bill 独立字段）

| consumptionType | 名称 | 颜色 | 说明 |
|----------------|------|------|------|
| 1 | 正常消费（必要） | 绿 `#4CAF50` | 必要生活开支 |
| 2 | 可优化消费（可省） | 橙 `#FF9800` | 可以减少的开支 |
| 3 | 冲动消费（冲动） | 红 `#F44336` | 非必要冲动消费 |

**重要：** 消费类型是 Bill 上的独立字段，与 Category 无关。添加账单时由用户独立选择。

### 6. 支付方式体系（Bill 独立字段）
- 微信（蓝色 `#1E90FF`）
- 支付宝（绿色 `#00C853`）
- 现金（橙色 `#FF6D00`）
- 银行卡（紫色 `#9C27B0`）
- 支持用户在设置页自定义添加/删除

### 7. 方向体系
- 支出 (direction=2)：金额绿色显示
- 收入 (direction=1)：金额红色显示

### 8. 自动登录
- JWT Token 有效期：7 天 (`604800000` ms)
- App 启动时检查本地 Token，有效则直接进入主页
- Token 过期后自动跳转登录页

### 9. 账单编辑
- 详情页编辑按钮打开 EditBillDialog（继承 AddBillDialog 的 BottomSheet）
- 预填现有数据，修改后调用 `PUT /api/bill/update`
- 编辑前后端均有验证

### 10. 分类方向
- category 表新增 `direction` 字段（1=收入, 2=支出，默认 2）
- 添加分类时选择方向，列表时按方向过滤
- GET `/api/category/list?direction=1` 只返回收入分类

---

## 当前进度

### ✅ 已完成

#### 后端
- 用户注册/登录 (JWT Token，7天过期)
- 分类管理 CRUD（含 direction 方向字段）
- 账单管理 CRUD（含分页、消费类型、支付方式、更新接口）
- 统计服务（总览/分类占比/趋势/三类对比/支付方式统计）
- 全局异常处理 + JWT 拦截器

#### Android
- 登录/注册页面（自动登录检查，保存 token/username/userId）
- 主页面框架（ViewPager2 + BottomNavigationView）
- 账单列表（分组 + 玻璃态卡片 + ChipGroup 筛选：全部/日/月/年 + 收入/支出指示条颜色 + 点击详情）
- 添加账单（BottomSheet + 方向切换 + 分类按 direction 过滤 + 消费类型三按钮 + 支付方式 + 时间选择）
- 账单编辑（EditBillDialog 复用添加逻辑，预填数据，PUT 提交）
- 账单详情对话框（查看信息含时间 + 编辑 + 删除）
- 统计页面（单饼图 + 带图标方向切换 + ChipGroup 筛选）
- 设置页面（玻璃态卡片 + 分类按方向管理 + 支付方式管理 + 退出登录）
- 极简玻璃态主题（半透明圆角卡片、Chip 筛选器、自定义按钮）
- Retrofit + OkHttp（Token 拦截器 + 日志）
- 支付方式本地持久化（SharedPreferences）
- 自定义启动图标（基于 icon.png 生成各密度）

---

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端 | Spring Boot 2.7 + MyBatis-Plus + MySQL 8 |
| Android | Java + MPAndroidChart + Retrofit + OkHttp + Material 3 |
| 接口 | `http://localhost:8081/api` |

---

## 数据库

### category 表新增字段
```sql
direction TINYINT NOT NULL DEFAULT 2 COMMENT '方向: 1=收入, 2=支出'
```

### bill 表字段
```sql
consumption_type TINYINT DEFAULT 1 COMMENT '消费类型: 1=正常, 2=可优化, 3=冲动'
payment_method VARCHAR(20) DEFAULT '微信' COMMENT '支付方式: 微信/支付宝/现金/银行卡'
bill_time VARCHAR(5) DEFAULT '' COMMENT '账单时间: HH:mm'
```

---

## 关键文件

```
backend/src/main/java/com/wealthwise/server/
├── controller/
│   ├── CategoryController.java   # GET /list 新增 direction 参数
│   └── BillController.java       # 新增 PUT /update 端点
├── service/
│   ├── CategoryService.java      # listByUserId 新增 direction 筛选
│   └── BillService.java          # 新增 update 方法
├── entity/
│   ├── Category.java             # 新增 direction 字段
│   └── Bill.java
├── dto/
│   ├── AddCategoryRequest.java   # 新增 direction 字段
│   ├── AddBillRequest.java
│   └── UpdateBillRequest.java    # 新建
└── common/                       # JWT / Result / 拦截器

android/app/src/main/java/com/wealthwise/app/
├── BillListFragment.java         # 账单列表（ChipGroup 筛选 + 玻璃态卡片）
├── BillAdapter.java              # 适配器
├── AddBillDialog.java            # 添加账单（BottomSheet + 方向/分类联动）
├── EditBillDialog.java           # 编辑账单（继承 AddBillDialog）
├── BillDetailDialog.java         # 账单详情（查看+编辑+删除）
├── StatisticsFragment.java       # 统计（ChipGroup 筛选 + 带图标切换）
├── SettingsFragment.java         # 设置（方向标签切换 + 玻璃态卡片）
├── api/
│   ├── Bill.java                 # 模型
│   ├── Category.java             # 新增 direction 字段
│   ├── AddCategoryRequest.java   # 新增 direction 字段
│   ├── UpdateBillRequest.java    # 新建
│   ├── ApiService.java           # 新增 updateBill / getCategoryListWithDirection
│   └── RetrofitClient.java
└── SharedPreferencesManager.java # 本地存储（含支付方式）

android/app/src/main/res/
├── drawable/
│   ├── bg_glass_card.xml         # 玻璃态卡片背景
│   ├── bg_glass_button.xml       # 玻璃态按钮背景
│   ├── bg_glass_dialog.xml       # 对话框背景
│   ├── bg_glass_bottom_sheet.xml # BottomSheet 背景
│   ├── bg_filter_chip.xml        # 筛选芯片背景（选中态）
│   └── ic_arrow_{down,up}.xml    # 统计页图标
├── layout/
│   ├── dialog_add_bill.xml       # 紧凑 BottomSheet 布局
│   ├── dialog_bill_detail.xml    # 玻璃态详情布局
│   ├── dialog_month_picker.xml   # 月份选择器（网格）
│   └── dialog_year_picker.xml    # 年份选择器
├── values/colors.xml             # 新增玻璃态颜色
└── values/styles.xml             # 玻璃态主题
```

---

## 设计规范

### 玻璃态（Glassmorphism）
- 卡片背景：半透明白色 `#F2FFFFFF`（亮色）/ 适当透明暗色（暗色）
- 边框：半透明白色细线 `0.5dp #33FFFFFF`
- 圆角：卡片 `12dp`，按钮 `8dp`，对话框 `16dp`
- 阴影：轻量 elevation `1-2dp`
- 背景色：暖灰 `#F9FAFB`

### 颜色系统
- 主色：`#0072F5`（蓝色）
- 收入/金额绿：`#4CAF50`
- 支出/金额红：`#F44336`
- 可优化：`#FF9800`（橙色）
- 按钮/卡片：使用 drawable 背景而非 backgroundTint

### 交互规范
- 筛选：使用 ChipGroup 替代 Spinner，选中态带主色半透明底
- 弹窗：添加/编辑使用 BottomSheet，详情使用居中 Dialog
- 分类联动：添加账单时 direction 切换自动重新加载对应分类
