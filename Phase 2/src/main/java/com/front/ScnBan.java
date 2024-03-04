package com.front;

import com.back.messengers.Group;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ScnBan implements Initializable {

    Group group;

    @FXML
    TextField txtID;

    @FXML
    Label label;

    @FXML
    public void check() {

        User user = DataBaseGetter.getInstance().getUser(txtID.getText());
        group = DataBaseGetter.getInstance().getGroup(Group.getOpenedGroup());

        ArrayList<User> users = DataBaseGetter.getInstance().getMembers(group);

        boolean mem = false;
        boolean ban = false;

        if (user == null) {
            return;
        }
        else {
            for (User user1 : users) {
                if (user1.getUserName().equals(user.getUserName())) {
                    mem = true;
                }
            }

            for (String bannedAccount : group.getBannedAccounts()) {
                if (bannedAccount.equals(user.getUserName())) {
                    ban = true;
                }
            }
        }


        if (ban) {
            group.banMember(group.getAdmin(), group, user.getUserName());
            StageManager.getInstance().showDoneDialog();
        }
        else if (mem) {
            group.getBannedAccounts().add(user.getUserName());
            group.banMember(group.getAdmin(), group, user.getUserName());
            StageManager.getInstance().showDoneDialog();
        }
        else {
            StageManager.getInstance().showErrorDialog("There isn't any user with this username");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label.setText("Ban / UnBan");
    }

}
