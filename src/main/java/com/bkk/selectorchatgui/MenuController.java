package com.bkk.selectorchatgui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {


    public void LoadClientView(MouseEvent event) throws IOException {


        Button btn =  (Button) event.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 700);
        scene.getStylesheets().add
                (ClientApplication.class.getResource("client.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public void loadAdminView(MouseEvent event) throws IOException {

        Button btn =  (Button) event.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("admin-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 700);
        scene.getStylesheets().add
                (ClientApplication.class.getResource("client.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
