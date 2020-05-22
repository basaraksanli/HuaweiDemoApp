package com.example.huaweidemoapp.Models;

import android.location.Location;

public class CurrentUserData {
    private static String displayName;
    private static String email;
    private static Location firstLocation = null;
    private static boolean darkMode= false;
    private static int distance=-1;

    public static void getUserData(User userObj){
        CurrentUserData.darkMode = userObj.isPreferenceDarkMode();
        CurrentUserData.distance = userObj.getPreferenceDistance();
        CurrentUserData.displayName = userObj.getName();
        CurrentUserData.email = userObj.getEmail();
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
