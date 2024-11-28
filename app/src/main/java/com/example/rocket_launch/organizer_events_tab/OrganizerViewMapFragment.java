package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.EntrantLocationData;
import com.example.rocket_launch.EventsDB;
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
import org.osmdroid.views.overlay.ScaleBarOverlay;

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
    private ScaleBarOverlay mapScaleBarOverlay;
    private UsersDB userDB;
    private EventsDB eventsDB;
    private User user;
    private String addressFacility;
    private GeoPoint facilityGeoPoint;

    //TODO: display coordinates of entrants
    // get facility coordinates from address (entered in userProfile) --> geocoding - DONE
    // make sure the address entered is valid --> Set a default instead: DONE
    // Get Entrant Locations on event sign up
    //                      --> ask location permission - DONE
    //                      --> put all entrant coordinates & Names into a list in EventDB
    //                      --> for geoplotting and list views
    // Enable edit facility address in mapView for convenience
    //                      --> and update facility address in DB
    //                      --> Create fragment for editing address
    //                      --> put hint: "address not showing up? try simplifying it
    //                      --> eg. ETLC University of Alberta Edmonton (no commas, special characters, or postal codes)
    // Add range functionality:
    //                      --> entrants within a specified range of facility & those outside
    //                      --> display it in a list so its easier to read
    //                      --> XML - DONE

    public OrganizerViewMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDB = new UsersDB();
        eventsDB = new EventsDB();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        if (getArguments() != null){
            String eventID = getArguments().getString("eventId");
        }
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
        mapController.setZoom(15.0); //setting zoom on launch
        mapView.setMultiTouchControls(true); //allows finger pinch zooming
        setMapScaleBarOverlay(); //create scale bar

        //Set default coordinate for map
        GeoPoint defaultStartPoint = new GeoPoint(53.52741517718694, -113.52959166397568); //ETLC coordinates
        String defaultAddress = "Engineering Teaching and Learning Complex Edmonton";

        //use the callback to fetch data from database asynchronously
        //geocode address to get geopoint for location on view start
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

        //map options button
        ImageButton mapOptionsButton = view.findViewById(R.id.organizer_map_options_button);
        mapOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrganizerMapViewOptionsFragment mapOptions = new OrganizerMapViewOptionsFragment();
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map_options_frame, mapOptions)
                        .addToBackStack(null)
                        .commit();
            }
        });

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

    private void setMapScaleBarOverlay(){
        //setting up scale bar attributes
        mapScaleBarOverlay = new ScaleBarOverlay(mapView);
        mapScaleBarOverlay.setAlignRight(true);
        mapScaleBarOverlay.setScaleBarOffset(350,50);
        mapScaleBarOverlay.setTextSize(40);
        mapView.getOverlays().add(mapScaleBarOverlay);
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

    private interface EntrantLocationDataListCallback{
        void onEntrantLocationDataListFetched(EntrantLocationData entrantLocationData);
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

    //get entrant location data from database
    private void getEntrantLocationDataList(EntrantLocationDataListCallback entrantLocationDataListCallback){

    }
}