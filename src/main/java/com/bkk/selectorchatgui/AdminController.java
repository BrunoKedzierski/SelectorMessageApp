package com.bkk.selectorchatgui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.w3c.dom.Text;

import java.io.IOException;

public class AdminController {
    MessageAdmin admin;
    @FXML
    TextField topic;
    @FXML
    TextArea message;



    @FXML
    void initialize(){
        admin = new MessageAdmin("localhost",12555);
        try {
            admin.connect();
        }catch (IOException e){


        }

    }

    public void sendMessage(MouseEvent event) {
        try {
            admin.sendData(new Message(topic.getText(), message.getText(), "Admin"));
        }catch (IOException e){
            topic.clear();
            message.clear();


        }
    }
}
