package com.lan.campsiteproject.controller.campsite;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lan.campsiteproject.model.Campsite;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CartPriceCalculator {

    private Context context;
    private CartManager cartManager;
    private Campsite selectedCampsite;

    private EditText edtNumAdults, edtNumChildren;
    private ImageButton btnIncreaseAdults, btnDecreaseAdults;
    private ImageButton btnIncreaseChildren, btnDecreaseChildren;
    private Button btnSelectDate;
    private TextView txtCampsiteTotal, totalPriceTextView;

    private Calendar startDate, endDate;
    private boolean isSelectingStartDate = true;
    private static final String PREF_NAME = "CartPrefs";
    private static final String KEY_START_DATE = "startDate";
    private static final String KEY_END_DATE = "endDate";
    private static final String KEY_NUM_ADULTS = "numAdults";
    private static final String KEY_NUM_CHILDREN = "numChildren";

    public CartPriceCalculator(Context context, CartManager cartManager) {
        this.context = context;
        this.cartManager = cartManager;
        this.startDate = Calendar.getInstance();
        this.endDate = Calendar.getInstance();
        this.endDate.add(Calendar.DAY_OF_MONTH, 1);
        Log.d("CartPriceCalculator", "CartPriceCalculator initialized");
        restoreFromPreferences();
    }

    public void initializeComponents(EditText edtNumAdults, EditText edtNumChildren,
                                     ImageButton btnIncreaseAdults, ImageButton btnDecreaseAdults,
                                     ImageButton btnIncreaseChildren, ImageButton btnDecreaseChildren,
                                     Button btnSelectDate, TextView txtCampsiteTotal,
                                     TextView totalPriceTextView) {
        this.edtNumAdults = edtNumAdults;
        this.edtNumChildren = edtNumChildren;
        this.btnIncreaseAdults = btnIncreaseAdults;
        this.btnDecreaseAdults = btnDecreaseAdults;
        this.btnIncreaseChildren = btnIncreaseChildren;
        this.btnDecreaseChildren = btnDecreaseChildren;
        this.btnSelectDate = btnSelectDate;
        this.txtCampsiteTotal = txtCampsiteTotal;
        this.totalPriceTextView = totalPriceTextView;

        edtNumAdults.setText(String.valueOf(getNumberOfAdults()));
        edtNumChildren.setText(String.valueOf(getNumberOfChildren()));
        Log.d("CartPriceCalculator", "Initialized components with numAdults: " + getNumberOfAdults() + ", numChildren: " + getNumberOfChildren());

        setupEventListeners();
        updateDateButtonText();
        calculateTotalPrice();
    }

    private void setupEventListeners() {
        btnIncreaseAdults.setOnClickListener(v -> {
            int current = Integer.parseInt(edtNumAdults.getText().toString());
            if (current < 20) {
                edtNumAdults.setText(String.valueOf(current + 1));
                saveToPreferences();
                calculateTotalPrice();
            }
        });

        btnDecreaseAdults.setOnClickListener(v -> {
            int current = Integer.parseInt(edtNumAdults.getText().toString());
            if (current > 1) {
                edtNumAdults.setText(String.valueOf(current - 1));
                saveToPreferences();
                calculateTotalPrice();
            }
        });

        btnIncreaseChildren.setOnClickListener(v -> {
            int current = Integer.parseInt(edtNumChildren.getText().toString());
            if (current < 15) {
                edtNumChildren.setText(String.valueOf(current + 1));
                saveToPreferences();
                calculateTotalPrice();
            }
        });

        btnDecreaseChildren.setOnClickListener(v -> {
            int current = Integer.parseInt(edtNumChildren.getText().toString());
            if (current > 0) {
                edtNumChildren.setText(String.valueOf(current - 1));
                saveToPreferences();
                calculateTotalPrice();
            }
        });

        edtNumAdults.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(s.toString());
                    if (value < 1) {
                        edtNumAdults.setText("1");
                        saveToPreferences();
                        return;
                    }
                    if (value > 20) {
                        edtNumAdults.setText("20");
                        saveToPreferences();
                        return;
                    }
                    saveToPreferences();
                    calculateTotalPrice();
                } catch (NumberFormatException e) {
                    edtNumAdults.setText("1");
                    saveToPreferences();
                }
            }
        });

        edtNumChildren.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(s.toString());
                    if (value < 0) {
                        edtNumChildren.setText("0");
                        saveToPreferences();
                        return;
                    }
                    if (value > 15) {
                        edtNumChildren.setText("15");
                        saveToPreferences();
                        return;
                    }
                    saveToPreferences();
                    calculateTotalPrice();
                } catch (NumberFormatException e) {
                    edtNumChildren.setText("0");
                    saveToPreferences();
                }
            }
        });

        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            if (isSelectingStartDate) {
                startDate.set(year, month, dayOfMonth);
                if (startDate.getTimeInMillis() >= endDate.getTimeInMillis()) {
                    endDate.setTimeInMillis(startDate.getTimeInMillis());
                    endDate.add(Calendar.DAY_OF_MONTH, 1);
                }
                isSelectingStartDate = false;
                showEndDatePicker();
            } else {
                endDate.set(year, month, dayOfMonth);
                if (endDate.getTimeInMillis() <= startDate.getTimeInMillis()) {
                    endDate.setTimeInMillis(startDate.getTimeInMillis());
                    endDate.add(Calendar.DAY_OF_MONTH, 1);
                }
                isSelectingStartDate = true;
                saveToPreferences();
                updateDateButtonText();
                calculateTotalPrice();
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.setTitle(isSelectingStartDate ? "Chọn ngày bắt đầu" : "Chọn ngày kết thúc");
        dialog.show();
    }

    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endDate.getTimeInMillis());

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            endDate.set(year, month, dayOfMonth);
            if (endDate.getTimeInMillis() <= startDate.getTimeInMillis()) {
                endDate.setTimeInMillis(startDate.getTimeInMillis());
                endDate.add(Calendar.DAY_OF_MONTH, 1);
            }
            isSelectingStartDate = true;
            saveToPreferences();
            updateDateButtonText();
            calculateTotalPrice();
        };

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.setTitle("Chọn ngày kết thúc");
        dialog.getDatePicker().setMinDate(startDate.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
        dialog.show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateStr = dateFormat.format(startDate.getTime());
        String endDateStr = dateFormat.format(endDate.getTime());
        int numberOfDays = calculateNumberOfDays();
        btnSelectDate.setText(String.format("%s - %s (%d ngày)", startDateStr, endDateStr, numberOfDays));
        Log.d("CartPriceCalculator", "Updated date button: " + btnSelectDate.getText());
    }

    private int calculateNumberOfDays() {
        long diffInMillis = endDate.getTimeInMillis() - startDate.getTimeInMillis();
        return (int) Math.max(1, TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1);
    }

    public void setCampsite(Campsite campsite) {
        this.selectedCampsite = campsite;
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        if (selectedCampsite == null) {
            if (txtCampsiteTotal != null) {
                txtCampsiteTotal.setText("Tổng tiền campsite: $0.00");
            }
            updateGrandTotal();
            return;
        }

        try {
            int adults = Integer.parseInt(edtNumAdults.getText().toString());
            int children = Integer.parseInt(edtNumChildren.getText().toString());
            int numberOfDays = calculateNumberOfDays();
            double pricePerDay = selectedCampsite.getCampPrice();
            double dailyCampsitePrice = (adults * pricePerDay) + (children * pricePerDay * 0.5);
            double totalCampsitePrice = dailyCampsitePrice * numberOfDays;

            if (txtCampsiteTotal != null) {
                txtCampsiteTotal.setText(String.format("Tổng tiền campsite: $%.2f", totalCampsitePrice));
            }
            updateGrandTotal();
        } catch (NumberFormatException e) {
            Log.e("CartPriceCalculator", "Invalid number format: " + e.getMessage());
            Toast.makeText(context, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGrandTotal() {
        double campsiteTotal = getCampsiteTotal();
        double gearTotal = getGearTotal();
        double grandTotal = campsiteTotal + gearTotal;
        if (totalPriceTextView != null) {
            totalPriceTextView.setText(String.format("💰 Tổng cộng: $%.2f", grandTotal));
        }
    }

    public double getCampsiteTotal() {
        if (selectedCampsite == null) return 0.0;
        try {
            int adults = Integer.parseInt(edtNumAdults.getText().toString());
            int children = Integer.parseInt(edtNumChildren.getText().toString());
            int numberOfDays = calculateNumberOfDays();
            double pricePerDay = selectedCampsite.getCampPrice();
            double dailyCampsitePrice = (adults * pricePerDay) + (children * pricePerDay * 0.5);
            return dailyCampsitePrice * numberOfDays;
        } catch (NumberFormatException e) {
            Log.e("CartPriceCalculator", "Invalid number format in getCampsiteTotal: " + e.getMessage());
            return 0.0;
        }
    }

    private double getGearTotal() {
        if (cartManager == null) return 0.0;
        double total = 0.0;
        for (var entry : cartManager.getGearMap().entrySet()) {
            total += entry.getKey().getGearPrice() * entry.getValue();
        }
        return total;
    }

    public void refreshPriceCalculation() {
        calculateTotalPrice();
    }

    public int getNumberOfAdults() {
        try {
            return Integer.parseInt(edtNumAdults.getText().toString());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public int getNumberOfChildren() {
        try {
            return Integer.parseInt(edtNumChildren.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public int getNumberOfDays() {
        return calculateNumberOfDays();
    }

    public double getGrandTotal() {
        return getCampsiteTotal() + getGearTotal();
    }

    private void saveToPreferences() {
        if (context == null) {
            Log.e("CartPriceCalculator", "Context is null, cannot save to SharedPreferences");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int adults = getNumberOfAdults();
        int children = getNumberOfChildren();
        editor.putInt(KEY_NUM_ADULTS, adults);
        editor.putInt(KEY_NUM_CHILDREN, children);
        editor.putLong(KEY_START_DATE, startDate.getTimeInMillis());
        editor.putLong(KEY_END_DATE, endDate.getTimeInMillis());
        Log.d("CartPriceCalculator", "Saving numAdults: " + adults + ", numChildren: " + children);
        Log.d("CartPriceCalculator", "Saving startDate: " + startDate.getTime() + ", endDate: " + endDate.getTime());
        editor.apply();
    }

    private void restoreFromPreferences() {
        if (context == null) {
            Log.e("CartPriceCalculator", "Context is null, cannot restore from SharedPreferences");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long startDateMillis = prefs.getLong(KEY_START_DATE, System.currentTimeMillis());
        long endDateMillis = prefs.getLong(KEY_END_DATE, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        startDate.setTimeInMillis(startDateMillis);
        endDate.setTimeInMillis(endDateMillis);
        int numAdults = prefs.getInt(KEY_NUM_ADULTS, 1);
        int numChildren = prefs.getInt(KEY_NUM_CHILDREN, 0);
        if (edtNumAdults != null) {
            edtNumAdults.setText(String.valueOf(numAdults));
        }
        if (edtNumChildren != null) {
            edtNumChildren.setText(String.valueOf(numChildren));
        }
        Log.d("CartPriceCalculator", "Restored numAdults: " + numAdults + ", numChildren: " + numChildren);
        Log.d("CartPriceCalculator", "Restored startDate: " + startDate.getTime() + ", endDate: " + endDate.getTime());
    }
}