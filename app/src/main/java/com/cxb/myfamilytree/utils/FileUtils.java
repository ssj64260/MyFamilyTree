package com.cxb.myfamilytree.utils;

import android.content.Context;
import android.text.format.Formatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件处理工具类
 */
public class FileUtils {

    public static boolean createdirectory(String path) {
        final File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    //获取文件大小
    public static long getFileSize(File f) throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis;
            fis = new FileInputStream(f);
            s = fis.available();
        }
        return s;
    }

    //获取文件夹大小
    public static long getDirSize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size += getDirSize(flist[i]);
            } else {
                size += flist[i].length();
            }
        }
        return size;
    }

    //转换文件大小单位(B/KB/MB/GB)
    public static String formatFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    //获取文件个数
    public static long getlist(File f) {// 递归求取目录文件个数
        long size = 0;
        File flist[] = f.listFiles();
        size = flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getlist(flist[i]);
                size--;
            }
        }
        return size;
    }

    public static File copyFile(File fromFile, File toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();

            return toFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File copyFile(String fromFilePath, String toDirPath) {
        File fromFile = new File(fromFilePath);
        File toFile = new File(toDirPath, fromFile.getName());
        return copyFile(fromFile, toFile);
    }

    public static File copyFile(String fromFilePath, String toDirPath, String fileName) {
        File fromFile = new File(fromFilePath);
        File toFile = new File(toDirPath, fileName);
        return copyFile(fromFile, toFile);
    }

    public static void copyFile(InputStream from, String toFilePath) {
        try {
            File file = new File(toFilePath);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = from.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            from.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName);
    }

}
