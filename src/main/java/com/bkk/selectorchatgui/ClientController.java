package com.bkk.selectorchatgui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.ConnectException;

public class ClientController {
    @FXML
    private TextArea terminal;
    @FXML
    private TextField subscribeTopic;
    @FXML
    private TextField unsubs;
    private MessageClient client;




    @FXML
    void initialize(){
        client = new MessageClient("localhost",12555);
        String message = "Connected to message broker!";
        try {
            client.connect();
        }catch (IOException exception) {
            message = "Could not connect, restart application!";

        }

        terminal.setText(message);
        terminal.setEditable(false);
    }

    public void subscribe(MouseEvent mouseEvent) {

        String cmd = "sub "+subscribeTopic.getText().trim();
        String back = "";
        try {
            client.sendData(cmd );
        }catch (IOException ex){
            back = "connection error, restart app";

        }
        appendToTerminal(back);
        subscribeTopic.clear();

    }

    public void showMessages(MouseEvent event){

        try {
            appendToTerminal(client.fetchData());
        }catch (IOException ex){

            appendToTerminal("connection error, restart app");
        }
    }

    public void showTopics(MouseEvent event){
        String cmd = "sh";
        String back = "";
        try {
            client.sendData(cmd );
        }catch (IOException ex){
            back = "connection error, restart app";
            appendToTerminal(back);
            return;
        }
        try {
            appendToTerminal(client.fetchData());
        }catch (IOException ex){

            appendToTerminal("connection error, restart app");
        }

    }

    public void unsubscribe(MouseEvent event){

        String cmd = "sub "+unsubs.getText().trim();
        String back = "";
        try {
            client.sendData(cmd );
        }catch (IOException ex){
            back = "connection error, restart app";

        }
        appendToTerminal(back);
        unsubs.clear();


    }

    public void appendToTerminal(String mess){
        StringBuilder builder = new StringBuilder();
        builder.append(terminal.getText()).append("\n").append(mess);
        terminal.setText(builder.toString());
    }

    public void clearTermminal(MouseEvent event) {
        terminal.clear();
    }
}