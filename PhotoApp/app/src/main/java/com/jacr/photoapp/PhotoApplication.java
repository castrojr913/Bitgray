package com.jacr.photoapp;

import android.app.Application;
import android.os.Environment;

import com.jacr.photoapp.utilities.LogHelper;

import java.io.File;

/**
 * PhotoApplication
 * Created by Jesus Castro on 12/11/2015.
 */
public class PhotoApplication extends Application implements App {

    /*
     * This class is executed once time. Therefore, we consider its instance as a Singleton.
     */

    private String photosDirectory;

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.getInstance().enableLogs();
        // Create directory for photos
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File photosDir = new File(appDir.getPath() + "/images");
        photosDirectory = !photosDir.exists() ? (photosDir.mkdirs() ?
                photosDir.getPath() + "/" : "") : photosDir.getPath() + "/";
    }

    @Override
    public String getPhotosDirectory() {
        return photosDirectory;
    }

}
