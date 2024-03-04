package com.front;

import com.back.messengers.Group;
import com.back.usersPackage.User;
import com.back.usersPackage.UserType;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ScnSearch implements Initializable {

    Group group;
    User user;
    @FXML
    TextField txtID;
    @FXML
    private ChoiceBox<String > stringChoiceBox;
    @FXML
    Label label;

    private String[] type = {"Group", "User"};

    @FXML
    public void searchGroupID() {

        Group.setSelectedGroup(txtID.getText());
        String groupID = Group.getSelectedGroup();
        Group group1 = DataBaseGetter.getInstance().getGroup(groupID);

        if (group1 != null) {
            StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewSearchGroupScene(), "Result!");
        } else {
            StageManager.getInstance().showErrorDialog("There isn't any group with this ID");
        }

    }

    @FXML
    public void searchUserID() {
        User user2 = DataBaseGetter.getInstance().getUser(txtID.getText());
        if (user2 == null) {
            return;
        }

        User.setSelectedUser(user2);
        String userName = User.getSelectedUser().getUserName();
        User user1 = DataBaseGetter.getInstance().getUser(userName);

        if (user1 == null) {
            return;
        }
        if (user1 != null) {
            StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewSearchUsernameScene(), "Result!");
        }
        else {
            StageManager.getInstance().showErrorDialog("There isn't any user with this ID");
        }

    }

    @FXML
    public void checkID() {
        String s = getType();
        if (s == "User") {
            FrontManager.textFieldSetter(txtID, ControllerType.USERNAME);
        } else {
            FrontManager.textFieldSetter(txtID, ControllerType.GROUP_ID);
        }
    }

    @FXML
    public void check() {
        String choice = stringChoiceBox.getValue();
        if (choice == "Group") {
            searchGroupID();
        } else {
            searchUserID();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stringChoiceBox.setItems(FXCollections.observableList(Arrays.stream(type).toList()));
        stringChoiceBox.getSelectionModel().select(0);
        label.setText("Search");
    }

    public String  getType() {
        String type = stringChoiceBox.getValue();
        return type;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

}
