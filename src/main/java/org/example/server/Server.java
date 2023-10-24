package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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

    public void sendMessage(String message){
        for(int i = 0; i < clientsList.size(); i ++){
//        for(ServerConnection sc : clientsList){
            System.out.println(clientsList.get(i).clientSocket);
            clientsList.get(i).send(message);
        }
    }
}
