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
import com.app.cookbook.activity.admin.AdminAddFoodActivity;
import com.app.cookbook.activity.admin.AdminFoodDetailActivity;
import com.app.cookbook.adapter.admin.AdminFoodAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.FragmentAdminFoodBinding;
import com.app.cookbook.listener.IOnAdminManagerFoodListener;
import com.app.cookbook.model.Destination;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminFoodFragment extends Fragment {

    private FragmentAdminFoodBinding binding;
    private List<Destination> mListDestination;
    private AdminFoodAdapter mAdminFoodAdapter;
    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminFoodBinding.inflate(inflater, container, false);

        initView();
        initListener();
        loadListFood("");

        return binding.getRoot();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvFood.setLayoutManager(linearLayoutManager);
        mListDestination = new ArrayList<>();
        mAdminFoodAdapter = new AdminFoodAdapter(mListDestination, new IOnAdminManagerFoodListener() {
            @Override
            public void onClickUpdateFood(Destination destination) {
                onClickEditFood(destination);
            }

            @Override
            public void onClickDeleteFood(Destination destination) {
                deleteFoodItem(destination);
            }

            @Override
            public void onClickDetailFood(Destination destination) {
                goToFoodDetail(destination);
            }
        });
        binding.rcvFood.setAdapter(mAdminFoodAdapter);
        binding.rcvFood.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.btnAddFood.hide();
                } else {
                    binding.btnAddFood.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        binding.btnAddFood.setOnClickListener(v -> onClickAddFood());

        binding.imgSearch.setOnClickListener(view1 -> searchFood());

        binding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood();
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
                    searchFood();
                }
            }
        });
    }

    private void goToFoodDetail(@NonNull Destination destination) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_FOOD, destination);
        GlobalFunction.startActivity(getActivity(), AdminFoodDetailActivity.class, bundle);
    }

    private void onClickAddFood() {
        GlobalFunction.startActivity(getActivity(), AdminAddFoodActivity.class);
    }

    private void onClickEditFood(Destination destination) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_FOOD, destination);
        GlobalFunction.startActivity(getActivity(), AdminAddFoodActivity.class, bundle);
    }

    private void deleteFoodItem(Destination destination) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) return;
                    MyApplication.get(getActivity()).foodDatabaseReference()
                            .child(String.valueOf(destination.getId())).removeValue((error, ref) ->
                                    Toast.makeText(getActivity(),
                                            getString(R.string.msg_delete_food_successfully),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchFood() {
        String strKey = binding.edtSearchName.getText().toString().trim();
        resetListFood();
        if (getActivity() != null) {
            MyApplication.get(getActivity()).foodDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
        loadListFood(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void resetListFood() {
        if (mListDestination != null) {
            mListDestination.clear();
        } else {
            mListDestination = new ArrayList<>();
        }
    }

    public void loadListFood(String keyword) {
        if (getActivity() == null) return;
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Destination destination = dataSnapshot.getValue(Destination.class);
                if (destination == null || mListDestination == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListDestination.add(0, destination);
                } else {
                    if (GlobalFunction.getTextSearch(destination.getName()).toLowerCase().trim()
                            .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                        mListDestination.add(0, destination);
                    }
                }
                if (mAdminFoodAdapter != null) mAdminFoodAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Destination destination = dataSnapshot.getValue(Destination.class);
                if (destination == null || mListDestination == null || mListDestination.isEmpty()) return;
                for (int i = 0; i < mListDestination.size(); i++) {
                    if (destination.getId() == mListDestination.get(i).getId()) {
                        mListDestination.set(i, destination);
                        break;
                    }
                }
                if (mAdminFoodAdapter != null) mAdminFoodAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Destination destination = dataSnapshot.getValue(Destination.class);
                if (destination == null || mListDestination == null || mListDestination.isEmpty()) return;
                for (Destination destinationObject : mListDestination) {
                    if (destination.getId() == destinationObject.getId()) {
                        mListDestination.remove(destinationObject);
                        break;
                    }
                }
                if (mAdminFoodAdapter != null) mAdminFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(getActivity()).foodDatabaseReference()
                .addChildEventListener(mChildEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).foodDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
    }
}
