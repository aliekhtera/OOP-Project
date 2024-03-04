package com.front;

import com.back.MethodReturns;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

public class ScnPassRecovery implements Initializable {
    @FXML
    TextField txtA, txtUn,txtP;
    @FXML
    Label lblQ;
    @FXML
    Button btnOk;

    private User user;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user=null;
    }
    @FXML
    private void btnOkClick(){
        if(user==null){
            user= DataBaseGetter.getInstance().getUser(txtUn.getText());
        }
        if(user ==null){
            StageManager.getInstance().showErrorDialog("No Such a Username!");
            return;
        }
        lblQ.setText(user.getSecurityQuestion());
        btnOk.setText("Done");
        txtUn.setEditable(false);
        txtA.setVisible(true);
        txtP.setVisible(true);
        MethodReturns m=user.securityQuestionPassEdit(txtA.getText(),txtP.getText());
        StageManager.getInstance().showDialog(m);
        if(m.equals(MethodReturns.DONE)){
            closeStage();
        }
    }

    @FXML
    private void closeStage(){
        ((Stage) txtA.getScene().getWindow()).close();
    }

    @FXML
    private void txtAChecker(){
        FrontManager.textFieldSetter(txtA,ControllerType.SECURITY_ANSWER);
    }
    @FXML
    private void txtUnChecker(){
        FrontManager.textFieldSetter(txtUn,ControllerType.OLD_USERNAME);
    }
    @FXML
    private void txtPChecker(){
        FrontManager.textFieldSetter(txtP,ControllerType.PASSWORD);
    }


}
