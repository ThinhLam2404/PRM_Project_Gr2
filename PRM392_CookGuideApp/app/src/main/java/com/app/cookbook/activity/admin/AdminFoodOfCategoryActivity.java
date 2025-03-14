package com.app.cookbook.activity.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.BaseActivity;
import com.app.cookbook.adapter.admin.AdminFoodAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityAdminFoodOfCategoryBinding;
import com.app.cookbook.listener.IOnAdminManagerFoodListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.model.Destination;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminFoodOfCategoryActivity extends BaseActivity {

    private ActivityAdminFoodOfCategoryBinding binding;
    private List<Destination> mListDestination;
    private AdminFoodAdapter mAdminFoodAdapter;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminFoodOfCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();
        loadListFood();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            mLocation = (Location) bundleReceived.get(Constant.OBJECT_CATEGORY);
        }
    }

    private void initToolbar() {
        binding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        binding.layoutToolbar.tvToolbarTitle.setText(mLocation.getName());
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcvFood.setLayoutManager(linearLayoutManager);
        mListDestination = new ArrayList<>();
        mAdminFoodAdapter = new AdminFoodAdapter(mListDestination, new IOnAdminManagerFoodListener() {
            @Override
            public void onClickUpdateFood(Destination destination) {
                onClickEditFood(destination);
            }

            @Override
            public void onClickDeleteFood(Destination destination) {
                deleteFoodItem(destination);
            }

            @Override
            public void onClickDetailFood(Destination destination) {
                goToFoodDetail(destination);
            }
        });
        binding.rcvFood.setAdapter(mAdminFoodAdapter);
    }

    private void goToFoodDetail(@NonNull Destination destination) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_FOOD, destination);
        GlobalFunction.startActivity(this, AdminFoodDetailActivity.class, bundle);
    }

    private void onClickEditFood(Destination destination) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_FOOD, destination);
        GlobalFunction.startActivity(this, AdminAddFoodActivity.class, bundle);
    }

    private void deleteFoodItem(Destination destination) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) ->
                        MyApplication.get(this).foodDatabaseReference()
                        .child(String.valueOf(destination.getId())).removeValue((error, ref) ->
                                Toast.makeText(this,
                                        getString(R.string.msg_delete_food_successfully),
                                        Toast.LENGTH_SHORT).show()))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void resetListFood() {
        if (mListDestination != null) {
            mListDestination.clear();
        } else {
            mListDestination = new ArrayList<>();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadListFood() {
        MyApplication.get(this).foodDatabaseReference()
                .orderByChild("categoryId").equalTo(mLocation.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        resetListFood();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Destination destination = dataSnapshot.getValue(Destination.class);
                            if (destination == null) return;
                            mListDestination.add(0, destination);
                        }
                        if (mAdminFoodAdapter != null) mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}