package com.jacr.photoapp.model.api.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jacr.photoapp.model.DataSource;
import com.jacr.photoapp.model.ModelError;
import com.jacr.photoapp.model.api.dtos.UserDto;
import com.jacr.photoapp.model.listeners.ManagerListener;
import com.jacr.photoapp.model.listeners.UserManagerListener;
import com.jacr.photoapp.utilities.LogHelper;

import java.lang.reflect.Type;
import java.util.List;

/**
 * UserManager
 * Created by Jesus Castro on 12/11/2015.
 */
public class UserManager extends ApiManager {

    private static UserManager singleton;

    //<editor-fold desc="Singleton">

    private UserManager() {
        // Blank
    }

    public static UserManager getInstance() {
        if (singleton == null) {
            singleton = new UserManager();
        }
        return singleton;
    }

    //</editor-fold>

    //<editor-fold desc="Manager Overrides">

    @Override
    protected Class<?> getLogTag() {
        return UserManager.class;
    }

    @Override
    protected void manageResponse(String url, byte[] response, final ManagerListener listener) {
        try {
            if (url.equals(DataSource.WebApi.USERS) && listener instanceof UserManagerListener) {
                Type type = new TypeToken<List<UserDto>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                List<UserDto> users = gson.fromJson(new String(response), type);
                // Let's select an user randomly
                int index = (int) ((users.size() - 1) * Math.random());
                ((UserManagerListener) listener).onLoadUser(users.get(index));
            } else {
                listener.onError(ModelError.WEBSERVICE_FAILURE);
            }
        } catch (Exception e) {
            LogHelper.getInstance().exception(getLogTag(), e, e.toString());
            listener.onError(ModelError.WEBSERVICE_FAILURE);
        }
    }

    //</editor-fold>

    public void getSomeUserRandomly(UserManagerListener listener) {
        String url = DataSource.WebApi.USERS;
        LogHelper.getInstance().debugRequest(getLogTag(), url, null);
        HttpClient.getInstance().get(url, createHttpCallback(url, listener));
    }

}
