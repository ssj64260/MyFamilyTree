package com.cxb.myfamilytree.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * sd卡路径
 */
public class SDCardUtils {

    public static String getSDCardPublicDir(String type) {
        return Environment
                .getExternalStoragePublicDirectory(type)
                .getAbsolutePath();
    }

    //  /storage/emulated/0
    public static String getSDCardDir(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return getExternalFilesDir(context);
//        }
        return getSDCardPublicDir(Environment.DIRECTORY_DCIM);
    }

    //  /data/data/<application package>/cache
    public static String getCacheDir(Context context) {
        return context.getCacheDir().getPath();
    }

    //  /data/data/<application package>/files
    public static String getFilesDir(Context context) {
        return context.getFilesDir().getPath();
    }

    //  /data/data/<application package>/databases
    public static String getDataBaseDir(Context context, String dbName) {
        return context.getDatabasePath(dbName).getPath();
    }

    //  /storage/emulated/0/Android/data/你的应用包名/cache/（APP卸载后，数据会被删除）
    public static String getExternalCacheDir(Context context) {
        return context.getExternalCacheDir().getPath();
    }

    //  /storage/emulated/0/Android/data/你的应用的包名/files/（APP卸载后，数据会被删除）
    public static String getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(null).getPath();
    }

    //自动选择Flies路径，若SD卡存在并且不能移除则用SD卡存储
    public static String getAutoFilesPath(Context context) {
        String filesPath;
        if (ExistSDCard() && !SDCardRemovable()) {
            filesPath = getExternalFilesDir(context);
        } else {
            filesPath = getFilesDir(context);
        }
        return filesPath;
    }

    //自动选择Cache路径，若SD卡存在并且不能移除则用SD卡存储
    public static String getAutoCachePath(Context context) {
        String cachePath;
        if (ExistSDCard() && !SDCardRemovable()) {
            cachePath = getExternalCacheDir(context);
        } else {
            cachePath = getCacheDir(context);
        }
        return cachePath;
    }

    //检查SD卡是否存在
    public static boolean ExistSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    //检查SD卡是否能被移除
    public static boolean SDCardRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    //获取内部存储剩余空间
    public static long getFreeSpace() {
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    //获取内部存储可用空间
    public static long getUsableSpace() {
        return Environment.getExternalStorageDirectory().getUsableSpace();
    }

    //获取内部存储总空间
    public static long getTotalSpace() {
        return Environment.getExternalStorageDirectory().getTotalSpace();
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize;
                long availableBlocks;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    blockSize = stat.getBlockSizeLong();
                    availableBlocks = stat.getAvailableBlocksLong();
                } else {
                    blockSize = stat.getBlockSize();
                    availableBlocks = stat.getAvailableBlocks();
                }

                freeSpace = availableBlocks * blockSize;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }

}
