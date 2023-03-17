package com.example.flowershop.helpers;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {

    Connection con;
    String username, password, ip, port, database, url;

    public Connection getConnection() {
        ip = "10.0.2.2";
        database = "FlowerShop";
        username = "quyen";
        password = "123456";
        port = "1433";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            url = "jdbc:jtds:sqlserver://"+ip+":"+port+";"+"databasename="+database+";user="+username+";password="+password;
            con = DriverManager.getConnection(url);
        }catch (Exception e){
            Log.e("Error is", e.getMessage());
        }
        return con;
    }
}
