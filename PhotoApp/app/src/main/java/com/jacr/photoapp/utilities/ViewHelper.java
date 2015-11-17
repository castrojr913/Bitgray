package com.jacr.photoapp.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * ViewHelper
 * Created by Jesus Castro on 12/11/2015.
 */
public class ViewHelper {

    public static void removeGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener
            listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            observer.removeOnGlobalLayoutListener(listener);
        } else {
            //noinspection deprecation
            observer.removeGlobalOnLayoutListener(listener);
        }
    }

    public static void setEmptyViewForList(ListView listView, View emptyView) {
        // http://stackoverflow.com/questions/3727063/call-to-listviews-setempty-method-not-working-nothing-is-displayed-when-the-li?rq=1
        ViewGroup parentGroup = (ViewGroup) listView.getParent();
        parentGroup.addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    public static void loadPicture(Context context, ImageView imageView, String picturePath) {
        // If there is a java.lang.memoryError loading the bitmap, then it loads the image which
        // is assumed as an error
        Picasso.with(context).load(Uri.fromFile(new File(picturePath)))
                .error(android.R.drawable.ic_dialog_alert)
                .into(imageView);
    }

}

