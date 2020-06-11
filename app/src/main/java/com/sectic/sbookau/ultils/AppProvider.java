package com.sectic.sbookau.ultils;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;
import com.sectic.sbookau.model.Catalog;

import java.util.ArrayList;

public class AppProvider extends Application {
    private static Context mContext;
    public static boolean mIsFirstAuth;
    public static ArrayList<Catalog> mLstCatItem;

    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Init
        /*
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        */
        mContext = getApplicationContext();
        mIsFirstAuth = false;
    }
    public static Context getContext() {
        return mContext;
    }
}