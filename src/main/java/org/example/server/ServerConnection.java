package org.example.server;

import java.io.*;
import java.net.Socket;

public class ServerConnection extends Thread{
    private static BufferedReader in;
    private static BufferedWriter out;
    Socket clientSocket;
    Server serverSocket;
    ServerConnection(Socket clientSocket, Server serverSocket){
        this.serverSocket = serverSocket;
        this.clientSocket = clientSocket;
        try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        start();
    }

    @Override
    public void run() {
        try{
            try {
                String message = "";
                while (!message.equals("/close")) {
                    message = in.readLine();
                    System.out.println("2:" + message);
                    serverSocket.sendMessage(message);
                }
            }finally{
                clientSocket.close();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String msg) {
        try {
            System.out.println("3:" + msg);
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
