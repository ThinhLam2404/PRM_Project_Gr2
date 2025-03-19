package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemLocationHomeBinding;
import com.app.cookbook.listener.IOnClickLocationListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class LocationHomeAdapter extends RecyclerView.Adapter<LocationHomeAdapter.LocationHomeViewHolder> {

    private final List<Location> listLocation;
    private final IOnClickLocationListener mListener;

    public LocationHomeAdapter(List<Location> listLocation, IOnClickLocationListener listener) {
        this.listLocation = listLocation;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public LocationHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLocationHomeBinding binding = ItemLocationHomeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new LocationHomeAdapter.LocationHomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHomeViewHolder holder, int position) {
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

    public static class LocationHomeViewHolder extends RecyclerView.ViewHolder {
        private final ItemLocationHomeBinding mBinding;

        public LocationHomeViewHolder(ItemLocationHomeBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
