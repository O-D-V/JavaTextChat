package org.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.entities.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

public class ConnectionAcceptThread extends Thread{
    Server server = null;
    public LinkedList<String> messagesList;
    org.slf4j.Logger logger;
    Selector selector;
    Selector readSelector;
    final int NUMOFMESSHISTORY = 10;
    final int PORT = 4004;

    public ConnectionAcceptThread(Server sc){
        server = sc;

        try {
            readSelector = server.getSelector();
            selector = Selector.open();
            server.getServerChannel().register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException e){
            logger.error("Server error:", e);
            System.err.println(e);
        }
        messagesList = new LinkedList<>();
        logger = org.slf4j.LoggerFactory.getLogger(ConnectionAcceptThread.class);
        //sc.setSelector(readSelector);
        start();
    }

    @Override
    public void run() {
        DB_adapter database = server.getDatabase();
        LinkedList<Message> messagesList = new LinkedList<>();
        logger.info("Thread has started");
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                Iterator<Message> messagesIterator = database.getLastNMessages(NUMOFMESSHISTORY).iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    SocketChannel newChannel = ((ServerSocketChannel) key.channel()).accept();
                    logger.info("Connection accepted");
                    // Неблокирующий
                    newChannel.configureBlocking(false);
                    //newChannel.register(key.selector(), SelectionKey.OP_WRITE);
                    newChannel.register(readSelector, SelectionKey.OP_READ);
                    ByteBuffer buffer;
                    ByteBuffer sizeBuffer;
                    int messSize;
                    while(messagesIterator.hasNext()){
                        Message message = messagesIterator.next();
                        String msg = messageObjtoJSONstring(message);
                        buffer = ByteBuffer.wrap(msg.getBytes());
                        messSize = buffer.limit();
                        sizeBuffer = ByteBuffer.allocate(4);
                        sizeBuffer.putInt(messSize);
                        sizeBuffer.flip();
                        newChannel.write(sizeBuffer);
                        newChannel.write(buffer);
                    }
                    server.addClient(newChannel);
                }
            } catch (IOException e) {
                logger.error("Server error:", e);
                System.err.println(e);
            }
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
}
