package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.HotelAdapter;
import com.app.cookbook.adapter.HotelFeaturedAdapter;
import com.app.cookbook.adapter.LocationHomeAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityMainBinding;
import com.app.cookbook.listener.IOnClickHotelListener;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;
import com.app.cookbook.model.User;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final Handler mHandlerBanner = new Handler(Looper.getMainLooper());
    private ActivityMainBinding mBinding;
    //    private LocationMenuAdapter mLocationMenuAdapter;
    private List<Location> mListLocation;
    private List<Location> mListLocationHome;
    private List<Hotel> mListHotel;
    private List<Hotel> mListHotelFeatured;
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListHotelFeatured == null || mListHotelFeatured.isEmpty()) return;
            if (mBinding.viewPager.getCurrentItem() == mListHotelFeatured.size() - 1) {
                mBinding.viewPager.setCurrentItem(0);
                return;
            }
            mBinding.viewPager.setCurrentItem(mBinding.viewPager.getCurrentItem() + 1);
        }
    };
    private ValueEventListener mCategoryValueEventListener;
    private ValueEventListener mHotelValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi");
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initListener();
        initNavigationMenuLeft();

        Switch switchLanguage = mBinding.switchLanguage;
        if (switchLanguage != null) {
            switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String languageCodes = isChecked ? "vi" : "en";
                saveLanguagePreference(languageCodes);
                changeLanguage(languageCodes);

                int drawableId = isChecked ? R.drawable.ic_language_vn : R.drawable.ic_language_en;
                Drawable drawable = getResources().getDrawable(drawableId);
                int width = (int) (drawable.getIntrinsicWidth() * 0.3);
                int height = (int) (drawable.getIntrinsicHeight() * 0.3);
                drawable.setBounds(0, 0, width, height);
                switchLanguage.setCompoundDrawables(drawable, null, null, null);
            });

            SharedPreferences prefss = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String savedLanguage = prefss.getString("language", "vi");
            switchLanguage.setChecked(savedLanguage.equals("vi"));
            int initialDrawableId = savedLanguage.equals("vi") ? R.drawable.ic_language_vn : R.drawable.ic_language_en;
            Drawable initialDrawable = getResources().getDrawable(initialDrawableId);
            int initialWidth = (int) (initialDrawable.getIntrinsicWidth() * 0.3);
            int initialHeight = (int) (initialDrawable.getIntrinsicHeight() * 0.3);
            initialDrawable.setBounds(0, 0, initialWidth, initialHeight);
            switchLanguage.setCompoundDrawables(initialDrawable, null, null, null);
        } else {
            Log.e("MainActivity", "Switch is null");
        }
    }

    private void saveLanguagePreference(String languageCode) {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageCode);
        editor.apply();
    }

    private void changeLanguage(String languageCode) {
        Locale currentLocale = getResources().getConfiguration().locale;
        if (!currentLocale.getLanguage().equals(languageCode)) {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            recreate();
        }
    }

    private void initToolbar() {
        mBinding.header.imgToolbar.setImageResource(R.drawable.ic_menu);
        mBinding.header.tvToolbarTitle.setText(getString(R.string.app_name));
    }

    private void initListener() {
        mBinding.header.imgToolbar.setOnClickListener(this);
        mBinding.layoutChangePassword.setOnClickListener(this);
        mBinding.layoutSignOut.setOnClickListener(this);
        mBinding.viewAllCategory.setOnClickListener(this);
        mBinding.viewAllFood.setOnClickListener(this);
        mBinding.layoutSearch.setOnClickListener(this);
        mBinding.layoutFavorite.setOnClickListener(this);
        mBinding.layoutHistory.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_toolbar:
                mBinding.drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.layout_change_password:
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                GlobalFunction.startActivity(this, ChangePasswordActivity.class);
                break;

            case R.id.layout_sign_out:
                onClickSignOut();
                break;

            case R.id.view_all_category:
                GlobalFunction.startActivity(this, ListLocationActivity.class);
                break;

            case R.id.view_all_food:
                GlobalFunction.startActivity(this, ListHotelActivity.class);
                break;

            case R.id.layout_search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("search", "destination");
                startActivity(intent);
                break;

            case R.id.layout_favorite:
                GlobalFunction.startActivity(this, FavoriteActivity.class);
                break;

            case R.id.layout_history:
                GlobalFunction.startActivity(this, HistoryActivity.class);
                break;

        }
    }

    private void initNavigationMenuLeft() {
        displayUserInformation();
        loadListLocationFromFirebase();
    }

    private void displayUserInformation() {
        User user = DataStoreManager.getUser();
        mBinding.tvUserEmail.setText(user.getEmail());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListLocationFromFirebase() {
        showProgressDialog(true);
        mCategoryValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListLocation();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Location location = dataSnapshot.getValue(Location.class);
                    if (location == null) return;
                    mListLocation.add(0, location);
                }
//                if (mLocationMenuAdapter != null) mLocationMenuAdapter.notifyDataSetChanged();
                displayListLocationHome();
                loadListHotelFromFirebase(); // Tải danh sách hotels
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
            }
        };
        MyApplication.get(this).locationDatabaseReference().addValueEventListener(mCategoryValueEventListener);
    }

    private void displayListLocationHome() {
        LinearLayoutManager layoutManagerHorizontal = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mBinding.rcvCategoryHome.setLayoutManager(layoutManagerHorizontal);
        LocationHomeAdapter locationHomeAdapter = new LocationHomeAdapter(loadListLocationHome(),
                category -> GlobalFunction.goToDestinationByLocation(MainActivity.this, category)); // Chuyển sang hiển thị destinations
        mBinding.rcvCategoryHome.setAdapter(locationHomeAdapter);
    }

    private List<Location> loadListLocationHome() {
        resetListLocationHome();
        for (Location location : mListLocation) {
            if (mListLocationHome.size() < Constant.MAX_SIZE_LIST_CATEGORY) {
                mListLocationHome.add(location);
            }
        }
        return mListLocationHome;
    }

    private void resetListLocation() {
        if (mListLocation == null) {
            mListLocation = new ArrayList<>();
        } else {
            mListLocation.clear();
        }
    }

    private void resetListLocationHome() {
        if (mListLocationHome == null) {
            mListLocationHome = new ArrayList<>();
        } else {
            mListLocationHome.clear();
        }
    }

    private void resetListHotel() {
        if (mListHotel == null) {
            mListHotel = new ArrayList<>();
        } else {
            mListHotel.clear();
        }
    }

    private void resetListHotelFeatured() {
        if (mListHotelFeatured == null) {
            mListHotelFeatured = new ArrayList<>();
        } else {
            mListHotelFeatured.clear();
        }
    }

    private void loadListHotelFromFirebase() {
        mHotelValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                resetListHotel();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Hotel hotel = dataSnapshot.getValue(Hotel.class);
                    if (hotel == null) return;
                    mListHotel.add(0, hotel);
                }
                displayListHotelFeatured();
                displayListPopularHotel();
                displayCountHotelOfLocation();
                displayCountFavorite();
                displayCountHistory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                GlobalFunction.showToastMessage(MainActivity.this, getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).hotelDatabaseReference().addValueEventListener(mHotelValueEventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayCountHotelOfLocation() {
        if (mListLocation == null || mListLocation.isEmpty()) return;
        for (Location location : mListLocation) {
            location.setCount(loadCountHotelOfLocation(location.getId()));
        }
//        if (mLocationMenuAdapter != null) mLocationMenuAdapter.notifyDataSetChanged();
    }

    private int loadCountHotelOfLocation(long locationId) {
        if (mListHotel == null || mListHotel.isEmpty()) return 0;
        List<Hotel> listHotels = new ArrayList<>();
        for (Hotel hotel : mListHotel) {
            if (locationId == hotel.getLocationId()) {
                listHotels.add(hotel);
            }
        }
        return listHotels.size();
    }

    private void displayCountFavorite() {
        int countFavorite = 0;
        if (mListHotel != null && !mListHotel.isEmpty()) {
            List<Hotel> listFavorite = new ArrayList<>();
            for (Hotel hotel : mListHotel) {
                // Giả sử Hotel có logic favorite tương tự Destination, nếu không thì bỏ qua
                if (GlobalFunction.isFavoriteHotel(hotel)) {
                    listFavorite.add(hotel);
                }
            }
            countFavorite = listFavorite.size();
        }
        mBinding.tvCountFavorite.setText(String.valueOf(countFavorite));
    }

    private void displayCountHistory() {
        int countHistory = 0;
        if (mListHotel != null && !mListHotel.isEmpty()) {
            List<Hotel> listHistory = new ArrayList<>();
            for (Hotel hotel : mListHotel) {
                // Giả sử Hotel có logic history tương tự Destination, nếu không thì bỏ qua
                // if (GlobalFunction.isHistoryFood(hotel)) {
                //     listHistory.add(hotel);
                // }
            }
            countHistory = listHistory.size();
        }
        mBinding.tvCountHistory.setText(String.valueOf(countHistory));
    }

    private void displayListHotelFeatured() {
        HotelFeaturedAdapter hotelFeaturedAdapter = new HotelFeaturedAdapter(loadListHotelFeatured(),
                new IOnClickHotelListener() {
                    //                    @Override
                    public void onClickItemHotel(Hotel hotel) {
//                        GlobalFunction.goToFoodDetail(MainActivity.this, hotel.getId());
                    }

                    @Override
                    public void onClickFavoriteHotel(Hotel hotel, boolean favorite) {
                        // Xử lý favorite nếu Hotel có trường này
                    }

                    @Override
                    public void onClickLocationOfHotel(Location category) {
                    }
                });
        mBinding.viewPager.setAdapter(hotelFeaturedAdapter);
        mBinding.indicator.setViewPager(mBinding.viewPager);

        mBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private List<Hotel> loadListHotelFeatured() {
        resetListHotelFeatured();
        for (Hotel hotel : mListHotel) {
            // Nếu Hotel không có trường featured, hiển thị tất cả hoặc dựa vào tiêu chí khác
            if (mListHotelFeatured.size() < Constant.MAX_SIZE_LIST_FEATURED) {
                mListHotelFeatured.add(hotel);
            }
        }
        return mListHotelFeatured;
    }

    private void displayListPopularHotel() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvFoodPopular.setLayoutManager(linearLayoutManager);

        HotelAdapter foodAdapter = new HotelAdapter(loadListPopularHotel(),
                new IOnClickHotelListener() {
                    @Override
                    public void onClickItemHotel(Hotel hotel) {
//                        GlobalFunction.goToFoodDetail(MainActivity.this, hotel.getId());
                    }

                    @Override
                    public void onClickFavoriteHotel(Hotel hotel, boolean favorite) {
                        GlobalFunction.onClickFavoriteHotel(MainActivity.this, hotel, favorite);
                    }

                    @Override
                    public void onClickLocationOfHotel(Location category) {
                        GlobalFunction.goToDestinationByLocation(MainActivity.this, category);
                    }
                });
        mBinding.rcvFoodPopular.setAdapter(foodAdapter);
    }

    private List<Hotel> loadListPopularHotel() {
        List<Hotel> list = new ArrayList<>();
        List<Hotel> allHotels = new ArrayList<>(mListHotel);
        Collections.sort(allHotels, (hotel1, hotel2) -> hotel2.getCount() - hotel1.getCount());
        for (Hotel hotel : allHotels) {
            if (list.size() < Constant.MAX_SIZE_LIST_POPULAR) {
                list.add(hotel);
            }
        }
        return list;
    }


    private void onClickSignOut() {
        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(this, LoginActivity.class);
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCategoryValueEventListener != null) {
            MyApplication.get(this).locationDatabaseReference().removeEventListener(mCategoryValueEventListener);
        }
        if (mHotelValueEventListener != null) {
            MyApplication.get(this).hotelDatabaseReference().removeEventListener(mHotelValueEventListener);
        }
    }
}