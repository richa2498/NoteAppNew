package com.rudrik.noteapp;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.util.Date;

public class utils {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String strLatLng(LatLng latLng){
        StringBuilder s = new StringBuilder();
        s.append(latLng.latitude);
        s.append(",");
        s.append(latLng.longitude);
        return String.valueOf(s);
    }

    public static LatLng fromStrLatLng(String str){
        LatLng latLng = new LatLng(0, 0);
        if (!str.isEmpty()) {
            double lat = Double.parseDouble(str.substring(0, str.indexOf(',')));
            double lng = Double.parseDouble(str.substring(str.indexOf(',')+1, str.length()-1));

            System.out.println("lat"+ lat);
            System.out.println("lng"+ lng);
            latLng = new LatLng(lat, lng);
        }
        return latLng;
    }

    public static String strDate(Date date){
        return MyApplication.dateFormat.format(date);
    }

    public static Date fromStrDate(String str){
        try {
            return MyApplication.dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    public static int calculateDistanceInKilometer(LatLng userLatLng,
                                                   LatLng destLatLng) {

        double latDistance = Math.toRadians(userLatLng.latitude - destLatLng.latitude);
        double lngDistance = Math.toRadians(userLatLng.longitude - destLatLng.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLatLng.latitude)) * Math.cos(Math.toRadians(destLatLng.latitude))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }

    public static String toCamelCase(String s){
//        String[] parts = s.split("_");
//        String camelCaseString = "";
//        for (String part : parts){
//            camelCaseString = camelCaseString + toProperCase(part);
//        }
//        return camelCaseString;

        return s;
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}
