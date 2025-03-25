package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.BookingHotelAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityMyHotelBinding;
import com.app.cookbook.model.BookingHotelModel;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyHotel extends BaseActivity {
    String userid;
    private ActivityMyHotelBinding mBinding;
    private BookingHotelAdapter mBookingAdapter;
    private List<BookingHotelModel> mListBooking;
    private ValueEventListener mFoodValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);

        userid = getIntent().getStringExtra("id");
        mBinding = ActivityMyHotelBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());
        initToolbar();
        initView();
        loadListBookingHotelFromFirebase();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvData.setLayoutManager(linearLayoutManager);
        mListBooking = new ArrayList<>();
        mBookingAdapter = new BookingHotelAdapter(mListBooking, trip -> GlobalFunction.goToTripDetail(MyHotel.this, trip.getId()));
        mBinding.rcvData.setAdapter(mBookingAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListBookingHotelFromFirebase() {
        mFoodValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListFood();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookingHotelModel bookingHotelModel = dataSnapshot.getValue(BookingHotelModel.class);
                    String id = dataSnapshot.getKey();
                    bookingHotelModel.setId(id);
                    if (bookingHotelModel == null) return;
                    mListBooking.add(0, bookingHotelModel);
                }
                if (mBookingAdapter != null) mBookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        MyApplication.get(this).bookingDatabaseReference().orderByChild("userId").equalTo(userid).addValueEventListener(mFoodValueEventListener);
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_my_trip));
    }

    private void resetListFood() {
        if (mListBooking == null) {
            mListBooking = new ArrayList<>();
        } else {
            mListBooking.clear();
        }
    }


}