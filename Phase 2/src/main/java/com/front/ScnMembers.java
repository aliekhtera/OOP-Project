package com.front;

import com.back.messengers.Group;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class ScnMembers implements Initializable {
    @FXML
    private ListView<String > listView;

    @FXML
    private Label label;

    String user;
    Group group;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        group = DataBaseGetter.getInstance().getGroup(Group.getOpenedGroup());

        for (User member : DataBaseGetter.getInstance().getGroup(group.getGroupID()).getMembers()) {
            listView.getItems().add(member.getUserName());
        }
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {

                user = listView.getSelectionModel().getSelectedItem();
                label.setText(user);
                User user1 = DataBaseGetter.getInstance().getUser(user);
                User.setSelectedUser(user1);

            }
        });

    }

}
