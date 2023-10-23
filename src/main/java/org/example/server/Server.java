package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class Server {
    private static ServerSocket serverSocket;
    public static LinkedList<ServerConnection> clientsList;
//    public static ArrayList<ServerConnection> clientsList;

    //  TODO  Delete connections from server when exit
    public Server(){
        clientsList = new LinkedList<>();
//        clientsList = new ArrayList<>();
        try {
            try {
                serverSocket = new ServerSocket(4004);
                System.out.println("Server has been started");
                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    clientsList.add(new ServerConnection(clientSocket, this));
                }
            } finally {
                System.out.println("Server closing...");
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message){
        for(ServerConnection sc : clientsList){
            System.out.println(sc.clientSocket);
            sc.send(message);
        }
    }
}
