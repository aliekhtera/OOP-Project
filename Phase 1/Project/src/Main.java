import back.MethodReturns;
import back.messages.Message;
import back.messengers.PV;
import back.usersPackage.User;
import back.usersPackage.UserType;
import dataBase.DataBaseGetter;
import dataBase.DataBaseManager;
import front.LoginMenu;
import front.MainMenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {

    public static void main(String[] args)  throws SQLException  {
	// write your code here
        DataBaseManager.getInstance().initialize();
        try {
            LoginMenu.getInstance().start();
        }catch(Exception e){
            if(User.getLoggedInUser()==null){
                LoginMenu.getInstance().start();
            }else {
                MainMenu.getInstance().start();
            }
        }

    }
}
