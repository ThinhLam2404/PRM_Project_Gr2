package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.databinding.ActivityTripDetailBinding;
import com.app.cookbook.model.Trip;
import com.app.cookbook.utils.DateUtil;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TripDetail extends BaseActivity {
    private ActivityTripDetailBinding mBinding;

    private String tripId;
    private Trip mTrip;
    private ValueEventListener mTripDetailValueEventListener;
    private EditText editTextTripDate;
    private TextView textViewGuestCount;
    private ImageButton buttonDecreaseGuest, buttonIncreaseGuest;
    private int guestCount;

    private long tripDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityTripDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        loadDataIntent();
        initToolbar();
        loadTripDetailFromFirebase();
        initListener();
    }

    private void initListener() {
        editTextTripDate = findViewById(R.id.editTextTripDate);
        // Đặt EditText không cho phép nhập tay
        editTextTripDate.setKeyListener(null);
        // Xử lý khi click vào EditText
        editTextTripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        textViewGuestCount = findViewById(R.id.textViewGuestCount);
        buttonDecreaseGuest = findViewById(R.id.buttonDecreaseGuest);
        buttonIncreaseGuest = findViewById(R.id.buttonIncreaseGuest);
        buttonDecreaseGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guestCount > 0) {
                    guestCount--;
                    textViewGuestCount.setText(String.valueOf(guestCount));
                }
            }
        });
        // Increase guest count
        buttonIncreaseGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestCount++;
                textViewGuestCount.setText(String.valueOf(guestCount));
            }
        });
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.editTextLocation);
        loadListDestinationFromFirebase(autoCompleteTextView);
        mBinding.buttonStartPlan.setOnClickListener(v -> onClickValidateField());
    }

    private void loadListDestinationFromFirebase(AutoCompleteTextView autoCompleteTextView) {
        DatabaseReference destinationRef = MyApplication.get(this).destinationDatabaseReference();

        destinationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> destinationList = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) childSnapshot.getValue();
                    if (map != null && map.get("name") != null && map.get("locationName") != null) {
                        destinationList.add(map.get("name").toString() + ", " + map.get("locationName").toString());
                    }
                }

                // Chỉ tạo adapter sau khi đã có dữ liệu
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        TripDetail.this,  // Lấy context từ Activity
                        android.R.layout.simple_dropdown_item_1line,
                        destinationList
                );

                autoCompleteTextView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi tải danh sách điểm đến: " + error.getMessage());
            }
        });
    }

    private void onClickValidateField() {
        String destination = mBinding.editTextLocation.getText().toString().trim();
        String date = mBinding.editTextTripDate.getText().toString().trim();
        String guestCount = mBinding.textViewGuestCount.getText().toString().trim();
        String tripName = mBinding.editTextTripName.getText().toString().trim();

        if (StringUtil.isEmpty(destination)) {
            showToastMessage(this, "Vui lòng nhập điểm đến");
        } else if (StringUtil.isEmpty(date)) {
            showToastMessage(this, "Vui lòng chon ngày đi");
        } else if (Integer.parseInt(guestCount) < 0) {
            showToastMessage(this, "Vui lòng nhập số lượng khách > 0");

        } else if (StringUtil.isEmpty(tripName)) {
            showToastMessage(this, "Vui lòng nhập tên chuyến đi");

        } else {
            updateTrip(destination, date, guestCount, tripName);
        }
    }

    private void updateTrip(String destination, String date, String guestCount, String tripName) {
        showProgressDialog(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            showToastMessage(this, "Người dùng chưa đăng nhập!");
            showProgressDialog(false);
            return;
        }

        // Cập nhật thông tin trong Firebase Realtime Database
        DatabaseReference tripRef = FirebaseDatabase.getInstance().getReference("trips").child(tripId);
        Map<String, Object> updates = new HashMap<>();

        long tripDateTimestamp = DateUtil.convertStringToTimestamp(date);


        updates.put("tripName", tripName);
        updates.put("destination", destination);
        updates.put("tripDate", tripDateTimestamp);
        updates.put("number", Integer.parseInt(guestCount));


        tripRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        showToastMessage(this, "Cập nhật thông tin thành công!");

                        finish();
                    } else {
                        showToastMessage(this, "Cập nhật thất bại, vui lòng thử lại!");
                    }
                });
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_food_detail));
    }

    private void loadTripDetailFromFirebase() {
        showProgressDialog(true);
        mTripDetailValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                Trip trip = snapshot.getValue(Trip.class); // Sử dụng Trip model
                if (trip == null) return;
                // Hiển thị dữ liệu lên giao diện
                mBinding.editTextLocation.setText(trip.getDestination());
                Date date = new Date(trip.getTripDate());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(date);
                guestCount = trip.getNumber();
                tripDate = trip.getTripDate();
                mBinding.editTextTripDate.setText(formattedDate);
                mBinding.textViewGuestCount.setText(String.valueOf(trip.getNumber()));
                mBinding.editTextTripName.setText(trip.getTripName());
                editTextTripDate.setText(formattedDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(TripDetail.this, getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).tripDetailDatabaseReference(tripId).addValueEventListener(mTripDetailValueEventListener);

    }


    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        tripId = bundle.getString(Constant.TRIP_ID);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTripDetailValueEventListener != null) {
            MyApplication.get(this).tripDetailDatabaseReference(tripId)
                    .removeEventListener(mTripDetailValueEventListener);
        }
    }

    private void showDatePickerDialog() {
        // Lấy ngày hiện tại làm mặc định
        long timestamp = tripDate; // Giả sử lưu theo Unix timestamp (millisecond)
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Lưu ý: Tháng trong Calendar bắt đầu từ 0 nên cần +1
        int year = calendar.get(Calendar.YEAR);


        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Định dạng ngày tháng (tháng bắt đầu từ 0 nên cần +1)
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        editTextTripDate.setText(selectedDate);
                    }
                },
                year, month, day
        );

        // Hiển thị dialog
        datePickerDialog.show();
    }

}