package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.adapter.FoodAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityFoodByCategoryBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.model.Destination;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodByCategoryActivity extends BaseActivity {

    private ActivityFoodByCategoryBinding mBinding;
    private FoodAdapter mFoodAdapter;
    private List<Destination> mListDestination;
    private Location mLocation;
    private ValueEventListener mFoodValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityFoodByCategoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();
        loadListFoodFromFirebase();
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLocation = (Location) bundle.get(Constant.OBJECT_CATEGORY);
        }
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(mLocation.getName());
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvData.setLayoutManager(linearLayoutManager);
        mListDestination = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(mListDestination, new IOnClickFoodListener() {
            @Override
            public void onClickItemFood(Destination destination) {
                GlobalFunction.goToFoodDetail(FoodByCategoryActivity.this, destination.getId());
            }

            @Override
            public void onClickFavoriteFood(Destination destination, boolean favorite) {
                GlobalFunction.onClickFavoriteFood(FoodByCategoryActivity.this, destination, favorite);
            }

            @Override
            public void onClickCategoryOfFood(Location location) {}
        });
        mBinding.rcvData.setAdapter(mFoodAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListFoodFromFirebase() {
        mFoodValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListFood();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Destination destination = dataSnapshot.getValue(Destination.class);
                    if (destination == null) return;
                    mListDestination.add(0, destination);
                }
                if (mFoodAdapter != null) mFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        MyApplication.get(this).destinationDatabaseReference()
                .orderByChild("locationId").equalTo(mLocation.getId())
                .addValueEventListener(mFoodValueEventListener);
    }

    private void resetListFood() {
        if (mListDestination == null) {
            mListDestination = new ArrayList<>();
        } else {
            mListDestination.clear();
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
