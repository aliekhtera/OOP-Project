package com.front;

import com.back.usersPackage.User;
import com.back.usersPackage.UserType;
import com.dataBase.DataBaseGetter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ScnSettings implements Initializable {
    @FXML
    private TextField txtUserName, txtFirstName, txtLastName, txtUserType, txtAnswer;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ChoiceBox<String> cmbSecurityQuestion;
    @FXML
    private ImageView imgProfile;
    private User user;
    private String pictureURL;
     static String nullUrl = String.valueOf(ScnSettings.class.getResource("nullUserImage.png"));

    @FXML
    private void userEdit() {
        String url = imgProfile.getImage().getUrl();
        File file;
        if (url == null) {
            if (pictureURL == null) {
                file = null;
            } else {
                file = new File(pictureURL);
            }
        } else if (url.equals(nullUrl)) {
            file = null;

        } else {
            file = new File(url);
        }
        if (StageManager.getInstance().showDialog(User.editUser(txtUserName.getText(),
                txtPassword.getText(), txtFirstName.getText(), txtLastName.getText(), txtAnswer.getText(), cmbSecurityQuestion.getSelectionModel().getSelectedIndex(), file))) {
            initialize(null, null);
        }
    }

    @FXML
    private void changeProfileImage() {
        String result = FrontManager.openImage(imgProfile);
        if (result != null) {
            pictureURL = result;
        }
    }


    @FXML
    private void txtPasswordChecker() {
        FrontManager.textFieldSetter(txtPassword, ControllerType.PASSWORD);
    }

    @FXML
    private void txtSFirstChecker() {
        FrontManager.textFieldSetter(txtFirstName, ControllerType.FIRST_NAME);
    }

    @FXML
    private void txtSLastChecker() {
        FrontManager.textFieldSetter(txtLastName, ControllerType.LAST_NAME);
    }

    @FXML
    private void txtSSAChecker() {
        FrontManager.textFieldSetter(txtAnswer, ControllerType.SECURITY_ANSWER);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user = DataBaseGetter.getInstance().getUser(User.getLoggedInUser().getUserName());
        txtUserName.setText(user.getUserName());
        txtFirstName.setText(user.getFirstName());
        txtLastName.setText(user.getLastName());
        txtAnswer.setText(user.getSecurityAnswers());
        cmbSecurityQuestion.setItems(FXCollections.observableList(User.getSecurityQ()));
        cmbSecurityQuestion.getSelectionModel().select(user.getSecurityQuestion());
        if (user.getUserType().equals(UserType.NORMAL_USER)) {
            txtUserType.setText("Normal User");
        } else {
            txtUserType.setText("Business User");
        }
        Image userP=DataBaseGetter.getInstance().getUserProfile(User.getLoggedInUser().getUserName());
        if(userP==null){
            imgProfile.setImage(new Image(nullUrl));
        }else{
            imgProfile.setImage(FrontManager.cropImage(userP));
        }


    }


}
