package org.example.client;

import org.example.entities.Message;

import java.io.*;
import java.net.Socket;

public class Client extends Thread{
    private  Socket serverSocket;
    private  BufferedReader reader;
    private  BufferedReader input;
    private  BufferedWriter out;
    ObjectOutputStream objOut;
    ObjectInputStream objIn;
    String name;
    org.slf4j.Logger logger;
    public Client(String address, int port){
        logger = org.slf4j.LoggerFactory.getLogger(Client.class);
        logger.info("The client is up and running");
        try {
            serverSocket = new Socket(address, port);
            reader = new BufferedReader(new InputStreamReader(System.in));
            input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
            objOut = new ObjectOutputStream(serverSocket.getOutputStream());
            objIn = new ObjectInputStream(serverSocket.getInputStream());
            write();
        } catch (IOException e) {
            logger.error("Client closed with:",e);
            System.err.println(e);
        }
    }

    public void askName(){
        System.out.println("Enter your name:");
        try {
            name = reader.readLine();
        }catch (IOException e){
            logger.error("Name is not entered:",e);
            System.err.println(e);
        }
    }

    public void write(){
            try {
                try {
                    start();
                    askName();
                    objOut.writeObject(new Message("new user connected", name));
                    out.flush();
                    String text = "";
                    while (!text.equals("/exit")) {
                        text = reader.readLine();
                        objOut.writeObject(new Message(text,name));
                        out.flush();
                    }
                } finally {
                    logger.info("Client is closing");
                }
            }catch (IOException e) {
                logger.error("Client closed with:",e);
                System.err.println(e);
            }
    }


    @Override
    public void run() {
        try {
            try{
            while (true) {
                String serverWord = input.readLine();
                if (serverWord.equals("/exit")) break;
                System.out.println(serverWord);
            }}finally {
                serverSocket.close();
                input.close();
                out.close();
                reader.close();
                logger.info("The server has terminated the connection");
            }
        }catch (IOException e) {
            logger.error("Client read-thread closed with: ",e);
            System.err.println(e);
        }
    }


    }