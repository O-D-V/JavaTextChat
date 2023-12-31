package org.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.client.view.MainFrame;
import org.client.view.MainPanel;
import org.entities.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Scanner;

//TODO Добавить возможность менять имя
//TODO Добавить логины и пароли

public class Client extends Thread{
    public SocketChannel channel;

    Scanner reader;
    SocketChannel server;
    final String HOSTNAME = "localhost";
    final int PORT = 4004;
    org.slf4j.Logger logger;
    MainFrame mainWindow;
    ObjectMapper objectMapper = new ObjectMapper();


    private String name;
    public Client(String address, int port){
        objectMapper.registerModule(new JavaTimeModule());
        logger = org.slf4j.LoggerFactory.getLogger(Client.class);
        try {
            server = SocketChannel.open(new InetSocketAddress(HOSTNAME, PORT));
            server.configureBlocking(false);
        }catch (IOException e){
            logger.error("Client error:", e);
        }
        reader =  new Scanner(System.in);
        logger.info("The client is up and running");
        mainWindow = new MainFrame(this);
        start();
    }

    public void setClientName(String name){
        this.name = name;
    }



    public void write(String text){
        try {
            ByteBuffer buffer;
            ByteBuffer sizeBuffer;
            String msg = text;
            msg = messageObjtoJSONstring(new Message(msg, name, java.time.LocalDateTime.now(Clock.tickSeconds(ZoneId.of("UTC+3")))));
            buffer = ByteBuffer.wrap(msg.getBytes());

            int messSize = buffer.limit();
            sizeBuffer = ByteBuffer.allocate(4);
            sizeBuffer.putInt(messSize);
            sizeBuffer.flip();
            server.write(sizeBuffer);

            sizeBuffer.clear();
            //buffer.flip();
            server.write(buffer);
            logger.info("End of write");
        }catch (IOException e) {
                logger.error("Client closed with:",e);
                System.err.println(e);
                reader.close();
            }
    }

    private String messageObjtoJSONstring(Message message){
        String json = "";
        try {
            json = objectMapper.writeValueAsString(message);
        }catch (IOException e){
            logger.error("Message to json error:", e);
        }
        return json;
    }

    private String BBtoString(ByteBuffer bb) {
        final byte[] bytes = new byte[bb.remaining()];
        bb.duplicate().get(bytes);
        return new String(bytes);
    }

    private void closeAll() throws IOException{
        reader.close();
        server.close();
    }

    @Override
    public void run() {
        Selector selector;
        ByteBuffer buffer;
        try {
            try{
                selector = Selector.open();
                SelectionKey key = server.register(selector, SelectionKey.OP_READ);
                while (true) {
                    selector.select();
                     buffer = ByteBuffer.allocate(4);
                    while(server.read(buffer) > 0);
                    buffer.flip();
                    int messSize = buffer.getInt();
                    buffer = ByteBuffer.allocate(messSize);
                    if(server.read(buffer) == 0){
                        try {
                            Thread.sleep(1000);
                        }catch (InterruptedException e){}
                        server.read(buffer);
                    }
                    buffer.clear();
                    String msg = BBtoString(buffer);
                    logger.info("recieved:" + msg);
                    Message mes = objectMapper.readValue(msg, Message.class);
                    mainWindow.updateChat(mes);
                }
            }finally {
                closeAll();
                logger.info("Terminated the connection");
//            }
        }}catch (IOException e) {
            logger.error("Client read-thread closed with: ",e);
            System.err.println(e);
        }
    }
    }