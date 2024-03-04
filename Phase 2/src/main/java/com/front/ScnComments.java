package com.front;

import com.back.messages.LikeView;
import com.back.messages.Message;
import com.back.messengers.Group;
import com.back.messengers.PV;
import com.back.messengers.Page;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class ScnComments {
    private Page page;
    private int postId;
    @FXML
    private VBox vbxMain;

    void setClass(Page page, int postid) {
        this.page = page;
        postId = postid;
        messageFiller();
    }

    private void listsRefresh() {
        page = Page.openPage(page.getOwnerUserName());
        messageFiller();
    }

    private void messageFiller() {
        List<Message> comments = page.getPostComments(postId);
        vbxMain.getChildren().clear();
        for (Message comment : comments) {
            vbxMain.getChildren().add(messageToPane(comment));
        }
    }

    private AnchorPane messageToPane(Message message) {
        message.viewedByLoggedInUser();
        double width = 530;
        ArrayList<Node> nodes = new ArrayList<>();

        ImageView profile = new ImageView();
        Image profileImage = DataBaseGetter.getInstance().getUserProfile(message.getSenderUserName());
        if (profileImage == null) {
            profileImage = new Image(ScnSettings.nullUrl);
        }
        profile.setImage(FrontManager.cropImage(profileImage));
        profile.setFitWidth(0.15 * width);
        profile.setFitHeight(0.15 * width);
        nodes.add(profile);

        Label sender = new Label();
        sender.setWrapText(false);
        sender.setPrefWidth(width * 0.6);
        sender.setText(message.getSender().getUserName());
        sender.setLayoutX(width * 0.166);
        sender.setLayoutY(profile.getFitHeight() / 2);
        sender.setFont(new Font("calibri", 20));
        nodes.add(sender);

        double icnSize = (width * 0.6) / 9;
        double i = 0.16 * width;
        double y = width * 0.011;
        {
            ImageView tempIcn = new ImageView(FrontManager.getIcnLiked(message.isLoggedUserLiked()));
            tempIcn.setFitWidth(icnSize);
            tempIcn.setFitHeight(icnSize);
            tempIcn.setLayoutX(i);
            tempIcn.setLayoutY(y);
            tempIcn.setId("a" + message.getKeyID());
            tempIcn.setCursor(Cursor.HAND);
            tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    likeMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
                }
            });
            nodes.add(tempIcn);
            i += icnSize * 1.1;
        }

        if (message.getSenderUserName().equals(User.getLoggedInUser().getUserName())) {
            ImageView tempIcn = new ImageView(FrontManager.getIcnDelete());
            tempIcn.setFitWidth(icnSize);
            tempIcn.setFitHeight(icnSize);
            tempIcn.setLayoutX(i);
            tempIcn.setLayoutY(y);
            tempIcn.setCursor(Cursor.HAND);
            tempIcn.setId("c" + message.getKeyID());
            tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    deleteMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
                }
            });
            nodes.add(tempIcn);
            i += icnSize * 1.1;
        }


        {
            ImageView tempIcn = new ImageView(FrontManager.getIcnEdited(message.getBooleanIsEdited()));
            tempIcn.setFitWidth(icnSize);
            tempIcn.setFitHeight(icnSize);
            tempIcn.setLayoutX(i);
            tempIcn.setLayoutY(y);
            if (message.getSenderUserName().equals(User.getLoggedInUser().getUserName())) {
                tempIcn.setCursor(Cursor.HAND);
            }
            tempIcn.setId("e" + message.getKeyID());
            tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (message.getSenderUserName().equals(User.getLoggedInUser().getUserName()))
                        editMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
                }
            });
            nodes.add(tempIcn);
            i += icnSize * 1.1;
        }


        if (DataBaseGetter.getInstance().getMessageFile(message) != null) {
            ImageView tempIcn = new ImageView(FrontManager.getIcnFile());
            tempIcn.setFitWidth(icnSize);
            tempIcn.setFitHeight(icnSize);
            tempIcn.setLayoutX(i);
            tempIcn.setLayoutY(y);
            tempIcn.setCursor(Cursor.HAND);
            tempIcn.setId("i" + message.getKeyID());
            tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    fileMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
                }
            });
            nodes.add(tempIcn);
            i += icnSize * 1.1;
        }

        if (message.getSender().isUserNameEqual(User.getLoggedInUser().getUserName())) {
            {
                ImageView tempIcn = new ImageView(FrontManager.getIcnShowLikes());
                tempIcn.setFitWidth(icnSize);
                tempIcn.setFitHeight(icnSize);
                tempIcn.setLayoutX(i);
                tempIcn.setLayoutY(y);
                tempIcn.setCursor(Cursor.HAND);
                tempIcn.setId("j" + message.getKeyID());
                tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        showLikesMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
                    }
                });
                nodes.add(tempIcn);
                i += icnSize * 1.1;
            }

        }


        Label text = new Label(message.getText());
        text.setMouseTransparent(true);
        text.setWrapText(true);
        text.setPrefWidth(width * 0.8);
        text.setLayoutY(width * 0.13);
        text.setLayoutX(width * 0.16);
        nodes.add(text);
        text.setFont(new Font("calibri", 15));


        Label sentTime = new Label(message.getSentTime());
        sentTime.setMouseTransparent(true);
        sentTime.setLayoutY(text.getLayoutY() + text.getHeight() + width * 0.02);
        sentTime.setLayoutX(width * 0.02);
        sentTime.setFont(new Font("calibri", 12));
        nodes.add(sentTime);

        Label sentDate = new Label(message.getSentDate());
        sentDate.setMouseTransparent(true);
        sentDate.setLayoutY(text.getLayoutY() + text.getHeight() + sentTime.getHeight() + width * 0.05);
        sentDate.setLayoutX(width * 0.02);
        sentDate.setFont(new Font("calibri", 12));
        nodes.add(sentDate);


        //   double h = Math.max(sentDate.getLayoutY() + sentDate.getHeight(), text.getLayoutY() + text.getHeight()) + width * 0.04;
        AnchorPane result = new AnchorPane();
        result.setPrefWidth(width);
        result.getChildren().addAll(nodes);
        result.setId(message.getStringKeyID());
        return result;
    }

    private void likeMessageClick(String id) {
        DataBaseGetter.getInstance().getMessage(id).likeDislikeByLoggedInUser();
        listsRefresh();
    }

    private void deleteMessageClick(String id) {
        int iD = Integer.parseInt(id);
        page.deletePost(iD);
        listsRefresh();
    }

    private void editMessageClick(String id) {
        Message message = DataBaseGetter.getInstance().getMessage(id);
        if (message == null) {
            return;
        }
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewNewMessageScene(message, null, false), "Edit Message");
        listsRefresh();
    }

    private void showLikesMessageClick(String id) {
        Message m = DataBaseGetter.getInstance().getMessage(id);
        ArrayList<String> list = new ArrayList<>();
        for (LikeView like : m.getLikes()) {
            list.add(like.getUserName());
        }
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(list, false), "Likes");
    }

    private void fileMessageClick(String id) {
        Message message = DataBaseGetter.getInstance().getMessage(id);
        Blob b = DataBaseGetter.getInstance().getMessageFile(message);
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewFileShowScene(b, message.getFileName()), "File");
        listsRefresh();
    }

}
