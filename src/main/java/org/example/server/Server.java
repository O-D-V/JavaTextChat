package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    private  ServerSocket serverSocket;
    public LinkedList<ServerConnection> clientsList;
//    public static ArrayList<ServerConnection> clientsList;

    //  TODO  Delete connections from server when exit
    public Server(){
        waitConnections();
    }

    public void waitConnections(){
        clientsList = new LinkedList<>();
        try {
            try {
                serverSocket = new ServerSocket(4004);
                System.out.println("Server has been started");
                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    clientsList.add(new ServerConnection(clientSocket, this));
                    if(clientsList.size() == 0) break;
                }
            } finally {
                System.out.println("Server closing...");
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<ServerConnection> getClientsList(){
        return clientsList;
    }
}
