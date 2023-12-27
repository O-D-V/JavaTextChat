package org.server;

import java.io.IOException;
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
        logger.info("Thread has started");
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    SocketChannel newChannel = ((ServerSocketChannel) key.channel()).accept();
                    logger.info("Connection accepted");
                    // Неблокирующий
                    newChannel.configureBlocking(false);
                    //newChannel.register(key.selector(), SelectionKey.OP_WRITE);
                    newChannel.register(readSelector, SelectionKey.OP_READ);
                    server.addClient(newChannel);
                }
            } catch (IOException e) {
                logger.error("Server error:", e);
                System.err.println(e);
            }
        }
    }
}
