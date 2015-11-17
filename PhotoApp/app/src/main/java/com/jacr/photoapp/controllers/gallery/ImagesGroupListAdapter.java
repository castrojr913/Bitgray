package com.jacr.photoapp.controllers.gallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.jacr.photoapp.R;
import com.jacr.photoapp.model.database.dtos.PhotosXDate;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * ImagesGroupListAdapter
 * Created by Jesus Castro on 15/11/2015.
 */
class ImagesGroupListAdapter extends BaseAdapter {

    private List<PhotosXDate> photos;
    private WeakReference<Activity> activity;

    public ImagesGroupListAdapter(Activity activity, List<PhotosXDate> photos) {
        this.activity = new WeakReference<>(activity);
        this.photos = photos;
    }

    // <editor-fold desc="Overrides">

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public PhotosXDate getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final PhotosXDate photosXDate = getItem(position);
        Activity activity_ = activity.get();
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflator = (LayoutInflater) activity_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.view_gallery_listview_item, parent, false);
            holder = new ViewHolder();
            holder.groupTitle = (TextView) convertView.findViewById(R.id.gallery_item_title);
            holder.imagesGrid = (GridView) convertView.findViewById(R.id.gallery_item_gridview);
            convertView.setTag(holder);
        }
        holder.groupTitle.setText(photosXDate.getFormattedDate());
        holder.imagesGrid.setAdapter(new ImagesGridAdapter(activity_, photosXDate.getPhotos()));
        return convertView;
    }

    // </editor-fold>

    static class ViewHolder {
        TextView groupTitle;
        GridView imagesGrid;
    }

}
