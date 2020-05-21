package com.example.huaweidemoapp.Models;

import android.location.Location;

public class CurrentUserData {
    private static String displayName;
    private static String email;
    private static String userID;
    private static Location firstLocation;
    private static boolean darkMode= false;
    private static int distance=10;

    public static void getUserData(User userObj, String userID){
        CurrentUserData.darkMode = userObj.isPreferenceDarkMode();
        CurrentUserData.distance = userObj.getPreferenceDistance();
        CurrentUserData.displayName = userObj.getName();
        CurrentUserData.email = userObj.getEmail();
        CurrentUserData.userID = userID;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static void setDisplayName(String displayName) {
        CurrentUserData.displayName = displayName;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        CurrentUserData.email = email;
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        CurrentUserData.userID = userID;
    }

    public static Location getFirstLocation() {
        return firstLocation;
    }

    public static void setFirstLocation(Location firstLocation) {
        CurrentUserData.firstLocation = firstLocation;
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean darkMode) {
        CurrentUserData.darkMode = darkMode;
    }

    public static int getDistance() {
        return distance;
    }

    public static void setDistance(int distance) {
        CurrentUserData.distance = distance;
    }
}
