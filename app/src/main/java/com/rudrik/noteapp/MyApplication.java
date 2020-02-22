package com.rudrik.noteapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.rudrik.noteapp.models.Note;
import com.rudrik.noteapp.room.MyDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

import static com.rudrik.noteapp.models.Folder.myFolders;

public class MyApplication extends Application {

    private static boolean activityVisible;
    public static String FOLDER_DATA_CHANGES = "FOLDER_DATA_CHANGES";
    public static String NOTES_DATA_CHANGES = "NOTES_DATA_CHANGES";
    public static String SEL_FOLDER = "SEL_FOLDER";
    public static String SEL_NOTE = "SEL_NOTE";

    public static MyDatabase db;
    public static Executor bg, main;

    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    public static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");

    //  get user location
    public static int LOC_REQ_CODE = 1;

    private static Context context = null;
    private static FusedLocationProviderClient fusedLocationProviderClient;
    static LocationCallback locationCallback;
    static LocationRequest locationRequest;

    public static Geocoder geocoder;

    private static LatLng userLoc = null;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("Application ", "CREATED");

        db = MyDatabase.getInstance(this);

        bg = AppExecutors.getInstance().diskIO();
        main = AppExecutors.getInstance().mainThread();

        prefs = getSharedPreferences("NOTE_APP", MODE_PRIVATE);
        editor = prefs.edit();

    }

    public void folderUpdates(Context context) {
        MyApplication.context = context;
        main.execute(() -> {

            //  fetching folders
            db.foldersDao().getLiveFolders().observe((LifecycleOwner) context, folders -> {
                Log.e("FOLDERS", folders.toString());

//                if (myFolders != folders) {
                    myFolders = folders;

                    editor.putBoolean(FOLDER_DATA_CHANGES, true);
                    editor.apply();
//                }
            });
        });
    }

    public static void noteUpdates(Context context, int fId){
        main.execute(()->{

            //  fetching notes
            db.notesDao().getLiveNotes(fId).observe((LifecycleOwner) context, notes -> {
                Log.e("NOTES", notes.toString());

//                if (Note.myNotes != notes) {
                Note.myNotes = notes;

                editor.putBoolean(NOTES_DATA_CHANGES, true);
                editor.apply();
//                }
            });
        });
    }

    public static boolean statusCheck(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context);
        }
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private static void buildAlertMessageNoGps(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public static void initUserLocation(Context context) {
        MyApplication.context = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getUserLocation();
        geocoder = new Geocoder(context, Locale.getDefault());

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private static void getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                SharedPreferences.Editor edit = getPrefs().edit();

                for (Location l : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(l.getLatitude(), l.getLongitude());
                    userLoc = userLocation;

                    edit.putFloat("U_LAT", (float) l.getLatitude());
                    edit.putFloat("U_LNG", (float) l.getLongitude());
                    edit.apply();
                }
            }
        };
    }

    public static SharedPreferences getPrefs() {
        return prefs;
    }

    //  DATABASE OPERATIONS

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        Log.e("Application ", "RESUMED");
        activityVisible = true;

//        Note.myNotes = db.getNotes();
    }

    public static void activityPaused() {
        Log.e("Application ", "PAUSED");
        activityVisible = false;

//        db.clearData();
//        db.addPlaces(Note.myNotes);
    }


    //  Other methods


    public static String getStrToday() {
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getStrToday(Date date) {
        return dateFormat.format(date);
    }

    public static Date getToday() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

}