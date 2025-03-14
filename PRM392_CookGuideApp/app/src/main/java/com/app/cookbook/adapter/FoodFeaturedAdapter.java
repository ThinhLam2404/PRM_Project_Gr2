package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemFoodFeaturedBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Destination;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class FoodFeaturedAdapter extends RecyclerView.Adapter<FoodFeaturedAdapter.FoodFeaturedViewHolder> {

    private final List<Destination> mListDestination;
    public final IOnClickFoodListener mListener;

    public FoodFeaturedAdapter(List<Destination> list, IOnClickFoodListener listener) {
        this.mListDestination = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FoodFeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodFeaturedBinding binding = ItemFoodFeaturedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodFeaturedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodFeaturedViewHolder holder, int position) {
        Destination destination = mListDestination.get(position);
        if (destination == null) return;
        GlideUtils.loadUrlBanner(destination.getImage(), holder.mBinding.imgFood);
        holder.mBinding.layoutItem.setOnClickListener(v -> mListener.onClickItemFood(destination));
    }

    @Override
    public int getItemCount() {
        if (mListDestination != null) {
            return mListDestination.size();
        }
        return 0;
    }

    public static class FoodFeaturedViewHolder extends RecyclerView.ViewHolder {

        private final ItemFoodFeaturedBinding mBinding;

        public FoodFeaturedViewHolder(@NonNull ItemFoodFeaturedBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
