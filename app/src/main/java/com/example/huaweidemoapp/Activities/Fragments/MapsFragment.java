package com.example.huaweidemoapp.Activities.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.huaweidemoapp.Controllers.MapControllers.MapsFragmentController;
import com.example.huaweidemoapp.Controllers.MapControllers.NotificationController;
import com.example.huaweidemoapp.Models.CurrentUserData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.example.huaweidemoapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;


public class MapsFragment extends Fragment implements OnMapReadyCallback {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 17f;
    private SupportMapFragment mapFragment;
    private Activity activity;
    private Bitmap profilePicture;
    private Marker marker;
    boolean firstTime = true;
    private MapsFragmentController mapsFragmentController;
    private NotificationController notificationController;
    private Location currentLocation;
    private FloatingActionButton moveCurrentPositionButton;
    private FloatingActionButton setFirstLocationButton;
    Marker FirstLocationMarker;
    private Context mContext;
    private ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_maps, container, false);
        mapsFragmentController = new MapsFragmentController();
        notificationController = new NotificationController();

        getLocationPermissions();
        if (mLocationPermissionGranted)
            getFirstPosition(getActivity());

        StorageReference sref = FirebaseStorage.getInstance().getReference();
        activity = getActivity();
        progressBar = root.findViewById(R.id.progressBarMaps);
        progressBar.setVisibility(View.VISIBLE);
        notificationController.createChannel((NotificationManager) Objects.requireNonNull(activity.getSystemService(Context.NOTIFICATION_SERVICE)));
        moveCurrentPositionButton = root.findViewById(R.id.currentPositionButton);
        setFirstLocationButton = root.findViewById(R.id.set_first_location);

        setFirstLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLocation!=null){
                    CurrentUserData.setFirstLocation(currentLocation);
                    FirstLocationMarker.remove();
                    FirstLocationMarker =mMap.addMarker(new MarkerOptions().position(new LatLng(CurrentUserData.getFirstLocation().getLatitude(), CurrentUserData.getFirstLocation().getLongitude())).title("First Location"));
                }
            }
        });

        moveCurrentPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLocation!=null)
                    mapsFragmentController.animateCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), mMap, DEFAULT_ZOOM);
            }
        });
        final long ONE_MEGABYTE = 1024 * 1024;

        final StorageReference imageRef = sref.child("users/" + CurrentUserData.getEmail().replace(".","") );
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                profilePicture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (currentLocation != null)
                    mapsFragmentController.drawMarker(currentLocation, profilePicture, activity, mMap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(mContext, "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(20.0f);
        mMap.setMyLocationEnabled(false);
        if (CurrentUserData.isDarkMode())
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style_dark));
        else
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style_light));


        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        assert lm != null;
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0, new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationChanged(Location location) {
                progressBar.setVisibility(View.GONE);
                currentLocation = location;
                if (firstTime == true) {
                    mapsFragmentController.moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), mMap, DEFAULT_ZOOM);
                    FirstLocationMarker =mMap.addMarker(new MarkerOptions().position(new LatLng(CurrentUserData.getFirstLocation().getLatitude(), CurrentUserData.getFirstLocation().getLongitude())).title("First Location"));
                    firstTime = false;
                }
                mapsFragmentController.drawMarker(currentLocation, profilePicture, activity, mMap);
                while (mContext == null) ;
                if (mapsFragmentController.calculateDistance(CurrentUserData.getFirstLocation(), location) >= CurrentUserData.getDistance()) {
                    notificationController.sendNotification(mContext, (NotificationManager) Objects.requireNonNull(activity.getSystemService(Context.NOTIFICATION_SERVICE)));
                } else {
                    NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        return;
                    }
                }

                mLocationPermissionGranted = true;
                initMap();
                getFirstPosition(getActivity());
            }
        }
    }

    private void getLocationPermissions() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(mContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(mContext, COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
                getFirstPosition(getActivity());
            } else {
                super.requestPermissions(
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            super.requestPermissions(
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.mapView, mapFragment).commit();
        }
        mapFragment.getMapAsync(MapsFragment.this);
    }

    public void getFirstPosition(Activity activity) {

        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null && CurrentUserData.getFirstLocation() == null) {
                            CurrentUserData.setFirstLocation(location);
                        }
                        if (mLocationPermissionGranted)
                            getDeviceLocation();
                    }
                });
    }


}
