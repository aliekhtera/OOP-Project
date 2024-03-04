package com.front;

import com.back.messengers.Group;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ScnRemove implements Initializable {
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

        if (user == null) {
            return;
        }

        for (User user1 : users) {
            if (user1.getUserName().equals(user.getUserName())) {
                mem = true;
            }
        }

        if (mem) {
           group.removeUser(group.getAdmin(), user, group);
            StageManager.getInstance().showDoneDialog();
        }
        else {
            StageManager.getInstance().showErrorDialog("There isn't any user with this username");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label.setText("Remove");
    }

}
