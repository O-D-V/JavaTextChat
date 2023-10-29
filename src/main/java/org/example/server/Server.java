package org.example.server;

import org.example.client.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    private  ServerSocket serverSocket;
    public LinkedList<ServerConnection> clientsList;
    org.slf4j.Logger logger;

    public Server(){
        logger = org.slf4j.LoggerFactory.getLogger(Server.class);
        waitConnections();
    }

    public void waitConnections(){
        clientsList = new LinkedList<>();
        try {
            try {
                serverSocket = new ServerSocket(4004);
                logger.info("Server is up and running");
                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    clientsList.add(new ServerConnection(clientSocket, this));
                    if(clientsList.size() == 0) break;
                }
            } finally {
                logger.info("Server is closing...");
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.error("Server is closed with:", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteConnection(ServerConnection sc){
        clientsList.remove(sc);
    }

    public LinkedList<ServerConnection> getClientsList(){
        return clientsList;
    }
}
