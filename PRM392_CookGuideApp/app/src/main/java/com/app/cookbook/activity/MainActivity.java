package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.CategoryHomeAdapter;
import com.app.cookbook.adapter.CategoryMenuAdapter;
import com.app.cookbook.adapter.FoodAdapter;
import com.app.cookbook.adapter.FoodFeaturedAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityMainBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;
import com.app.cookbook.model.Destination;
import com.app.cookbook.model.RequestFood;
import com.app.cookbook.model.User;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
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

    private ActivityMainBinding mBinding;
    private CategoryMenuAdapter mCategoryMenuAdapter;
    private List<Location> mListLocation;
    private List<Location> mListLocationHome;
    private List<Destination> mListDestination;
    private List<Destination> mListDestinationFeatured;


    private List<Hotel> mListHotel;


    private ValueEventListener mCategoryValueEventListener;
    private ValueEventListener mFoodValueEventListener;
    private final Handler mHandlerBanner = new Handler(Looper.getMainLooper());
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListDestinationFeatured == null || mListDestinationFeatured.isEmpty()) return;
            if (mBinding.viewPager.getCurrentItem() == mListDestinationFeatured.size() - 1) {
                mBinding.viewPager.setCurrentItem(0);
                return;
            }
            mBinding.viewPager.setCurrentItem(mBinding.viewPager.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initListener();
        initNavigationMenuLeft();


        // Khai báo Switch
        Switch switchLanguage = mBinding.switchLanguage;
// Thiết lập OnCheckedChangeListener
        if (switchLanguage != null) {
            switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String languageCodes = isChecked ? "vi" : "en";
                saveLanguagePreference(languageCodes);
                changeLanguage(languageCodes);

                // Change the icon based on the switch state
                int drawableId = isChecked ? R.drawable.ic_language_vn : R.drawable.ic_language_en;
                Drawable drawable = getResources().getDrawable(drawableId);
                int width = (int) (drawable.getIntrinsicWidth() * 0.3); // Scale to 50%
                int height = (int) (drawable.getIntrinsicHeight() * 0.3); // Scale to 50%
                drawable.setBounds(0, 0, width, height);
                switchLanguage.setCompoundDrawables(drawable, null, null, null);
            });
            // Set default to the saved language
            SharedPreferences prefss = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String savedLanguage = prefss.getString("language", "vi"); // Default to Vietnamese
            switchLanguage.setChecked(savedLanguage.equals("vi"));
            // Set the initial icon based on the saved language
            int initialDrawableId = savedLanguage.equals("vi") ? R.drawable.ic_language_vn : R.drawable.ic_language_en;
            Drawable initialDrawable = getResources().getDrawable(initialDrawableId);
            int initialWidth = (int) (initialDrawable.getIntrinsicWidth() * 0.3); // Scale to 50%
            int initialHeight = (int) (initialDrawable.getIntrinsicHeight() * 0.3); // Scale to 50%
            initialDrawable.setBounds(0, 0, initialWidth, initialHeight);
            switchLanguage.setCompoundDrawables(initialDrawable, null, null, null);
        } else {
            Log.e("MainActivity", "Switch is null");
        }
    }
//Language
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
            recreate(); // Reload the activity to apply the new language
        }
    }
    //end language

    private void initToolbar() {
        mBinding.header.imgToolbar.setImageResource(R.drawable.ic_menu);
        mBinding.header.tvToolbarTitle.setText(getString(R.string.app_name));
    }

    private void initListener() {
        mBinding.header.imgToolbar.setOnClickListener(this);
//        mBinding.tvRequestFood.setOnClickListener(this);
//        mBinding.layoutFeedback.setOnClickListener(this);
//        mBinding.layoutContact.setOnClickListener(this);
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

//            case R.id.tv_request_food:
//                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
//                showDialogRequestFood();
//                break;

//            case R.id.layout_feedback:
//                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
//                GlobalFunction.startActivity(this, FeedbackActivity.class);
//                break;
//
//            case R.id.layout_contact:
//                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
//                GlobalFunction.startActivity(this, ContactActivity.class);
//                break;

            case R.id.layout_change_password:
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                GlobalFunction.startActivity(this, ChangePasswordActivity.class);
                break;

            case R.id.layout_sign_out:
                onClickSignOut();
                break;

            case R.id.view_all_category:
                GlobalFunction.startActivity(this, ListCategoryActivity.class);
                break;

            case R.id.view_all_food:
                GlobalFunction.startActivity(this, ListFoodActivity.class);
                break;

            case R.id.layout_search:
                GlobalFunction.startActivity(this, SearchActivity.class);
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        mBinding.rcvCategory.setLayoutManager(linearLayoutManager);
//        mListCategory = new ArrayList<>();
//        mCategoryMenuAdapter = new CategoryMenuAdapter(mListCategory,
//                category -> GlobalFunction.goToFoodByCategory(MainActivity.this, category));
//        mBinding.rcvCategory.setAdapter(mCategoryMenuAdapter);

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
                if (mCategoryMenuAdapter != null) mCategoryMenuAdapter.notifyDataSetChanged();
                displayListLocationHome();

                loadListHotelFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(true);
            }
        };
        MyApplication.get(this).locationDatabaseReference().addValueEventListener(mCategoryValueEventListener);
    }

    private void displayListLocationHome() {
        LinearLayoutManager layoutManagerHorizontal = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mBinding.rcvCategoryHome.setLayoutManager(layoutManagerHorizontal);
        CategoryHomeAdapter categoryHomeAdapter = new CategoryHomeAdapter(loadListLocationHome(),
                category -> GlobalFunction.goToFoodByCategory(MainActivity.this, category));
        mBinding.rcvCategoryHome.setAdapter(categoryHomeAdapter);
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

    private void resetListFood() {
        if (mListDestination == null) {
            mListDestination = new ArrayList<>();
        } else {
            mListDestination.clear();
        }
    }

    private void resetListFoodFeatured() {
        if (mListDestinationFeatured == null) {
            mListDestinationFeatured = new ArrayList<>();
        } else {
            mListDestinationFeatured.clear();
        }
    }

    private void loadListHotelFromFirebase() {
        mFoodValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                resetListFood();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Destination destination = dataSnapshot.getValue(Destination.class);
                    if (destination == null) return;
                    mListDestination.add(0, destination);
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
        MyApplication.get(this).hotelDatabaseReference().addValueEventListener(mFoodValueEventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayCountHotelOfLocation() {
        if (mListLocation == null || mListLocation.isEmpty()) return;
        for (Location location : mListLocation) {
            location.setCount(loadCountDestinationOfLocation(location.getId()));
        }
        if (mCategoryMenuAdapter != null) mCategoryMenuAdapter.notifyDataSetChanged();
    }

    private int loadCountDestinationOfLocation(long locationId) {
        if (mListDestination == null || mListDestination.isEmpty()) return 0;
        List<Destination> listDestinations = new ArrayList<>();
        for (Destination destination : mListDestination) {
            if (locationId == destination.getLocationId()) {
                listDestinations.add(destination);
            }
        }
        return listDestinations.size();
    }

    private void displayCountFavorite() {
        int countFavorite = 0;
        if (mListDestination != null && !mListDestination.isEmpty()) {
            List<Destination> listFavorite = new ArrayList<>();
            for (Destination destination : mListDestination) {
                if (GlobalFunction.isFavoriteFood(destination)) {
                    listFavorite.add(destination);
                }
            }
            countFavorite = listFavorite.size();
        }
        mBinding.tvCountFavorite.setText(String.valueOf(countFavorite));
    }

    private void displayCountHistory() {
        int countHistory = 0;
        if (mListDestination != null && !mListDestination.isEmpty()) {
            List<Destination> listHistory = new ArrayList<>();
            for (Destination destination : mListDestination) {
                if (GlobalFunction.isHistoryFood(destination)) {
                    listHistory.add(destination);
                }
            }
            countHistory = listHistory.size();
        }
        mBinding.tvCountHistory.setText(String.valueOf(countHistory));
    }

    private void displayListHotelFeatured() {
        FoodFeaturedAdapter foodFeaturedAdapter = new FoodFeaturedAdapter(loadListFoodFeatured(), new IOnClickFoodListener() {

            @Override
            public void onClickItemFood(Destination food) {
                GlobalFunction.goToFoodDetail(MainActivity.this, food.getId());
            }

            @Override
            public void onClickFavoriteFood(Destination food, boolean favorite) {}

            @Override
            public void onClickCategoryOfFood(Location category) {}
        });
        mBinding.viewPager.setAdapter(foodFeaturedAdapter);
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

    private List<Destination> loadListFoodFeatured() {
        resetListFoodFeatured();
        for (Destination destination : mListDestination) {
            if (destination.isFeatured() && mListDestinationFeatured.size() < Constant.MAX_SIZE_LIST_FEATURED) {
                mListDestinationFeatured.add(destination);
            }
        }
        return mListDestinationFeatured;
    }

    private void displayListPopularHotel() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvFoodPopular.setLayoutManager(linearLayoutManager);

        FoodAdapter foodAdapter = new FoodAdapter(loadListPopularFood(), new IOnClickFoodListener() {
            @Override
            public void onClickItemFood(Destination food) {
                GlobalFunction.goToFoodDetail(MainActivity.this, food.getId());
            }

            @Override
            public void onClickFavoriteFood(Destination food, boolean favorite) {
                    GlobalFunction.onClickFavoriteFood(MainActivity.this, food, favorite);
            }

            @Override
            public void onClickCategoryOfFood(Location category) {
                GlobalFunction.goToFoodByCategory(MainActivity.this, category);
            }
        });
        mBinding.rcvFoodPopular.setAdapter(foodAdapter);
    }

    private List<Destination> loadListPopularFood() {
        List<Destination> list = new ArrayList<>();
        List<Destination> allDestinations = new ArrayList<>(mListDestination);
        Collections.sort(allDestinations, (food1, food2) -> food2.getCount() - food1.getCount());
        for (Destination destination : allDestinations) {
            if (list.size() < Constant.MAX_SIZE_LIST_POPULAR) {
                list.add(destination);
            }
        }
        return list;
    }

    private void showDialogRequestFood() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_request_food);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final ImageView imgClose = dialog.findViewById(R.id.img_close);
        final EditText edtFoodName = dialog.findViewById(R.id.edt_food_name);
        final TextView tvSendRequest = dialog.findViewById(R.id.tv_send_request);

        imgClose.setOnClickListener(v -> dialog.dismiss());

        tvSendRequest.setOnClickListener(v -> {
            String strFoodName = edtFoodName.getText().toString().trim();
            if (StringUtil.isEmpty(strFoodName)) {
                GlobalFunction.showToastMessage(this,
                        getString(R.string.msg_name_food_request));
            } else {
                showProgressDialog(true);
                long requestId = System.currentTimeMillis();
                RequestFood requestFood = new RequestFood(requestId, strFoodName);
                MyApplication.get(this).requestFoodDatabaseReference()
                        .child(String.valueOf(System.currentTimeMillis()))
                        .setValue(requestFood, (databaseError, databaseReference) -> {
                            showProgressDialog(false);
                            GlobalFunction.hideSoftKeyboard(this);
                            GlobalFunction.showToastMessage(this,
                                    getString(R.string.msg_send_request_food_success));
                            dialog.dismiss();
                        });
            }
        });
        dialog.show();
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
        if (mFoodValueEventListener != null) {
            MyApplication.get(this).destinationDatabaseReference().removeEventListener(mFoodValueEventListener);
        }
    }
}
