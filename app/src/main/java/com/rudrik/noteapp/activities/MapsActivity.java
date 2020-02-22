package com.rudrik.noteapp.activities;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.rudrik.noteapp.MyApplication;
import com.rudrik.noteapp.R;
import com.rudrik.noteapp.models.Note;
import com.rudrik.noteapp.models.direction.DirectionData;
import com.rudrik.noteapp.models.direction.LegsItem;
import com.rudrik.noteapp.models.direction.RoutesItem;
import com.rudrik.noteapp.models.direction.StepsItem;
import com.rudrik.noteapp.retrofit.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rudrik.noteapp.MyApplication.LOC_REQ_CODE;
import static com.rudrik.noteapp.MyApplication.SEL_NOTE;
import static com.rudrik.noteapp.MyApplication.getStrToday;
import static com.rudrik.noteapp.MyApplication.prefs;
import static com.rudrik.noteapp.MyApplication.statusCheck;
import static com.rudrik.noteapp.utils.strLatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleMap mMap;
    private LatLng userLocation = null;
    private LatLng destLocation = null;
    private Note selNote;
    private Marker userMarker = null;
    private Marker destMarker = null;

    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkNote();
    }

    private void checkNote() {
        selNote = (Note) getIntent().getSerializableExtra(SEL_NOTE);
        if (selNote != null) {
            destLocation = new LatLng(selNote.getLat(), selNote.getLng());
            System.out.println("from note : " + selNote.getTitle());
        } else {
            selNote = new Note();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        userLocation = new LatLng(prefs.getFloat("U_LAT", 0f), prefs.getFloat("U_LNG", 0f));

        Log.e("LOCATION_SERVICE", String.valueOf(statusCheck(this)));

        if (userLocation.latitude == 0f || userLocation.longitude == 0f) {
            if (checkPermissions()) {
                MyApplication.initUserLocation(this);
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setHomeMarker();

        if (selNote != null) {
            MarkerOptions options = new MarkerOptions()
                    .title(selNote.getTitle())
                    .position(destLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            destMarker = mMap.addMarker(options);

            animateToSelectedPlace(destLocation);

            checkPath();
        }

        checkPath();
    }

    private void animateToSelectedPlace(LatLng latLng) {
        CameraPosition position = CameraPosition.builder()
                .target(latLng)
                .zoom(15)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    //  set user location marker
    private void setHomeMarker() {

        animateToSelectedPlace(userLocation);

        userMarker = mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .icon(bitmapFromVector(R.drawable.ic_user_loc))
                .title("Your location!"));

        Log.e("TODAY", getStrToday());

//        mMap.setOnInfoWindowClickListener(this);

//        String strPlace = getIntent().getStringExtra("PLACE");
//        if (strPlace != null) {
//            MyPlace p = new Gson().fromJson(strPlace, MyPlace.class);
//            selPlace = p;
//            animateToSelectedPlace(selPlace);
//
//            Log.e("PLACE", p.toString());
//        }

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CameraPosition position = CameraPosition.builder()
//                        .target(userLocation)
//                        .zoom(15)
//                        .bearing(0)
//                        .tilt(45)
//                        .build();
//
//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
//            }
//        });
    }

    private void checkPath() {
        if (polyline != null){
            polyline.remove();
        }

        showPath(userLocation, destLocation);

    }

    private void showPath(LatLng origin, LatLng dest) {
        Api.getInstance().getServices().getDirections(getResources().getString(R.string.google_maps_key),
                "driving",
                "metric",
                strLatLng(origin),
                strLatLng(dest)).enqueue(new Callback<DirectionData>() {
            @Override
            public void onResponse(Call<DirectionData> call, Response<DirectionData> response) {
                System.out.println("Direction : -> " + call.request().url());
                DirectionData data = response.body();
                if (data != null && data.getStatus().equals("OK")) {
                    PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                    List<LatLng> points = new ArrayList<>();
                    for (RoutesItem route : data.getRoutes()) {
                        for (LegsItem leg : route.getLegs()) {
                            for (StepsItem step : leg.getSteps()) {
                                points.addAll(PolyUtil.decode(String.valueOf(step.getPolyline().getPoints())));
                            }
                        }
                    }
                    options.addAll(points);
                    options.width(12);
                    options.color(Color.RED);
                    options.geodesic(true);

                    if (polyline != null) {
                        polyline.remove();
                    }

                    polyline = mMap.addPolyline(options);
                }
            }

            @Override
            public void onFailure(Call<DirectionData> call, Throwable t) {

            }
        });
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("U_LAT") || key.equals("U_LNG")) {
            userLocation = new LatLng(prefs.getFloat("U_LAT", 0f), prefs.getFloat("U_LNG", 0f));
            Log.e("USER_LOCATION", userLocation.toString());

            if (userMarker != null) {
                userMarker.remove();
            }

            setHomeMarker();
        }
    }

    @SuppressLint("NewApi")
    private boolean checkPermissions() {
        boolean isPermitted = (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED;
        if (!isPermitted) {
            requestPermission();
        }
        return isPermitted;
    }

    @SuppressLint("NewApi")
    private void requestPermission() {
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
    }

    private BitmapDescriptor bitmapFromVector(int id) {
        Drawable drawable = ContextCompat.getDrawable(this, id);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
