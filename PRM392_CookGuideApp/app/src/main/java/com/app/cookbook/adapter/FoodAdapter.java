package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.R;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ItemFoodBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.model.Destination;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<Destination> listDestination;
    private final IOnClickFoodListener mListener;

    public FoodAdapter(List<Destination> listDestination, IOnClickFoodListener mListener) {
        this.listDestination = listDestination;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodAdapter.FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Destination destination = listDestination.get(position);
        if (destination == null) return;

        GlideUtils.loadUrl(destination.getImage(), holder.mBinding.imgFood);
        holder.mBinding.tvName.setText(destination.getName());
        holder.mBinding.tvCategory.setText(destination.getLocationName());
        boolean isFavorite = GlobalFunction.isFavoriteFood(destination);
        holder.mBinding.tvCountHistory.setText(String.valueOf(destination.getCount()));
        holder.mBinding.tvCountFavorite.setText(destination.countFavorites());

        if (isFavorite) {
            holder.mBinding.imgFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.mBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite);
        }

        holder.mBinding.imgFavorite.setOnClickListener(v -> mListener.onClickFavoriteFood(destination, !isFavorite));
        holder.mBinding.layoutImage.setOnClickListener(v -> mListener.onClickItemFood(destination));
        holder.mBinding.layoutInfo.setOnClickListener(v -> mListener.onClickItemFood(destination));
        holder.mBinding.tvCategory.setOnClickListener(v -> mListener.onClickCategoryOfFood(
                new Location(destination.getLocationId(), destination.getLocationName())));
    }

    @Override
    public int getItemCount() {
        return null == listDestination ? 0 : listDestination.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodBinding mBinding;

        public FoodViewHolder(ItemFoodBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
