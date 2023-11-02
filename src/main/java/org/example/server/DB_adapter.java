package org.example.server;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
public class DB_adapter {
    org.slf4j.Logger logger;
    public DB_adapter(){
        System.out.println("DB_adapter start working");
        logger = org.slf4j.LoggerFactory.getLogger(DB_adapter.class);
        try(Connection DB_connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb", "postgres", "1123")){
            try (Statement stmt = DB_connection.createStatement()) {
                String tableSql = "CREATE TABLE IF NOT EXISTS message"
                        + "(emp_id int PRIMARY KEY, name varchar(30),"
                        + "position varchar(30), salary double precision)";
                stmt.execute(tableSql);
            }
        }catch (SQLException e){
            logger.error("DB connection interrupted with:", e);
            System.err.println(e);
        }
    }

}
