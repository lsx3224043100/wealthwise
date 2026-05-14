package com.wealthwise.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.wealthwise.app.api.AddCategoryRequest;
import com.wealthwise.app.api.Category;
import com.wealthwise.app.api.RetrofitClient;
import com.wealthwise.app.api.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private TextView tvUserInfo, tvCategoryList, tvPaymentList;
    private EditText etNewCategory, etNewPayment;
    private TextView btnAddCategory, btnAddPayment, btnLogout;
    private TextView btnCatExpense, btnCatIncome;
    private TextView btnPayExpense, btnPayIncome;
    private List<Category> categoryList;
    private List<String> paymentMethods;
    private int categoryDirection = 2; // 默认显示支出分类
    private int paymentDirection = 2; // 默认显示支出支付方式

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        loadUserInfo();
        loadCategories();
        loadPaymentMethods();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        tvUserInfo = view.findViewById(R.id.tv_user_info);
        tvCategoryList = view.findViewById(R.id.tv_category_list);
        tvPaymentList = view.findViewById(R.id.tv_payment_list);
        etNewCategory = view.findViewById(R.id.et_new_category);
        etNewPayment = view.findViewById(R.id.et_new_payment);
        btnAddCategory = view.findViewById(R.id.btn_add_category);
        btnAddPayment = view.findViewById(R.id.btn_add_payment);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnCatExpense = view.findViewById(R.id.btn_cat_expense);
        btnCatIncome = view.findViewById(R.id.btn_cat_income);
        btnPayExpense = view.findViewById(R.id.btn_pay_expense);
        btnPayIncome = view.findViewById(R.id.btn_pay_income);
    }

    private void setupClickListeners() {
        btnAddCategory.setOnClickListener(v -> addCategory());
        btnAddPayment.setOnClickListener(v -> addPayment());
        btnLogout.setOnClickListener(v -> logout());

        // 分类方向切换
        btnCatExpense.setOnClickListener(v -> {
            categoryDirection = 2;
            updateCatDirectionUI();
            loadCategories(); // 重新按方向加载
        });
        btnCatIncome.setOnClickListener(v -> {
            categoryDirection = 1;
            updateCatDirectionUI();
            loadCategories();
        });
        updateCatDirectionUI();

        // 支付方式方向切换
        btnPayExpense.setOnClickListener(v -> {
            paymentDirection = 2;
            updatePayDirectionUI();
            loadPaymentMethods();
        });
        btnPayIncome.setOnClickListener(v -> {
            paymentDirection = 1;
            updatePayDirectionUI();
            loadPaymentMethods();
        });
        updatePayDirectionUI();
    }

    // ===== 方向切换 UI =====

    private void updateCatDirectionUI() {
        if (categoryDirection == 2) {
            btnCatExpense.setBackgroundColor(0xFF4CAF50);
            btnCatExpense.setTextColor(0xFFFFFFFF);
            btnCatIncome.setBackgroundColor(0xFFF5F5F5);
            btnCatIncome.setTextColor(0xFF999999);
        } else {
            btnCatIncome.setBackgroundColor(0xFFF44336);
            btnCatIncome.setTextColor(0xFFFFFFFF);
            btnCatExpense.setBackgroundColor(0xFFF5F5F5);
            btnCatExpense.setTextColor(0xFF999999);
        }
    }

    private void updatePayDirectionUI() {
        if (paymentDirection == 2) {
            btnPayExpense.setBackgroundColor(0xFF4CAF50);
            btnPayExpense.setTextColor(0xFFFFFFFF);
            btnPayIncome.setBackgroundColor(0xFFF5F5F5);
            btnPayIncome.setTextColor(0xFF999999);
        } else {
            btnPayIncome.setBackgroundColor(0xFFF44336);
            btnPayIncome.setTextColor(0xFFFFFFFF);
            btnPayExpense.setBackgroundColor(0xFFF5F5F5);
            btnPayExpense.setTextColor(0xFF999999);
        }
    }

    private void loadUserInfo() {
        String username = SharedPreferencesManager.getUsername(getContext());
        tvUserInfo.setText(username != null ? username : "未登录");
    }

    // ===== 分类管理 =====

    private void loadCategories() {
        // 按当前方向从 API 加载分类
        Call<Result<List<Category>>> call = RetrofitClient.getInstance().getCategoryListWithDirection(null, categoryDirection);
        call.enqueue(new Callback<Result<List<Category>>>() {
            @Override
            public void onResponse(Call<Result<List<Category>>> call, Response<Result<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    categoryList = response.body().getData();
                } else {
                    categoryList = new ArrayList<>();
                }
                displayCategories();
            }
            @Override
            public void onFailure(Call<Result<List<Category>>> call, Throwable t) {
                categoryList = new ArrayList<>();
                tvCategoryList.setText("加载失败");
            }
        });
    }

    private void displayCategories() {
        if (categoryList == null || categoryList.isEmpty()) {
            tvCategoryList.setText("暂无" + (categoryDirection == 2 ? "支出" : "收入") + "分类，请在下方添加");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Category cat : categoryList) {
            String tag = getTypeTag(cat.getType());
            sb.append(tag).append(" ").append(cat.getName()).append("\n");
        }
        tvCategoryList.setText(sb.toString());

        tvCategoryList.setOnClickListener(v -> showCategoryDeleteDialog());
    }

    private void showCategoryDeleteDialog() {
        if (categoryList == null || categoryList.isEmpty()) return;

        String[] names = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            names[i] = categoryList.get(i).getName();
        }
        new AlertDialog.Builder(getContext())
                .setTitle("选择要删除的分类")
                .setItems(names, (dialog, which) -> {
                    Category target = categoryList.get(which);
                    confirmDeleteCategory(target);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void confirmDeleteCategory(Category category) {
        new AlertDialog.Builder(getContext())
                .setTitle("确认删除")
                .setMessage("确定删除分类「" + category.getName() + "」吗？")
                .setPositiveButton("删除", (dialog, which) -> doDeleteCategory(category))
                .setNegativeButton("取消", null)
                .show();
    }

    private void doDeleteCategory(Category category) {
        Call<Result<Void>> call = RetrofitClient.getInstance().deleteCategory(category.getId());
        call.enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    Toast.makeText(getContext(), "删除失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategory() {
        String name = etNewCategory.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "请输入分类名称", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(getContext())
                .setTitle("选择消费类型")
                .setItems(new String[]{"正常消费", "可优化消费", "冲动消费"}, (dialog, which) -> {
                    int type = which + 1;
                    doAddCategory(new AddCategoryRequest(name, type, "", categoryDirection));
                })
                .show();
    }

    private void doAddCategory(AddCategoryRequest request) {
        Call<Result<Void>> call = RetrofitClient.getInstance().addCategory(request);
        call.enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    etNewCategory.setText("");
                    loadCategories();
                } else {
                    Toast.makeText(getContext(), "添加失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===== 支付方式管理 =====

    private void loadPaymentMethods() {
        paymentMethods = SharedPreferencesManager.getPaymentMethods(getContext(), paymentDirection);
        displayPaymentMethods();
    }

    private void displayPaymentMethods() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paymentMethods.size(); i++) {
            sb.append("● ").append(paymentMethods.get(i)).append("\n");
        }
        tvPaymentList.setText(sb.toString());

        tvPaymentList.setOnClickListener(v -> showPaymentDeleteDialog());
    }

    private void showPaymentDeleteDialog() {
        String[] names = paymentMethods.toArray(new String[0]);
        new AlertDialog.Builder(getContext())
                .setTitle("选择要删除的支付方式")
                .setItems(names, (dialog, which) -> {
                    String target = paymentMethods.get(which);
                    confirmDeletePayment(target);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void confirmDeletePayment(String method) {
        new AlertDialog.Builder(getContext())
                .setTitle("确认删除")
                .setMessage("确定删除支付方式「" + method + "」吗？")
                .setPositiveButton("删除", (dialog, which) -> doDeletePayment(method))
                .setNegativeButton("取消", null)
                .show();
    }

    private void doDeletePayment(String method) {
        paymentMethods.remove(method);
        SharedPreferencesManager.savePaymentMethods(getContext(), paymentMethods, paymentDirection);
        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
        loadPaymentMethods();
    }

    private void addPayment() {
        String name = etNewPayment.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "请输入支付方式名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (paymentMethods.contains(name)) {
            Toast.makeText(getContext(), "该支付方式已存在", Toast.LENGTH_SHORT).show();
            return;
        }
        paymentMethods.add(name);
        SharedPreferencesManager.savePaymentMethods(getContext(), paymentMethods, paymentDirection);
        Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
        etNewPayment.setText("");
        loadPaymentMethods();
    }

    // ===== 通用 =====

    private void logout() {
        SharedPreferencesManager.clearAll(getContext());
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }

    private String getTypeTag(Integer type) {
        if (type == null) return "○";
        switch (type) {
            case 1: return "●";
            case 2: return "●";
            case 3: return "●";
            default: return "○";
        }
    }
}
