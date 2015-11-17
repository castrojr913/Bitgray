package com.jacr.photoapp.model.api.managers;

import com.google.gson.Gson;
import com.jacr.photoapp.model.ModelError;
import com.jacr.photoapp.model.listeners.ManagerListener;
import com.jacr.photoapp.utilities.LogHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * ApiManager
 * Created by Jesus Castro on 12/11/2015.
 */
abstract class ApiManager {

    final Gson gson = new Gson();

    //<editor-fold desc="Abstract Methods">

    protected abstract Class<?> getLogTag();

    protected abstract void manageResponse(String url, byte[] response, ManagerListener listener);

    //</editor-fold>

    final Callback createHttpCallback(final String url, final ManagerListener listener) {
        final Class<?> logTag = getLogTag();
        return new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                ModelError error;
                String exception = e.toString();
                if (exception.contains("ConnectException") || exception.contains("UnknownHostException")) {
                    error = ModelError.CONNECTIVITY_FAILURE;
                } else if (exception.contains("SocketTimeoutException")) {
                    error = ModelError.TIMEOUT_FAILURE;
                } else {
                    error = ModelError.WEBSERVICE_FAILURE;
                }
                LogHelper.getInstance().exception(logTag, e, exception);
                // Notifies the controller that there was an error
                listener.onError(error);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String requestType = response.request().method();
                byte[] responseStream = response.body().bytes();
                if (response.isSuccessful()) { // status code: [200, 300)
                    // Delegate the concrete manager how it should manage this successful response
                    LogHelper.getInstance().debugResponse(logTag, url, requestType, response.code(), responseStream);
                    manageResponse(url, responseStream, listener);
                } else {
                    LogHelper.getInstance().errorResponse(logTag, url, requestType, response.code(), responseStream);
                    // Notifies the controller that there was an error
                    listener.onError(ModelError.WEBSERVICE_FAILURE);
                }
            }

        };
    }

}
