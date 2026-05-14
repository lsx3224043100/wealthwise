package com.wealthwise.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wealthwise.app.api.Bill;
import com.wealthwise.app.api.RetrofitClient;
import com.wealthwise.app.api.Result;
import com.wealthwise.app.api.UpdateBillRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBillDialog extends AddBillDialog {

    private Bill bill;

    public EditBillDialog(Bill bill) {
        this.bill = bill;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        android.app.Dialog dialog = super.onCreateDialog(savedInstanceState);
        fillData();
        return dialog;
    }

    private void fillData() {
        if (bill == null) return;

        // 设置金额
        if (bill.getAmount() != null) {
            etAmount.setText(String.valueOf(bill.getAmount().doubleValue()));
        }

        // 设置方向
        selectedDirection = bill.getDirection() != null ? bill.getDirection() : 2;
        updateDirectionUI();
        if (selectedDirection == 1) {
            layoutConsumptionSection.setVisibility(View.GONE);
        } else {
            layoutConsumptionSection.setVisibility(View.VISIBLE);
        }

        // 设置日期和时间
        if (bill.getDate() != null) {
            etDate.setText(bill.getDate());
        }
        if (bill.getBillTime() != null && !bill.getBillTime().isEmpty()) {
            etTime.setText(bill.getBillTime());
        }

        // 设置消费类型
        if (bill.getConsumptionType() != null) {
            selectedConsumptionType = bill.getConsumptionType();
            updateConsumptionTypeUI();
        }

        // 设置描述
        if (bill.getDescription() != null) {
            etDescription.setText(bill.getDescription());
        }

        // 加载匹配的分类和支付方式
        loadCategories();

        // 加载支付方式后选中已有的
        etDate.post(() -> {
            // 延迟选中，等 Spinner 加载完成
            if (bill.getPaymentMethod() != null) {
                for (int i = 0; i < paymentMethods.size(); i++) {
                    if (paymentMethods.get(i).equals(bill.getPaymentMethod())) {
                        spinnerPayment.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void loadFallbackCategories() {
        super.loadFallbackCategories();
        // 选中已有分类
        if (bill != null && bill.getCategory() != null) {
            for (int i = 0; i < categoryNames.size(); i++) {
                if (categoryNames.get(i).equals(bill.getCategory())) {
                    final int pos = i;
                    spinnerCategory.post(() -> spinnerCategory.setSelection(pos));
                    break;
                }
            }
        }
    }

    @Override
    protected void setupCategorySpinner() {
        super.setupCategorySpinner();
        // 选中已有分类
        if (bill != null && bill.getCategory() != null) {
            for (int i = 0; i < categoryNames.size(); i++) {
                if (categoryNames.get(i).equals(bill.getCategory())) {
                    final int pos = i;
                    spinnerCategory.post(() -> spinnerCategory.setSelection(pos));
                    break;
                }
            }
        }
    }

    @Override
    protected void doSubmit(Integer categoryId, String amountStr, int direction,
                            String description, String date, int consumptionType,
                            String paymentMethod, String time) {
        if (bill == null || bill.getId() == null) {
            Toast.makeText(getContext(), "账单ID为空", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateBillRequest request = new UpdateBillRequest(
                bill.getId(), categoryId, amountStr, direction, description,
                date, consumptionType, paymentMethod, time
        );

        Call<Result<Void>> call = RetrofitClient.getInstance().updateBill(request);
        call.enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "账单更新成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                } else {
                    Toast.makeText(getContext(), "更新失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
