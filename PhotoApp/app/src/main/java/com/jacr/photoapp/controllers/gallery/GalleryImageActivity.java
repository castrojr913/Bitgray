package com.jacr.photoapp.controllers.gallery;

import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.jacr.photoapp.App;
import com.jacr.photoapp.R;
import com.jacr.photoapp.model.database.dtos.Photo;
import com.jacr.photoapp.utilities.ViewHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * GalleryImageActivity
 * Created by Jesus Castro on 16/11/2015.
 */
@EActivity(R.layout.activity_gallery_image)
public class GalleryImageActivity extends AppCompatActivity {

    // <editor-fold desc="View Instances">

    @ViewById(R.id.activity_gallery_image_title)
    TextView photoTitle;

    @ViewById(R.id.activity_gallery_image_location)
    TextView photoLocation;

    @ViewById(R.id.activity_gallery_image_date)
    TextView photoDate;

    @ViewById(R.id.activity_gallery_image_picture)
    ImageView photoImage;

    @ViewById(R.id.activity_gallery_image_button)
    ButtonRectangle dismissButton;

    //</editor-fold>

    @Extra("gallery_photo")
    Photo photoInfo;

    @AfterViews
    void init() {
        photoTitle.setText(getString(R.string.activity_gallery_image_textview_title,
                photoInfo.getPhotoTitle()));
        photoLocation.setText(getString(R.string.activity_gallery_image_textview_location,
                photoInfo.getLocation()));
        photoDate.setText(getString(R.string.activity_gallery_image_textview_date,
                photoInfo.getFormattedDate()));
        String photoDir = ((App) getApplicationContext()).getPhotosDirectory();
        ViewHelper.loadPicture(this, photoImage, photoDir + photoInfo.getPhotoName());
    }

    @Click(R.id.activity_gallery_image_button)
    void dismiss() {
        finish();
        System.gc();
    }

}