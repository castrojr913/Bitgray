package com.jacr.photoapp.model.database.managers;

import android.content.Context;

import com.jacr.photoapp.model.DateFormat;
import com.jacr.photoapp.model.ModelError;
import com.jacr.photoapp.model.database.dtos.Photo;
import com.jacr.photoapp.model.database.dtos.PhotosXDate;
import com.jacr.photoapp.model.listeners.PhotoManagerListener;
import com.jacr.photoapp.utilities.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PhotoManager
 * Created by Jesus Castro on 13/11/2015.
 */
public class PhotoManager {

    private static PhotoManager singleton;
    private Context context;

    //<editor-fold desc="Singleton">

    private PhotoManager(Context context) {
        this.context = context;
    }

    public static PhotoManager getInstance(Context context) {
        if (singleton == null) {
            singleton = new PhotoManager(context);
        }
        return singleton;
    }

    //</editor-fold>

    public void savePhoto(final String title, final String location, final String fileName,
                          PhotoManagerListener listener) {
        Photo photo = new Photo();
        photo.setPhotoTitle(title);
        photo.setPhotoName(fileName);
        photo.setDate(DateHelper.dateToString(new Date(), DateFormat.SQL_FORMAT));
        photo.setLocation(location);
        if (DatabaseManager.getInstance(context).insert(photo)) {
            listener.onSuccess();
        } else {
            listener.onError(ModelError.SQL_ERROR);
        }
    }

    public List<PhotosXDate> getSavedPhotos() {
        List<Photo> listForDates = DatabaseManager.getInstance(context)
                .sendQuery("SELECT date FROM photo GROUP BY date", Photo.class);
        List<PhotosXDate> photosXDates = new ArrayList<>();
        for (Photo iterator : listForDates) {
            List<Photo> photos = DatabaseManager.getInstance(context).sendQuery(
                    String.format("SELECT * FROM photo WHERE date = '%s'", iterator.getDate()), Photo.class);
            photosXDates.add(new PhotosXDate(iterator.getDate(), photos));
        }
        return photosXDates;
    }

}
