package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseManager {
    private DataBaseManager(){}
    private static DataBaseManager singletonInstance=new DataBaseManager();

    public static DataBaseManager getInstance() {
        return singletonInstance;
    }
    private Connection connection;
    public void initialize() throws SQLException {
        if(connection==null){
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oop_projectdb","oop","Oop12345") ;
        }
    }

    public Statement getStatement() throws SQLException {
        return connection.createStatement();
    }
}
