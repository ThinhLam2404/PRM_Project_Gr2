package com.app.cookbook.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.admin.AdminAddCategoryActivity;
import com.app.cookbook.activity.admin.AdminFoodOfCategoryActivity;
import com.app.cookbook.adapter.admin.AdminCategoryAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.FragmentAdminCategoryBinding;
import com.app.cookbook.listener.IOnAdminManagerCategoryListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryFragment extends Fragment {

    private FragmentAdminCategoryBinding binding;
    private List<Location> mListLocation;
    private AdminCategoryAdapter mAdminCategoryAdapter;
    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminCategoryBinding.inflate(inflater, container, false);

        initView();
        initListener();
        loadListCategory("");

        return binding.getRoot();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvCategory.setLayoutManager(linearLayoutManager);
        mListLocation = new ArrayList<>();
        mAdminCategoryAdapter = new AdminCategoryAdapter(mListLocation, new IOnAdminManagerCategoryListener() {
            @Override
            public void onClickUpdateCategory(Location location) {
                onClickEditCategory(location);
            }

            @Override
            public void onClickDeleteCategory(Location location) {
                deleteCategoryItem(location);
            }

            @Override
            public void onClickDetailCategory(Location location) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.OBJECT_CATEGORY, location);
                GlobalFunction.startActivity(getActivity(), AdminFoodOfCategoryActivity.class, bundle);
            }
        });
        binding.rcvCategory.setAdapter(mAdminCategoryAdapter);
        binding.rcvCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.btnAddCategory.hide();
                } else {
                    binding.btnAddCategory.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        binding.btnAddCategory.setOnClickListener(v -> onClickAddCategory());

        binding.imgSearch.setOnClickListener(view1 -> searchCategory());

        binding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCategory();
                return true;
            }
            return false;
        });

        binding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    searchCategory();
                }
            }
        });
    }

    private void onClickAddCategory() {
        GlobalFunction.startActivity(getActivity(), AdminAddCategoryActivity.class);
    }

    private void onClickEditCategory(Location location) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_CATEGORY, location);
        GlobalFunction.startActivity(getActivity(), AdminAddCategoryActivity.class, bundle);
    }

    private void deleteCategoryItem(Location location) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).locationDatabaseReference()
                            .child(String.valueOf(location.getId())).removeValue((error, ref) ->
                                    Toast.makeText(getActivity(),
                                            getString(R.string.msg_delete_category_successfully),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchCategory() {
        String strKey = binding.edtSearchName.getText().toString().trim();
        resetListCategory();
        if (getActivity() != null) {
            MyApplication.get(getActivity()).locationDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
        loadListCategory(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void resetListCategory() {
        if (mListLocation != null) {
            mListLocation.clear();
        } else {
            mListLocation = new ArrayList<>();
        }
    }

    public void loadListCategory(String keyword) {
        if (getActivity() == null) return;
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Location location = dataSnapshot.getValue(Location.class);
                if (location == null || mListLocation == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListLocation.add(0, location);
                } else {
                    if (GlobalFunction.getTextSearch(location.getName()).toLowerCase().trim()
                            .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                        mListLocation.add(0, location);
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Location location = dataSnapshot.getValue(Location.class);
                if (location == null || mListLocation == null || mListLocation.isEmpty()) return;
                for (int i = 0; i < mListLocation.size(); i++) {
                    if (location.getId() == mListLocation.get(i).getId()) {
                        mListLocation.set(i, location);
                        break;
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Location location = dataSnapshot.getValue(Location.class);
                if (location == null || mListLocation == null || mListLocation.isEmpty()) return;
                for (Location locationObject : mListLocation) {
                    if (location.getId() == locationObject.getId()) {
                        mListLocation.remove(locationObject);
                        break;
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(getActivity()).locationDatabaseReference().addChildEventListener(mChildEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).locationDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
    }
}
