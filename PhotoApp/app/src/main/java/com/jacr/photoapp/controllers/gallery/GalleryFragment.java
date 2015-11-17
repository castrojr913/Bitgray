package com.jacr.photoapp.controllers.gallery;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jacr.photoapp.R;
import com.jacr.photoapp.model.database.managers.PhotoManager;
import com.jacr.photoapp.utilities.ViewHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;

/**
 * GalleryFragment
 * Created by Jesus Castro on 12/11/2015.
 */
@EFragment(R.layout.fragment_gallery)
public class GalleryFragment extends Fragment {

    //<editor-fold desc="Views Instances">

    @ViewById(R.id.fragment_gallery_listview)
    ListView imagesListView;

    private WeakReference<View> listEmptyView;

    //</editor-fold>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set empty view if there are no images to show
        View view = inflater.inflate(R.layout.view_gallery_listview_empty, container, false);
        listEmptyView = new WeakReference<>(view);
        return null;
    }

    @AfterViews
    void init() {
        Activity activity = getActivity();
        ViewHelper.setEmptyViewForList(imagesListView, listEmptyView.get());
        imagesListView.setAdapter(new ImagesGroupListAdapter(activity,
                PhotoManager.getInstance(activity).getSavedPhotos()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listEmptyView.clear();
        imagesListView = null;
        System.gc();
    }

}
