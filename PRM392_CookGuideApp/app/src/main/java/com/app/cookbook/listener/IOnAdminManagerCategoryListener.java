package com.app.cookbook.listener;

import com.app.cookbook.model.Location;

public interface IOnAdminManagerCategoryListener {
    void onClickUpdateCategory(Location location);
    void onClickDeleteCategory(Location location);
    void onClickDetailCategory(Location location);
}
