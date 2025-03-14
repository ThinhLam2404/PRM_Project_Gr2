package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemCategoryMenuBinding;
import com.app.cookbook.listener.IOnClickCategoryListener;
import com.app.cookbook.model.Location;

import java.util.List;

public class CategoryMenuAdapter extends RecyclerView.Adapter<CategoryMenuAdapter.CategoryViewHolder> {

    private final List<Location> listLocation;
    private final IOnClickCategoryListener mListener;

    public CategoryMenuAdapter(List<Location> listLocation, IOnClickCategoryListener listener) {
        this.listLocation = listLocation;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryMenuBinding binding = ItemCategoryMenuBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryMenuAdapter.CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Location location = listLocation.get(position);
        if (location == null) return;
        holder.mBinding.tvTitle.setText(location.getName());
        String strCount = location.getCount() + " món";
        holder.mBinding.tvCount.setText(strCount);
        holder.mBinding.layoutItem.setOnClickListener(v -> mListener.onClickItemCategory(location));
    }

    @Override
    public int getItemCount() {
        return null == listLocation ? 0 : listLocation.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryMenuBinding mBinding;

        public CategoryViewHolder(ItemCategoryMenuBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
