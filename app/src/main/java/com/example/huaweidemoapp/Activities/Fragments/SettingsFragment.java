package com.example.huaweidemoapp.Activities.Fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.huaweidemoapp.Models.CurrentUserData;
import com.example.huaweidemoapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SettingsFragment extends Fragment {


    private SeekBar distanceSeekBar;
    private TextView distanceText;
    private Switch darkMode;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        distanceSeekBar = root.findViewById(R.id.seekBar);
        distanceSeekBar.setProgress(CurrentUserData.getDistance()-1);
        distanceText = root.findViewById(R.id.distanceTextView);
        darkMode= root.findViewById(R.id.darkModeSwitch);
        darkMode.setChecked(CurrentUserData.isDarkMode());
        distanceText.setText(CurrentUserData.getDistance()+ "m");
        Button saveButton = root.findViewById(R.id.saveButton);
        Button backButton = root.findViewById(R.id.backButton);


        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(CurrentUserData.getEmail().replace(".",""));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CurrentUserData.setDarkMode(darkMode.isChecked());
                CurrentUserData.setDistance(distanceSeekBar.getProgress()+1);
                mDatabase.child("preferenceDarkMode").setValue(darkMode.isChecked());
                mDatabase.child("preferenceDistance").setValue(distanceSeekBar.getProgress()+1);
                getFragmentManager().popBackStackImmediate();
            }
        });


        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                progress+=1;
                distanceText.setText(progress + "m");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return root;
    }


}
