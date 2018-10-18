package com.cxb.myfamilytree.app;

import android.app.Application;

import com.cxb.myfamilytree.config.Constants;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * application
 */

public class APP extends Application {

    private static APP mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_APP_ID, false);
    }

    public static APP get() {
        return mApp;
    }

}
