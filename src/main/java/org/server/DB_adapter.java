package org.server;

import java.sql.*;
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

    public boolean createTable(){
        try(Connection DB_connection = DriverManager.getConnection(DB_URL,USER ,PASS )){
            try (Statement stmt = DB_connection.createStatement()) {

                String tableSql = "create table chat.Messages(text text, name varchar(30), datetime timestamp);";

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
