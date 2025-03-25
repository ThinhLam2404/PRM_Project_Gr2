package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.DestinationAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityBookingHotelBinding;
import com.app.cookbook.model.BookingHotelModel;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class BookingHotel extends BaseActivity {
    private long id;
    private ActivityBookingHotelBinding mBinding;
    private Location location;
    private EditText editTextLocation, editTextTripDate, editTextTripName;
    private TextView textViewGuestCount;
    private ImageButton buttonDecreaseGuest, buttonIncreaseGuest;
    private int guestCount = 0;
    private DestinationAdapter mDestinationAdapter;
    private ValueEventListener mValueEventListener;

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_book_hotel));
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        id = bundle.getLong(Constant.FOOD_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityBookingHotelBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());
        loadDataIntent();
        initToolbar();
        loadHotelDetailFromFirebase();

        initListener();
    }

    private void loadHotelDetailFromFirebase() {
        showProgressDialog(true);
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                Hotel hotel = snapshot.getValue(Hotel.class);
                if (hotel == null) return;

                mBinding.edtName.setText(hotel.getName());
                mBinding.edtLocation.setText(hotel.getLocationName());
                GlideUtils.loadUrl(hotel.getImage(), mBinding.imgHotel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(BookingHotel.this, getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).hotelDetailDatabaseReference(id).addValueEventListener(mValueEventListener);
    }

    private void initListener() {
//        editTextTripDate = findViewById(R.id.editTextTripDate);
//        // Đặt EditText không cho phép nhập tay
//        editTextTripDate.setKeyListener(null);
//        // Xử lý khi click vào EditText
//        editTextTripDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePickerDialog();
//            }
//        });
//        textViewGuestCount = findViewById(R.id.textViewGuestCount);
//        buttonDecreaseGuest = findViewById(R.id.buttonDecreaseGuest);
//        buttonIncreaseGuest = findViewById(R.id.buttonIncreaseGuest);
//        textViewGuestCount.setText(String.valueOf(guestCount));
//        // Decrease guest count
//        buttonDecreaseGuest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (guestCount > 0) {
//                    guestCount--;
//                    textViewGuestCount.setText(String.valueOf(guestCount));
//                }
//            }
//        });
//        // Increase guest count
//        buttonIncreaseGuest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                guestCount++;
//                textViewGuestCount.setText(String.valueOf(guestCount));
//            }
//        });
//        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.editTextLocation);
//        // Danh sách gợi ý địa điểm (có thể lấy từ API hoặc cơ sở dữ liệu)
//        ArrayList<String> locations = new ArrayList<>();
//
//        loadListDestinationFromFirebase(locations);
//        // Tạo ArrayAdapter để cung cấp dữ liệu cho AutoCompleteTextView
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_dropdown_item_1line, // Layout mặc định cho từng mục gợi ý
//                locations
//        );
//        // Gán adapter cho AutoCompleteTextView
//        autoCompleteTextView.setAdapter(adapter);

//        mBinding.buttonStartPlan.setOnClickListener(v -> onClickValidateField());
        mBinding.btnBooking.setOnClickListener(v -> onClickValidateField());
    }

    private void onClickValidateField() {
//        String destination = mBinding.editTextLocation.getText().toString().trim();
//        String date = mBinding.editTextTripDate.getText().toString().trim();
//        String guestCount = mBinding.textViewGuestCount.getText().toString().trim();
//        String tripName = mBinding.editTextTripName.getText().toString().trim();
//
//        if (StringUtil.isEmpty(destination)) {
//            showToastMessage(this, "Vui lòng nhập điểm đến");
//        } else if (StringUtil.isEmpty(date)) {
//            showToastMessage(this, "Vui lòng chon ngày đi");
//        } else if (Integer.parseInt(guestCount) < 0) {
//            showToastMessage(this, "Vui lòng nhập số lượng khách > 0");
//
//        } else if (StringUtil.isEmpty(tripName)) {
//            showToastMessage(this, "Vui lòng nhập tên chuyến đi");
//
//        } else {
//            createTrip(destination, date, guestCount, tripName);
//        }
//        String hotelName = mBinding.edtName.getText().toString().trim();


        bookingHotel(id);

        getHotelById(id);
    }

    private void getHotelById(long hotelId) {

        DatabaseReference hotelRef = FirebaseDatabase.getInstance().getReference("hotels").child(String.valueOf(hotelId));

        hotelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        hotel.setId(hotelId); // Gán ID từ Firebase vào model
                        GlobalFunction.addToHistory(BookingHotel.this, hotel);
                    }
                } else {
                    Log.e("Firebase", "Không tìm thấy khách sạn với ID: " + hotelId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu khách sạn: " + error.getMessage());
            }
        });
    }


    private void getHotelById(long hotelId, OnHotelLoadedListener listener) {


        DatabaseReference hotelRef = FirebaseDatabase.getInstance().getReference("Hotels").child(String.valueOf(hotelId));

        hotelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Hotel hotel = snapshot.getValue(Hotel.class); // Chuyển đổi DataSnapshot thành Model
                    if (hotel != null) {
                        hotel.setId(hotelId); // Gán ID nếu chưa có
                        listener.onHotelLoaded(hotel);
                    }
                } else {
                    Log.e("Firebase", "Không tìm thấy khách sạn với ID: " + hotelId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu khách sạn: " + error.getMessage());
            }
        });
    }

    private void bookingHotel(long hotelId) {
        showProgressDialog(true);

        // Lấy UID của user hiện tại
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            showProgressDialog(false);
            showToastMessage(this, "Lỗi: Người dùng chưa đăng nhập!");
            return;
        }

        String userId = firebaseUser.getUid();

        // Chuyển đổi ngày sang kiểu `long` (timestamp)
//        long tripDateTimestamp = DateUtil.convertStringToTimestamp(date);

        // Tạo đối tượng Trip
        BookingHotelModel newTrip = new BookingHotelModel(userId, hotelId);

        // Thêm vào Firebase
        DatabaseReference bookingHotelRef = FirebaseDatabase.getInstance().getReference("bookingHotels");
        String tripId = bookingHotelRef.push().getKey(); // Tạo ID duy nhất cho chuyến đi

        bookingHotelRef.child(tripId).setValue(newTrip)
                .addOnCompleteListener(task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        showToastMessage(this, "Đặt khách sạn thành công!");
                        finish(); // Quay lại màn hình trước đó
                    } else {
                        showToastMessage(this, "Lỗi khi đặt khách sạn: " + task.getException().getMessage());
                    }
                });
    }

    private void showDatePickerDialog() {
        // Lấy ngày hiện tại làm mặc định
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

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

    public void showProgressDialog(boolean value) {
        if (value) {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setCancelable(false);
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    // Interface callback để xử lý dữ liệu bất đồng bộ
    public interface OnHotelLoadedListener {
        void onHotelLoaded(Hotel hotel);
    }

    // Interface callback để xử lý dữ liệu bất đồng bộ


}