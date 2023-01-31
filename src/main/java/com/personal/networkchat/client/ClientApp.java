package com.personal.networkchat.client;

import com.personal.networkchat.client.controllers.AuthController;
import com.personal.networkchat.client.controllers.ChatController;
import com.personal.networkchat.client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    private Network network;
    private Stage primaryStage;
    private Stage authStage;

    //Класс сообщения
    //* отправитель
    //* дата отправки
    //* текст сообщения
    //
    //Список<Класс сообщения>

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

//        network = new Network();
//        network.connect();

        openAuthDialog();
//        createChatDialog();





//        network.waitMessage(chatController);
    }

    private void openAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader(ClientApp.class.getResource("/com/personal/networkchat/auth-view.fxml"));
        Scene authScene = new Scene(authLoader.load());
        authStage = new Stage();
        authStage.setScene(authScene);

        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);

        authStage.setResizable(false);
//        authStage.setAlwaysOnTop(true);
        authStage.centerOnScreen();
        authStage.setTitle("Network chat");
        authStage.show();

//        AuthController authController = authLoader.getController();
//        authController.setNetwork(network);
//        authController.setClientApp(this);
    }

    private void createChatDialog() throws IOException {
        FXMLLoader chatLoader = new FXMLLoader(ClientApp.class.getResource("/com/personal/networkchat/chat-view.fxml"));
        Scene chatScene = new Scene(chatLoader.load());
        primaryStage.setScene(chatScene);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Network chat");

        ChatController chatController = chatLoader.getController();
        chatController.setNetwork(network);
    }

    public static void main(String[] args) {
        launch();
    }
}