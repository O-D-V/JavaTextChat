package org.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.entities.Message;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

public class ProcessingThread extends Thread{
    org.slf4j.Logger logger;
    Server server;

    ProcessingThread(Server server){
        logger = org.slf4j.LoggerFactory.getLogger(ProcessingThread.class);
        this.server = server;
        start();
    }

    @Override
    public void run() {
        DB_adapter database = server.getDatabase();
        LinkedList list;
        LinkedList messagesList = server.getMessagesList();
        ByteBuffer buffer;
        ByteBuffer sizeBuffer;
        String msg;
        int messSize;
        while(true) {
            try {
                while (server.getMessagesList().isEmpty()) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                logger.error("Processing thread sleep is interrupted", e);
            }
            Iterator messagesIterator = messagesList.iterator();
            while (messagesIterator.hasNext()) {
                list = server.getClientsList();
                Message message = (Message) messagesIterator.next();
                database.addMessage(message);
                logger.info("Send:" + message);
                messagesIterator.remove();
                msg = messageObjtoJSONstring(message);
                buffer = ByteBuffer.wrap(msg.getBytes());
                messSize = buffer.limit();
                sizeBuffer = ByteBuffer.allocate(4);
                sizeBuffer.putInt(messSize);
                sizeBuffer.flip();

                try {
                    Iterator iterator1 = list.iterator();
                    System.out.println("clients list size:" + list.size());
                    while (iterator1.hasNext()) {
                        SocketChannel sc = (SocketChannel) iterator1.next();
                        logger.info("To:" + sc.toString());
                        //if (sc == client) continue;
                        sc.write(sizeBuffer.duplicate());
                        sc.write(buffer.duplicate());

                    }
                } catch (IOException e) {
                    logger.error("Processing Thread interrupt with error:", e);
                    return;
                }
            }
            logger.debug("all messages delivered");
        }
    }


    private String messageObjtoJSONstring(Message message){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String json = "";
        try {
            json = objectMapper.writeValueAsString(message);
        }catch (IOException e){
            logger.error("Message to json error:", e);
        }
        return json;
    }

    public static String BBtoString(ByteBuffer bb) {
        final byte[] bytes = new byte[bb.remaining()];
        bb.duplicate().get(bytes);
        return new String(bytes);
    }
}
