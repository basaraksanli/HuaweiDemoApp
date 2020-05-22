package com.example.huaweidemoapp.Controllers.LoginControllers;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.huaweidemoapp.Activities.LoginActivity;
import com.example.huaweidemoapp.Activities.MapsActivity;
import com.example.huaweidemoapp.Models.User;
import com.example.huaweidemoapp.Models.CurrentUserData;
import com.example.huaweidemoapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class FacebookAuth implements IBaseAuth {
    private FirebaseAuth firebaseAuth;
    private static LoginActivity loginActivity;
    private static ProgressBar progressBar;
    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private com.shobhitpuri.custombuttons.GoogleSignInButton googleSignInButton;

    private FirebaseDatabase db;

    public FacebookAuth(LoginActivity loginActivity) {
        db = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        this.loginActivity = loginActivity;
        progressBar = loginActivity.findViewById(R.id.progressBar);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = loginActivity.findViewById(R.id.facebook_login_button);
        googleSignInButton= loginActivity.findViewById(R.id.google_sign_in_button);

    }

    @Override
    public void login() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                try {
                    handleSuccessfulLogin(loginResult.getAccessToken());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                googleSignInButton.setEnabled(true);
                facebookLoginButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                googleSignInButton.setEnabled(true);
                facebookLoginButton.setEnabled(true);
                Log.d("Login facebook", "handleSignInResult:" + error.getMessage());
                Toast.makeText(loginActivity, error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void handleSuccessfulLogin(final AccessToken accessToken) throws SocketException, SocketTimeoutException, MalformedURLException, IOException, Exception {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());


        firebaseAuth.signInWithCredential(credential).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                googleSignInButton.setEnabled(true);
                facebookLoginButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                googleSignInButton.setEnabled(true);
                facebookLoginButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        final User userObj = new User(user.getProviderData().get(1).getEmail(),user.getDisplayName(),darkModeProbability());
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference ref = database.child("users").child(userObj.getEmail().replace(".",""));
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    getPreferences();
                                }
                                else {
                                    addUserToDatabase(userObj);
                                    CurrentUserData.getUserData(userObj);
                                    try {
                                        getFacebookProfilePicture(user.getPhotoUrl(), userObj.getEmail());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                googleSignInButton.setEnabled(true);
                                facebookLoginButton.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }else {
                        getPreferences();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Login facebook", "handleSignInResult:" + task.getException().getMessage());
                    Toast.makeText(loginActivity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public static void getFacebookProfilePicture(Uri photoUrl, String email) {
        InputStream input = null;
        try {
            // Download Image from URL
            input = new java.net.URL(photoUrl.toString()+ "?type=large").openStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StorageReference sref = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = sref.child("users/"+ email.replace(".",""));


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
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(loginActivity, MapsActivity.class);
                loginActivity.startActivity(intent);
            }
        });


    }
    public void addUserToDatabase(User user){
        DatabaseReference mDatabaseReferance = db.getReference();
        mDatabaseReferance = mDatabaseReferance.child("users").child(user.getEmail().replace(".",""));
        mDatabaseReferance.setValue(user);
    }
    public void activityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
        final FirebaseDatabase firebaseDatabase;
        final DatabaseReference databaseRef;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef= firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getProviderData().get(1).getEmail().replace(".", ""));
        databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user[0] = dataSnapshot.getValue(User.class);
                assert user[0] != null;
                CurrentUserData.getUserData(user[0]);

                progressBar.setVisibility(View.GONE);
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

}
