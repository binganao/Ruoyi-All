package com.ruoyi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("若依一键利用工具");
        primaryStage.setScene(new Scene(root, 900, 680));
        primaryStage.show();
        stage = primaryStage;
    }

    public static Stage getStage(){
        return stage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
