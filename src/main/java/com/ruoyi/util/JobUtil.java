package com.ruoyi.util;

import com.ruoyi.global.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobUtil {


    public final static String listParam = "pageSize=10&pageNum=1&orderByColumn=createTime&isAsc=desc&jobName=&jobGroup=&status=";
    public final static String createParam = "createBy=admin&jobName=1&jobGroup=DEFAULT&invokeTarget=1&cronExpression=1&misfirePolicy=1&concurrent=1&status=0&remark=";
    public final static String updateParam = "jobId={jobId}&updateBy=1&jobName=1&jobGroup=DEFAULT&invokeTarget={jonInvokeTarget}&cronExpression=0%2F10+*+*+*+*+%3F&misfirePolicy=1&concurrent=1&status=1&remark=";
    public final static String runParam = "jobId={jobId}";
    public final static String logListParam = "pageSize=1&pageNum=1&orderByColumn=createTime&isAsc=desc&jobName=&jobGroup=&status=&params%5BbeginTime%5D=&params%5BendTime%5D=";
    public final static String exceptionPatternStr = "\"exceptionInfo\":\"(.*?)\",";
    public static String currentInvokeTarget;

    public static int logCount = 0;


    public static List<String> getList() {
        List<String> jobList = new ArrayList<String>();
        String resp = Config.post(Config.jobListPath, listParam);
        Pattern jobPattern = Pattern.compile("\"jobId\":(.*?),");
        Matcher matcher = jobPattern.matcher(resp);
        while (matcher.find()) {
            jobList.add((matcher.group(1)));
        }
        return jobList;
    }

    public static void createJob() {
        Config.post(Config.jobAddPath, createParam);
    }


    public static boolean updateJob(String jobInvokeTarget) {
        String param = updateParam.replace("{jobId}", Config.jobId).replace("{jonInvokeTarget}", jobInvokeTarget);
        String resp = Config.post(Config.jobEditPath, param);
        currentInvokeTarget = jobInvokeTarget;
        return resp.contains("\"code\":0");
    }

    public static String runJob() {
        Config.post(Config.jobRunPath, runParam.replace("{jobId}", Config.jobId));
        return getLog();
    }

    // 直接返回日志，无需再次调用
    public static String expJob(String jobInvokeTarget) {
        if (updateJob(jobInvokeTarget)) {
            return runJob();
        } else {
            return "";
        }
    }

    public static String getLog() {
        // 递归获取日志信息，如无异常则返回""
        logCount++;
//        String currentExceptionPatternStr = exceptionPatternStr.replace("{currentInvokeTarget}", currentInvokeTarget.replace("\"", "\\\\\"").replace("(", "\\(").replace(")", "\\)"));
        Pattern exceptionPattern = Pattern.compile(exceptionPatternStr);
        String resp = Config.post(Config.jobLogListPath, logListParam);
        System.out.println(resp);
        Matcher matcher = exceptionPattern.matcher(resp);
        if (matcher.find()) {
            logCount = 0;
            clearLog();
            return matcher.group(1);
        } else {
            if (logCount > 10) {
                logCount = 0;
                clearLog();
                return "";
            }
            return getLog();
        }
    }

    public static void clearLog() {
        Config.post(Config.jobLogCleanPath, "");
    }

}
