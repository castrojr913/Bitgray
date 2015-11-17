package com.jacr.photoapp.controllers.gallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jacr.photoapp.App;
import com.jacr.photoapp.R;
import com.jacr.photoapp.model.database.dtos.Photo;
import com.jacr.photoapp.utilities.ViewHelper;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * ImagesGridAdapter
 * Created by Jesus Castro on 15/11/2015.
 */
class ImagesGridAdapter extends BaseAdapter {

    private List<Photo> photos;
    private WeakReference<Activity> activity;

    public ImagesGridAdapter(Activity activity, List<Photo> photos) {
        this.activity = new WeakReference<>(activity);
        this.photos = photos;
    }

    // <editor-fold desc="Overrides">

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Photo getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Activity activity_ = activity.get();
        final Photo photo = getItem(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            LayoutInflater inflator = (LayoutInflater) activity_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.view_gallery_gridview, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_gridview_image);
            holder.image = new WeakReference<>(imageView);
            convertView.setTag(holder);
        }
        holder.image.get().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryImageActivity_.intent(activity_).extra("gallery_photo", photo).start();
            }
        });
        String photoPath = ((App) activity_.getApplicationContext()).getPhotosDirectory() + photo.getPhotoName();
        ViewHelper.loadPicture(activity_,holder.image.get(), photoPath);
        return convertView;
    }

    // </editor-fold>

    static class ViewHolder {
        WeakReference<ImageView> image;
    }

}
