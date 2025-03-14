package com.app.cookbook.adapter.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemAdminFoodBinding;
import com.app.cookbook.listener.IOnAdminManagerFoodListener;
import com.app.cookbook.model.Destination;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.AdminFoodViewHolder> {

    private final List<Destination> mListDestinations;
    public final IOnAdminManagerFoodListener mListener;

    public AdminFoodAdapter(List<Destination> list, IOnAdminManagerFoodListener listener) {
        this.mListDestinations = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminFoodBinding binding = ItemAdminFoodBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdminFoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFoodViewHolder holder, int position) {
        Destination destination = mListDestinations.get(position);
        if (destination == null) return;
        GlideUtils.loadUrl(destination.getImage(), holder.mBinding.imgFood);
        holder.mBinding.tvName.setText(destination.getName());
        holder.mBinding.tvCategory.setText(destination.getLocationName());
        if (destination.isFeatured()) {
            holder.mBinding.tvFeatured.setText("Có");
        } else {
            holder.mBinding.tvFeatured.setText("Không");
        }

        holder.mBinding.imgEdit.setOnClickListener(v -> mListener.onClickUpdateFood(destination));
        holder.mBinding.imgDelete.setOnClickListener(v -> mListener.onClickDeleteFood(destination));
        holder.mBinding.layoutItem.setOnClickListener(v -> mListener.onClickDetailFood(destination));
    }

    @Override
    public int getItemCount() {
        return null == mListDestinations ? 0 : mListDestinations.size();
    }

    public static class AdminFoodViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminFoodBinding mBinding;

        public AdminFoodViewHolder(ItemAdminFoodBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
