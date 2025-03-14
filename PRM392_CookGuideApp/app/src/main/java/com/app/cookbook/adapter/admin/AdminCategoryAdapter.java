package com.app.cookbook.adapter.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemAdminCategoryBinding;
import com.app.cookbook.listener.IOnAdminManagerCategoryListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.AdminCategoryViewHolder> {

    private final List<Location> mListLocation;
    private final IOnAdminManagerCategoryListener mListener;

    public AdminCategoryAdapter(List<Location> list, IOnAdminManagerCategoryListener listener) {
        this.mListLocation = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminCategoryBinding binding = ItemAdminCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AdminCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryViewHolder holder, int position) {
        Location location = mListLocation.get(position);
        if (location == null) return;
        GlideUtils.loadUrl(location.getImage(), holder.itemBinding.imgCategory);
        holder.itemBinding.tvName.setText(location.getName());
        holder.itemBinding.imgEdit.setOnClickListener(v -> mListener.onClickUpdateCategory(location));
        holder.itemBinding.imgDelete.setOnClickListener(v -> mListener.onClickDeleteCategory(location));
        holder.itemBinding.layoutItem.setOnClickListener(v -> mListener.onClickDetailCategory(location));
    }

    @Override
    public int getItemCount() {
        if (mListLocation != null) {
            return mListLocation.size();
        }
        return 0;
    }

    public static class AdminCategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminCategoryBinding itemBinding;

        public AdminCategoryViewHolder(@NonNull ItemAdminCategoryBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
    }
}
