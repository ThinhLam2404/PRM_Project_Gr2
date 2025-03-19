package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemDestinationBinding;
import com.app.cookbook.listener.IOnClickDestinationListener;
import com.app.cookbook.model.Destination;
import com.app.cookbook.model.Location;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder> {

    private final List<Destination> listDestination;
    private final IOnClickDestinationListener mListener;

    public DestinationAdapter(List<Destination> listDestination, IOnClickDestinationListener mListener) {
        this.listDestination = listDestination;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDestinationBinding binding = ItemDestinationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DestinationAdapter.DestinationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        Destination destination = listDestination.get(position);
        if (destination == null) return;

        GlideUtils.loadUrl(destination.getImage(), holder.mBinding.imgFood);
        holder.mBinding.tvName.setText(destination.getName());
        holder.mBinding.tvCategory.setText(destination.getLocationName());
//        boolean isFavorite = GlobalFunction.isFavoriteFood(destination);
        holder.mBinding.tvCountHistory.setText(String.valueOf(destination.getCount()));

        holder.mBinding.layoutImage.setOnClickListener(v -> mListener.onClickItemDestination(destination));
        holder.mBinding.layoutInfo.setOnClickListener(v -> mListener.onClickItemDestination(destination));
        holder.mBinding.tvCategory.setOnClickListener(v -> mListener.onClickLocationOfDestination(
                new Location(destination.getLocationId(), destination.getLocationName())));
    }

    @Override
    public int getItemCount() {
        return null == listDestination ? 0 : listDestination.size();
    }

    public static class DestinationViewHolder extends RecyclerView.ViewHolder {
        private final ItemDestinationBinding mBinding;

        public DestinationViewHolder(ItemDestinationBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
