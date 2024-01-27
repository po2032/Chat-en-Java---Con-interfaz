package com.clientechat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static ChatController chatController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("chat.fxml"));
        Parent root = fxmlLoader.load();
        chatController = fxmlLoader.getController();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    public static ChatController getChatController() {
        return chatController;
    }

    public static void main(String[] args) {
        launch();
    }
}
