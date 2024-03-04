package com.front;

import com.back.messengers.Group;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class ScnSearchGroup implements Initializable {
    Group group;

    @FXML
    Label groupName, groupMembers, groupAdmin, name, members, admin;

    @FXML
    public void join() {
        User admin = group.getAdmin();
        group.addUser(admin, User.getLoggedInUser(), group);
        StageManager.getInstance().showJoinDialog("You joined the group");
        ScnMain.getScnMain().listsRefresh();
    }

    @FXML
    public void leave() {
        User admin = group.getAdmin();
        group.removeUser(admin, User.getLoggedInUser(), group);
        StageManager.getInstance().showLeaveDialog("You left the group");
        ScnMain.getScnMain().listsRefresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        group = DataBaseGetter.getInstance().getGroup(Group.getSelectedGroup());
        groupName.setText(group.getGroupName());
        groupMembers.setText(Integer.toString(group.getMembers().size() )+ " members");
        groupAdmin.setText(group.getAdmin().getUserName());

        name.setText("Name :");
        members.setText("Members :");
        admin.setText("Admin :");
    }

}
