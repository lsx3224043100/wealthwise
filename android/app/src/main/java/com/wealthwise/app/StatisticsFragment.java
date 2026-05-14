package com.wealthwise.app;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.chip.ChipGroup;
import com.wealthwise.app.api.Bill;
import com.wealthwise.app.api.BillPage;
import com.wealthwise.app.api.Category;
import com.wealthwise.app.api.RetrofitClient;
import com.wealthwise.app.api.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsFragment extends Fragment {

    private TextView btnToggleExpense, btnToggleIncome;
    private ChipGroup chipGroupFilter;
    private View layoutNav;
    private TextView btnPrev, btnNext;
    private TextView tvCurrentPeriod;
    private PieChart pieChart;
    private TextView tvTotalExpense, tvTotalIncome, tvTabInfo, tvStatTitle;
    private View layoutConsumptionBreakdown;
    private TextView tvConsumeNormal, tvConsumeOptimizable, tvConsumeImpulse;

    private List<Bill> allBills = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    private int filterMode = -1; // -1=全部, 0=按日期, 1=按月份, 2=按年份
    private Calendar currentCal = Calendar.getInstance();
    private boolean showIncome = false; // false=expense, true=income

    private static final String[] WEEK_DAYS = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        initViews(view);
        setupPieChart();
        setupToggle();
        setupFilters();
        loadData();
        return view;
    }

    private void initViews(View view) {
        btnToggleExpense = view.findViewById(R.id.btn_toggle_expense);
        btnToggleIncome = view.findViewById(R.id.btn_toggle_income);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        layoutNav = view.findViewById(R.id.layout_nav);
        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);
        tvCurrentPeriod = view.findViewById(R.id.tv_current_period);
        pieChart = view.findViewById(R.id.pieChart);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        tvTotalIncome = view.findViewById(R.id.tv_total_income);
        tvTabInfo = view.findViewById(R.id.tv_tab_info);
        tvStatTitle = view.findViewById(R.id.tv_stat_title);
        layoutConsumptionBreakdown = view.findViewById(R.id.layout_consumption_breakdown);
        tvConsumeNormal = view.findViewById(R.id.tv_consume_normal);
        tvConsumeOptimizable = view.findViewById(R.id.tv_consume_optimizable);
        tvConsumeImpulse = view.findViewById(R.id.tv_consume_impulse);
    }

    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(61f);
    }

    private void setupToggle() {
        updateToggleUI();
        btnToggleExpense.setOnClickListener(v -> {
            showIncome = false;
            updateToggleUI();
            refreshChart();
        });
        btnToggleIncome.setOnClickListener(v -> {
            showIncome = true;
            updateToggleUI();
            refreshChart();
        });
    }

    private void updateToggleUI() {
        if (showIncome) {
            btnToggleIncome.setBackgroundColor(0xFFF44336);
            btnToggleIncome.setTextColor(0xFFFFFFFF);
            btnToggleExpense.setBackgroundColor(0xFFF5F5F5);
            btnToggleExpense.setTextColor(0xFF999999);
        } else {
            btnToggleExpense.setBackgroundColor(0xFF4CAF50);
            btnToggleExpense.setTextColor(0xFFFFFFFF);
            btnToggleIncome.setBackgroundColor(0xFFF5F5F5);
            btnToggleIncome.setTextColor(0xFF999999);
        }
    }

    private void setupFilters() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_all) {
                filterMode = -1;
                layoutNav.setVisibility(View.GONE);
                refreshChart();
            } else if (checkedId == R.id.chip_day) {
                showDatePicker();
            } else if (checkedId == R.id.chip_month) {
                showMonthPicker();
            } else if (checkedId == R.id.chip_year) {
                showYearPicker();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (filterMode < 0) return;
            if (filterMode == 0) currentCal.add(Calendar.DAY_OF_YEAR, -1);
            else if (filterMode == 1) currentCal.add(Calendar.MONTH, -1);
            else if (filterMode == 2) currentCal.add(Calendar.YEAR, -1);
            updatePeriodDisplay();
            refreshChart();
        });

        btnNext.setOnClickListener(v -> {
            if (filterMode < 0) return;
            if (filterMode == 0) currentCal.add(Calendar.DAY_OF_YEAR, 1);
            else if (filterMode == 1) currentCal.add(Calendar.MONTH, 1);
            else if (filterMode == 2) currentCal.add(Calendar.YEAR, 1);
            updatePeriodDisplay();
            refreshChart();
        });

        tvCurrentPeriod.setOnClickListener(v -> {
            if (filterMode < 0) return;
            if (filterMode == 0) showDatePicker();
            else if (filterMode == 1) showMonthPicker();
            else if (filterMode == 2) showYearPicker();
        });
    }

    private void showDatePicker() {
        new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
            currentCal.set(Calendar.YEAR, year);
            currentCal.set(Calendar.MONTH, month);
            currentCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            filterMode = 0;
            layoutNav.setVisibility(View.VISIBLE);
            updatePeriodDisplay();
            refreshChart();
            chipGroupFilter.check(R.id.chip_day);
        }, currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH),
                currentCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showMonthPicker() {
        int year = currentCal.get(Calendar.YEAR);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_picker, null);
        TextView tvYearTitle = dialogView.findViewById(R.id.tv_year_title);
        View btnYearPrev = dialogView.findViewById(R.id.btn_year_prev);
        View btnYearNext = dialogView.findViewById(R.id.btn_year_next);
        android.widget.GridLayout grid = dialogView.findViewById(R.id.grid_months);

        tvYearTitle.setText(String.valueOf(year));

        // 先创建 dialog，让点击监听器可以安全引用
        final androidx.appcompat.app.AlertDialog monthDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setNegativeButton("取消", null)
                .create();

        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月",
                "7月", "8月", "9月", "10月", "11月", "12月"};
        for (int i = 0; i < grid.getChildCount(); i++) {
            View child = grid.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setText(months[i]);
                final int month = i;
                child.setOnClickListener(v -> {
                    int y = Integer.parseInt(tvYearTitle.getText().toString());
                    currentCal.set(Calendar.YEAR, y);
                    currentCal.set(Calendar.MONTH, month);
                    currentCal.set(Calendar.DAY_OF_MONTH, 1);
                    filterMode = 1;
                    layoutNav.setVisibility(View.VISIBLE);
                    updatePeriodDisplay();
                    refreshChart();
                    chipGroupFilter.check(R.id.chip_month);
                    if (monthDialog.isShowing()) monthDialog.dismiss();
                });
            }
        }

        btnYearPrev.setOnClickListener(v -> {
            int y = Integer.parseInt(tvYearTitle.getText().toString()) - 1;
            tvYearTitle.setText(String.valueOf(y));
        });
        btnYearNext.setOnClickListener(v -> {
            int y = Integer.parseInt(tvYearTitle.getText().toString()) + 1;
            tvYearTitle.setText(String.valueOf(y));
        });

        monthDialog.show();
    }

    private void showYearPicker() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_year_picker, null);
        TextView etYear = dialogView.findViewById(R.id.et_year);
        etYear.setText(String.valueOf(currentCal.get(Calendar.YEAR)));

        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle("选择年份")
                .setView(dialogView)
                .setPositiveButton("确定", (dialog, which) -> {
                    String yStr = etYear.getText().toString();
                    if (!yStr.isEmpty()) {
                        int y = Integer.parseInt(yStr);
                        currentCal.set(Calendar.YEAR, y);
                        filterMode = 2;
                        layoutNav.setVisibility(View.VISIBLE);
                        updatePeriodDisplay();
                        refreshChart();
                        chipGroupFilter.check(R.id.chip_year);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updatePeriodDisplay() {
        if (filterMode < 0) {
            tvCurrentPeriod.setText("全部");
            return;
        }
        if (filterMode == 0) {
            int dayOfWeek = currentCal.get(Calendar.DAY_OF_WEEK) - 1;
            String wd = WEEK_DAYS[dayOfWeek >= 0 ? dayOfWeek : 0];
            Calendar today = Calendar.getInstance();
            boolean isToday = today.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR)
                    && today.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR);
            tvCurrentPeriod.setText((isToday ? "今天 " : "") +
                    (currentCal.get(Calendar.MONTH) + 1) + "月" +
                    currentCal.get(Calendar.DAY_OF_MONTH) + "日 " + wd);
        } else if (filterMode == 1) {
            tvCurrentPeriod.setText(currentCal.get(Calendar.YEAR) + "年" +
                    (currentCal.get(Calendar.MONTH) + 1) + "月");
        } else if (filterMode == 2) {
            tvCurrentPeriod.setText(currentCal.get(Calendar.YEAR) + "年");
        }
    }

    private void loadData() {
        Call<Result<List<Category>>> catCall = RetrofitClient.getInstance().getCategoryList(null);
        catCall.enqueue(new Callback<Result<List<Category>>>() {
            @Override
            public void onResponse(Call<Result<List<Category>>> call, Response<Result<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    categoryList = response.body().getData();
                }
                fetchBills();
            }
            @Override
            public void onFailure(Call<Result<List<Category>>> call, Throwable t) {
                fetchBills();
            }
        });
    }

    private void fetchBills() {
        Call<Result<BillPage>> call = RetrofitClient.getInstance().getBillList(0, 500, null, null);
        call.enqueue(new Callback<Result<BillPage>>() {
            @Override
            public void onResponse(Call<Result<BillPage>> call, Response<Result<BillPage>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    allBills = response.body().getData().getRecords();
                    for (Bill bill : allBills) {
                        bill.setDate(bill.getBillDate());
                        if (bill.getCategoryId() != null) {
                            for (Category cat : categoryList) {
                                if (cat.getId().equals(bill.getCategoryId())) {
                                    bill.setCategory(cat.getName());
                                    break;
                                }
                            }
                        }
                    }
                }
                refreshChart();
            }
            @Override
            public void onFailure(Call<Result<BillPage>> call, Throwable t) {
                refreshChart();
            }
        });
    }

    private void refreshChart() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate, endDate;

        if (filterMode >= 0) {
            Calendar cal = (Calendar) currentCal.clone();
            if (filterMode == 0) {
                startDate = sdf.format(cal.getTime());
                endDate = startDate;
            } else if (filterMode == 1) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = sdf.format(cal.getTime());
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate = sdf.format(cal.getTime());
            } else {
                cal.set(Calendar.DAY_OF_YEAR, 1);
                startDate = sdf.format(cal.getTime());
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
                endDate = sdf.format(cal.getTime());
            }
        } else {
            startDate = "2000-01-01";
            endDate = "2099-12-31";
        }

        List<Bill> filtered = new ArrayList<>();
        double totalExpense = 0, totalIncome = 0;
        for (Bill bill : allBills) {
            String d = bill.getDate();
            if (d == null || d.compareTo(startDate) < 0 || d.compareTo(endDate) > 0) continue;
            filtered.add(bill);
            double amount = bill.getAmount() != null ? bill.getAmount().doubleValue() : 0;
            if (bill.getDirection() != null && bill.getDirection() == 1) {
                totalIncome += amount;
            } else {
                totalExpense += amount;
            }
        }

        tvTotalExpense.setText(String.format("总支出: ¥%.2f", totalExpense));
        tvTotalIncome.setText(String.format("总收入: ¥%.2f", totalIncome));

        Map<String, Double> categoryMap = new LinkedHashMap<>();
        for (Bill bill : filtered) {
            boolean isIncome = bill.getDirection() != null && bill.getDirection() == 1;
            if (isIncome != showIncome) continue;
            String cat = bill.getCategory() != null ? bill.getCategory() : "其他";
            double amount = bill.getAmount() != null ? bill.getAmount().doubleValue() : 0;
            categoryMap.merge(cat, amount, Double::sum);
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> e : categoryMap.entrySet()) {
            entries.add(new PieEntry(e.getValue().floatValue(), e.getKey()));
        }

        if (entries.isEmpty()) {
            tvTabInfo.setText("暂无" + (showIncome ? "收入" : "支出") + "数据");
            pieChart.setData(null);
            pieChart.invalidate();
            updateConsumptionBreakdown(filtered);
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();

        tvStatTitle.setText((showIncome ? "收入" : "支出") + "分类统计");
        tvTabInfo.setText("共 " + categoryMap.size() + " 个分类");

        updateConsumptionBreakdown(filtered);
    }

    private void updateConsumptionBreakdown(List<Bill> filtered) {
        if (showIncome) {
            layoutConsumptionBreakdown.setVisibility(View.GONE);
            return;
        }

        double normalTotal = 0, optimizableTotal = 0, impulseTotal = 0;
        for (Bill bill : filtered) {
            boolean isIncome = bill.getDirection() != null && bill.getDirection() == 1;
            if (isIncome) continue; // only expenses
            double amount = bill.getAmount() != null ? bill.getAmount().doubleValue() : 0;
            Integer ct = bill.getConsumptionType();
            if (ct == null || ct == 1) {
                normalTotal += amount;
            } else if (ct == 2) {
                optimizableTotal += amount;
            } else if (ct == 3) {
                impulseTotal += amount;
            }
        }

        double grandTotal = normalTotal + optimizableTotal + impulseTotal;
        if (grandTotal <= 0) {
            layoutConsumptionBreakdown.setVisibility(View.GONE);
            return;
        }

        layoutConsumptionBreakdown.setVisibility(View.VISIBLE);
        int np = (int) Math.round(normalTotal / grandTotal * 100);
        int op = (int) Math.round(optimizableTotal / grandTotal * 100);
        int ip = 100 - np - op; // ensure it sums to 100

        tvConsumeNormal.setText(String.format("必要 %d%% (¥%.1f)", np, normalTotal));
        tvConsumeOptimizable.setText(String.format("可省 %d%% (¥%.1f)", op, optimizableTotal));
        tvConsumeImpulse.setText(String.format("冲动 %d%% (¥%.1f)", ip, impulseTotal));
    }
}
