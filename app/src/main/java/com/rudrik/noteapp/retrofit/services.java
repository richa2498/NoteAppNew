package com.rudrik.noteapp.retrofit;

import com.rudrik.noteapp.models.direction.DirectionData;
import com.rudrik.noteapp.models.distance.DistanceData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface services {

    @GET("distancematrix/json?")
    Call<DistanceData> getDistance(@Query("key") String key, @Query("travelMode") String transportMode, @Query("units") String units, @Query("origins") String user, @Query("destinations") String dest);

//    @GET("place/nearbysearch/json?")
//    Call<NearbyData> getNearByPlaces(@Query("key") String key, @Query("radius") long radius, @Query("types") String type, @Query("location") String location);

    @GET("directions/json?")
    Call<DirectionData> getDirections(@Query("key") String key, @Query("mode") String transportMode, @Query("units") String units, @Query("origin") String user, @Query("destination") String dest);


}
