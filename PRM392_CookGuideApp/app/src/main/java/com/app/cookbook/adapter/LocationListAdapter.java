package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemLocationListBinding;
import com.app.cookbook.listener.IOnClickLocationListener;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder> {

    private final List<Location> listLocation;
    private final IOnClickLocationListener mListener;

    public LocationListAdapter(List<Location> listLocation, IOnClickLocationListener listener) {
        this.listLocation = listLocation;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public LocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLocationListBinding binding = ItemLocationListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new LocationListAdapter.LocationListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationListViewHolder holder, int position) {
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

    public static class LocationListViewHolder extends RecyclerView.ViewHolder {
        private final ItemLocationListBinding mBinding;

        public LocationListViewHolder(ItemLocationListBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
