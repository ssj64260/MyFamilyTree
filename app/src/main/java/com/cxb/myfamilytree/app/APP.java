package com.cxb.myfamilytree.app;

import android.app.Application;

import com.cxb.myfamilytree.R;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * application
 */

public class APP extends Application {

    private static APP INStANCE;

    public APP() {
        INStANCE = this;
    }

    public static APP getInstance() {
        if (INStANCE == null) {
            synchronized (APP.class) {
                if (INStANCE == null) {
                    INStANCE = new APP();
                }
            }
        }
        return INStANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        LeakCanary.install(getInstance());//内存泄漏监听

        CrashReport.initCrashReport(getApplicationContext(), "e581318ce4", false);//Bugly

        Logger.init(getString(R.string.app_name));
    }
}
