package fr.insapp.insapp;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by thomas on 15/07/2017.
 */

public class App extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
