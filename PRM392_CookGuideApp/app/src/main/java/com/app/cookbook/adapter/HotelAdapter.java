package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.R;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ItemHotelBinding;
import com.app.cookbook.listener.IOnClickHotelListener;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private final List<Hotel> listHotel;
    private final IOnClickHotelListener mListener;

    public HotelAdapter(List<Hotel> listHotel, IOnClickHotelListener mListener) {
        this.listHotel = listHotel;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHotelBinding binding = ItemHotelBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HotelAdapter.HotelViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = listHotel.get(position);
        if (hotel == null) return;

        GlideUtils.loadUrl(hotel.getImage(), holder.mBinding.imgFood);
        holder.mBinding.tvName.setText(hotel.getName());
        holder.mBinding.tvCategory.setText(hotel.getLocationName());
        boolean isFavorite = GlobalFunction.isFavoriteHotel(hotel);
        holder.mBinding.tvCountHistory.setText(String.valueOf(hotel.getCount()));
        holder.mBinding.tvCountFavorite.setText(hotel.countFavorites());

        if (isFavorite) {
            holder.mBinding.imgFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.mBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite);
        }
        holder.mBinding.imgFavorite.setOnClickListener(v -> mListener.onClickFavoriteHotel(hotel, !isFavorite));
//        holder.mBinding.layoutImage.setOnClickListener(v -> mListener.onClickItemHotel(hotel));
//        holder.mBinding.layoutInfo.setOnClickListener(v -> mListener.onClickItemHotel(hotel));

        holder.mBinding.tvCategory.setOnClickListener(v -> mListener.onClickLocationOfHotel(
                new Location(hotel.getLocationId(), hotel.getLocationName())));
    }

    @Override
    public int getItemCount() {
        return null == listHotel ? 0 : listHotel.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        private final ItemHotelBinding mBinding;

        public HotelViewHolder(ItemHotelBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
