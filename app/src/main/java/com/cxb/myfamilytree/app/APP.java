package com.cxb.myfamilytree.app;

import android.app.Application;

import com.cxb.myfamilytree.R;
import com.orhanobut.logger.Logger;
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
        Logger.init(getString(R.string.app_name));
    }

    public static APP get(){
        return mApp;
    }

}
