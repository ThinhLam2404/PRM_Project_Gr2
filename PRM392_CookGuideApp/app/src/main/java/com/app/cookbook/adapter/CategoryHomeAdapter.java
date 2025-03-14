package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemCategoryHomeBinding;
import com.app.cookbook.listener.IOnClickCategoryListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class CategoryHomeAdapter extends RecyclerView.Adapter<CategoryHomeAdapter.CategoryHomeViewHolder> {

    private final List<Location> listLocation;
    private final IOnClickCategoryListener mListener;

    public CategoryHomeAdapter(List<Location> listLocation, IOnClickCategoryListener listener) {
        this.listLocation = listLocation;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CategoryHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryHomeBinding binding = ItemCategoryHomeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryHomeAdapter.CategoryHomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHomeViewHolder holder, int position) {
        Location location = listLocation.get(position);
        if (location == null) return;
        GlideUtils.loadUrl(location.getImage(), holder.mBinding.imgCategory);
        holder.mBinding.tvName.setText(location.getName());
        holder.mBinding.layoutItem.setOnClickListener(v -> mListener.onClickItemCategory(location));
    }

    @Override
    public int getItemCount() {
        return null == listLocation ? 0 : listLocation.size();
    }

    public static class CategoryHomeViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryHomeBinding mBinding;

        public CategoryHomeViewHolder(ItemCategoryHomeBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
