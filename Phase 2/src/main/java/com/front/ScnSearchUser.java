package com.front;

import com.back.messengers.PV;
import com.back.messengers.Page;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ScnSearchUser implements Initializable{

    @FXML
    Label userName, firstName, lastName, userName1, firstName1, lastName1;

    @FXML
    public void directMessage() {
        PV.openPV(User.getLoggedInUser().getUserName(), userName.getText());
        StageManager.getInstance().showDoneDialog();
        ScnMain.getScnMain().listsRefresh();
    }

    @FXML
    public void followUnFollow() {
        Page page = DataBaseGetter.getInstance().getPage(userName.getText());
        ArrayList<String > followers = page.getFollowers();
        boolean follow = false;
        for (String follower : followers) {
            if (follower.equals(User.getLoggedInUser().getUserName())) {
                follow = true;
            }
        }

        if (follow) {
            page.unfollow();
            StageManager.getInstance().showUnFollowDialog("You unfollowed "+ page.getPageName());
        } else {
            page.follow();
            StageManager.getInstance().showFollowDialog("You follow " + page.getPageName());
        }
        ScnMain.getScnMain().listsRefresh();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userName.setText(User.getSelectedUser().getUserName());
        firstName.setText(User.getSelectedUser().getFirstName());
        lastName.setText(User.getSelectedUser().getLastName());
        userName1.setText("Username");
        firstName1.setText("Firstname");
        lastName1.setText("Lastname");
    }

}
