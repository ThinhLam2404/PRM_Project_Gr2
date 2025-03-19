package com.app.cookbook.constant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.app.cookbook.MyApplication;
import com.app.cookbook.activity.DestinationByLocationActivity;
import com.app.cookbook.activity.DestinationDetailActivity;
import com.app.cookbook.model.Destination;
import com.app.cookbook.model.Hotel;
import com.app.cookbook.model.Location;
import com.app.cookbook.model.UserInfo;
import com.app.cookbook.prefs.DataStoreManager;
import com.google.firebase.auth.FirebaseAuth;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GlobalFunction {

    public static void startActivity(Context context, Class<?> clz) {
        Intent intent = new Intent(context, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(context, clz);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static void goToDestinationDetail(Context context, long foodId) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constant.FOOD_ID, foodId);
        startActivity(context, DestinationDetailActivity.class, bundle);
    }

    public static void goToDestinationByLocation(Context context, Location location) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_CATEGORY, location);
        startActivity(context, DestinationByLocationActivity.class, bundle);
    }

    public static void onClickFavoriteHotel(Context context, Hotel hotel, boolean isFavorite) {
        if (context == null) return;
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "null";
        Log.d("Favorite", "Current user UID: " + userId);
        if (isFavorite) {
            String userEmail = DataStoreManager.getUser().getEmail();
            Log.d("userEmail", "onClickFavoriteHotel: " + userEmail);
            UserInfo userInfo = new UserInfo(System.currentTimeMillis(), userEmail);
            MyApplication.get(context).hotelDatabaseReference()
                    .child(String.valueOf(hotel.getId()))
                    .child("favorite")
                    .child(String.valueOf(userInfo.getId()))
                    .setValue(userInfo).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Favorite", "Added favorite for hotel: " + hotel.getId() + " with ID: " + userInfo.getId());
                        } else {
                            Log.e("Favorite", "Failed to add favorite: " + task.getException().getMessage());
                        }
                    });
        } else {
            UserInfo userInfo = getUserFavoriteHotel(hotel);
            if (userInfo != null) {
                MyApplication.get(context).hotelDatabaseReference()
                        .child(String.valueOf(hotel.getId()))
                        .child("favorite")
                        .child(String.valueOf(userInfo.getId()))
                        .removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Favorite", "Removed favorite for hotel: " + hotel.getId() + " with ID: " + userInfo.getId());
                            } else {
                                Log.e("Favorite", "Failed to remove favorite: " + task.getException().getMessage());
                            }
                        });
            }
        }
    }

    private static UserInfo getUserFavoriteHotel(Hotel hotel) {
        UserInfo userInfo = null;
        if (hotel.getFavorite() == null || hotel.getFavorite().isEmpty()) return null;
        List<UserInfo> listUsersFavorite = new ArrayList<>(hotel.getFavorite().values());
        for (UserInfo userObject : listUsersFavorite) {
            if (DataStoreManager.getUser().getEmail().equals(userObject.getEmailUser())) {
                userInfo = userObject;
                break;
            }
        }
        return userInfo;
    }

//    public static boolean isFavoriteFood(Destination destination) {
//        if (destination.getFavorite() == null || destination.getFavorite().isEmpty()) return false;
//        List<UserInfo> listUsersFavorite = new ArrayList<>(destination.getFavorite().values());
//        if (listUsersFavorite.isEmpty()) return false;
//        for (UserInfo userInfo : listUsersFavorite) {
//            if (DataStoreManager.getUser().getEmail().equals(userInfo.getEmailUser())) {
//                return true;
//            }
//        }
//        return false;
//    }

    public static boolean isFavoriteHotel(Hotel hotel) {
        if (hotel.getFavorite() == null || hotel.getFavorite().isEmpty()) return false;
        List<UserInfo> listUsersFavorite = new ArrayList<>(hotel.getFavorite().values());
        if (listUsersFavorite.isEmpty()) return false;
        for (UserInfo userInfo : listUsersFavorite) {
            if (DataStoreManager.getUser().getEmail().equals(userInfo.getEmailUser())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHistoryFood(Destination destination) {
        if (destination.getHistory() == null || destination.getHistory().isEmpty()) return false;
        List<UserInfo> listUsersHistory = new ArrayList<>(destination.getHistory().values());
        if (listUsersHistory.isEmpty()) return false;
        for (UserInfo userInfo : listUsersHistory) {
            if (DataStoreManager.getUser().getEmail().equals(userInfo.getEmailUser())) {
                return true;
            }
        }
        return false;
    }

    public static int encodeEmailUser() {
        int hashCode = DataStoreManager.getUser().getEmail().hashCode();
        if (hashCode < 0) {
            hashCode = hashCode * -1;
        }
        return hashCode;
    }
}
