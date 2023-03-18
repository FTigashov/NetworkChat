package com.personal.networkchat.client;

import com.personal.networkchat.client.controllers.AuthController;
import com.personal.networkchat.client.controllers.ChatController;
import com.personal.networkchat.client.controllers.RegistrationController;
import com.personal.networkchat.client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ClientApp extends Application {
    private Network network;
    private Stage primaryStage;
    private Stage authStage;
    private Stage regStage;
    private ChatController chatController;

    //Класс сообщения
    //* отправитель
    //* дата отправки
    //* текст сообщения
    //
    //Список<Класс сообщения>

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        network = new Network();
        network.connect();

        openAuthDialog();
        createRegDialog();
        createChatDialog();
    }

    private void openAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader(ClientApp.class.getResource("/com/personal/networkchat/auth-view.fxml"));
        Scene authScene = new Scene(authLoader.load());
        authStage = new Stage();
        authStage.setScene(authScene);

        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);

        authStage.setResizable(false);
        authStage.centerOnScreen();
        authStage.setTitle("Network chat");
        authStage.show();

        AuthController authController = authLoader.getController();
        authController.setNetwork(network);
        authController.setClientApp(this);
    }

    private void createChatDialog() throws IOException {
        FXMLLoader chatLoader = new FXMLLoader(ClientApp.class.getResource("/com/personal/networkchat/chat-view.fxml"));
        Scene chatScene = new Scene(chatLoader.load());
        primaryStage.setScene(chatScene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Network chat");

        chatController = chatLoader.getController();
        chatController.setNetwork(network);
    }

    public void createRegDialog() throws IOException {
        FXMLLoader regLoader = new FXMLLoader(ClientApp.class.getResource("/com/personal/networkchat/reg-view.fxml"));
        Scene regScene = new Scene(regLoader.load());
        regStage = new Stage();
        regStage.setScene(regScene);

        regStage.initModality(Modality.WINDOW_MODAL);
        regStage.initOwner(primaryStage);

        regStage.setResizable(false);
        regStage.centerOnScreen();
        regStage.setTitle("Network chat");

        RegistrationController registrationController = regLoader.getController();
        registrationController.setNetwork(network);
        registrationController.setClientApp(this);
    }

    public static void main(String[] args) {
        launch();
    }

    public void openChatDialog(String userFullName, File userHistoryFileName) {
        authStage.close();
        regStage.close();
        chatController.setUserFullName(userFullName);
        chatController.setHistory(userHistoryFileName);
        network.waitMessage(chatController);
        primaryStage.show();
    }

    public void openRegDialog() {
        authStage.hide();
        regStage.show();
    }

    public void backToAuthDialog() {
        regStage.hide();
        authStage.show();
    }
}