package com.example.networkchat;

import com.example.networkchat.controllers.AuthController;
import com.example.networkchat.controllers.ChatController;
import com.example.networkchat.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class StartClient extends Application {

    private Network network;
    private Stage primaryStage;
    private Stage authStage;
    private ChatController chatController;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        network = new Network();
        network.connect();

        openAuthDialog();
        createChatDialog();
    }

    private void openAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader(StartClient.class.getResource("auth-view.fxml"));
        Scene scene = new Scene(authLoader.load());

        authStage = new Stage();
        authStage.setTitle("Аутентификация");
        authStage.setResizable(false);
        authStage.setScene(scene);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        authStage.show();

        AuthController authController = authLoader.getController();
        authController.setNetwork(network);
        authController.setStartClient(this);
    }

    private void createChatDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Чат");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        chatController = fxmlLoader.getController();
        chatController.setNetwork(network);
    }

    public static void main(String[] args) {
        launch();
    }

    public void openChatDialog() {
        authStage.close();
        chatController.setUserName(network.getUsername());
        primaryStage.show();
        network.waitMessage(chatController);
    }

    public void showErrorAlert(String title, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(errorMessage);
        alert.show();
    }
}