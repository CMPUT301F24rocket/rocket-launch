package com.example.rocket_launch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    MutableLiveData<Integer> radius = new MutableLiveData<>();

    public void setFacilityAddress(String address){facilityAddress.setValue(address);}
    public LiveData<String> getFacilityAddress(){return facilityAddress;}

    public void setFacilityName(String name){facilityName.setValue(name);}
    public LiveData<String> getFacilityName(){return facilityName;}

    public void setEntrantLocationDataList(List<EntrantLocationData> locationDataList){entrantLocationDataList.setValue(locationDataList);}
    public LiveData<List<EntrantLocationData>> getEntrantLocationDataList(){return entrantLocationDataList;}

    public void setRadius(Integer r) {radius.setValue(r);}
    public LiveData<Integer> getRadius(){return radius;}
}
