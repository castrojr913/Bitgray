package com.jacr.photoapp.model;

/**
 * DataSource
 * Created by Jesus Castro on 14/11/2015.
 */
public class DataSource {

    public static class Database {

        public static final String NAME = "photoapp.db";
        public static final String ASSETS_PATH = "database/";
        public static final int SQLITE_VERSION = 3;

    }

    public static class WebApi {

        private static final String API_BASE = "http://jsonplaceholder.typicode.com/";
        public static final String USERS = API_BASE + "users";

    }

    public static class GoogleApi {

        public static final String API_GEOCODE_KEY = "AIzaSyAPuQYtRmTyoxfa5-H2HNIwENU5b76qY-I";
        public static final String API_GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json";

    }

}
