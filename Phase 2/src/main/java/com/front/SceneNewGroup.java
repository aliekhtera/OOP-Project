package com.front;

import com.back.MethodReturns;
import com.back.messages.Message;
import com.back.messengers.Group;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SceneNewGroup implements Initializable {

    @FXML
    private TextField txtGroupID, txtGroupName;
    @FXML
    private Label label;

    @FXML
    ImageView imgProfile;
    String pictureURL;
    //////// ????????
    static String nullUrl = String.valueOf(ScnGroupSetting.class.getResource("nullUserImage.png"));

    @FXML
    private void ProfileImage() {
        String result = FrontManager.openImage(imgProfile);
        if (result != null) {
            pictureURL = result;
        }
    }

    @FXML
    public void createGroup() {

        ArrayList<User> users = new ArrayList<>();
        ArrayList<String > banned = new ArrayList<>();
        ArrayList<Message> messages = new ArrayList<>();

        users.add(User.getLoggedInUser());

        if (DataBaseGetter.getInstance().getGroup(txtGroupID.getText()) != null) {
            return;
        }

        Group group = new Group(users, messages, User.getLoggedInUser(), User.getLoggedInUser(), txtGroupName.getText(), txtGroupID.getText(), banned);

        DataBaseSetter.getInstance().addNewGroupToDataBase(group);
        try {
                StageManager.getInstance().showCreateGroupDialog("Group was created");
                StageManager.getInstance().changeScene(SceneManager.getInstance().getNewMainScene());
        } catch (Exception e) {
            StageManager.getInstance().showDialog(MethodReturns.UNKNOWN_DATABASE_ERROR);
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label.setText("Create Group");
    }

    @FXML
    private void txtGroupIDChecker() {
        FrontManager.textFieldSetter(txtGroupID, ControllerType.NEW_GROUP_ID);
    }

    @FXML
    private void TxtGroupNameChecker() {
        FrontManager.textFieldSetter(txtGroupName, ControllerType.NEW_GROUP_NAME);
    }

}
