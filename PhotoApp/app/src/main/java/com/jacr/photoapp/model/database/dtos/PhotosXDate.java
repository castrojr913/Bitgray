package com.jacr.photoapp.model.database.dtos;

import com.jacr.photoapp.model.DateFormat;
import com.jacr.photoapp.utilities.DateHelper;

import java.util.List;

/**
 * PhotosXDate
 * Created by Jesus Castro on 15/11/2015.
 */
public class PhotosXDate {

    private String date;
    private List<Photo> photos;

    public PhotosXDate(String date, List<Photo> photos) {
        this.date = date;
        this.photos = photos;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getFormattedDate() {
        return DateHelper.dateToString(DateHelper.stringToDate(date, DateFormat.SQL_FORMAT),
                DateFormat.DATE_DESCRIPTION_FORMAT);
    }

}
