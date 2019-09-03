package com.frontepic.thequizmagnificent.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class Prefs {

    private SharedPreferences preferences;

    public Prefs(Activity  activity) {
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void saveHighestScore(int score) {
        int currentScore = score;
        int highestScore = preferences.getInt("highest_score", 0);

        if(currentScore > highestScore) {
            preferences.edit().putInt("highest_score", currentScore).apply();
        }
    }

    public int getHighestScore() {
        return preferences.getInt("highest_score", 0);
    }

    public void setState(int index) {
        preferences.edit().putInt("index_state", index).apply();
    }

    public int getState() {
        return preferences.getInt("index_state", 0);
    }

}

