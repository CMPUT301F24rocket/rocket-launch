package com.example.rocket_launch.organizer_events_tab;

import static org.osmdroid.views.overlay.Polygon.pointsAsCircle;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rocket_launch.EntrantLocationData;
import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.MapOptionsViewModel;
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
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fragment used for displaying a mapView to an organizer
 * Displays: map using OSMDROID, location of user facility, locations of entrants, defined range around facility & entrants in/out of that range
 * Author: Rachel
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
    private Event event;
    private String eventID;
    private String addressFacility;
    private GeoPoint defaultGeoPoint;
    private MapOptionsViewModel mapOptionsViewModel;
    private Marker facilityMarker;
    private Polygon definedRadiusPolygon;
    TextView noEventGeolocationTextView;

    public OrganizerViewMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDB = new UsersDB();
        eventsDB = new EventsDB();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        mapOptionsViewModel = new ViewModelProvider(requireActivity()).get(MapOptionsViewModel.class);
        definedRadiusPolygon = new Polygon();

        assert getArguments() != null;
        eventID = getArguments().getString("eventId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_view_map_fragment, container, false);
        getFacilityName();
        Log.i("View Map Fragment Facility Name", "onCreateView: facility name: " + mapOptionsViewModel.getFacilityName().getValue());

        noEventGeolocationTextView = view.findViewById(R.id.no_entrant_location_data);

        //Setting up mapView
        mapView = view.findViewById(R.id.organizer_map_view);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
       // mapView.setScrollableAreaLimitDouble(new BoundingBox(85, 180, -85, -180));
        mapView.setScrollableAreaLimitLatitude(85, -85, 0);
        mapView.setMaxZoomLevel(20.0);
        mapView.setMinZoomLevel(4.0);

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
                defaultGeoPoint = defaultStartPoint;
            }
            mapOptionsViewModel.setFacilityAddress(addressFacility);
            Log.i("getAddressFacilityTest", "address Facility: " + mapOptionsViewModel.getFacilityAddress().getValue()); //log facility address

            //create the starting point and set mapView to that location
            createStartGeoPosition(mapController, mapOptionsViewModel.getFacilityAddress().getValue(), defaultGeoPoint);
        });

        checkAddressFacilityChangedViewModel(mapController, defaultGeoPoint); //create new facility location if the address changes
        radiusOutlineViewModel(mapController);
        changeRadiusOutlineLocationViewModel(mapController);

        //Get the list of entrant locations
        getEntrantLocationDataList(locationListFetched -> {
            if (locationListFetched != null) {
                mapOptionsViewModel.setEntrantLocationDataList(locationListFetched);
                Log.i("GetLocationDataListTest", "onCreateView: Entrants location data in list");
                createEntrantLocationMarkers();

            } else {
                Log.i("GetLocationDataListTest", "onCreateView: No entrants location data in list");
            }
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

        ImageButton mapRefreshButton = view.findViewById(R.id.map_refresh_button);
        mapRefreshButton.setOnClickListener(v -> refreshFragment());

        return view;
    }

    /**
     * Close the fragment and return to the Created Activities view
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * Take the facility address and geocode it to get lat-long coordinates to make a GeoPoint
     * Set the GeoPoint to a default starting position if geocoding fails
     * Create a Marker for the geocoded facility address and display it on the mapView
     * center the mapView on the new facility point when it changes
     * @param mapController the mapView map controller
     * @param address the address of the facility
     * @param defaultGeoPoint the default location if the geocoding fails
     */
    private void createStartGeoPosition(IMapController mapController,String address, GeoPoint defaultGeoPoint){
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
                            //put start point in view model
                            mapOptionsViewModel.setFacilityPoint(startPoint); //will update whenever this method is ran
                            mapController.setCenter(startPoint);
                            facilityMarker = createFacilityMarker("Your Facility", startPoint);
                        }
                    });
                } else { //can't geocode the address, use default start point
                    handler.post(() -> {
                        mapOptionsViewModel.setFacilityPoint(defaultGeoPoint);
                        mapController.setCenter(defaultGeoPoint);
                        facilityMarker = createFacilityMarker("Default Location", defaultGeoPoint);
                    });
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Set up and create a scale bar for the mapView
     */
    private void setMapScaleBarOverlay(){
        //setting up scale bar attributes
        mapScaleBarOverlay = new ScaleBarOverlay(mapView);
        mapScaleBarOverlay.setAlignRight(true);
        mapScaleBarOverlay.setScaleBarOffset(350,50);
        mapScaleBarOverlay.setTextSize(40);
        mapView.getOverlays().add(mapScaleBarOverlay);
    }

    /**
     * Create a Marker object for a facility
     * @param name - name/title of the facility marker
     * @param geoPoint - GeoPoint containing the location for the marker
     * @return returns a Marker object for a facility
     */
    private Marker createFacilityMarker(String name, GeoPoint geoPoint){
        org.osmdroid.views.overlay.Marker marker = new Marker(mapView);

        //styles for facility location marker
        Drawable markerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.map_marker_star_outline);
        assert markerIcon != null;

        Drawable facilityMarker = DrawableCompat.wrap(markerIcon);
        DrawableCompat.setTint(facilityMarker, ContextCompat.getColor(requireContext(), R.color.facility_marker));

        marker.setPosition(geoPoint);
        marker.setTitle(name);
        marker.setIcon(facilityMarker);

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
        return marker;
    }

    /**
     * Create a Marker object for an entrant
     * @param name Entrant name - used as the Marker Title
     * @param geoPoint GeoPoint of Entrant - the coordinates for the Marker location
     * @return returns a Marker object for a specific Entrant
     */
    private Marker createEntrantMarker(String name, GeoPoint geoPoint){
        org.osmdroid.views.overlay.Marker marker = new Marker(mapView);

        //styles for entrant location marker
        Drawable markerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.map_marker);
        Drawable entrantMarker = DrawableCompat.wrap(markerIcon);
        DrawableCompat.setTint(entrantMarker, ContextCompat.getColor(requireContext(), R.color.entrant_marker));

        marker.setPosition(geoPoint);
        marker.setTitle(name);
        marker.setIcon(entrantMarker);

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        return marker;
    }

    /**
     * Create mapView Markers for every Entrant in the event, using EntrantLocationData
     * stored in a list the mapOptionsViewModel.
     * Create Geomarkers from the lat-long data in the EntrantLocationData
     * Get entrantID from the EntrantLocationData
     * Display each entrant marker on the mapView
     */
    private void createEntrantLocationMarkers(){
        List<EntrantLocationData> entrantLocationDataList = mapOptionsViewModel.getEntrantLocationDataList().getValue();

        assert entrantLocationDataList != null;
        //for every location data object in the list, get the entrant name and create a location maker
        for (EntrantLocationData entrantLocationData : entrantLocationDataList){
            //get the entrant name for each location
            String entrantID = entrantLocationData.getEntrantID();
            double entrantLatitude = entrantLocationData.getEntrantLatitude();
            double entrantLongitude = entrantLocationData.getEntrantLongitude();
            GeoPoint entrantGeoPoint = new GeoPoint(entrantLatitude, entrantLongitude);

            //for every name, create a marker
            getEntrantUserName(entrantID, entrantNameFetched -> {
                if (entrantNameFetched != null){
                    Marker entrantMarker = new Marker(mapView);
                    entrantMarker = createEntrantMarker(entrantNameFetched, entrantGeoPoint);
                    mapView.getOverlays().add(entrantMarker);
                    mapOptionsViewModel.addToEntrantMarkers(entrantMarker);
                    mapView.invalidate();
                    Log.i("CreateEntrantMarker", "createEntrantLocationMarkers: " + entrantMarker);

                } else {
                    Log.i("Create Entrant GeoMarkers", "createEntrantLocationMarkers: no entrant marker created");
                }
            });

        }
    }

    /**
     * Callback interface to handle data fetched through an asynchronous method
     */
    private interface EntrantNameCallBack{
        void onEntrantNameFetched(String entrantName);
    }

    /**
     * Fetch the entrant name from the database based on their androidID
     * @param entrantID the entrantID of an entrant user class
     * @param entrantNameCallBack callback interface to handle asynchronous data fetch
     */
    private void getEntrantUserName(String entrantID, EntrantNameCallBack entrantNameCallBack){
        UsersDB entrantDB = new UsersDB();

        entrantDB.getUser(entrantID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null){
                    User entrantUser = user;
                    String entrantName = entrantUser.getUserName();
                    Log.i("Fetch Entrant Name", "onSuccess: entrant name fetched: " + entrantName);
                    entrantNameCallBack.onEntrantNameFetched(entrantName);
                } else {Log.e("Fetch Entrant Name", "getEntrantUserName: error fetching entrant name");}
            }
        }, e -> {
                    Log.e("Fetch Entrant Name", "getEntrantUserName: error fetching entrant name", e);
                    entrantNameCallBack.onEntrantNameFetched(null);
                });
    }

    /**
     * Resume mapView
     */
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    /**
     * Pause mapView
     */
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    /**
     * Refresh the fragment and reset the radius, rangeLists, entrantMarkerList's in the mapOptionsViewModel
     */
    private void refreshFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventID);
        OrganizerViewMapFragment refreshOrganizerViewMapFragment = new OrganizerViewMapFragment();
        refreshOrganizerViewMapFragment.setArguments(bundle);
        resetRangeLists();
        mapOptionsViewModel.clearRadius();
        mapOptionsViewModel.clearEntrantMarkersList();

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        getParentFragmentManager().popBackStackImmediate();

        fragmentTransaction.replace(R.id.fragment_frame, refreshOrganizerViewMapFragment);
        fragmentTransaction.addToBackStack(null); //prevent double stacking of fragments that cause pop backStack issues
        fragmentTransaction.commit();
        mapView.invalidate();
    }

    /**
     * Callback Interface for Address fetched from the Database --> since fetching from the DB is asynchronous
     */
    private interface AddressCallback{
        void onAddressFetched(String address);
    }

    /**
     * Callback Interface for entrantLocationDataList fetched from database to handle asynchronous method
     */
    private interface EntrantLocationDataListCallback{
        void onEntrantLocationDataListFetched(List<EntrantLocationData> entrantLocationDataList);
    }

    //get organizer facility address from database

    /**
     * Get the organizer facility address from the database
     * @param addressCallback callback function to deal with asynchronous method for fetching address
     */
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

    /**
     * Get the Facility Name from the User Database & Set the value in the mapOptionsViewModel
     */
    private void getFacilityName(){
        userDB.getUser(androidId, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User newUser) {
                user = newUser;
                String facilityName = newUser.getUserFacility();
                Log.i("FETCH FACILITY NAME", "facility name: " + facilityName);

                mapOptionsViewModel.setFacilityName(facilityName);

            }
        }, e -> {Log.e("getFacilityAddress", "No matching document found or task failed", e);
        });
    }

    /**
     * mapOptionsViewModel observer to see if the facility address changes
     * If the facility address changed: creates a new facility GeoPoint on the mapView and removes the previous one
     * @param mapController the mapView mapController
     * @param defaultGeoPoint the default GeoPoint if geocoding fails
     */
   private void checkAddressFacilityChangedViewModel(IMapController mapController, GeoPoint defaultGeoPoint){
       //have view model listen for any changes in the facility address and update map marker
       mapOptionsViewModel.getFacilityAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
           @Override
           public void onChanged(String s) {
               Log.i("Map View Options", "Facility Address: " + mapOptionsViewModel.getFacilityAddress().getValue());
               addressFacility = s;

               if (facilityMarker != null){
                   mapView.getOverlays().remove(facilityMarker);
                   createStartGeoPosition(mapController, addressFacility, defaultGeoPoint);
                   mapView.invalidate();
               }
           }
       });
   }

    /**
     * Gets the entrant location data List from the database
     * set the list values into the mapOptionsViewModel for later integration
     * @param entrantLocationDataListCallback callback interface to handle asynchronous data fetch
     */
    private void getEntrantLocationDataList(EntrantLocationDataListCallback entrantLocationDataListCallback){

        eventsDB.loadEvent(eventID, loadedEvent -> {
            if (loadedEvent != null){
                event = loadedEvent;
                List<EntrantLocationData> entrantLocationDataList = event.getEntrantLocationDataList();
                //mapOptionsViewModel.setEntrantLocationDataList(entrantLocationDataList);
                Log.i("Fetch Entrant Location Data List", "onSuccess: Entrant location data list fetched " + entrantLocationDataList);

                entrantLocationDataListCallback.onEntrantLocationDataListFetched(entrantLocationDataList);
            } else {
                Log.e("Fetch Entrant Location Data List", "getEntrantLocationDataList: error getting entrant location data list");
                entrantLocationDataListCallback.onEntrantLocationDataListFetched(null);
            }

            if (!event.getGeolocationRequired()){
                noEventGeolocationTextView.setVisibility(View.VISIBLE);
            } else {noEventGeolocationTextView.setVisibility(View.GONE);}
        });
    }

    /**
     * A mapOptionsViewModel observer that looks if the the radius value changes
     * if the radius value changes: draw a circle polygon on the map
     * @param mapController the mapController
     */
    private void radiusOutlineViewModel(IMapController mapController){
     //Radius changes in the map options fragment, draw the radius on the mapView
     mapOptionsViewModel.getRadius().observe(getViewLifecycleOwner(), new Observer<Double>() {
         @Override
         public void onChanged(Double radiusKm) {
             GeoPoint centerPoint = mapOptionsViewModel.getFacilityPoint().getValue();
             resetRangeLists(); //If range changes --> reset the lists to prevent duplication

             Log.i("MapViewModel Radius Observer", "onChanged: Radius: " + radiusKm);
             Log.i("MapViewModel Radius Observer", "onChanged: Center point: " + centerPoint);

             if (radiusKm == null){ //if there's no value set, draw nothing
                 return;
             } else if (radiusKm == 0.0) { //don't draw anything/delete anything drawn
                 mapView.getOverlays().remove(definedRadiusPolygon);

             } else {
                 //draw the polygon & create the range list data
                 createCirclePolygon(radiusKm, centerPoint);
                 checkEntrantRangeStatus(radiusKm, centerPoint);
                 Log.i("Map View", "onChanged: radius used for polygon: " + radiusKm);
             }
         }
     });
    }

    /**
     * A mapOptionsViewModel observer that looks if the the facility location changes
     * Moves the circle polygon to new facility location if the facility location changes
     * @param mapController the map controller
     */
    private void changeRadiusOutlineLocationViewModel(IMapController mapController){
        //check to see if the facility point changed
        mapOptionsViewModel.getFacilityPoint().observe(getViewLifecycleOwner(), new Observer<GeoPoint>() {
            @Override
            public void onChanged(GeoPoint geoPoint) {
                radiusOutlineViewModel(mapController);
            }
        });

    }

    /**
     * Create a circle polygon and display it on the mapView at a specified coordinate
     * @param radiusKm the radius in kilometers
     * @param centerPoint the center GeoPoint for the circle polygon
     */
    //Referenced: https://github.com/osmdroid/osmdroid/blob/master/osmdroid-android/src/main/java/org/osmdroid/views/overlay/Polygon.java, Accessed 2024-11-29
    private void createCirclePolygon(Double radiusKm, GeoPoint centerPoint){
        double radiusMeterValue = radiusKm * 1000; //change km to meters
        ArrayList<GeoPoint> circlePoints = pointsAsCircle(centerPoint, radiusMeterValue); //create an array of geopoints in a circle around the facility
        mapView.getOverlays().remove(definedRadiusPolygon); //to prevent overlap of the same polygon
        definedRadiusPolygon.setPoints(circlePoints);

        //define style of polygon
        Paint polygonFill = new Paint();
        polygonFill.setARGB(50,225,0,0);
        polygonFill.setStyle(Paint.Style.FILL);

        Paint polygonOutline = new Paint();
        polygonOutline.setColor(ContextCompat.getColor(requireContext(), R.color.facility_marker));
        polygonOutline.setStyle(Paint.Style.STROKE);
        polygonOutline.setStrokeWidth(2);

        definedRadiusPolygon.getFillPaint().setARGB(30,225,0,0);
        definedRadiusPolygon.getOutlinePaint().setColor(ContextCompat.getColor(requireContext(), R.color.facility_marker));
        definedRadiusPolygon.getOutlinePaint().setStrokeWidth(2);
        mapView.getOverlays().add(definedRadiusPolygon);
        mapView.invalidate();

    }

    /**
     * Compute if an entrant marker is within a specified range of another GeoPoint
     * change entrant marker colour based on being in range or out of range
     * and add markers to mapOptionViewModel lists based on being in range or out of range
     * @param radiusKm radius in kilometers
     * @param comparePoint the GeoPoint to compare the entrant location to
     */
    //Reference: https://github.com/osmdroid/osmdroid/blob/master/osmdroid-android/src/main/java/org/osmdroid/util/GeoPoint.java, Accessed: 2024-11-30
    private void checkEntrantRangeStatus(Double radiusKm, GeoPoint comparePoint){
        List<Marker> entrantMarkersList = mapOptionsViewModel.getEntrantMarkersList().getValue();
        double rangeMeterValue = radiusKm * 1000;

        assert entrantMarkersList != null;
        for (Marker entrantMarker : entrantMarkersList){
            assert entrantMarker != null;
            GeoPoint entrantGeoPoint = entrantMarker.getPosition();
            double distance;
            distance = entrantGeoPoint.distanceToAsDouble(comparePoint); //get the distance between the two geopoints in meters

            if (distance <= rangeMeterValue){ //distance lower than the range value, it is in range
                Log.i("EntrantRange", "checkEntrantRangeStatus: is within range" + entrantMarker.getTitle());

                //change the marker colour
                Drawable markerIcon = entrantMarker.getIcon();
                if (markerIcon != null){
                    Drawable markerNewColor = DrawableCompat.wrap(markerIcon);
                    DrawableCompat.setTint(markerNewColor, ContextCompat.getColor(requireContext(), R.color.in_range_marker));

                    entrantMarker.setIcon(markerNewColor);
                    mapView.invalidate();
                    //add to the in range list
                    mapOptionsViewModel.addToEntrantsInRangeList(entrantMarker);
                }

                Log.i("IN RANGE LIST", "checkEntrantRangeStatus: Range list markers: " + mapOptionsViewModel.getEntrantsInRangeList().getValue().toString());
            }
            else { //entrant not in range, add them to the not in range list
                Log.i("EntrantRange", "checkEntrantRangeStatus: is NOT within range" + entrantMarker.getTitle());

                //change the marker colour
                Drawable markerIcon = entrantMarker.getIcon();
                if (markerIcon != null){
                    Drawable markerNewColor = DrawableCompat.wrap(markerIcon);
                    DrawableCompat.setTint(markerNewColor, ContextCompat.getColor(requireContext(), R.color.out_of_range_marker));

                    entrantMarker.setIcon(markerNewColor);
                    mapView.invalidate();
                    mapOptionsViewModel.addToEntrantsOutOfRangeList(entrantMarker);
                }

                Log.i("OUT OF RANGE LIST", "checkEntrantRangeStatus: Range list markers: " + mapOptionsViewModel.getEntrantsOutOfRangeList().getValue().toString());
            }

        }
    }

    /**
     * Set the range lists in the mapOptionsViewModel to a new ArrayList<>()
     * to clear data and prevent duplicate values
     */
    private void resetRangeLists(){
        mapOptionsViewModel.clearEntrantsInRangeList();
        mapOptionsViewModel.clearEntrantsOutOfRangeList();
    }
}