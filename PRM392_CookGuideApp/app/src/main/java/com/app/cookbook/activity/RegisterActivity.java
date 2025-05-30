package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.app.cookbook.R;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityRegisterBinding;
import com.app.cookbook.model.User;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding mActivityRegisterBinding;
    private boolean isEnableButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mActivityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(mActivityRegisterBinding.getRoot());


        initListener();
        initToolbar();
    }


    private void initToolbar() {
        mActivityRegisterBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mActivityRegisterBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_register));
    }

    private void initListener() {
        mActivityRegisterBinding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityRegisterBinding.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mActivityRegisterBinding.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }

                String strPassword = mActivityRegisterBinding.edtPassword.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strPassword)) {
                    isEnableButtonRegister = true;
                    mActivityRegisterBinding.btnRegister.setBackgroundResource(R.drawable.bg_button_enable_corner_10);
                } else {
                    isEnableButtonRegister = false;
                    mActivityRegisterBinding.btnRegister.setBackgroundResource(R.drawable.bg_button_disable_corner_10);
                }
            }
        });

        mActivityRegisterBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityRegisterBinding.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mActivityRegisterBinding.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }

                String strEmail = mActivityRegisterBinding.edtEmail.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strEmail)) {
                    isEnableButtonRegister = true;
                    mActivityRegisterBinding.btnRegister.setBackgroundResource(R.drawable.bg_button_enable_corner_10);
                } else {
                    isEnableButtonRegister = false;
                    mActivityRegisterBinding.btnRegister.setBackgroundResource(R.drawable.bg_button_disable_corner_10);
                }
            }
        });

        mActivityRegisterBinding.layoutLogin.setOnClickListener(v -> finish());
        mActivityRegisterBinding.btnRegister.setOnClickListener(v -> onClickValidateRegister());
    }

    private void onClickValidateRegister() {
        if (!isEnableButtonRegister) return;
        String strName = mActivityRegisterBinding.edtName.getText().toString().trim();
        String strEmail = mActivityRegisterBinding.edtEmail.getText().toString().trim();
        String strPassword = mActivityRegisterBinding.edtPassword.getText().toString().trim();
        String strConfirmPassword = mActivityRegisterBinding.edtConfirmPassword.getText().toString().trim();
        String strPhone = mActivityRegisterBinding.edtPhone.getText().toString().trim();
        String strAddress = mActivityRegisterBinding.edtAddress.getText().toString().trim();

        if (StringUtil.isEmpty(strEmail)) {
            showToastMessage(this, getString(R.string.msg_email_require));
        } else if (StringUtil.isEmpty(strName)) {
            showToastMessage(this, getString(R.string.msg_name_require));
        } else if (StringUtil.isEmpty(strPassword)) {
            showToastMessage(this, getString(R.string.msg_password_require));
        } else if (StringUtil.isEmpty(strPhone)) {
            showToastMessage(this, getString(R.string.msg_phone_require));
        } else if (StringUtil.isEmpty(strAddress)) {
            showToastMessage(this, getString(R.string.msg_address_require));
        } else if (StringUtil.isEmpty(strConfirmPassword)) {
            showToastMessage(this, getString(R.string.msg_confirm_password_require));
        } else if (!strPassword.equals(strConfirmPassword)) {
            showToastMessage(this, getString(R.string.msg_passwords_do_not_match));
        } else {
//            registerUserFirebase(strEmail, strPassword);
            registerUserFirebase(strEmail, strPassword, strName, strPhone, strAddress);

        }
    }

    private void registerUserFirebase(String email, String password, String name, String phone, String address) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        Log.d("firebaseUser", "registerUserFirebase: " + firebaseUser);
                        if (firebaseUser != null) {
                            // Gửi email xác thực
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            // Thông báo gửi email xác thực thành công
                                            Toast.makeText(RegisterActivity.this,
                                                    "Vui lòng kiểm tra email để xác thực tài khoản!",
                                                    Toast.LENGTH_LONG).show();
                                            // Lưu thông tin người dùng (đặt cờ admin nếu cần)
                                            User userObject = new User(firebaseUser.getEmail(), password, name, phone, address);
                                            Log.d("USER", "registerUserFirebase: " + userObject);
                                            // Lưu thông tin người dùng vào Firebase Realtime Database
                                            saveUserToDatabase(userObject);
                                            // Chuyển hướng về trang đăng nhập sau khi đăng ký
                                            firebaseAuth.signOut(); // Đăng xuất người dùng để họ không đăng nhập ngay lập tức
                                            finish();
                                        } else {
                                            // Thông báo lỗi khi gửi email
                                            Toast.makeText(RegisterActivity.this,
                                                    "Gửi email xác thực thất bại.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        showToastMessage(this, getString(R.string.msg_email_already_exists));
                    }
                });
    }

    private void registerUserFirebase(String email, String password) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Gửi email xác thực
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            // Thông báo gửi email xác thực thành công
                                            Toast.makeText(RegisterActivity.this,
                                                    "Vui lòng kiểm tra email để xác thực tài khoản!",
                                                    Toast.LENGTH_LONG).show();

                                            // Lưu thông tin người dùng (đặt cờ admin nếu cần)
                                            User userObject = new User(firebaseUser.getEmail(), password);

                                            Log.d("USER", "registerUserFirebase: " + userObject);
//
                                            // Lưu thông tin người dùng vào Firebase Realtime Database
                                            saveUserToDatabase(userObject);

                                            // Chuyển hướng về trang đăng nhập sau khi đăng ký
                                            firebaseAuth.signOut(); // Đăng xuất người dùng để họ không đăng nhập ngay lập tức
                                            finish();
                                        } else {
                                            // Thông báo lỗi khi gửi email
                                            Toast.makeText(RegisterActivity.this,
                                                    "Gửi email xác thực thất bại.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        showToastMessage(this, getString(R.string.msg_email_already_exists));
                    }
                });
    }


    private void saveUserToDatabase(User user) {


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user"); // Dùng trực tiếp getReference()
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Register", "User data saved successfully for UID: " + userId);
                    } else {
                        Log.e("Register", "Failed to save user data: " + task.getException().getMessage());
                    }
                });
    }

    private void goToMainActivity() {
        GlobalFunction.startActivity(RegisterActivity.this, MainActivity.class);
        finishAffinity();
    }
}