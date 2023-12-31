package org;

import org.client.Client;
import org.entities.Message;
import org.server.DB_adapter;
import org.server.Server;

import java.util.Iterator;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("1")) {
                System.out.println("Сервер запущен");
                Server server = new Server();
            } else {
                System.out.println("Клиент запущен");
                Client client = new Client("localhost", 4004);
        }

    }
    }
