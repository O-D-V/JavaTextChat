package org.server;

import org.entities.Message;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class DB_adapter {
    org.slf4j.Logger logger;
    static final String DB_URL = "jdbc:postgresql://localhost:5432/testdb";
    static final String USER = "postgres";
    static final String PASS = "1123";
    public DB_adapter(){
        System.out.println("DB_adapter start working");
        logger = org.slf4j.LoggerFactory.getLogger(DB_adapter.class);
    }

    public boolean addMessage(String name, String text, java.time.LocalDateTime dateTime){
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "Insert into chat.message(text, name, datetime) values ('" + text + "','" + name + "','" + dateTime + "' )";

                stmt.executeUpdate(tableSql);
            } }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            System.err.println(e);
            return false;
        }
        return true;
    }

    public boolean addMessage(Message message){
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                System.out.println(message.getDateTime());
                String tableSql = "Insert into chat.message(text, name, datetime) values ('" + message.getText() + "','" + message.getSenderNickname() + "','" + message.getDateTime() + "' )";

                stmt.executeUpdate(tableSql);
            } }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            System.err.println(e);
            return false;
        }
        return true;
    }

    public LinkedList<Message> getLastNMessages(int n){
        LinkedList<Message> messagesList = new LinkedList<>();
        try (Connection DB_connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            try (Statement stmt = DB_connection.createStatement()) {
                String tableSql = "with t as (SELECT * FROM chat.message \n" +
                        "ORDER BY datetime DESC LIMIT " + n +") \n" +
                        "select * from t \n" +
                        "Order by datetime asc";
                ResultSet res = stmt.executeQuery(tableSql);
                int columns = res.getMetaData().getColumnCount();
                // Перебор строк с данными
                while (res.next()) {
                        Message message;
                        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        message = new Message(res.getString(1), res.getString(2), LocalDateTime.parse(res.getString(3), formatter));
                        messagesList.add(message);
                }
                res.close();
            }
        } catch (SQLException e) {
            logger.error("DB connection interrupted with:", e);
            System.err.println(e);
        }
        return messagesList;
    }

    public Message getLastMessage(){
        Message message = new Message();
        try (Connection DB_connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            try (Statement stmt = DB_connection.createStatement()) {
                String tableSql = "SELECT * \n" +
                        "  FROM chat.message \n" +
                        " ORDER BY datetime DESC \n" +
                        " LIMIT 1;";
                ResultSet res = stmt.executeQuery(tableSql);
                int columns = res.getMetaData().getColumnCount();
                res.next();
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                message = new Message(res.getString(1), res.getString(2), LocalDateTime.parse(res.getString(3), formatter));
                res.close();
            }
        } catch (SQLException e) {
            logger.error("DB connection interrupted with:", e);
            System.err.println(e);
        }
        return message;
    }

    public boolean createTable(){
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "create table chat.Message(text text, name varchar(30), datetime timestamp);";

                stmt.executeUpdate(tableSql);
            } }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            System.err.println(e);
            return false;
        }
        return true;
    }

    public boolean deleteData(String nameORtext){
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "delete from chat.message where text = '" + nameORtext + "' or name = '" + nameORtext + "'";

                stmt.executeUpdate(tableSql);
            } }catch (SQLException e){
            logger.error("DB remove operation aborted:", e);
            System.err.println(e);
            return false;
        }
        return true;
    }



    public void showAllMessages(){
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {
                String tableSql = "Select * from chat.message";
                ResultSet res = stmt.executeQuery(tableSql);
                int columns = res.getMetaData().getColumnCount();
                // Перебор строк с данными
                while(res.next()){
                    for (int i = 1; i <= columns; i++){
                        System.out.print(res.getString(i) + "\t");
                    }
                    System.out.println();
                }
                System.out.println();
                res.close();
            }
        }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            System.err.println(e);
        }

    }

}
