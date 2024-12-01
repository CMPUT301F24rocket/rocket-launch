package com.example.rocket_launch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class that takes an address, passes it into a 3rd party API: NominatimGeocode
 * To get a JSON file to derive lat-long coordinates for osmdroid implementation
 * uses OkHttp to handle url and Nominatim integration
 */
//References:
// https://square.github.io/okhttp/, Accessed 2024-11-24
// https://nominatim.org/release-docs/latest/library/NominatimAPI/, Accessed 2024-11-24
public class NominatimGeocode {
    private static final String BASE_URL =  "https://nominatim.openstreetmap.org/search?format=json&q="; //base url to enter an address and return a geocoded JSON file type
    GeoPoint defaultStartPoint = new GeoPoint(53.52741517718694, -113.52959166397568); //ETLC Coordinates

    //OkHttp reference
    private OkHttpClient client;

    public NominatimGeocode(){
        client = new OkHttpClient();
    }

    /**
     * Gets a JSON file from an address through NominatimGeocode
     * @param address address that will be geocoded
     * @return a responseBody (JSON file response from 3rd party)
     * @throws IOException exception if the responseBody is null, or an unexpected value
     */
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

    /**
     * Get the latitude and longitude values from the JSON file and create a GeoPoint
     * @param jsonResponse JSON file containing an array of best matching results for an address
     * @return a GeoPoint object for an address
     * @throws JSONException if GeoPoint can not be made from JSON file, set to a default start point
     */
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


