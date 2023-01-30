package com.personal.networkchat.client;

import com.personal.networkchat.client.controllers.ChatController;
import com.personal.networkchat.client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientApp extends Application {

    //Класс сообщения
    //* отправитель
    //* дата отправки
    //* текст сообщения
    //
    //Список<Класс сообщения>

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/com/personal/networkchat/chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("Network chat");
        stage.setScene(scene);
        stage.show();

        Network network = new Network();
        ChatController chatController = fxmlLoader.getController();

        chatController.setNetwork(network);

        network.connect();
        network.waitMessage(chatController);
    }

    public static void main(String[] args) {
        launch();
    }
}