package org.example.client;

import java.io.*;
import java.net.Socket;

public class Client extends Thread{
    private static Socket serverSocket;
    private static BufferedReader reader;
    private static BufferedReader input;
    private static BufferedWriter out;

    public Client(String address, int port){
        try {
            try {
                serverSocket = new Socket(address, port);
                reader = new BufferedReader(new InputStreamReader(System.in));
                input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));

                start();
                String text = "";
                while (!text.equals("exit")){
                    text = reader.readLine();
                    System.out.println("1:"+text);
                    out.write(text + "\n");
                    out.flush();
                }
            } finally {
                System.out.println("Client is closing...");
                serverSocket.close();
                input.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String serverWord = input.readLine();
                System.out.println(serverWord);
            }
        }catch (IOException e) {
            System.err.println(e);
        }
    }
}
