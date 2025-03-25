package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemFoodFeaturedBinding;
import com.app.cookbook.listener.IOnClickHotelListener;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class HotelFeaturedAdapter extends RecyclerView.Adapter<HotelFeaturedAdapter.FoodFeaturedViewHolder> {

    public final IOnClickHotelListener mListener;
    private final List<Hotel> mListHotel;

    public HotelFeaturedAdapter(List<Hotel> list, IOnClickHotelListener listener) {
        this.mListHotel = list;
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
        Hotel hotel = mListHotel.get(position);
        if (hotel == null) return;
        GlideUtils.loadUrlBanner(hotel.getImage(), holder.mBinding.imgFood);
        holder.mBinding.layoutItem.setOnClickListener(v -> mListener.onClickItemHotel(hotel));
    }

    @Override
    public int getItemCount() {
        if (mListHotel != null) {
            return mListHotel.size();
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
