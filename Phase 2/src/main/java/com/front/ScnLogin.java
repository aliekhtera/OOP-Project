package com.front;

import com.back.MethodReturns;
import com.back.usersPackage.User;
import com.back.usersPackage.UserType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ScnLogin implements Initializable {
    @FXML
    private TextField txtSun, txtLun, txtSFirst, txtSLast, txtSSA;

    @FXML
    private PasswordField txtLp,txtSp;

    @FXML
    private ChoiceBox<String> cmbSut, cmbSSQ;

    @FXML
    private void recoverPassWord() {
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewPassRecoveryScene(), "Password Recovery!");
    }

    @FXML
    private void signUp() {
        try {
            StageManager.getInstance().showSignUpDialog(User.signUpNewUser(txtSun.getText(), txtSp.getText(), txtSFirst.getText(), txtSLast.getText(),
                    UserType.indexToUserType(cmbSut.getSelectionModel().getSelectedIndex())
                    , txtSSA.getText(), cmbSSQ.getSelectionModel().getSelectedIndex()));

        } catch (SQLException e) {
            StageManager.getInstance().showDialog(MethodReturns.UNKNOWN_DATABASE_ERROR);
        }
    }

    @FXML
    private void login() {
        try {
            if (StageManager.getInstance().showLoginDialog(User.loginUser(txtLun.getText(), txtLp.getText()))) {
                StageManager.getInstance().changeScene(SceneManager.getInstance().getNewMainScene());
            }
        } catch (Exception e) {
            StageManager.getInstance().showDialog(MethodReturns.UNKNOWN_DATABASE_ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbSut.setItems(FXCollections.observableList(UserType.toList()));
        cmbSut.getSelectionModel().select(0);
        cmbSSQ.setItems(FXCollections.observableList(User.getSecurityQ()));
        cmbSSQ.getSelectionModel().select(0);

    }

    @FXML
    private void txtLpChecker(){
     FrontManager.textFieldSetter(txtLp,ControllerType.PASSWORD);
    }
    @FXML
    private void txtSpChecker(){
        FrontManager.textFieldSetter(txtSp,ControllerType.PASSWORD);
    }
    @FXML
    private void txtLunChecker(){
        FrontManager.textFieldSetter(txtLun,ControllerType.OLD_USERNAME);
    }
    @FXML
    private void txtSunChecker(){
        FrontManager.textFieldSetter(txtSun,ControllerType.NEW_USERNAME);
    }
    @FXML
    private void txtSFirstChecker(){
        FrontManager.textFieldSetter(txtSFirst,ControllerType.FIRST_NAME);
    }
    @FXML
    private void txtSLastChecker(){
        FrontManager.textFieldSetter(txtSLast,ControllerType.LAST_NAME);
    }
    @FXML
    private void txtSSAChecker(){
        FrontManager.textFieldSetter(txtSSA,ControllerType.SECURITY_ANSWER);
    }

}
