package com.cxb.myfamilytree.utils;

/**
 * 防止快速点击工具
 */
public class FastClick {

    private static long lastClickTime;
    private static long exitClickTime;

    //快速点击控制
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    //退出点击控制
    public synchronized static boolean isExitClick() {
        long time = System.currentTimeMillis();
        if ( time - exitClickTime < 2000) {
            return true;
        }
        exitClickTime = time;
        return false;
    }
}
