package com.app.cookbook.listener;

import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;

public interface IOnClickHotelListener {
    void onClickItemHotel(Hotel hotel);

    void onClickFavoriteHotel(Hotel hotel, boolean favorite);

    void onClickLocationOfHotel(Location location);
}
