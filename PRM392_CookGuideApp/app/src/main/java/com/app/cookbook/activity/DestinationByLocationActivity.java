package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.adapter.DestinationAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityDestinationByLocationBinding;
import com.app.cookbook.listener.IOnClickDestinationListener;
import com.app.cookbook.model.Destination;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DestinationByLocationActivity extends BaseActivity {

    private ActivityDestinationByLocationBinding mBinding;
    private DestinationAdapter mDestinationAdapter;
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
        mBinding = ActivityDestinationByLocationBinding.inflate(getLayoutInflater());
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
        mDestinationAdapter = new DestinationAdapter(mListDestination, new IOnClickDestinationListener() {
            @Override
            public void onClickItemDestination(Destination destination) {
                GlobalFunction.goToDestinationDetail(DestinationByLocationActivity.this, destination.getId());
            }

//            @Override
//            public void onClickFavoriteDestination(Destination destination, boolean favorite) {
//                GlobalFunction.onClickFavoriteDestination(DestinationByLocationActivity.this, destination, favorite);
//            }

            @Override
            public void onClickLocationOfDestination(Location location) {
            }
        });
        mBinding.rcvData.setAdapter(mDestinationAdapter);
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
                if (mDestinationAdapter != null) mDestinationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        MyApplication.get(this).destinationDatabaseReference()
                .orderByChild("locationId").equalTo(mLocation.getId())
                .addValueEventListener(mFoodValueEventListener);
    }

    private void loadListDestinationFromFirebase() {
        mFoodValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListFood();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Destination destination = dataSnapshot.getValue(Destination.class);
                    if (destination == null) return;
                    mListDestination.add(0, destination);
                }
                if (mDestinationAdapter != null) mDestinationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
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
