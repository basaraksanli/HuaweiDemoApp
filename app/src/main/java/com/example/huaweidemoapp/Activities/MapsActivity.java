package com.example.huaweidemoapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.huaweidemoapp.Controllers.MapControllers.MapsController;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.huaweidemoapp.R;
import com.google.firebase.auth.FirebaseAuth;




public class MapsActivity extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        MapsController mapsController = new MapsController();
        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();

        mapsController.setNavigationView(this,mAppBarConfiguration,drawer,navigationView);
        mapsController.setProfileUI(navigationView, this);


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
    public void logoutClick(MenuItem item) {
        final MapsActivity mapsActivity =this;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int answer) {
                switch (answer) {
                    case DialogInterface.BUTTON_POSITIVE:
                        LoginManager.getInstance().logOut();
                        auth.signOut();
                        Intent loginActivity = new Intent(mapsActivity, LoginActivity.class);
                        startActivity(loginActivity);
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mapsActivity);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }
}
