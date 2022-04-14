package com.ruoyi.global;


import com.ruoyi.util.RequestUtil;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {

    public static String url;
    public static String cookie;
    public static Boolean isConnected = false;
    public static String snakeyamlUrl = "";
    public static TextArea resultText;
    public static String jobId;
    public static List<String> vulMode = new ArrayList<String>();
    public static String uploadPath = "";
    public static File jarFile = null;


    public final static String jobListPath = "/monitor/job/list";
    public final static String jobAddPath = "/monitor/job/add";
    public final static String jobEditPath = "/monitor/job/edit";
    public final static String jobRunPath = "/monitor/job/run";
    public final static String jobLogListPath = "/monitor/jobLog/list";
    public final static String jobLogCleanPath = "/monitor/jobLog/clean";
    public final static String uploadUrlPath = "/common/upload";


    public static String get(String path) {
        return RequestUtil.get(url + path, cookie);
    }

    public static String post(String path, String param) {
        return RequestUtil.post(url + path, param, cookie);
    }

    public static String upload(String path, String filename, File file) {
        try {
            HashMap<String , InputStream> hashMap = new HashMap<>();
            hashMap.put(filename, new FileInputStream(file));
            return RequestUtil.upload(url + path, hashMap, cookie);
        } catch (Exception e){
            return "";
        }
    }

}
