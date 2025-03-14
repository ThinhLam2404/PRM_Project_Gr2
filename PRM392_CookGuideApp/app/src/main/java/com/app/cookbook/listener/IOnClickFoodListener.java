package com.app.cookbook.listener;

import com.app.cookbook.model.Location;
import com.app.cookbook.model.Destination;

public interface IOnClickFoodListener {
    void onClickItemFood(Destination destination);
    void onClickFavoriteFood(Destination destination, boolean favorite);
    void onClickCategoryOfFood(Location location);
}
