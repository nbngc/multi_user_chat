/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.tcp.multipleclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author nbngc
 */
public class DatabaseConnection {
    public Connection getDatabaseConnection() throws ClassNotFoundException{
     String dbUrl = "jdbc:mysql://localhost:3306/chat_application";
     String dbUname = "root";
     String dbPassword = "";
     String dbDriver = "com.mysql.cj.jdbc.Driver";
     Class.forName(dbDriver);
     Connection connection = null;
     
         try {
             connection = DriverManager.getConnection(dbUrl, dbUname, dbPassword);
         } catch (SQLException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
         return connection;
     }   
}
