package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.DestinationAdapter;
import com.app.cookbook.adapter.HotelAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivitySearchBinding;
import com.app.cookbook.listener.IOnClickDestinationListener;
import com.app.cookbook.listener.IOnClickHotelListener;
import com.app.cookbook.model.Destination;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {
    private ActivitySearchBinding mBinding;
    private List<Object> mListData;
    private RecyclerView.Adapter mAdapter;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);
        mBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initToolbar();
        initUi();
        initListener();
        String searchType = getIntent().getStringExtra("search");
        loadDataFromFirebase(searchType, "");
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_search));
    }

    private void initUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvSearchResult.setLayoutManager(linearLayoutManager);
        mListData = new ArrayList<>();
    }

    private void initListener() {
        mBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.isEmpty()) {
                    loadDataFromFirebase(getIntent().getStringExtra("search"), "");
                }
            }
        });

        mBinding.imgSearch.setOnClickListener(view -> searchData());
        mBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchData();
                return true;
            }
            return false;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDataFromFirebase(String searchType, String key) {
        if (mValueEventListener != null) {
            getDatabaseReference(searchType).removeEventListener(mValueEventListener);
        }
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListData();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (searchType.equals("hotel")) {
                        Hotel hotel = dataSnapshot.getValue(Hotel.class);
                        if (hotel != null && (StringUtil.isEmpty(key) || matchesSearch(hotel.getName(), key))) {
                            mListData.add(hotel);
                        }
                    } else if (searchType.equals("destination")) {
                        Destination destination = dataSnapshot.getValue(Destination.class);
                        if (destination != null && (StringUtil.isEmpty(key) || matchesSearch(destination.getName(), key))) {
                            mListData.add(destination);
                        }
                    }
                }
                updateAdapter(searchType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(SearchActivity.this,
                        getString(R.string.msg_get_date_error));
            }
        };
        getDatabaseReference(searchType).addValueEventListener(mValueEventListener);
    }

    private DatabaseReference getDatabaseReference(String searchType) {
        if (searchType.equals("hotel")) {
            return MyApplication.get(this).hotelDatabaseReference();
        } else {
            return MyApplication.get(this).destinationDatabaseReference();
        }
    }

    private boolean matchesSearch(String name, String key) {
        return GlobalFunction.getTextSearch(name).toLowerCase().trim()
                .contains(GlobalFunction.getTextSearch(key).toLowerCase().trim());
    }

    private void updateAdapter(String searchType) {
        if (searchType.equals("hotel")) {
            mAdapter = new HotelAdapter((List<Hotel>) (List<?>) mListData, new IOnClickHotelListener() {
                @Override
                public void onClickItemHotel(Hotel hotel) {
                }

                @Override
                public void onClickFavoriteHotel(Hotel hotel, boolean favorite) {
                    GlobalFunction.onClickFavoriteHotel(SearchActivity.this, hotel, favorite);
                }

                @Override
                public void onClickLocationOfHotel(Location location) {
                    GlobalFunction.goToDestinationByLocation(SearchActivity.this, location);
                }
            });
        } else {
            mAdapter = new DestinationAdapter((List<Destination>) (List<?>) mListData, new IOnClickDestinationListener() {
                @Override
                public void onClickItemDestination(Destination destination) {
                    GlobalFunction.goToDestinationDetail(SearchActivity.this, destination.getId());
                }

                @Override
                public void onClickLocationOfDestination(Location location) {
                    GlobalFunction.goToDestinationByLocation(SearchActivity.this, location);
                }
            });
        }
        mBinding.rcvSearchResult.setAdapter(mAdapter);
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }

    private void resetListData() {
        if (mListData == null) {
            mListData = new ArrayList<>();
        } else {
            mListData.clear();
        }
    }

    private void searchData() {
        String strKey = mBinding.edtSearchName.getText().toString().trim();
        loadDataFromFirebase(getIntent().getStringExtra("search"), strKey);
        GlobalFunction.hideSoftKeyboard(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            getDatabaseReference(getIntent().getStringExtra("search")).removeEventListener(mValueEventListener);
        }
    }
}
