package com.jacr.photoapp.controllers.users;


import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.jacr.photoapp.R;
import com.jacr.photoapp.model.ModelError;
import com.jacr.photoapp.model.api.dtos.UserDto;
import com.jacr.photoapp.model.api.managers.UserManager;
import com.jacr.photoapp.model.listeners.UserManagerListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

/**
 * UsersFragment
 * Created by Jesus Castro on 12/11/2015.
 */
@EFragment(R.layout.fragment_users)
public class UsersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //<editor-fold desc="Variables & Constants">

    private static final Class<?> LOG_TAG = UsersFragment.class;

    //</editor-fold>

    //<editor-fold desc="Views Instances">

    @ViewById(R.id.fragment_users_layout_refresh)
    SwipeRefreshLayout swipeToRefreshLayout;

    @ViewById(R.id.fragment_users_textview_user)
    TextView userTextView;

    @ViewById(R.id.fragment_users_view_progressbar)
    View progressBarView;

    //</editor-fold>

    //<editor-fold desc="String Resources">

    @StringRes(R.string.fragment_users_progressbar_text)
    String progressBarText;

    @StringRes(R.string.error_connectivity)
    String connectivityError;

    @StringRes(R.string.error_timeout)
    String timeoutError;

    @StringRes(R.string.error_webservice)
    String webserviceError;

    //</editor-fold>

    //<editor-fold desc="Color Resources">

    @ColorRes(R.color.app_user_text_error)
    int errorColor;

    @ColorRes(R.color.app_user_text_normal)
    int normalColor;

    //</editor-fold>

    @AfterViews
    void init() {
        // Setting up Pull to Refresh
        swipeToRefreshLayout.setOnRefreshListener(this);
        swipeToRefreshLayout.setColorSchemeResources(R.color.app_refresh_color_1, R.color.app_refresh_color_2);
        // Setting up Progress Bar
        TextView progressBarTextView = (TextView) progressBarView.findViewById(R.id.progressbar_text);
        progressBarTextView.setText(progressBarText);
        setProgressBarVisibility(true);
        // --
        loadUser();
    }

    @Override
    public void onDetach() {
        swipeToRefreshLayout.setRefreshing(false);
        super.onDetach();
    }

    //<editor-fold desc="Events Handling">

    @Override
    public void onRefresh() {
        loadUser();
    }

    //</editor-fold>

    private void loadUser() {
        UserManager.getInstance().getSomeUserRandomly(new UserManagerListener() {

            @Override
            public void onLoadUser(UserDto user) {
                printUserInformation(user);
            }

            @Override
            public void onError(ModelError error) {
                showErrorMessage(error);
            }

        });
    }

    //<editor-fold desc="Others">

    @UiThread
    void printUserInformation(UserDto user) {
        if (!isAdded() || !isVisible()) return;
        swipeToRefreshLayout.setRefreshing(false);
        setProgressBarVisibility(false);
        String text = getString(R.string.fragment_users_data_user,
                user.getName(), user.getUserName(), user.getEmail(), user.getPhone(),
                user.getCompanyName());
        userTextView.setText(text);
        userTextView.setTextColor(normalColor);
    }

    @UiThread
    void setProgressBarVisibility(boolean isVisible) {
        progressBarView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        userTextView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @UiThread
    void showErrorMessage(ModelError error) {
        if (!isAdded() || !isVisible()) return;
        swipeToRefreshLayout.setRefreshing(false);
        setProgressBarVisibility(false);
        String message = (error == ModelError.CONNECTIVITY_FAILURE ? connectivityError :
                (error == ModelError.TIMEOUT_FAILURE) ? timeoutError : webserviceError);
        userTextView.setText(message);
        userTextView.setTextColor(errorColor);
    }

    //</editor-fold>

}
