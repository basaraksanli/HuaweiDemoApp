package com.example.huaweidemoapp.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huaweidemoapp.Controllers.LoginControllers.FacebookAuth;
import com.example.huaweidemoapp.Controllers.LoginControllers.GoogleAuth;
import com.example.huaweidemoapp.Controllers.LoginControllers.IBaseAuth;
import com.example.huaweidemoapp.Models.User;
import com.example.huaweidemoapp.Models.CurrentUserData;
import com.example.huaweidemoapp.R;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Arrays;



public class LoginActivity extends AppCompatActivity  {


    FirebaseAuth auth;
    FirebaseUser user;
    com.shobhitpuri.custombuttons.GoogleSignInButton googleSignInButton;
    LoginButton facebookSignInButton;
    IBaseAuth baseAuth;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);

        auth= FirebaseAuth.getInstance();
        user= auth.getCurrentUser();


        if(user == null){
            setContentView(R.layout.activity_login);
            FacebookSdk.sdkInitialize(getApplicationContext());
            googleSignInButton = findViewById(R.id.google_sign_in_button);
            progressBar = findViewById(R.id.progressBar);
            facebookSignInButton = findViewById(R.id.facebook_login_button);
            facebookSignInButton.setReadPermissions(Arrays.asList("email"));

            final LoginActivity loginActivity = this;

            googleSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    googleSignInButton.setEnabled(false);
                    facebookSignInButton.setEnabled(false);
                    baseAuth = new GoogleAuth(loginActivity);
                    baseAuth.login();
                }
            });
            facebookSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    facebookSignInButton.setEnabled(false);
                    googleSignInButton.setEnabled(false);
                    baseAuth= new FacebookAuth(loginActivity);
                    baseAuth.login();
                }
            });
        }
        else{
            getPreferences(this);
        }
    }
    public void getPreferences(final LoginActivity loginActivity){
        final User[] user = new User[1];
        FirebaseDatabase firebaseDatabase;
        final DatabaseReference databaseRef;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef= firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getEmail().replace(".",""));
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user[0] = dataSnapshot.getValue(User.class);
                CurrentUserData.getUserData(user[0]);
                Intent intent = new Intent(loginActivity, MapsActivity.class);
                loginActivity.startActivity(intent);
                databaseRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        progressBar.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
            GoogleAuth googleAuth= (GoogleAuth) baseAuth;
            googleAuth.activityResult(data,this);
        }
        else if(requestCode == 64206){
            FacebookAuth facebookAuth = (FacebookAuth) baseAuth;
            facebookAuth.activityResult(requestCode,resultCode,data);
        }
    }
}
