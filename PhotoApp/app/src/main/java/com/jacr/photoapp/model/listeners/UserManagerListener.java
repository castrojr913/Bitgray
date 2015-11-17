package com.jacr.photoapp.model.listeners;

import com.jacr.photoapp.model.api.dtos.UserDto;

/**
 * UserManagerListener
 * Created by Jesus on 12/11/2015.
 */
public interface UserManagerListener extends ManagerListener {

    void onLoadUser(UserDto user);

}
