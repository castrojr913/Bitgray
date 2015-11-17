package com.jacr.photoapp.model.api.dtos;

import com.google.gson.annotations.SerializedName;

/**
 * LocationDetailsDto
 * Created by Jesus Castro on 13/11/2015.
 */
public class LocationDetailsDto {

    @SerializedName("formatted_address")
    private String place;

    public String getPlace() {
        return place;
    }

    @Override
    public String toString() {
        return place;
    }

}
