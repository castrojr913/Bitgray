package com.jacr.photoapp.model.api.managers;

import com.jacr.photoapp.model.DataSource;
import com.jacr.photoapp.model.ModelError;
import com.jacr.photoapp.model.api.dtos.LocationDto;
import com.jacr.photoapp.model.listeners.CoordinatesManagerListener;
import com.jacr.photoapp.model.listeners.ManagerListener;
import com.jacr.photoapp.utilities.LogHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * CoordinatesManager
 * Created by Jesus Castro on 13/11/2015.
 */
public class CoordinatesManager extends ApiManager {

    private static CoordinatesManager singleton;

    //<editor-fold desc="Singleton">

    private CoordinatesManager() {
        // Blank
    }

    public static CoordinatesManager getInstance() {
        if (singleton == null) {
            singleton = new CoordinatesManager();
        }
        return singleton;
    }

    //</editor-fold>

    //<editor-fold desc="Manager Overrides">

    @Override
    protected Class<?> getLogTag() {
        return CoordinatesManager.class;
    }

    @Override
    protected void manageResponse(String url, byte[] response, ManagerListener listener) {
        try {
            if (url.equals(DataSource.GoogleApi.API_GEOCODE) && listener instanceof CoordinatesManagerListener) {
                LocationDto location = gson.fromJson(new String(response), LocationDto.class);
                ((CoordinatesManagerListener) listener).onLocation(location.getLocationDetails());
            } else {
                listener.onError(ModelError.WEBSERVICE_FAILURE);
            }
        } catch (Exception e) {
            LogHelper.getInstance().exception(getLogTag(), e, e.toString());
            listener.onError(ModelError.WEBSERVICE_FAILURE);
        }
    }

    //</editor-fold>

    public void searchPlaceFromLocation(final double latitude, final double longitude,
                                        CoordinatesManagerListener listener) {
        String url = DataSource.GoogleApi.API_GEOCODE;
        Map<String, String> parameters = new HashMap<String, String>() {{
            put("latlng", String.format("%s,%s", latitude, longitude));
            put("key", DataSource.GoogleApi.API_GEOCODE_KEY);
        }};
        LogHelper.getInstance().debugRequest(getLogTag(), url, parameters.toString());
        HttpClient.getInstance().get(url, parameters, createHttpCallback(url, listener));
    }

}
