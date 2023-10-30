package org.example.server;

import org.example.entities.Message;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class ServerConnection extends Thread{
    private final BufferedReader in;
    private final BufferedWriter out;
    Socket clientSocket;
    Server serverSocket;
    ObjectOutputStream objOut;
    ObjectInputStream objIn;
    org.slf4j.Logger logger;

    ServerConnection(Socket clientSocket, Server serverSocket){
        logger = org.slf4j.LoggerFactory.getLogger(ServerConnection.class);
        this.serverSocket = serverSocket;
        this.clientSocket = clientSocket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            objOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objIn = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            logger.error("Server connection error:", e);
            throw new RuntimeException(e);
        }
        start();
    }

    public void closeConnection(){
        serverSocket.deleteConnection(this);
        send("/exit");
        try {
            out.close();
            in.close();
            clientSocket.close();
            objIn.close();
            objOut.close();

        }catch (IOException e){
            logger.error("Server connection error:", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try{
            try {
                Message message;
                while (true) {
                    message = (Message) objIn.readObject();
                    if(message.getText().equals("/exit")){
                        logger.info("Server connection is closing...");
                        return;
                    }
                    System.out.println(message);
                    LinkedList<ServerConnection> list =  serverSocket.getClientsList();
                    for(ServerConnection sc : list){
                        if (sc.equals(this)) continue;
                        sc.send(message.toString());
                    }
                }
            } finally{
                closeConnection();
            }
        }catch (ClassNotFoundException|IOException e) {
            logger.error("Server connection error:", e);
            throw new RuntimeException(e);
        }
    }

    public void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            logger.error("Server connection error:", e);
            throw new RuntimeException(e);
        }
    }
}
