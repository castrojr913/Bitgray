package com.jacr.photoapp.model.api.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * LocationDto
 * Created by Jesus Castro on 13/11/2015.
 */
public class LocationDto {

    @SerializedName("results")
    private List<LocationDetailsDto> locationDetails;

    public List<LocationDetailsDto> getLocationDetails() {
        return locationDetails;
    }

}
