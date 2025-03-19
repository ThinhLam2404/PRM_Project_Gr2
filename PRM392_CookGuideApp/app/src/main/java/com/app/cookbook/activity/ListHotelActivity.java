package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.HotelAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityListHotelBinding;
import com.app.cookbook.listener.IOnClickHotelListener;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListHotelActivity extends BaseActivity {

    private ActivityListHotelBinding mBinding;
    private HotelAdapter mHotelAdapter;
    private List<Hotel> mListHotel;
    private ValueEventListener mFoodValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityListHotelBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initView();
        loadListFoodFromFirebase();
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_food_popular));
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvData.setLayoutManager(linearLayoutManager);
        mListHotel = new ArrayList<>();
        mBinding.layoutSearch.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("search", "hotel");
            startActivity(intent);
        });

        mHotelAdapter = new HotelAdapter(mListHotel, new IOnClickHotelListener() {
            @Override
            public void onClickItemHotel(Hotel hotel) {
//                GlobalFunction.goToDestinationDetail(List.this, hotel.getId());
            }

            @Override
            public void onClickFavoriteHotel(Hotel hotel, boolean favorite) {
                GlobalFunction.onClickFavoriteHotel(ListHotelActivity.this, hotel, favorite);
            }

            @Override
            public void onClickLocationOfHotel(Location location) {
                GlobalFunction.goToDestinationByLocation(ListHotelActivity.this, location);
            }
        });
        mBinding.rcvData.setAdapter(mHotelAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListFoodFromFirebase() {
        mFoodValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListFood();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Hotel hotel = dataSnapshot.getValue(Hotel.class);
                    if (hotel == null) return;
                    mListHotel.add(0, hotel);
                }
                Collections.sort(mListHotel, (food1, food2) -> food2.getCount() - food1.getCount());
                if (mHotelAdapter != null) mHotelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        MyApplication.get(this).hotelDatabaseReference().addValueEventListener(mFoodValueEventListener);
    }

    private void resetListFood() {
        if (mListHotel == null) {
            mListHotel = new ArrayList<>();
        } else {
            mListHotel.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFoodValueEventListener != null) {
            MyApplication.get(this).destinationDatabaseReference().removeEventListener(mFoodValueEventListener);
        }
    }
}
