package com.dataBase;

import java.sql.*;

public class DataBaseManager {
    private DataBaseManager(){}
    private static DataBaseManager singletonInstance=new DataBaseManager();

    public static DataBaseManager getInstance() {
        return singletonInstance;
    }
    private Connection connection;
    public void initialize() throws SQLException {
        if(connection==null){
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oop_phase2","oop","Oop12345") ;
        }
    }

    public Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

}
