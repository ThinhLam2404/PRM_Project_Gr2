package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.DestinationAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityHistoryBinding;
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
    private DestinationAdapter mDestinationAdapter;
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
//        mDestinationAdapter = new DestinationAdapter(mListDestination, new IOnClickDestinationListener() {
//            @Override
//            public void onClickItemDestination(Destination destination) {
//                GlobalFunction.goToDestinationDetail(HistoryActivity.this, destination.getId());
//            }
//
//            @Override
//            public void onClickFavoriteDestination(Destination destination, boolean favorite) {
//                GlobalFunction.onClickFavoriteDestination(HistoryActivity.this, destination, favorite);
//            }
//
//            @Override
//            public void onClickLocationOfDestination(Location location) {
//                GlobalFunction.goToDestinationByLocation(HistoryActivity.this, location);
//            }
//        });
        mBinding.rcvData.setAdapter(mDestinationAdapter);
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
                if (mDestinationAdapter != null) mDestinationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(HistoryActivity.this,
                        getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).destinationDatabaseReference().addValueEventListener(mValueEventListener);
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
            MyApplication.get(this).destinationDatabaseReference().removeEventListener(mValueEventListener);
        }
    }
}
