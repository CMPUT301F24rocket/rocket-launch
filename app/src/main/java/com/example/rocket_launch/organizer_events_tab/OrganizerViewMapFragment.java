package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.rocket_launch.NominatimGeocode;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fragment used for displaying a map to an organizer
 */
//References used for map views & processing
// https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java), Accessed 2024-11-24
// https://square.github.io/okhttp/, Accessed 2024-11-24
// https://nominatim.org/release-docs/latest/library/NominatimAPI/, Accessed 2024-11-24
public class OrganizerViewMapFragment extends Fragment {
    private String androidId;
    private MapView mapView = null;
    private UsersDB userDB;
    private User user;
    private String addressFacility;
    private GeoPoint facilityGeoPoint;

    //TODO: display coordinates of entrants
    // get facility coordinates from address (entered in userProfile) --> geocoding
    // make sure the address entered is valid
    // add extra options: entrants within a specified range of facility & those outside
    //                      --> display it in a list so its easier to read

    public OrganizerViewMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDB = new UsersDB();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_view_map_fragment, container, false);

        //Setting up mapView
        mapView = view.findViewById(R.id.organizer_map_view);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapView.setMultiTouchControls(true); //allows finger pinch zooming

        //Set default coordinate for map
        GeoPoint defaultStartPoint = new GeoPoint(53.52741517718694, -113.52959166397568); //ETLC coordinates
        String defaultAddress = "Engineering Teaching and Learning Complex Edmonton";

        //String addressFacilityTest = defaultAddress; //TODO: replace this with organizer facility address later

        //use the callback to fetch data from database asynchronously
        //geocode address to get geopoint
        getFacilityAddress(addressFetched -> {
            if (addressFetched != null){
                addressFacility = addressFetched;
            } else {
                addressFacility = defaultAddress;
                facilityGeoPoint = defaultStartPoint;
            }
            Log.i("getAddressFacilityTest", "address Facility: " + addressFacility); //log facility address

            //create the starting point and set mapView to that location
            createStartGeoPosition(mapController, addressFacility, facilityGeoPoint);
        });

        //back button
        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> closeFragment());

        return view;
    }

    /**
     * Close the fragment and return to the Created Activities view
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void createStartGeoPosition(IMapController mapController,String address, GeoPoint facilityGeoPoint){
        //run geocoding in the background
        //note: nominatim is limited in geocoding --> may not work for specific/complex addresses
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() ->{
            try {
                //geocode address to geopoint
                NominatimGeocode geocode = new NominatimGeocode();
                String addressJson = geocode.geocodeAddress(address);

                if (addressJson.length() > 0){
                    GeoPoint startPoint = geocode.getLatLongFromJson(addressJson);

                    //post geopoint onto map
                    handler.post(() -> {
                        if (startPoint != null) {
                            mapController.setCenter(startPoint);
                            createMapMarker("Your Facility", startPoint);
                        }
                    });
                } else {
                    handler.post(() -> {
                        mapController.setCenter(facilityGeoPoint);
                        createMapMarker("Your Facility", facilityGeoPoint);
                    });
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createMapMarker(String name, GeoPoint geoPoint){
        org.osmdroid.views.overlay.Marker facilityMarker = new Marker(mapView);
        facilityMarker.setPosition(geoPoint);
        facilityMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        facilityMarker.setTitle(name);
        mapView.getOverlays().add(facilityMarker);
    }

    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    //Callback for Address fetched from the Database --> since fetching from the DB is asynchronoua
    private interface AddressCallback{
        void onAddressFetched(String address);
    }

    //get organizer facility address
    private void getFacilityAddress(AddressCallback addressCallback){
        userDB.getUser(androidId, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User newUser) {
                user = newUser;
                String address = newUser.getUserFacilityAddress();
                Log.i("FETCH FACILITY ADDRESS", "onSuccess: addressToSet: " + address);

                addressCallback.onAddressFetched(address);

            }
        }, e -> {Log.e("getFacilityAddress", "No matching document found or task failed", e);
                addressCallback.onAddressFetched(null);
        });
    }
}