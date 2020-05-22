package com.example.huaweidemoapp.Controllers.MapControllers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.huaweidemoapp.Models.CurrentUserData;
import com.example.huaweidemoapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MapsController {
    public MapsController() {
    }

    public void setProfileUI(NavigationView navigationView, final Activity activity){
        StorageReference sref = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = sref.child("users/"+ CurrentUserData.getEmail().replace(".",""));
        TextView emailText = navigationView.getHeaderView(0).findViewById(R.id.emailTextView);
        TextView nameText = navigationView.getHeaderView(0).findViewById(R.id.nameTextView);
        final ShapeableImageView profilePicture = navigationView.getHeaderView(0).findViewById(R.id.profilePictureView);

        emailText.setText(CurrentUserData.getEmail());
        nameText.setText(CurrentUserData.getDisplayName());
        final long ONE_MEGABYTE = 1024 * 1024;

        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePicture.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(activity.getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setNavigationView(Activity activity, AppBarConfiguration mAppBarConfiguration, DrawerLayout drawer, NavigationView navigationView){

        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
}
