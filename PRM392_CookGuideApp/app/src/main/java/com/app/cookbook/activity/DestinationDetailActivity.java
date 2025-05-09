package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.databinding.ActivityDestinationDetailBinding;
import com.app.cookbook.model.Destination;
import com.app.cookbook.model.UserInfo;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DestinationDetailActivity extends BaseActivity {

    private ActivityDestinationDetailBinding mBinding;
    private long mFoodId;
    private Destination mDestination;
    private ValueEventListener mFoodDetailValueEventListener;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityDestinationDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();
        loadDestinationDetailFromFirebase();
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mFoodId = bundle.getLong(Constant.FOOD_ID);
    }

    private void loadDestinationDetailFromFirebase() {
        showProgressDialog(true);
        mFoodDetailValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                mDestination = snapshot.getValue(Destination.class);
                if (mDestination == null) return;
                initData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(DestinationDetailActivity.this, getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).destinationDetailDatabaseReference(mFoodId).addValueEventListener(mFoodDetailValueEventListener);
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_food_detail));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setSaveFormData(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mBinding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setUseWideViewPort(true);
        mBinding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgressDialog(true);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                showProgressDialog(false);
                isLoaded = true;
                addHistory();
                changeCountViewFood();
            }
        });

    }

    private void initData() {
        if (!isLoaded) {
            loadWebViewFoodDetail();
        }

    }


    private void loadWebViewFoodDetail() {
        if (mDestination == null || StringUtil.isEmpty(mDestination.getUrl())) return;
        mBinding.webView.loadUrl(mDestination.getUrl());
    }

    private void addHistory() {
        if (mDestination == null || isHistory(mDestination)) return;
        String userEmail = DataStoreManager.getUser().getEmail();
        UserInfo userInfo = new UserInfo(System.currentTimeMillis(), userEmail);
        MyApplication.get(this).destinationDatabaseReference()
                .child(String.valueOf(mDestination.getId()))
                .child("history")
                .child(String.valueOf(userInfo.getId()))
                .setValue(userInfo);
    }

    private boolean isHistory(Destination destination) {
        if (destination.getHistory() == null || destination.getHistory().isEmpty()) {
            return false;
        }
        List<UserInfo> listHistory = new ArrayList<>(destination.getHistory().values());
        if (listHistory.isEmpty()) {
            return false;
        }
        for (UserInfo userInfo : listHistory) {
            if (DataStoreManager.getUser().getEmail().equals(userInfo.getEmailUser())) {
                return true;
            }
        }
        return false;
    }

    private void changeCountViewFood() {
        MyApplication.get(this).countDestinationDatabaseReference(mFoodId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentCount = snapshot.getValue(Integer.class);
                        int newCount = 1;
                        if (currentCount != null) {
                            newCount = currentCount + 1;
                        }
                        MyApplication.get(DestinationDetailActivity.this)
                                .countDestinationDatabaseReference(mFoodId).removeEventListener(this);
                        MyApplication.get(DestinationDetailActivity.this)
                                .countDestinationDatabaseReference(mFoodId).setValue(newCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFoodDetailValueEventListener != null) {
            MyApplication.get(this).destinationDetailDatabaseReference(mFoodId)
                    .removeEventListener(mFoodDetailValueEventListener);
        }
    }
}
