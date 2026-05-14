package com.wealthwise.app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.wealthwise.app.api.AddBillRequest;
import com.wealthwise.app.api.Category;
import com.wealthwise.app.api.RetrofitClient;
import com.wealthwise.app.api.Result;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBillDialog extends DialogFragment {

    protected EditText etAmount, etDescription, etDate, etTime;
    protected Spinner spinnerCategory, spinnerPayment;
    protected View layoutConsumptionSection;
    protected TextView btnAdd, btnCancel;
    protected TextView btnExpense, btnIncome;
    protected TextView btnConsumeNormal, btnConsumeOptimizable, btnConsumeImpulse;

    protected List<Category> categoryList = new ArrayList<>();
    protected List<String> categoryNames = new ArrayList<>();
    protected List<String> paymentMethods = new ArrayList<>();

    protected int selectedDirection = 2; // 默认支出
    protected int selectedConsumptionType = 1; // 默认正常消费

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_bill, null);
        initViews(view);
        setupDirectionToggle();
        setupConsumptionTypeToggle();
        setupDatePicker();
        setupPaymentSpinner();
        loadCategories();
        btnAdd.setOnClickListener(v -> submitBill());
        btnCancel.setOnClickListener(v -> dismiss());

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    protected void initViews(View view) {
        etAmount = view.findViewById(R.id.et_amount);
        etDescription = view.findViewById(R.id.et_description);
        etDate = view.findViewById(R.id.et_date);
        etTime = view.findViewById(R.id.et_time);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerPayment = view.findViewById(R.id.spinner_payment);
        layoutConsumptionSection = view.findViewById(R.id.layout_consumption_section);
        btnAdd = view.findViewById(R.id.btn_add);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnExpense = view.findViewById(R.id.btn_expense);
        btnIncome = view.findViewById(R.id.btn_income);
        btnConsumeNormal = view.findViewById(R.id.btn_consume_normal);
        btnConsumeOptimizable = view.findViewById(R.id.btn_consume_optimizable);
        btnConsumeImpulse = view.findViewById(R.id.btn_consume_impulse);

        Calendar cal = Calendar.getInstance();
        String today = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        etDate.setText(today);
        String now = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        etTime.setText(now);
    }

    protected void setupDirectionToggle() {
        updateDirectionUI();
        btnExpense.setOnClickListener(v -> {
            selectedDirection = 2;
            updateDirectionUI();
            layoutConsumptionSection.setVisibility(View.VISIBLE);
            loadCategories();
            setupPaymentSpinner();
        });
        btnIncome.setOnClickListener(v -> {
            selectedDirection = 1;
            updateDirectionUI();
            layoutConsumptionSection.setVisibility(View.GONE);
            loadCategories();
            setupPaymentSpinner();
        });
    }

    protected void updateDirectionUI() {
        if (selectedDirection == 2) {
            btnExpense.setBackgroundColor(0xFF4CAF50);
            btnExpense.setTextColor(0xFFFFFFFF);
            btnIncome.setBackgroundColor(0xFFF0F0F0);
            btnIncome.setTextColor(0xFF999999);
        } else {
            btnIncome.setBackgroundColor(0xFFF44336);
            btnIncome.setTextColor(0xFFFFFFFF);
            btnExpense.setBackgroundColor(0xFFF0F0F0);
            btnExpense.setTextColor(0xFF999999);
        }
    }

    protected void setupConsumptionTypeToggle() {
        updateConsumptionTypeUI();
        btnConsumeNormal.setOnClickListener(v -> {
            selectedConsumptionType = 1;
            updateConsumptionTypeUI();
        });
        btnConsumeOptimizable.setOnClickListener(v -> {
            selectedConsumptionType = 2;
            updateConsumptionTypeUI();
        });
        btnConsumeImpulse.setOnClickListener(v -> {
            selectedConsumptionType = 3;
            updateConsumptionTypeUI();
        });
    }

    protected void updateConsumptionTypeUI() {
        int[][] states = {
            {1, 0xFF4CAF50, 0x1A4CAF50}, // normal: text green, bg light green
            {2, 0xFFFF9800, 0x1AFF9800},
            {3, 0xFFF44336, 0x1AF44336}
        };
        TextView[] btns = {btnConsumeNormal, btnConsumeOptimizable, btnConsumeImpulse};
        for (int i = 0; i < 3; i++) {
            boolean active = selectedConsumptionType == states[i][0];
            btns[i].setTextColor(active ? 0xFFFFFFFF : states[i][1]);
            btns[i].setBackgroundColor(active ? states[i][1] : 0xFFF5F5F5);
        }
    }

    protected void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
                etTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });
    }

    protected void setupPaymentSpinner() {
        paymentMethods = SharedPreferencesManager.getPaymentMethods(getContext(), selectedDirection);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, paymentMethods);
        spinnerPayment.setAdapter(adapter);
    }

    protected void loadCategories() {
        String token = SharedPreferencesManager.getToken(getContext());
        if (token == null) return;

        // 根据 direction 筛选分类
        Call<Result<List<Category>>> call = RetrofitClient.getInstance().getCategoryListWithDirection(null, selectedDirection);
        call.enqueue(new Callback<Result<List<Category>>>() {
            @Override
            public void onResponse(Call<Result<List<Category>>> call, Response<Result<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    categoryList = response.body().getData();
                    categoryNames.clear();
                    for (Category cat : categoryList) {
                        categoryNames.add(cat.getName());
                    }
                    setupCategorySpinner();
                } else {
                    loadFallbackCategories();
                }
            }

            @Override
            public void onFailure(Call<Result<List<Category>>> call, Throwable t) {
                loadFallbackCategories();
            }
        });
    }

    protected void loadFallbackCategories() {
        categoryNames.clear();
        categoryList.clear();
        String[] names = selectedDirection == 2
                ? new String[]{"交通", "住房", "医疗", "教育", "零食", "饮料", "餐饮", "游戏充值", "订阅会员"}
                : new String[]{"工资", "生活费"};
        for (String name : names) {
            categoryNames.add(name);
            Category cat = new Category();
            cat.setName(name);
            categoryList.add(cat);
        }
        setupCategorySpinner();
    }

    protected void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spinnerCategory.setAdapter(adapter);
    }

    protected void submitBill() {
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(getContext(), "请选择日期", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isNumeric(amountStr)) {
            Toast.makeText(getContext(), "金额格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        int direction = selectedDirection;
        int pos = spinnerCategory.getSelectedItemPosition();
        Integer categoryId = (pos >= 0 && pos < categoryList.size()) ? categoryList.get(pos).getId() : null;

        int consumptionType = direction == 2 ? selectedConsumptionType : 1;

        int payPos = spinnerPayment.getSelectedItemPosition();
        String paymentMethod = payPos >= 0 && payPos < paymentMethods.size() ? paymentMethods.get(payPos) : "微信";

        String time = etTime.getText().toString().trim();

        doSubmit(categoryId, amountStr, direction, description, date, consumptionType, paymentMethod, time);
    }

    protected void doSubmit(Integer categoryId, String amountStr, int direction,
                            String description, String date, int consumptionType,
                            String paymentMethod, String time) {
        AddBillRequest request = new AddBillRequest(
                categoryId, amountStr, direction, description, date,
                consumptionType, paymentMethod, time
        );

        Call<Result<Void>> call = RetrofitClient.getInstance().addBill(request);
        call.enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "账单添加成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                } else {
                    Toast.makeText(getContext(), "添加失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
