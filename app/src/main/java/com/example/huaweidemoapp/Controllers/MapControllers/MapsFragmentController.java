package com.example.huaweidemoapp.Controllers.MapControllers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.example.huaweidemoapp.Models.CurrentUserData;
import com.example.huaweidemoapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsFragmentController {
    static Marker currentPositionMarker;
    public MapsFragmentController() {
    }
    public Bitmap createUserBitmap(Activity activity, Bitmap profilePicture) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62,activity), dp(76,activity), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = activity.getResources().getDrawable(R.drawable.pin);
            drawable.setBounds(0, 0, dp(62,activity), dp(76,activity));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = profilePicture;
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52,activity) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5,activity), dp(5,activity));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5,activity), dp(5,activity), dp(52 + 5, activity), dp(52 + 5,activity));
                canvas.drawRoundRect(bitmapRect, dp(26,activity), dp(26,activity), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception ignored) {}
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }
    private int dp(float value, Activity fragment) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(fragment.getResources().getDisplayMetrics().density * value);
    }
    public void drawMarker(Location location , Bitmap profilePicture, Activity activity, GoogleMap mMap){


        MarkerOptions options = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);
        Bitmap bitmap = createUserBitmap(activity,profilePicture );
        if(profilePicture!=null) {
            options.title(CurrentUserData.getDisplayName());
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            options.anchor(0.5f, 0.907f);
            if(currentPositionMarker!=null)
                currentPositionMarker.remove();
            currentPositionMarker=mMap.addMarker(options);
        }
    }

    public void moveCamera(LatLng latLng, GoogleMap mMap, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    public float calculateDistance(Location location1, Location location2)
    {
        if(location1!=null && location2!= null)
            return location1.distanceTo(location2);
        else return 0;
    }
    public void animateCamera(LatLng latLng, GoogleMap mMap, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

}
