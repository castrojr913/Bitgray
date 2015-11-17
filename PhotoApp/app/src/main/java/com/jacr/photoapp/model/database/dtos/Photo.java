package com.jacr.photoapp.model.database.dtos;

import com.jacr.photoapp.model.DateFormat;
import com.jacr.photoapp.utilities.DateHelper;

/**
 * Photo
 * Created by Jesus Castro on 13/11/2015.
 */
public class Photo implements Dto {

    @SQLField(name = "title")
    private String photoTitle;

    @SQLField(name = "location")
    private String location;

    @SQLField(name = "date")
    private String date;

    @SQLField(name = "filename")
    private String photoName;

    @Override
    public String getTableName() {
        return "photo";
    }

    // <editor-fold desc="Getter / setter">

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    // </editor-fold>

    public String getFormattedDate() {
        return DateHelper.dateToString(DateHelper.stringToDate(date, DateFormat.SQL_FORMAT),
                DateFormat.DATE_DESCRIPTION_FORMAT);
    }

}