package com.wealthwise.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wealthwise.app.api.Bill;
import com.wealthwise.app.api.BillPage;
import com.wealthwise.app.api.Category;
import com.wealthwise.app.api.RetrofitClient;
import com.wealthwise.app.api.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillListFragment extends Fragment {

    private RecyclerView recyclerView;
    private BillAdapter adapter;
    private FloatingActionButton fabAddBill;
    private TextView tvEmpty;
    private ChipGroup chipGroupFilter;
    private View layoutNav;
    private TextView btnPrev, btnNext;
    private TextView tvCurrentPeriod;
    private Map<Integer, String> categoryNameMap = new HashMap<>();

    private List<Bill> allBills = new ArrayList<>();
    private int filterMode = -1; // -1=全部, 0=按日期, 1=按月份, 2=按年份
    private Calendar currentCal = Calendar.getInstance();

    private static final String[] WEEK_DAYS = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupFab();
        setupFilters();
        loadData();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        fabAddBill = view.findViewById(R.id.fab_add_bill);
        tvEmpty = view.findViewById(R.id.tv_empty);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        layoutNav = view.findViewById(R.id.layout_nav);
        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);
        tvCurrentPeriod = view.findViewById(R.id.tv_current_period);
    }

    private void setupRecyclerView() {
        adapter = new BillAdapter();
        adapter.setOnBillClickListener(bill -> {
            BillDetailDialog dialog = new BillDetailDialog(bill);
            dialog.show(getChildFragmentManager(), "BillDetailDialog");
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        fabAddBill.setOnClickListener(v -> {
            AddBillDialog dialog = new AddBillDialog();
            dialog.show(getChildFragmentManager(), "AddBillDialog");
        });
    }

    private void setupFilters() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_all) {
                filterMode = -1;
                layoutNav.setVisibility(View.GONE);
                applyFilter();
            } else if (checkedId == R.id.chip_day) {
                showDatePicker();
            } else if (checkedId == R.id.chip_month) {
                showMonthPicker();
            } else if (checkedId == R.id.chip_year) {
                showYearPicker();
            }
        });

        // 日期导航
        btnPrev.setOnClickListener(v -> {
            if (filterMode < 0) return;
            if (filterMode == 0) currentCal.add(Calendar.DAY_OF_YEAR, -1);
            else if (filterMode == 1) currentCal.add(Calendar.MONTH, -1);
            else if (filterMode == 2) currentCal.add(Calendar.YEAR, -1);
            updatePeriodDisplay();
            applyFilter();
        });

        btnNext.setOnClickListener(v -> {
            if (filterMode < 0) return;
            if (filterMode == 0) currentCal.add(Calendar.DAY_OF_YEAR, 1);
            else if (filterMode == 1) currentCal.add(Calendar.MONTH, 1);
            else if (filterMode == 2) currentCal.add(Calendar.YEAR, 1);
            updatePeriodDisplay();
            applyFilter();
        });

        // 点击日期打开对应选择器
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
            applyFilter();
            // 同步chip选中状态
            chipGroupFilter.check(R.id.chip_day);
        }, currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH),
                currentCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showMonthPicker() {
        int year = currentCal.get(Calendar.YEAR);
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月",
                "7月", "8月", "9月", "10月", "11月", "12月"};

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_picker, null);

        TextView tvYearTitle = dialogView.findViewById(R.id.tv_year_title);
        TextView btnYearPrev = dialogView.findViewById(R.id.btn_year_prev);
        TextView btnYearNext = dialogView.findViewById(R.id.btn_year_next);
        android.widget.GridLayout grid = dialogView.findViewById(R.id.grid_months);

        tvYearTitle.setText(String.valueOf(year));

        // Create dialog first so we can dismiss it in listeners
        final androidx.appcompat.app.AlertDialog monthDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setNegativeButton("取消", null)
                .create();

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
                    applyFilter();
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
                        applyFilter();
                        chipGroupFilter.check(R.id.chip_year);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updatePeriodDisplay() {
        if (filterMode < 0) {
            tvCurrentPeriod.setText("全部账单");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = sdf.format(currentCal.getTime());

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
                    for (Category cat : response.body().getData()) {
                        categoryNameMap.put(cat.getId(), cat.getName());
                    }
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
                    allBills.clear();
                    BillPage billPage = response.body().getData();
                    for (Bill bill : billPage.getRecords()) {
                        bill.setDescription(bill.getRemark());
                        bill.setDate(bill.getBillDate());
                        if (bill.getCategoryId() != null && categoryNameMap.containsKey(bill.getCategoryId())) {
                            bill.setCategory(categoryNameMap.get(bill.getCategoryId()));
                        }
                        allBills.add(bill);
                    }
                }
                applyFilter();
            }
            @Override
            public void onFailure(Call<Result<BillPage>> call, Throwable t) {
                loadDemoData();
            }
        });
    }

    private void applyFilter() {
        if (filterMode == -1) {
            adapter.setData(allBills);
            updateEmptyState(allBills.isEmpty());
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate, endDate;
        Calendar cal = (Calendar) currentCal.clone();

        if (filterMode == 0) {
            startDate = sdf.format(cal.getTime());
            endDate = startDate;
        } else if (filterMode == 1) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            startDate = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDate = sdf.format(cal.getTime());
        } else if (filterMode == 2) {
            cal.set(Calendar.DAY_OF_YEAR, 1);
            startDate = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
            endDate = sdf.format(cal.getTime());
        } else {
            startDate = "2000-01-01";
            endDate = "2099-12-31";
        }

        List<Bill> filtered = new ArrayList<>();
        for (Bill bill : allBills) {
            String d = bill.getDate();
            if (d != null && d.compareTo(startDate) >= 0 && d.compareTo(endDate) <= 0) {
                filtered.add(bill);
            }
        }

        adapter.setData(filtered);
        updateEmptyState(filtered.isEmpty());
    }

    private void loadDemoData() {
        allBills.clear();
        String[] cats = {"餐饮", "交通", "购物", "娱乐", "餐饮"};
        String[] descs = {"午餐", "地铁", "衣服", "电影票", "晚餐"};
        String[] dates = {"2026-05-13", "2026-05-13", "2026-05-12", "2026-05-12", "2026-05-11"};
        double[] amounts = {35.0, 15.0, 299.0, 88.0, 52.0};
        Integer[] types = {1, 1, 3, 2, 1};

        for (int i = 0; i < cats.length; i++) {
            Bill bill = new Bill();
            bill.setId((long) (i + 1));
            bill.setCategory(cats[i]);
            bill.setDescription(descs[i]);
            bill.setDate(dates[i]);
            bill.setAmount(java.math.BigDecimal.valueOf(amounts[i]));
            bill.setConsumptionType(types[i]);
            bill.setUserId(1L);
            allBills.add(bill);
        }
        applyFilter();
    }

    private void updateEmptyState(boolean isEmpty) {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }
}
