package com.cxb.myfamilytree.app;

import android.app.Application;

import com.cxb.myfamilytree.R;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
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

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(getString(R.string.app_name))
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    public static APP get(){
        return mApp;
    }

}
