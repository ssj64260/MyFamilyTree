package com.cxb.myfamilytree.config;

import android.Manifest;

/**
 * 常量
 */

public class Config {
    public static final String TYPE_ADD_PARENT = "type_add_parent";//添加父母
    public static final String TYPE_ADD_SPOUSE = "type_add_spouse";//添加配偶
    public static final String TYPE_ADD_CHILD = "type_add_child";//添加子女
    public static final String TYPE_ADD_BROTHERS_AND_SISTERS = "type_add_brothers_and_sisters";//添加兄弟姐妹

    public static final String MY_ID = "601";//游客形式默认ID
    public static final String FAMILY_LIST_DATA_FILE_NAME = "family_tree.txt";//家庭成员列表文件名

    public static final int REQUEST_TO_SETTING = 0;//跳转到系统设置权限页面

    public static final String PERMISSION_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;//存储
}
