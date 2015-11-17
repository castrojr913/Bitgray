package com.jacr.photoapp.model.listeners;

import com.jacr.photoapp.model.api.dtos.LocationDetailsDto;

import java.util.List;

/**
 * CoordinatesManagerListener
 * Created by Jesus Castro on 11/13/2015.
 */
public interface CoordinatesManagerListener extends ManagerListener {

    void onLocation(List<LocationDetailsDto> locationList);

}
