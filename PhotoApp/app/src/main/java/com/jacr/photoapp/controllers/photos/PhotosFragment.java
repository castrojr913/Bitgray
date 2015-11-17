package com.jacr.photoapp.controllers.photos;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.jacr.photoapp.App;
import com.jacr.photoapp.R;
import com.jacr.photoapp.model.DateFormat;
import com.jacr.photoapp.model.ModelError;
import com.jacr.photoapp.model.api.dtos.LocationDetailsDto;
import com.jacr.photoapp.model.api.managers.CoordinatesManager;
import com.jacr.photoapp.model.database.managers.PhotoManager;
import com.jacr.photoapp.model.listeners.CoordinatesManagerListener;
import com.jacr.photoapp.model.listeners.PhotoManagerListener;
import com.jacr.photoapp.utilities.DateHelper;
import com.jacr.photoapp.utilities.StringHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.functions.Action1;

/**
 * PhotosFragment
 * Created by Jesus Castro on 12/11/2015.
 */
@EFragment(R.layout.fragment_photos)
public class PhotosFragment extends Fragment {

    //<editor-fold desc="Constants & Variables">

    private static final int REQUEST_TAKE_PHOTO = 1;

    private Activity parentActivity;
    private String photoLocation;
    private String photoFileName;
    private Uri photoUri;

    //</editor-fold>

    //<editor-fold desc="Views Instances">

    @ViewById(R.id.fragment_photos_imageview_thumbnail)
    ImageView thumbnail;

    @ViewById(R.id.fragment_photos_layout_form)
    LinearLayout photoFormLayout;

    @ViewById(R.id.fragment_photos_edittext_title)
    MaterialEditText photoTitleEditText;

    @ViewById(R.id.fragment_photos_textview_location)
    TextView locationTextview;

    @ViewById(R.id.fragment_photos_button_add)
    ButtonRectangle saveButton;

    @ViewById(R.id.fragment_photos_textview_take_photo)
    TextView photoMessageTextView;

    //</editor-fold>

    //<editor-fold desc="String Resources">

    @StringRes(R.string.fragment_photos_error_camera_disabled)
    String cameraError;

    @StringRes(R.string.fragment_photos_error_take_photo)
    String takePhotoError;

    @StringRes(R.string.fragment_photos_error_save_photo)
    String savePhotoError;

    @StringRes(R.string.fragment_photos_error_gps)
    String gpsError;

    @StringRes(R.string.fragment_photos_error_location)
    String locationError;

    @StringRes(R.string.fragment_photos_error_unavailable_location)
    String unavailableLocationError;

    @StringRes(R.string.fragment_photos_msg_search_location)
    String locationSearchMessage;

    @StringRes(R.string.fragment_photos_error_form_missed_data)
    String missedDataFormError;

    @StringRes(R.string.fragment_photos_msg_save_photo_success)
    String savePhotoFormSuccess;

    //</editor-fold>

    @AfterViews
    void init() {
        parentActivity = getActivity();
        showTapPhotoText(true);
    }

    //<editor-fold desc="Activity Overrides">

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            thumbnail.setImageURI(photoUri);
            showTapPhotoText(false); // Show edition form for photo
        }
    }

    //</editor-fold>

    //<editor-fold desc="About Location">

    @Click(R.id.fragment_photos_textview_location)
    void searchPlaceAsToCoordinates() {
        if (isGPSEnabled()) {
            photoLocation = "";
            locationTextview.setText(locationSearchMessage);
            ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(parentActivity);
            locationProvider.getLastKnownLocation()
                    .subscribe(new Action1<Location>() {
                        @Override
                        public void call(Location location) {
                            searchPlaceAsToCoordinates(location);
                        }
                    });
        } else {
            showMessage(gpsError);
        }
    }

    private void searchPlaceAsToCoordinates(Location location) {
        if (!isAdded() || !isVisible()) return;
        if (location != null) {
            CoordinatesManager.getInstance().searchPlaceFromLocation(
                    location.getLatitude(), location.getLongitude(),
                    new CoordinatesManagerListener() {

                        @Override
                        public void onLocation(List<LocationDetailsDto> locationList) {
                            loadPlacesOnLocation(locationList);
                        }

                        @Override
                        public void onError(ModelError error) {
                            showMessage(locationError);
                        }

                    });
        } else {
            showMessage(locationError);
        }
    }

    private boolean isGPSEnabled() {
        LocationManager lm = (LocationManager) parentActivity.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @UiThread
    void loadPlacesOnLocation(@NonNull List<LocationDetailsDto> locationList) {
        if (!isAdded() || !isVisible()) return;
        if (locationList.isEmpty()) {
            locationTextview.setText(unavailableLocationError);
        } else {
            photoLocation = locationList.get(0).getPlace();
            locationTextview.setText(photoLocation);
        }
    }

    // </editor-fold>

    // <editor-fold desc="About Photos">

    @Click(R.id.fragment_photos_button_add)
    public void savePhoto() {
        String photoTitle = photoTitleEditText.getText().toString();
        if (StringHelper.isEmpty(photoTitle) || StringHelper.isEmpty(photoLocation) ||
                StringHelper.isEmpty(photoFileName)) {
            showMessage(missedDataFormError);
            return;
        }
        PhotoManager.getInstance(parentActivity).savePhoto(photoTitle, photoLocation, photoFileName,
                new PhotoManagerListener() {

                    @Override
                    public void onSuccess() {
                        cleanTypedDataOfForm();
                        showMessage(savePhotoFormSuccess);
                        showTapPhotoText(true);
                    }

                    @Override
                    public void onError(ModelError error) {
                        showMessage(savePhotoError);
                    }

                });
    }

    @Click(R.id.fragment_photos_textview_take_photo)
    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(parentActivity.getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createPhotoFile();
                photoFileName = photoFile.getName();
                photoUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                showMessage(takePhotoError);
            }
        } else {
            showMessage(cameraError);
        }
    }

    private File createPhotoFile() throws IOException {
        String timeStamp = DateHelper.dateToString(new Date(), DateFormat.TIMESTAMP_FORMAT);
        String imageFileName = "JPEG_" + timeStamp;
        App app = ((App) parentActivity.getApplicationContext());
        return File.createTempFile(imageFileName, ".jpg", new File(app.getPhotosDirectory()));
    }

    @UiThread
    void showTapPhotoText(boolean isVisible) {
        photoMessageTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        photoFormLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @UiThread
    void cleanTypedDataOfForm() {
        photoTitleEditText.setText("");
        locationTextview.setText("");
    }

    // </editor-fold>

    @UiThread
    void showMessage(@NonNull String message) {
        if (!isAdded() || !isVisible()) return;
        Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
    }

}
