package com.ruoyi.util;

import com.ruoyi.global.Config;

public class ResultUtil {

    public static void log(String str) {
        Config.resultText.appendText("[*] " + str + "\n");
    }

    public static void success(String str) {
        Config.resultText.appendText("[+] " + str + "\n");
    }

    public static void fail(String str) {
        Config.resultText.appendText("[-] " + str + "\n");
    }

    public static void clear(){
        Config.resultText.clear();
    }
}
