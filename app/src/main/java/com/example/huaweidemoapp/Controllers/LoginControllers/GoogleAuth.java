package com.example.huaweidemoapp.Controllers.LoginControllers;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.huaweidemoapp.Activities.MapsActivity;
import com.example.huaweidemoapp.Models.User;
import com.example.huaweidemoapp.Models.CurrentUserData;
import com.example.huaweidemoapp.R;
import com.example.huaweidemoapp.Activities.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

public class GoogleAuth implements IBaseAuth, GoogleApiClient.OnConnectionFailedListener {
    private static FirebaseAuth firebaseAuth;
    private static LoginActivity loginActivity;
    private static ProgressBar progressBar;
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;

    public GoogleAuth(LoginActivity loginActivity) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.loginActivity = loginActivity;
        progressBar = loginActivity.findViewById(R.id.progressBar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(loginActivity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(loginActivity.getApplicationContext())
                .enableAutoManage(loginActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void login() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        loginActivity.startActivityForResult(signIntent, RC_SIGN_IN);
    }

    public void authWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        User userObj = new User(user.getProviderData().get(1).getEmail(),user.getDisplayName(), darkModeProbability());
                        addUserToDatabase(userObj, user.getUid());
                        CurrentUserData.getUserData(userObj, user.getUid());
                        try {
                            getGmailProfilePicture(user.getPhotoUrl(), user.getUid());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        getPreferences();
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(loginActivity, "Auth Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addUserToDatabase(User user, String uid){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("users").child(uid);
        ref.setValue(user);
    }
    public void activityResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        Log.d("Login Google", "handleSignInResult:" + result.getStatus());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            authWithGoogle(account);
        } else
            progressBar.setVisibility(View.GONE);
    }

    public static void getGmailProfilePicture(Uri imageUrl,String userID) {
        InputStream input = null;
        try {
            input = new java.net.URL(imageUrl.toString()+ "?type=large`").openStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StorageReference sref = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = sref.child("users/"+ userID+ ".jpg");

        UploadTask uploadTask = imageRef.putStream(input);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(loginActivity, MapsActivity.class);
                loginActivity.startActivity(intent);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(loginActivity, MapsActivity.class);
                loginActivity.startActivity(intent);
            }
        });
    }
    public boolean darkModeProbability(){
        Random rand= new Random();
        if(rand.nextInt(100)<60) {
            return true;
        }
        else
            return false;
    }
    public void getPreferences(){
        final User[] user = new User[1];
        FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseRef;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef= firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid());
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user[0] = dataSnapshot.getValue(User.class);
                CurrentUserData.getUserData(user[0], firebaseAuth.getUid());
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(loginActivity, MapsActivity.class);
                loginActivity.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
