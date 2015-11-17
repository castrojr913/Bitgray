package com.jacr.photoapp.model.api.dtos;

import com.google.gson.annotations.SerializedName;

/**
 * UserDto
 * Created by Jesus Castro on 12/11/2015.
 */
public class UserDto {

    @SerializedName("username")
    private String userName;

    private String name;

    private String email;

    private String phone;

    private CompanyDto company;

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getCompanyName() {
        return company != null ? company.getName() : "";
    }

}
