package com.example.rocket_launch;


//References:
// https://square.github.io/okhttp/, Accessed 2024-11-24
// https://nominatim.org/release-docs/latest/library/NominatimAPI/, Accessed 2024-11-24

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NominatimGeocode {
    private static final String BASE_URL =  "https://nominatim.openstreetmap.org/search?format=json&q=";
    GeoPoint defaultStartPoint = new GeoPoint(53.52741517718694, -113.52959166397568); //ETLC Coordinates

    //OkHttp reference
    private OkHttpClient client;

    public NominatimGeocode(){
        client = new OkHttpClient();
    }

    //Get a JSON file from a string address
    public String geocodeAddress(String address) throws IOException {
        //encode address into a URL
        String url = BASE_URL + java.net.URLEncoder.encode(address, "UTF-8");
        Log.i("URL", "geocodeAddress: " + url);

        //create HTTP request
        Request request = new Request.Builder()
                .url(url)
                .build();

        //Execute the request
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()){
            if (response.body() != null){
                String responseBody = response.body().string();

                if (responseBody.isEmpty()){
                    throw new IOException("Empty response from server");
                }
                Log.i("geocodeAddressResponse", "JSON response: " + responseBody);
                return responseBody;
            } else {
                throw new IOException("Response body is null");
            }
        } else {
            throw new IOException("Unexpected response: " + response);
            }
        }

    //get the latitude and longitude and return a geopoint
    public GeoPoint getLatLongFromJson(String jsonResponse) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonResponse);

        try {
            //if the response is not empty
            if (jsonArray.length() > 0) {
                JSONObject location = jsonArray.getJSONObject(0); //first result is the best match

                //lat and long are doubles
                double latitude = Double.parseDouble(location.getString("lat"));
                double longitude = Double.parseDouble(location.getString("lon"));

                //return Geopoint with lat & long
                return new GeoPoint(latitude, longitude);
            } else {
                Log.e("Geocode", "No results found in JSON response: " + jsonResponse);
                return defaultStartPoint;
            }
        } catch (JSONException e){
            Log.e("Geocode", "JSONException: " + e.getMessage());
            return defaultStartPoint;
        }
    }
}


