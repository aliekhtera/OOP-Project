package com.front;

import com.back.MethodReturns;
import com.dataBase.DataBaseGetter;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FrontManager extends Application {
    public static void startProgram(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("");// todo title
        stage.setResizable(false);
        stage.setScene(SceneManager.getInstance().getNewLoginScene());
        stage.show();
        StageManager.getInstance().setMainStage(stage);

        /*  FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();*/
    }

    static void textFieldSetter(TextField textField, ControllerType controllerType) {
        String text = textField.getText();
        Boolean flag = null;
        if (text == null) {
            text = "";
        }
        int l = text.length();
        switch (controllerType) {
            case PASSWORD:
                flag = (l > 7 && l < 45);
                break;
            case NEW_USERNAME:
                flag = (l > 0 && l < 45 && DataBaseGetter.getInstance().getUser(text) == null);
                break;
            case OLD_USERNAME:
                flag = (l > 0 && l < 45 && DataBaseGetter.getInstance().getUser(text) != null);
                break;
            case SECURITY_ANSWER:
            case LAST_NAME:
            case FIRST_NAME:
                flag = (l > 0 && l < 45);
                break;
            case GROUP_ID:
            case REMOVE:
            case BAN:
                flag = (DataBaseGetter.getInstance().getGroup(text) != null);
                break;
            case USERNAME :
                flag = (DataBaseGetter.getInstance().getUser(text) != null);
                break;
            case NEW_GROUP_ID:
                flag = (DataBaseGetter.getInstance().getGroup(text) == null);
                break;
            case NEW_GROUP_NAME:
                flag = (l > 0);
                break;

        }
        if (flag == null) {
            return;
        }
        if (flag) {
            textField.setStyle("-fx-text-box-border : #438d46 ; -fx-focus-color :#438d46;");
        } else {
            textField.setStyle("-fx-text-box-border : #B22222 ; -fx-focus-color :#B22222;");
        }
    }


    static String openImage(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Image Files",
                "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File result = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        return fileToImage(imageView, result);
    }

    public static String fileToImage(ImageView imageView, File file) {
        if (file == null) {
            return null;
        }
        Image oldImage = new Image(file.toURI().toString());
        Image newImage = cropImage(oldImage);
        imageView.setImage(newImage);
        return file.getAbsolutePath();
    }

    public static Image cropImage(Image oldImage) {
        Image newImage;
        if (oldImage.getWidth() != oldImage.getHeight()) {
            int x, y, a;
            if (oldImage.getWidth() > oldImage.getHeight()) {
                y = 0;
                a = (int) oldImage.getHeight();
                x = (int) ((oldImage.getWidth() - oldImage.getHeight()) / 2);
            } else {
                x = 0;
                a = (int) oldImage.getWidth();
                y = (int) (oldImage.getHeight() - oldImage.getWidth()) / 2;
            }
            newImage = new WritableImage(oldImage.getPixelReader(), x, y, a, a);
        } else {
            newImage = oldImage;
        }
        return newImage;
    }


    static String getIcnLiked(boolean isLiked) {
        if (isLiked) {
            return FrontManager.class.getResource("icnLike.png").toString();
        } else {
            return FrontManager.class.getResource("icnNLike.png").toString();
        }
    }


    static String getIcnEdited(boolean isEdited) {
        if (isEdited)
            return FrontManager.class.getResource("icnEdited.png").toString();
        else
            return FrontManager.class.getResource("icnNEdited.png").toString();
    }


    static String getIcnReply() {
        return FrontManager.class.getResource("icnReply.png").toString();
    }

    static String getIcnReplied() {
        return FrontManager.class.getResource("icnRepliedMessage.png").toString();
    }

    static String getIcnForward(boolean isForwarded) {
        if (isForwarded)
            return FrontManager.class.getResource("icnForward.png").toString();
        else
            return FrontManager.class.getResource("icnNForward.png").toString();
    }

    static String getIcnDelete() {
        return FrontManager.class.getResource("icnDelete.png").toString();
    }

    static String getIcnViews(int n) {
        if (n > 0)
            return FrontManager.class.getResource("icnViews.png").toString();
        else
            return FrontManager.class.getResource("icnNViews.png").toString();
    }

    static String getIcnShowLikes() {
        return FrontManager.class.getResource("icnLikes.png").toString();
    }

    static String getIcnFile() {
        return FrontManager.class.getResource("icnFile.png").toString();
    }

    public static String getIcnNewComment() {
        return FrontManager.class.getResource("icnFile.png").toString();
    }

    public static String getIcnComments() {
        return FrontManager.class.getResource("icnFile.png").toString();
    }

    static String getIcnBlock(boolean b) {
        if (b)
            return FrontManager.class.getResource("icnBlock.png").toString();
        else
            return FrontManager.class.getResource("icnNBlock.png").toString();
    }

    static String getIcnFollow(boolean b) {
        if (!b)
            return FrontManager.class.getResource("icnFollow.png").toString();
        else
            return FrontManager.class.getResource("icnNFollow.png").toString();
    }

}
