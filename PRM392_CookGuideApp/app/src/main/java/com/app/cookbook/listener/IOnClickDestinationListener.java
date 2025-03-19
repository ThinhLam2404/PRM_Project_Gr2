package com.app.cookbook.listener;

import com.app.cookbook.model.Destination;
import com.app.cookbook.model.Location;

public interface IOnClickDestinationListener {
    void onClickItemDestination(Destination destination);

    //    void onClickFavoriteDestination(Destination destination, boolean favorite);
    void onClickLocationOfDestination(Location location);
}
