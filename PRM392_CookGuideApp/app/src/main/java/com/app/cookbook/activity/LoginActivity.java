package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.admin.AdminMainActivity;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityLogInBinding;
import com.app.cookbook.model.User;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {

    private ActivityLogInBinding mActivityLogInBinding;
    private boolean isEnableButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mActivityLogInBinding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(mActivityLogInBinding.getRoot());

        initListener();


    }

    private void initListener() {
        mActivityLogInBinding.rdbUser.setChecked(true);
        mActivityLogInBinding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityLogInBinding.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mActivityLogInBinding.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }

                String strPassword = mActivityLogInBinding.edtPassword.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strPassword)) {
                    isEnableButtonLogin = true;
                    mActivityLogInBinding.btnLogin.setBackgroundResource(R.drawable.bg_button_enable_corner_10);
                } else {
                    isEnableButtonLogin = false;
                    mActivityLogInBinding.btnLogin.setBackgroundResource(R.drawable.bg_button_disable_corner_10);
                }
            }
        });

        mActivityLogInBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityLogInBinding.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mActivityLogInBinding.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }

                String strEmail = mActivityLogInBinding.edtEmail.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strEmail)) {
                    isEnableButtonLogin = true;
                    mActivityLogInBinding.btnLogin.setBackgroundResource(R.drawable.bg_button_enable_corner_10);
                } else {
                    isEnableButtonLogin = false;
                    mActivityLogInBinding.btnLogin.setBackgroundResource(R.drawable.bg_button_disable_corner_10);
                }
            }
        });

        mActivityLogInBinding.layoutRegister.setOnClickListener(
                v -> GlobalFunction.startActivity(this, RegisterActivity.class));

        mActivityLogInBinding.btnLogin.setOnClickListener(v -> onClickValidateLogin());
        mActivityLogInBinding.tvForgotPassword.setOnClickListener(
                v -> GlobalFunction.startActivity(this, ForgotPasswordActivity.class));

        mActivityLogInBinding.btnChangeLanguage.setOnClickListener(v -> {
            // Toggle between languages (e.g., "en" and "vi")
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String currentLanguage = prefs.getString("language", "vi");
            String newLanguage = "vi".equals(currentLanguage) ? "en" : "vi";
            LocaleHelper.setLocale(this, newLanguage);
            recreate(); // Restart the activity to apply the new language
        });
    }

    private void onClickValidateLogin() {
        if (!isEnableButtonLogin) return;

        String strEmail = mActivityLogInBinding.edtEmail.getText().toString().trim();
        String strPassword = mActivityLogInBinding.edtPassword.getText().toString().trim();
        if (StringUtil.isEmpty(strEmail)) {
            showToastMessage(this, getString(R.string.msg_email_require));
        } else if (StringUtil.isEmpty(strPassword)) {
            showToastMessage(this, getString(R.string.msg_password_require));
        } else {
            loginUserFirebase(strEmail, strPassword);
        }
    }


private void loginUserFirebase(String email, String password) {
    showProgressDialog(true);
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                showProgressDialog(false);
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            Log.d("Login", "Email verified, retrieving user data for: " + email);
                            retrieveUserData(email, password);
                        } else {
                            showToastMessage(this, getString(R.string.msg_email_not_verified));
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            showToastMessage(this, getString(R.string.msg_verification_email_sent));
                                        } else {
                                            showToastMessage(this, "Gửi email xác thực thất bại: " + verificationTask.getException().getMessage());
                                        }
                                    });
                        }
                    } else {
                        showToastMessage(this, "Không lấy được thông tin người dùng");
                    }
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                    Log.e("Login", "Login failed: " + errorMessage);
                    showToastMessage(this, "Đăng nhập thất bại: " + errorMessage);
                }
            });
}
    private void retrieveUserData(String email, String password) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isAdmin = dataSnapshot.child("admin").getValue(Boolean.class);
                    if (isAdmin == null) isAdmin = false;

                    User userObject = new User(email, password);
                    userObject.setAdmin(isAdmin);
                    DataStoreManager.setUser(userObject);
                    Log.d("Login", "User data retrieved: " + userObject.toString());
                    goToMainActivity();
                } else {
                    Log.e("Login", "No user data found for UID: " + userId);
                    showToastMessage(LoginActivity.this, "Không tìm thấy dữ liệu người dùng trong database");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Login", "Database error: " + databaseError.getMessage());
                showToastMessage(LoginActivity.this, "Lỗi: " + databaseError.getMessage());
            }
        });
    }
    private void goToMainActivity() {
        if (DataStoreManager.getUser().isAdmin()) {
            GlobalFunction.startActivity(LoginActivity.this, AdminMainActivity.class);
        } else {
            GlobalFunction.startActivity(LoginActivity.this, MainActivity.class);
        }
        finishAffinity();
    }
}