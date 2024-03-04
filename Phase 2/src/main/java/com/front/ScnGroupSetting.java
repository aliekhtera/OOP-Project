package com.front;

import com.back.messengers.Group;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.w3c.dom.Text;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ScnGroupSetting implements Initializable {
     Group group;

    @FXML
     TextField txtGroupID, txtGroupName, txtAddNewMember;

    @FXML
    ImageView imgProfile;

    String pictureURL;
    static String nullUrl = String.valueOf(ScnGroupSetting.class.getResource("nullUserImage.png"));

    @FXML
     void groupEdit() {
        if (User.getLoggedInUser().getUserName().equals(group.getAdmin().getUserName())) {
            String url = imgProfile.getImage().getUrl();
            File file;
            if (url == null) {
                if (pictureURL == null) {
                    file = null;
                } else {
                    file = new File(pictureURL);
                }
            }
            else if (url.equals(nullUrl)) {
                file = null;
            }
            else {
                file = new File(url);
            }

            group.changeGroupName(group.getAdmin(), group, txtGroupName.getText());
            Group  group1 = DataBaseGetter.getInstance().getGroup(group.getGroupID());

            User user = DataBaseGetter.getInstance().getUser( txtAddNewMember.getText());
            if(user == null) {
                return;
            }
            ArrayList<User > users = DataBaseGetter.getInstance().getMembers(group);
            boolean mem = false;
            for (User user1 : users) {
                if (user1.getUserName().equals(user.getUserName())) {
                    mem = true;
                    break;
                }
            }
            if ( (! mem )&& (user != null) ) {
                group.addUser(group.getAdmin(), user, group);
            }


            StageManager.getInstance().showDoneDialog();
        }
        else {
            StageManager.getInstance().showErrorDialog("You can't change the information because you are not admin");
        }

        ScnMain.getScnMain().listsRefresh();
    }

    @FXML
     void members() {
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewMembersScene(), "Members!");
    }

    @FXML
    private void changeProfileImage() {
        String result = FrontManager.openImage(imgProfile);
        if (result != null) {
            pictureURL = result;
        }
    }

    @FXML
    void checkTxtAddNewMember() {
        FrontManager.textFieldSetter(txtAddNewMember, ControllerType.ADD_NEW_MEMBER);
    }

    @FXML
    void checkTxtGroupName() {
        FrontManager.textFieldSetter(txtGroupName, ControllerType.GROUP_NAME);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        group = DataBaseGetter.getInstance().getGroup(Group.getOpenedGroup());

        txtGroupID.setText(group.getGroupID());
        txtGroupName.setText(group.getGroupName());


        Image groupP= DataBaseGetter.getInstance().getGroupProfile(group.getGroupID());
        if(groupP==null){
            imgProfile.setImage(new Image(nullUrl));
        }else{
            imgProfile.setImage(FrontManager.cropImage(groupP));
        }
    }


    public void setGroup(Group group) {
        this.group = group;
    }

}
