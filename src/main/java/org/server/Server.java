package org.server;


import org.entities.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.LinkedList;

public class Server {
    ServerSocketChannel serverChannel = null;
    Selector selector;
    public LinkedList<SocketChannel> clientsList;
    public LinkedList<Message> messagesList;
    org.slf4j.Logger logger;
    DB_adapter database;
    final String HOSTNAME = "localhost";
    final int PORT = 4004;
    final int THREAD_NUMBER = 1;
    public Server(){
        database = new DB_adapter();
        clientsList = new LinkedList<>();
        messagesList = new LinkedList<>();
        logger = org.slf4j.LoggerFactory.getLogger(Server.class);
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            //serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException e){
            logger.error("Server error:", e);
            System.err.println(e);
        }
        createThreads();
    }


    public void createThreads(){
        for(int i = 0; i < THREAD_NUMBER; i++){
            new ConnectionAcceptThread(this);
        }
        for(int i = 0; i < THREAD_NUMBER; i++){
            new DataRecieveThread(this);
        }
        for(int i = 0; i < THREAD_NUMBER; i++){
            new ProcessingThread(this);
        }
    }

    public DB_adapter getDatabase() {
        return database;
    }

    public void setDatabase(DB_adapter database) {
        this.database = database;
    }

    public void addMessage(Message message){
        messagesList.add(message);
    }

    public LinkedList<Message> getMessagesList() {
        return messagesList;
    }

    public ServerSocketChannel getServerChannel(){
        return serverChannel;
    }

    public void setSelector(Selector selector){
        this.selector = selector;
    }

    public Selector getSelector(){
        return selector;
    }

    public void waitConnections(){
        SocketChannel client = null;
            try {
                while (true) {
                    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                    selector.select();
                    client = serverChannel.accept();
                    client.register(selector,SelectionKey.OP_ACCEPT);
                    logger.info("Connection accepted");
                }
            } catch (IOException e) {
                logger.error("Waiting connections stopped:", e);
                throw new RuntimeException(e);
            }
    }

//    public void closeServer(){
//        try {
//            //serverSocket.close();
//        }catch(IOException e){
//                logger.error("Server close with error:", e);
//                throw new RuntimeException(e);
//            }
//    }

    public synchronized void deleteClient(SocketChannel sc){
        clientsList.remove(sc);
    }

    public void addClient(SocketChannel sc){clientsList.add(sc);}

    public LinkedList<SocketChannel> getClientsList(){
        return clientsList;
    }
}
