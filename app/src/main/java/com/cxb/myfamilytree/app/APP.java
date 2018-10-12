package com.cxb.myfamilytree.app;

import android.app.Application;

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

        CrashReport.initCrashReport(getApplicationContext(), "e581318ce4", false);
    }

    public static APP get(){
        return mApp;
    }

}
