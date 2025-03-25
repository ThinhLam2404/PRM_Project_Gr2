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
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityHotelDetailBinding;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class HotelDetailActivity extends BaseActivity {

    private ActivityHotelDetailBinding mBinding;

    private long mFoodId;
    private Hotel mHotel;
    private ValueEventListener mFoodDetailValueEventListener;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityHotelDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        loadDataIntent();
        initToolbar();
        initView();
        loadHotelDetailFromFirebase();
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
//                addHistory();
                changeCountViewFood();
            }
        });

    }

    private void changeCountViewFood() {
        MyApplication.get(this).countHotelDatabaseReference(mFoodId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentCount = snapshot.getValue(Integer.class);
                        int newCount = 1;
                        if (currentCount != null) {
                            newCount = currentCount + 1;
                        }
                        MyApplication.get(HotelDetailActivity.this)
                                .countHotelDatabaseReference(mFoodId).removeEventListener(this);
                        MyApplication.get(HotelDetailActivity.this)
                                .countHotelDatabaseReference(mFoodId).setValue(newCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mFoodId = bundle.getLong(Constant.FOOD_ID);
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

    private void loadHotelDetailFromFirebase() {
        showProgressDialog(true);
        mFoodDetailValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                mHotel = snapshot.getValue(Hotel.class);
                if (mHotel == null) return;
                initData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(HotelDetailActivity.this, getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).hotelDetailDatabaseReference(mFoodId).addValueEventListener(mFoodDetailValueEventListener);
    }

    private void initData() {
        if (!isLoaded) {
            loadWebViewHotelDetail();
        }


        mBinding.btnBooking.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.FOOD_ID, mFoodId);
            GlobalFunction.startActivity(HotelDetailActivity.this, BookingHotel.class, bundle);
        });

    }

    private void loadWebViewHotelDetail() {
        if (mHotel == null || StringUtil.isEmpty(mHotel.getUrl())) return;
        mBinding.webView.loadUrl(mHotel.getUrl());
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_food_detail));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFoodDetailValueEventListener != null) {
            MyApplication.get(this).hotelDetailDatabaseReference(mFoodId)
                    .removeEventListener(mFoodDetailValueEventListener);
        }
    }
}