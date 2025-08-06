
package com.deals.app;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        
        // Enable Crashlytics collection
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }
}
