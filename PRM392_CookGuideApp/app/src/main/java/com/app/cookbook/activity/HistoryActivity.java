package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.FoodAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityHistoryBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.model.Destination;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    private ActivityHistoryBinding mBinding;
    private List<Destination> mListDestination;
    private FoodAdapter mFoodAdapter;
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

        mListDestination = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(mListDestination, new IOnClickFoodListener() {
            @Override
            public void onClickItemFood(Destination destination) {
                GlobalFunction.goToFoodDetail(HistoryActivity.this, destination.getId());
            }

            @Override
            public void onClickFavoriteFood(Destination destination, boolean favorite) {
                GlobalFunction.onClickFavoriteFood(HistoryActivity.this, destination, favorite);
            }

            @Override
            public void onClickCategoryOfFood(Location location) {
                GlobalFunction.goToFoodByCategory(HistoryActivity.this, location);
            }
        });
        mBinding.rcvData.setAdapter(mFoodAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDataHistory() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListData();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Destination destination = dataSnapshot.getValue(Destination.class);
                    if (destination == null) return;
                    if (GlobalFunction.isHistoryFood(destination)) {
                        mListDestination.add(0, destination);
                    }
                }
                if (mFoodAdapter != null) mFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(HistoryActivity.this,
                        getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).foodDatabaseReference().addValueEventListener(mValueEventListener);
    }

    private void resetListData() {
        if (mListDestination == null) {
            mListDestination = new ArrayList<>();
        } else {
            mListDestination.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).foodDatabaseReference().removeEventListener(mValueEventListener);
        }
    }
}
