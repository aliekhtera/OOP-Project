package com;

import com.back.messages.Message;
import com.back.messengers.PV;
import com.back.usersPackage.User;
import com.dataBase.DataBaseManager;
import com.dataBase.DataBaseSetter;
import com.front.FrontManager;
import com.front.SceneManager;
import com.front.StageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main  {
    public static void main(String[] args) throws SQLException {
        DataBaseManager.getInstance().initialize();
        FrontManager.startProgram(args);
    }
}

