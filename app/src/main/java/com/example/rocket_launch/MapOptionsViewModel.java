package com.example.rocket_launch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that is used to sync changing data between the OrganizerViewMapFragment and OrganizerMapViewOptionsFragment
 * Author: Rachel
 */
//Referenced: https://developer.android.com/topic/libraries/architecture/viewmodel#java, Accessed 2024-11-27
public class MapOptionsViewModel extends ViewModel {
    MutableLiveData<String> facilityAddress =  new MutableLiveData<>();
    MutableLiveData<String> facilityName = new MutableLiveData<>();
    MutableLiveData<List<EntrantLocationData>> entrantLocationDataList = new MutableLiveData<>();
    MutableLiveData<Double> radius = new MutableLiveData<>();
    MutableLiveData<GeoPoint> facilityPoint = new MutableLiveData<>();
    MutableLiveData<MapView> mapView;

    MutableLiveData<List<Marker>> entrantMarkersList = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<List<Marker>> entrantsInRangeList = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<List<Marker>> entrantsOutOfRangeList = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<List<String>> inRangeNames = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<List<String>> outRangeNames = new MutableLiveData<>(new ArrayList<>());

    public void setFacilityAddress(String address){facilityAddress.setValue(address);}
    public LiveData<String> getFacilityAddress(){return facilityAddress;}

    public void setFacilityName(String name){facilityName.setValue(name);}
    public LiveData<String> getFacilityName(){return facilityName;}

    public void setEntrantLocationDataList(List<EntrantLocationData> locationDataList){entrantLocationDataList.setValue(locationDataList);}
    public LiveData<List<EntrantLocationData>> getEntrantLocationDataList(){return entrantLocationDataList;}

    public void setRadius(Double r) {radius.setValue(r);}
    public LiveData<Double> getRadius(){return radius;}
    public void clearRadius() {radius.setValue(null);}

    public void setFacilityPoint(GeoPoint geoPoint) {facilityPoint.setValue(geoPoint);}
    public LiveData<GeoPoint> getFacilityPoint(){return facilityPoint;}

    public void addToEntrantMarkers(Marker marker){ //add a marker to the list
        List<Marker> markerList = entrantMarkersList.getValue();
        if (markerList != null){
            markerList.add(marker);
            entrantMarkersList.setValue(markerList);
        }
    }
    public LiveData<List<Marker>> getEntrantMarkersList(){return entrantMarkersList;}
    public void clearEntrantMarkersList(){entrantMarkersList.setValue(new ArrayList<>());}

    public void addToEntrantsInRangeList(Marker marker){ //add an entrant location data object to the list
        List<Marker> dataList = entrantsInRangeList.getValue();
        if (dataList != null){
            dataList.add(marker);
            entrantsInRangeList.setValue(dataList);
        }
    }
    public LiveData<List<Marker>> getEntrantsInRangeList(){return entrantsInRangeList;}
    public void clearEntrantsInRangeList() {entrantsInRangeList.setValue(new ArrayList<>());}

    public void addToEntrantsOutOfRangeList(Marker marker){ //add an entrant location data object to the list
        List<Marker> dataList = entrantsOutOfRangeList.getValue();
        if (dataList != null){
            dataList.add(marker);
            entrantsOutOfRangeList.setValue(dataList);
        }
    }
    public LiveData<List<Marker>> getEntrantsOutOfRangeList(){return entrantsOutOfRangeList;}
    public void clearEntrantsOutOfRangeList() {entrantsOutOfRangeList.setValue(new ArrayList<>());}

    public void setInRangeNames(List<String> list) {inRangeNames.setValue(list);}
    public LiveData<List<String>> getInRangeNames() {return inRangeNames;}

    public void setOutRangeNames(List<String> list) {outRangeNames.setValue(list);}
    public LiveData<List<String>> getOutRangeNames() {return outRangeNames;}
}
