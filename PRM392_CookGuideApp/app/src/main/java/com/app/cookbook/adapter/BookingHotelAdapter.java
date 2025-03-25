package com.app.cookbook.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemBookingBinding;
import com.app.cookbook.listener.IOnClickBookingHotelListener;
import com.app.cookbook.model.BookingHotelModel;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingHotelAdapter extends RecyclerView.Adapter<BookingHotelAdapter.BookingHotelViewHolder> {

    private final List<BookingHotelModel> listBookingHotel;
    private final IOnClickBookingHotelListener mListener;

    public BookingHotelAdapter(List<BookingHotelModel> listBookingHotel, IOnClickBookingHotelListener mListener) {
        this.listBookingHotel = listBookingHotel;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public BookingHotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingBinding binding = ItemBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingHotelAdapter.BookingHotelViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHotelViewHolder holder, int position) {
        BookingHotelModel bookingHotel = listBookingHotel.get(position);
        if (bookingHotel == null) return;

        long hotelId = bookingHotel.getHotelId();

        getHotelInfo(hotelId, holder.mBinding.imgFood, holder.mBinding.hotelName);
        holder.mBinding.guest.setText(bookingHotel.getNumberOfGuest());
        holder.mBinding.price.setText("Chi phí: " + bookingHotel.getPrice());

        Date startDate = new Date(bookingHotel.getStartDate());
        Date endDate = new Date(bookingHotel.getEndDate());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedStartDate = sdf.format(startDate);
        String formattedEndDate = sdf.format(endDate);


        holder.mBinding.date.setText(formattedStartDate + " to " + formattedEndDate);
        holder.mBinding.layoutInfo.setOnClickListener(v -> mListener.onClickItemBooking(bookingHotel));
    }

    private void getHotelInfo(long hotelId, ImageView imageView, TextView textView) {

        DatabaseReference hotelRef = FirebaseDatabase.getInstance().getReference("hotels").child(String.valueOf(hotelId));

        hotelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String hotelName = snapshot.child("name").getValue(String.class);

                    // Gán ảnh khách sạn vào ImageView
                    textView.setText(hotelName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu khách sạn: " + error.getMessage());
            }
        });

        hotelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String hotelImage = snapshot.child("image").getValue(String.class);

                    // Gán ảnh khách sạn vào ImageView
                    Glide.with(imageView.getContext()).load(hotelImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu khách sạn: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == listBookingHotel ? 0 : listBookingHotel.size();
    }

    public static class BookingHotelViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingBinding mBinding;

        public BookingHotelViewHolder(ItemBookingBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
