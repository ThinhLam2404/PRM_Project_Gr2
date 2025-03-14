package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.CategoryListAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityListCategoryBinding;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListCategoryActivity extends BaseActivity {

    private ActivityListCategoryBinding mBinding;
    private CategoryListAdapter mCategoryListAdapter;
    private List<Location> mListLocation;
    private ValueEventListener mCategoryValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityListCategoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initView();
        loadListCategoryFromFirebase();
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_category));
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mBinding.rcvData.setLayoutManager(gridLayoutManager);
        mListLocation = new ArrayList<>();
        mCategoryListAdapter = new CategoryListAdapter(mListLocation,
                category -> GlobalFunction.goToFoodByCategory(ListCategoryActivity.this, category));
        mBinding.rcvData.setAdapter(mCategoryListAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListCategoryFromFirebase() {
        mCategoryValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListCategory();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Location location = dataSnapshot.getValue(Location.class);
                    if (location == null) return;
                    mListLocation.add(0, location);
                }
                if (mCategoryListAdapter != null) mCategoryListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        MyApplication.get(this).locationDatabaseReference().addValueEventListener(mCategoryValueEventListener);
    }

    private void resetListCategory() {
        if (mListLocation == null) {
            mListLocation = new ArrayList<>();
        } else {
            mListLocation.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCategoryValueEventListener != null) {
            MyApplication.get(this).locationDatabaseReference().removeEventListener(mCategoryValueEventListener);
        }
    }
}
