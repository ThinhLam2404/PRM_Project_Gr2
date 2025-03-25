package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.HotelAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityHistoryBinding;
import com.app.cookbook.listener.IOnClickHotelListener;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    private ActivityHistoryBinding mBinding;
    private List<Hotel> mListHotel;
    private HotelAdapter mHotelAdapter;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initUi();
        loadDataHistory();
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_history));
    }

    private void initUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvData.setLayoutManager(linearLayoutManager);

        mListHotel = new ArrayList<>();
        mHotelAdapter = new HotelAdapter(mListHotel, new IOnClickHotelListener() {
            @Override
            public void onClickItemHotel(Hotel hotel) {
                GlobalFunction.goToHotelDetail(HistoryActivity.this, hotel.getId());
            }

            @Override
            public void onClickFavoriteHotel(Hotel destination, boolean favorite) {
                GlobalFunction.onClickFavoriteHotel(HistoryActivity.this, destination, favorite);
            }

            @Override
            public void onClickLocationOfHotel(Location location) {
                GlobalFunction.goToDestinationByLocation(HistoryActivity.this, location);
            }
        });
        mBinding.rcvData.setAdapter(mHotelAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDataHistory() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListData();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Hotel hotel = dataSnapshot.getValue(Hotel.class);
                    if (hotel == null) return;
                    if (GlobalFunction.isHistoryFood(hotel)) {
                        mListHotel.add(0, hotel);
                    }
                }
                if (mHotelAdapter != null) mHotelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(HistoryActivity.this,
                        getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).hotelDatabaseReference().addValueEventListener(mValueEventListener);
    }

    private void resetListData() {
        if (mListHotel == null) {
            mListHotel = new ArrayList<>();
        } else {
            mListHotel.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).hotelDatabaseReference().removeEventListener(mValueEventListener);
        }
    }
}
