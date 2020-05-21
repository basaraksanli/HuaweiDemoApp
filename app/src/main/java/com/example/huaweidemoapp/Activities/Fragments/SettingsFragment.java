package com.example.huaweidemoapp.Activities.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.huaweidemoapp.Models.CurrentUserData;
import com.example.huaweidemoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {


    SeekBar distanceSeekBar;
    TextView distanceText;
    Switch darkmode;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        distanceSeekBar = root.findViewById(R.id.seekBar);
        distanceSeekBar.setProgress(CurrentUserData.getDistance()/5-1);
        distanceText = root.findViewById(R.id.distanceTextView);
        darkmode= root.findViewById(R.id.darkModeSwitch);
        darkmode.setChecked(CurrentUserData.isDarkMode());
        distanceText.setText(CurrentUserData.getDistance()+ "m");

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(CurrentUserData.getUserID());

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                progress+=1;
                distanceText.setText(progress * 5 + "m");
                CurrentUserData.setDistance(progress*5);
                mDatabase.child("preferenceDistance").setValue(progress*5);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CurrentUserData.setDarkMode(isChecked);
                mDatabase.child("preferenceDarkMode").setValue(isChecked);
            }
        });
        return root;
    }
}
