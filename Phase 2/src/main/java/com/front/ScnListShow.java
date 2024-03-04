package com.front;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class ScnListShow {
    @FXML
    ListView lstMain;
    @FXML
    Button btnSelect;
    List<Object> list;
    public void setListShow  (List<Object> l,boolean selectable) {
        lstMain.setItems(FXCollections.observableList(l));
        btnSelect.setVisible(selectable);
        list=l;
    }

    @FXML
    void select(){
        if(lstMain.getSelectionModel().getSelectedIndex()<0){
            StageManager.getInstance().showErrorDialog("Please Select An Option!");
            return;
        }else{
            int index=lstMain.getSelectionModel().getSelectedIndex();
            for (int i = 0; i < index; i++) {
                list.remove(0);
            }
            int size=list.size();
            for (int i = 1; i < size; i++) {
                list.remove(1);
            }
            list.add(null);
            ((Stage)lstMain.getScene().getWindow()).close();
        }
    }
}
