package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.TripAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityMyTripBinding;
import com.app.cookbook.model.Trip;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyTrip extends AppCompatActivity {
    String userid;
    private ActivityMyTripBinding mBinding;
    private TripAdapter mTripAdapter;
    private List<Trip> mListTrip;
    private ValueEventListener mFoodValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);

        userid = getIntent().getStringExtra("id");
        mBinding = ActivityMyTripBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());
        initToolbar();
        initView();
        loadListTripFromFirebase();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvData.setLayoutManager(linearLayoutManager);
        mListTrip = new ArrayList<>();
        mTripAdapter = new TripAdapter(mListTrip, trip -> GlobalFunction.goToTripDetail(MyTrip.this, trip.getId()));
        mBinding.rcvData.setAdapter(mTripAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListTripFromFirebase() {
        mFoodValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListFood();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String tripId = dataSnapshot.getKey();
                    trip.setId(tripId);
                    if (trip == null) return;
                    mListTrip.add(0, trip);
                }
                if (mTripAdapter != null) mTripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        MyApplication.get(this).tripDatabaseReference().orderByChild("userId").equalTo(userid).addValueEventListener(mFoodValueEventListener);
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_my_trip));
    }

    private void resetListFood() {
        if (mListTrip == null) {
            mListTrip = new ArrayList<>();
        } else {
            mListTrip.clear();
        }
    }
}