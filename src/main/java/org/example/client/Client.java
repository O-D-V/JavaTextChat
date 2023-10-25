package org.example.client;

import java.io.*;
import java.net.Socket;

public class Client extends Thread{
    private  Socket serverSocket;
    private  BufferedReader reader;
    private  BufferedReader input;
    private  BufferedWriter out;

    public Client(String address, int port){
            try {
                serverSocket = new Socket(address, port);
                reader = new BufferedReader(new InputStreamReader(System.in));
                input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
                write();
            } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void write(){
            try {

                out.write("New client connected" + "\n");
                out.flush();
                start();
                String text = "";
                while (!text.equals("exit")) {
                    text = reader.readLine();
                    out.write(text + "\n");
                    out.flush();
                }
                System.out.println("Client is closing...");
                serverSocket.close();
                input.close();
                out.close();

                }catch (IOException e) {
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