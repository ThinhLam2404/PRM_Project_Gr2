package com.app.cookbook.listener;

import com.app.cookbook.model.Destination;

public interface IOnAdminManagerFoodListener {
    void onClickUpdateFood(Destination destination);
    void onClickDeleteFood(Destination destination);
    void onClickDetailFood(Destination destination);
}
