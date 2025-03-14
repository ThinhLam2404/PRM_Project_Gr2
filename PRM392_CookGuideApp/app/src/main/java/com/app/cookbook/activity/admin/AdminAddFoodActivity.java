package com.app.cookbook.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.BaseActivity;
import com.app.cookbook.adapter.admin.AdminSelectAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityAdminAddFoodBinding;
import com.app.cookbook.model.Location;
import com.app.cookbook.model.Destination;
import com.app.cookbook.model.SelectObject;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddFoodActivity extends BaseActivity {

    private ActivityAdminAddFoodBinding binding;
    private boolean isUpdate;
    private Destination mDestination;
    private SelectObject mCategorySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();

        binding.btnAddOrEdit.setOnClickListener(v -> addOrEditFood());
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mDestination = (Destination) bundleReceived.get(Constant.OBJECT_FOOD);
        }
    }

    private void initToolbar() {
        binding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
    }

    private void initView() {
        if (isUpdate) {
            binding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_update_food));
            binding.btnAddOrEdit.setText(getString(R.string.action_edit));

            binding.edtName.setText(mDestination.getName());
            binding.edtImage.setText(mDestination.getImage());
            binding.edtLink.setText(mDestination.getUrl());
            binding.chbFeatured.setChecked(mDestination.isFeatured());
        } else {
            binding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_add_food));
            binding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
        loadListCategory();
    }

    private void loadListCategory() {
        MyApplication.get(this).locationDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<SelectObject> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Location location = dataSnapshot.getValue(Location.class);
                            if (location == null) return;
                            list.add(0, new SelectObject(location.getId(), location.getName()));
                        }
                        AdminSelectAdapter adapter = new AdminSelectAdapter(AdminAddFoodActivity.this,
                                R.layout.item_choose_option, list);
                        binding.spnCategory.setAdapter(adapter);
                        binding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mCategorySelected = adapter.getItem(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });

                        if (mDestination != null && mDestination.getLocationId() > 0) {
                            binding.spnCategory.setSelection(getPositionSelected(list, mDestination.getLocationId()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private int getPositionSelected(List<SelectObject> list, long id) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getId()) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void addOrEditFood() {
        String strName = binding.edtName.getText().toString().trim();
        String strImage = binding.edtImage.getText().toString().trim();
        String strUrl = binding.edtLink.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_input_name_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_input_image_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strUrl)) {
            Toast.makeText(this, getString(R.string.msg_input_url_require), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update food
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("image", strImage);
            map.put("url", strUrl);
            map.put("featured", binding.chbFeatured.isChecked());
            map.put("locationId", mCategorySelected.getId());
            map.put("locationName", mCategorySelected.getName());

            MyApplication.get(this).foodDatabaseReference()
                    .child(String.valueOf(mDestination.getId())).updateChildren(map, (error, ref) -> {
                showProgressDialog(false);
                Toast.makeText(AdminAddFoodActivity.this,
                        getString(R.string.msg_edit_food_success), Toast.LENGTH_SHORT).show();
                GlobalFunction.hideSoftKeyboard(this);
            });
            return;
        }

        // Add food
        showProgressDialog(true);
        long foodId = System.currentTimeMillis();
        Destination destination = new Destination(foodId, strName, strImage, strUrl, binding.chbFeatured.isChecked(),
                mCategorySelected.getId(), mCategorySelected.getName());
        MyApplication.get(this).foodDatabaseReference()
                .child(String.valueOf(foodId)).setValue(destination, (error, ref) -> {
            showProgressDialog(false);
            binding.edtName.setText("");
            binding.edtImage.setText("");
            binding.edtLink.setText("");
            binding.chbFeatured.setChecked(false);
            binding.spnCategory.setSelection(0);
            GlobalFunction.hideSoftKeyboard(this);
            Toast.makeText(this, getString(R.string.msg_add_food_success), Toast.LENGTH_SHORT).show();
        });
    }
}