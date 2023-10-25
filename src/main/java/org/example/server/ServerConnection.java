package org.example.server;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class ServerConnection extends Thread{
    private  BufferedReader in;
    private  BufferedWriter out;
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
                while (true) {
                    message = in.readLine();
                    if(message.equals("/exit")){
                        System.out.println("Client disconected");
                        return;
                    }
                    System.out.println(message);
                    LinkedList<ServerConnection> list =  serverSocket.getClientsList();
                    for(ServerConnection sc : list){
                        if (sc.equals(this)) continue;
                        sc.send(message);
                    }
                }
            }finally{
                serverSocket.deleteConnection(this);
                send("/exit");
                out.close();
                in.close();
                clientSocket.close();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
