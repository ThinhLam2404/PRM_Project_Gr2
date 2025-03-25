package com.app.cookbook.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemTripBinding;
import com.app.cookbook.listener.IOnClickTripListener;
import com.app.cookbook.model.Trip;
import com.app.cookbook.utils.GlideUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private final List<Trip> listHotel;
    private final IOnClickTripListener mListener;

    public TripAdapter(List<Trip> listHotel, IOnClickTripListener mListener) {
        this.listHotel = listHotel;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripBinding binding = ItemTripBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TripAdapter.TripViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = listHotel.get(position);
        if (trip == null) return;

        getHotelImage(trip.getDestination().split(",")[0], holder.mBinding.imgFood);

        holder.mBinding.tripName.setText("Trip name: " + trip.getTripName());
        holder.mBinding.destination.setText(trip.getDestination());
        Date date = new Date(trip.getTripDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(date);
        holder.mBinding.date.setText("date: " + formattedDate);
        holder.mBinding.guest.setText(String.valueOf(trip.getNumber()));


        holder.mBinding.layoutInfo.setOnClickListener(v -> mListener.onClickItemTrip(trip));


    }

    private void getHotelImage(String hotelName, ImageView imageView) {
        DatabaseReference hotelRef = FirebaseDatabase.getInstance().getReference("destinations");
        hotelRef.orderByChild("name").equalTo(hotelName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                        String imageUrl = hotelSnapshot.child("image").getValue(String.class);
                        if (imageUrl != null) {
                            GlideUtils.loadUrl(imageUrl, imageView);
                        }
                    }
                } else {
                    Log.e("Firebase", "Không tìm thấy khách sạn: " + hotelName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi truy vấn: " + error.getMessage());
            }
        });
    }


    @Override
    public int getItemCount() {
        return null == listHotel ? 0 : listHotel.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        private final ItemTripBinding mBinding;

        public TripViewHolder(ItemTripBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
