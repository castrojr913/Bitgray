package com.jacr.photoapp.controllers;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import com.jacr.photoapp.R;
import com.jacr.photoapp.controllers.gallery.GalleryFragment_;
import com.jacr.photoapp.controllers.photos.PhotosFragment_;
import com.jacr.photoapp.controllers.users.UsersFragment_;
import com.jacr.photoapp.utilities.ViewHelper;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;

/**
 * DrawerActivity
 * Created by Jesus Castro on 12/11/2015.
 */
@EActivity(R.layout.activity_drawer)
public class DrawerActivity extends AppCompatActivity {

    //<editor-fold desc="Constants & Variables">

    private static final int DRAWER_ITEM_USERS = 1;
    private static final int DRAWER_ITEM_PHOTOS = 2;
    private static final int DRAWER_ITEM_GALLERY = 3;

    private String earlyFragmentTag;
    private Fragment fragmentToLoad;

    //</editor-fold>

    //<editor-fold desc="View Instances">

    @ViewById(R.id.activity_drawer_toolbar)
    Toolbar toolbar;

    //</editor-fold>

    //<editor-fold desc="String Resources">

    @StringRes(R.string.activity_drawer_item_users)
    String usersDrawerItem;

    @StringRes(R.string.activity_drawer_item_photos)
    String photosDrawerItem;

    @StringRes(R.string.activity_drawer_item_gallery)
    String galleryDrawerItem;

    //</editor-fold>

    //<editor-fold desc="Drawable & Color Resources">

    @DrawableRes(R.drawable.drawer_header_background)
    Drawable headerBackground;

    @DrawableRes(R.drawable.ic_launcher)
    Drawable headerIcon;

    @ColorRes(R.color.app_drawer_text_selected)
    int drawerSelectedText;

    //</editor-fold>

    @AfterViews
    void init() {
        // Setting up about Action Bar
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        // Build and set listeners for drawer items
        setupDrawer();
    }

    //<editor-fold desc="Drawer Management">

    private void setupDrawer() {
        // Drawer Header
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(headerBackground)
                .withSelectionListEnabled(false)
                .addProfiles(new ProfileDrawerItem().withIcon(headerIcon))
                .build();
        // Creating Items for Drawer
        ArrayList<IDrawerItem> drawerItems = new ArrayList<>();
        drawerItems.add(new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_USERS)
                .withName(usersDrawerItem)
                .withTag(usersDrawerItem)
                .withSelectedTextColor(drawerSelectedText));
        drawerItems.add(new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_PHOTOS)
                .withName(photosDrawerItem)
                .withTag(photosDrawerItem)
                .withSelectedTextColor(drawerSelectedText));
        drawerItems.add(new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_GALLERY)
                .withName(galleryDrawerItem)
                .withTag(galleryDrawerItem)
                .withSelectedTextColor(drawerSelectedText));
        // Building Drawer
        final Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .withDrawerItems(drawerItems)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        loadFragmentAsToDrawerItem(drawerItem);
                        return false;
                    }

                }).build();
        // Load the default view with respect to the drawer. Thus, we add an observer that notifies
        // when the drawer is rendered
        drawer.getContent().getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        drawer.setSelection(DRAWER_ITEM_USERS);
                        // Remove observer
                        ViewHelper.removeGlobalLayoutListener(drawer.getContent().getViewTreeObserver(), this);
                    }

                });
    }

    private void loadFragmentAsToDrawerItem(IDrawerItem drawerItem) {
        String tag = drawerItem.getTag().toString();
        setTitle(tag); // Toolbar title
        if (earlyFragmentTag != null && !tag.contentEquals(earlyFragmentTag)) {
            Fragment fragment = getFragmentManager().findFragmentByTag(earlyFragmentTag);
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        earlyFragmentTag = tag;
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_drawer_view_fragment, getFragmentAsToDrawerItem(drawerItem), tag)
                .commit();
    }

    private Fragment getFragmentAsToDrawerItem(IDrawerItem drawerItem) {
        switch (drawerItem.getIdentifier()) {
            case DRAWER_ITEM_USERS:
                fragmentToLoad = new UsersFragment_();
                break;
            case DRAWER_ITEM_PHOTOS:
                fragmentToLoad = new PhotosFragment_();
                break;
            case DRAWER_ITEM_GALLERY:
                fragmentToLoad = new GalleryFragment_();
                break;
            default:
                fragmentToLoad = new Fragment();
        }
        return fragmentToLoad;
    }

    //</editor-fold>

}

