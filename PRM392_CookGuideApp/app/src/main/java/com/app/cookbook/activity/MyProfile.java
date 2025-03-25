package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.databinding.ActivityMyProfileBinding;
import com.app.cookbook.model.User;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MyProfile extends BaseActivity {
    private ActivityMyProfileBinding mBinding;
    private String id;
    private ValueEventListener mUserDetailValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        id = getIntent().getStringExtra("id");

        setContentView(mBinding.getRoot());
        initToolbar();
        loadUserDetailFromFirebase();
        initListener();
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_my_profile));
    }

    public void showProgressDialog(boolean value) {
        if (value) {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setCancelable(false);
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void initListener() {
        mBinding.editFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mBinding.editFullName.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mBinding.editFullName.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }
            }
        });
        mBinding.editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mBinding.editPhone.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mBinding.editPhone.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }
            }
        });
        mBinding.editAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mBinding.editAddress.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mBinding.editAddress.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }
            }
        });

        mBinding.saveButton.setOnClickListener(v -> onClickValidateField());
    }

    private void onClickValidateField() {
        String fullname = mBinding.editFullName.getText().toString().trim();
        String phone = mBinding.editPhone.getText().toString().trim();
        String address = mBinding.editAddress.getText().toString().trim();
        if (StringUtil.isEmpty(fullname)) {
            showToastMessage(this, getString(R.string.msg_name_require));
        } else if (StringUtil.isEmpty(phone)) {
            showToastMessage(this, getString(R.string.msg_phone_require));
        } else if (StringUtil.isEmpty(address)) {
            showToastMessage(this, getString(R.string.msg_address_require));

        } else {
            updateProfile(fullname, phone, address);
        }
    }

    private void updateProfile(String name, String phone, String address) {
        showProgressDialog(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            showToastMessage(this, "Người dùng chưa đăng nhập!");
            showProgressDialog(false);
            return;
        }

        // Cập nhật thông tin trong Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(user.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("address", address);

        userRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        showToastMessage(this, "Cập nhật thông tin thành công!");

                        // Cập nhật dữ liệu trong DataStoreManager
                        User userLogin = DataStoreManager.getUser();
                        userLogin.setName(name);
                        userLogin.setPhone(phone);
                        userLogin.setAddress(address);
                        DataStoreManager.setUser(userLogin);
                        finish();
                    } else {
                        showToastMessage(this, "Cập nhật thất bại, vui lòng thử lại!");
                    }
                });
    }

    private void loadUserDetailFromFirebase() {
        showProgressDialog(true);


        mUserDetailValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                User user = snapshot.getValue(User.class); // Sử dụng User model

                if (user == null) return;

                // Hiển thị dữ liệu lên giao diện
                mBinding.editFullName.setText(user.getName());
                mBinding.editEmail.setText(user.getEmail());
                mBinding.editPhone.setText(user.getPhone());
                mBinding.editAddress.setText(user.getAddress());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(MyProfile.this, getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).userDetailDatabaseReference(id).addValueEventListener(mUserDetailValueEventListener);

    }
}