package com.example.huaweidemoapp.Models;

public class User {
    private String email;
    private String name;
    private boolean preferenceDarkMode;
    private int preferenceDistance;

    public User() {}
    public User(String email, String name, boolean preferenceDarkMode) {
        this.email = email;
        this.name = name;
        this.preferenceDarkMode= preferenceDarkMode;
        preferenceDistance = 10;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isPreferenceDarkMode() {
        return preferenceDarkMode;
    }

    public int getPreferenceDistance() {
        return preferenceDistance;
    }

    public void setPreferenceDarkMode(boolean preferenceDarkMode) {
        this.preferenceDarkMode = preferenceDarkMode;
    }

    public void setPreferenceDistance(int preferencaDistance) {
        this.preferenceDistance = preferencaDistance;
    }
}
