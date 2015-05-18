package com.uc.contactmanager.app;

import android.app.Application;

/**
 * Created by lvrh on 15/5/18.
 */
public class ContactApplication extends Application {
    private static  ContactApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static ContactApplication getInstance() {
        return mApp;
    }
}
