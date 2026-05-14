package com.wealthwise.app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.wealthwise.app.api.Bill;
import com.wealthwise.app.api.RetrofitClient;
import com.wealthwise.app.api.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillDetailDialog extends AppCompatDialogFragment {

    private Bill bill;
    private static final int COLOR_EXPENSE = 0xFF4CAF50;
    private static final int COLOR_INCOME = 0xFFF44336;

    public BillDetailDialog(Bill bill) {
        this.bill = bill;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bill_detail, null);

        initViews(view);

        builder.setView(view);
        return builder.create();
    }

    private void initViews(View view) {
        TextView tvAmount = view.findViewById(R.id.detail_amount);
        TextView tvCategory = view.findViewById(R.id.detail_category);
        TextView tvDescription = view.findViewById(R.id.detail_description);
        TextView tvDate = view.findViewById(R.id.detail_date);
        TextView tvTime = view.findViewById(R.id.detail_time);
        TextView tvDirection = view.findViewById(R.id.detail_direction);
        TextView tvConsumptionType = view.findViewById(R.id.detail_consumption_type);
        TextView tvPayment = view.findViewById(R.id.detail_payment);
        View consumptionRow = view.findViewById(R.id.detail_consumption_row);
        View btnEdit = view.findViewById(R.id.btn_detail_edit);
        View btnDelete = view.findViewById(R.id.btn_detail_delete);
        View btnClose = view.findViewById(R.id.btn_detail_close);

        boolean isIncome = bill.getDirection() != null && bill.getDirection() == 1;
        double amount = bill.getAmount() != null ? bill.getAmount().doubleValue() : 0;

        tvAmount.setText(String.format("¥%.0f", amount));
        tvAmount.setTextColor(isIncome ? COLOR_INCOME : COLOR_EXPENSE);

        tvCategory.setText(bill.getCategory() != null ? bill.getCategory() : "-");
        tvDescription.setText(bill.getDescription() != null && !bill.getDescription().isEmpty()
                ? bill.getDescription() : "-");
        tvDate.setText(bill.getDate() != null ? bill.getDate() : "-");
        tvTime.setText(bill.getBillTime() != null && !bill.getBillTime().isEmpty() ? bill.getBillTime() : "-");
        tvDirection.setText(isIncome ? "收入" : "支出");
        tvDirection.setTextColor(isIncome ? COLOR_INCOME : COLOR_EXPENSE);

        // 消费类型
        Integer ct = bill.getConsumptionType();
        if (ct != null && !isIncome) {
            String[] typeNames = {"", "正常消费", "可优化消费", "冲动消费"};
            int[] colors = {0, 0xFF4CAF50, 0xFFFF9800, 0xFFF44336};
            tvConsumptionType.setText(ct >= 1 && ct <= 3 ? typeNames[ct] : "未知");
            tvConsumptionType.setTextColor(ct >= 1 && ct <= 3 ? colors[ct] : 0xFF666666);
        } else {
            consumptionRow.setVisibility(View.GONE);
        }

        tvPayment.setText(bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "-");

        btnEdit.setOnClickListener(v -> {
            dismiss();
            EditBillDialog editDialog = new EditBillDialog(bill);
            editDialog.show(getParentFragmentManager(), "EditBillDialog");
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("确认删除")
                    .setMessage("确定删除此账单吗？")
                    .setPositiveButton("删除", (dialog, which) -> deleteBill())
                    .setNegativeButton("取消", null)
                    .show();
        });

        btnClose.setOnClickListener(v -> dismiss());
    }

    private void deleteBill() {
        if (bill.getId() == null) {
            Toast.makeText(getContext(), "无法删除：账单ID为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<Result<Void>> call = RetrofitClient.getInstance().deleteBill(bill.getId());
        call.enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                    if (getActivity() != null) getActivity().recreate();
                } else {
                    Toast.makeText(getContext(), "删除失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
