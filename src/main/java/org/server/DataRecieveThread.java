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

public class DataRecieveThread extends Thread{
    public LinkedList<String> messagesList;
    ServerSocketChannel serverChannel = null;
    org.slf4j.Logger logger;
    Selector selector;
    Server server;

    public DataRecieveThread(Server server) {
        this.server = server;
        selector = server.getSelector();
        messagesList = new LinkedList<>();
        logger = org.slf4j.LoggerFactory.getLogger(DataRecieveThread.class);
        start();
    }

    @Override
    public void run() {
        ByteBuffer buffer;
        SelectionKey key = null;
        logger.info("Thread has started");
        try {
            while (selector.select(1) > -1) {
                // Получаем ключи на которых произошли события в момент
                // последней выборки
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    SocketChannel client = ((SocketChannel) key.channel());
                    String JSONmessage = "";

                        buffer = ByteBuffer.allocate(4);
                        while (client.read(buffer) > 0) {}
                        buffer.flip();
                        int messSize = buffer.getInt();
                        System.out.println(messSize);

                        buffer = ByteBuffer.allocate(messSize);

                    if(client.read(buffer) == 0){
                        try {
                            Thread.sleep(1000);
                        }catch (InterruptedException e){ }
                        client.read(buffer);
                    }
                        buffer.clear();
                        JSONmessage = BBtoString(buffer);
                        Message mes = objectMapper.readValue(JSONmessage, Message.class);


                       ////// //Send to others clients/////////
                        server.addMessage(mes);

                }}
        } catch (IOException e) {
            logger.error("Read error:", e);
            System.err.println(e);
        }

    }
    public static String BBtoString(ByteBuffer bb) {
        final byte[] bytes = new byte[bb.remaining()];
        bb.duplicate().get(bytes);
        return new String(bytes);
    }
}
