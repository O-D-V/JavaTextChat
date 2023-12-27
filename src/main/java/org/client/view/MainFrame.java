package org.client.view;

import org.entities.Message;
import org.client.Client;

import javax.swing.*;

public class MainFrame extends JFrame {

    Client client;
    MainPanel panel;
    public MainFrame(Client client){
        super("Main ");
        this.client = client;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new MainPanel(this);
        this.add(panel);
        // Определение размера окна
        setSize(1000, 500);
        // Открытие окна
        setVisible(true);
    }

    public void setName(String name){
        client.setClientName(name);
    }

    public void write(String text) {
        client.write(text);
    }
    public void updateChat(Message mes){
        panel.updateChat(mes);
    }
}
