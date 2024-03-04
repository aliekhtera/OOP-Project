package com.front;

import com.back.MethodReturns;
import com.back.messages.Message;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Blob;

public class ScnNewMessage {
    @FXML
    private ImageView imageView,imgFile;
    @FXML
    private TextArea txtText;

    private File file;
    private Message message, rep;
    private boolean fileEdit, isForwarded;

    private final static String fileAddress = ScnNewMessage.class.getResource("icnFile.png").toString();

    public void setMessage(Message mainMessage, Message rep, boolean isForwarded,boolean canSendFile) {
        imgFile.setVisible(canSendFile);
        this.message = mainMessage;
        fileEdit = false;
        this.rep = rep;
        this.isForwarded = isForwarded;
        if (mainMessage == null) {
            return;
        }
        if(mainMessage.getKeyID()<0){
            return;
        }
        try {
            Blob blob = DataBaseGetter.getInstance().getMessageFile(mainMessage);
            if (mainMessage.getFileName().contains(".png") || mainMessage.getFileName().contains(".jpg") || mainMessage.getFileName().contains(".jpeg")) {
                imageView.setImage(new Image(blob.getBinaryStream()));
            }
            txtText.setText(message.getText());
        } catch (Exception e) {

        }

    }

    @FXML
    private void done() {
        try {
            if (txtText.getText().isEmpty()) {
                StageManager.getInstance().showDialog(MethodReturns.BAD_INPUT);
                return;
            }
        } catch (Exception e) {
            StageManager.getInstance().showDialog(MethodReturns.BAD_INPUT);
            return;
        }
        if (message.getKeyID() < 0) {
            message.setAllFields(Message.newMessage(txtText.getText(), rep, isForwarded));
        } else {
            Message.editMessage(message.getKeyID(), txtText.getText());
        }
        if (fileEdit) {
            if (StageManager.getInstance().showDialog(DataBaseSetter.getInstance().editMessageFile(message.getStringKeyID(), file))) {
                close();
            }
        } else {
            StageManager.getInstance().showDoneDialog();
            close();
        }

    }

    @FXML
    private void newImage() {
        String result = FrontManager.openImage(imageView);
        if (result == null) {
            return;
        }
        fileEdit = true;
        file = new File(result);
        imageView.setImage(new Image(result));
    }

    @FXML
    private void newFile() {
        FileChooser fileChooser=new FileChooser();
        File f=fileChooser.showOpenDialog(txtText.getScene().getWindow());
        if (f == null) {
            return;
        }
        imageView.setImage(new Image(fileAddress));
        file=f;
        fileEdit=true;
    }

    @FXML
    private void removeFile() {
        fileEdit = true;
        file = null;
        imageView.setImage(null);
    }

    @FXML
    private void close(){
        ((Stage)imageView.getScene().getWindow()).close();
    }

}
