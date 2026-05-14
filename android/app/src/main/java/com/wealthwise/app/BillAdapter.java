package com.wealthwise.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wealthwise.app.api.Bill;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DATE_HEADER = 0;
    private static final int TYPE_BILL_ITEM = 1;

    private static final int COLOR_EXPENSE = 0xFF4CAF50;
    private static final int COLOR_INCOME = 0xFFF44336;

    private List<ListItem> items = new ArrayList<>();
    private OnBillClickListener listener;

    public interface OnBillClickListener {
        void onBillClick(Bill bill);
    }

    public void setOnBillClickListener(OnBillClickListener listener) {
        this.listener = listener;
    }

    public static class ListItem {
        public enum Type { HEADER, BILL }
        public Type type;
        public String date;
        public Bill bill;
        public double dayTotalExpense;
        public double dayTotalIncome;
    }

    public BillAdapter() {}

    public void setData(List<Bill> billList) {
        items.clear();
        if (billList == null || billList.isEmpty()) return;

        Map<String, List<Bill>> grouped = new HashMap<>();
        for (Bill bill : billList) {
            String date = bill.getDate();
            if (date == null) date = "未知日期";
            grouped.computeIfAbsent(date, k -> new ArrayList<>()).add(bill);
        }

        List<String> dates = new ArrayList<>(grouped.keySet());
        Collections.sort(dates, Comparator.reverseOrder());

        for (String date : dates) {
            List<Bill> dayBills = grouped.get(date);
            double totalExp = 0, totalInc = 0;
            for (Bill b : dayBills) {
                if (b.getAmount() != null) {
                    if (b.getDirection() != null && b.getDirection() == 1) {
                        totalInc += b.getAmount().doubleValue();
                    } else {
                        totalExp += b.getAmount().doubleValue();
                    }
                }
            }

            ListItem header = new ListItem();
            header.type = ListItem.Type.HEADER;
            header.date = formatDateDisplay(date);
            header.dayTotalExpense = totalExp;
            header.dayTotalIncome = totalInc;
            items.add(header);

            for (Bill bill : dayBills) {
                ListItem item = new ListItem();
                item.type = ListItem.Type.BILL;
                item.bill = bill;
                items.add(item);
            }
        }
        notifyDataSetChanged();
    }

    private String formatDateDisplay(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            String weekDay = weekDays[dayOfWeek >= 0 ? dayOfWeek : 0];

            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);

            String prefix;
            if (isSameDay(cal, today)) {
                prefix = "今天";
            } else if (isSameDay(cal, yesterday)) {
                prefix = "昨天";
            } else {
                prefix = dateStr;
            }

            return prefix + " " + weekDay;
        } catch (Exception e) {
            return dateStr;
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < items.size()) {
            return items.get(position).type == ListItem.Type.HEADER ? TYPE_DATE_HEADER : TYPE_BILL_ITEM;
        }
        return TYPE_BILL_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_DATE_HEADER) {
            View view = inflater.inflate(R.layout.item_date_header, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_bill, parent, false);
            return new BillViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        if (holder instanceof DateHeaderViewHolder) {
            DateHeaderViewHolder h = (DateHeaderViewHolder) holder;
            h.tvDateHeader.setText(item.date);
            if (item.dayTotalIncome > 0 && item.dayTotalExpense > 0) {
                h.tvDayTotal.setText(String.format(Locale.getDefault(), "支出: ¥%.2f  收入: ¥%.2f",
                        item.dayTotalExpense, item.dayTotalIncome));
            } else if (item.dayTotalExpense > 0) {
                h.tvDayTotal.setText(String.format(Locale.getDefault(), "支出: ¥%.2f", item.dayTotalExpense));
            } else if (item.dayTotalIncome > 0) {
                h.tvDayTotal.setText(String.format(Locale.getDefault(), "收入: ¥%.2f", item.dayTotalIncome));
            } else {
                h.tvDayTotal.setText("¥0.00");
            }
        } else if (holder instanceof BillViewHolder) {
            BillViewHolder h = (BillViewHolder) holder;
            Bill bill = item.bill;
            h.tvCategory.setText(bill.getCategory());
            h.tvDescription.setText(bill.getDescription());
            h.tvAmount.setText(String.format(Locale.getDefault(), "¥%.2f", bill.getAmount().doubleValue()));

            // 指示条和金额按收入/支出着色
            boolean isIncome = bill.getDirection() != null && bill.getDirection() == 1;
            int color = isIncome ? COLOR_INCOME : COLOR_EXPENSE;
            h.indicatorType.setBackgroundColor(color);
            h.tvAmount.setTextColor(color);

            h.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onBillClick(bill);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ===== ViewHolders =====

    public static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateHeader, tvDayTotal;
        public DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateHeader = itemView.findViewById(R.id.tv_date_header);
            tvDayTotal = itemView.findViewById(R.id.tv_day_total);
        }
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        View indicatorType;
        TextView tvCategory, tvDescription, tvAmount;
        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            indicatorType = itemView.findViewById(R.id.indicator_type);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}