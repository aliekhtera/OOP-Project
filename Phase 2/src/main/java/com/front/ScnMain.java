package com.front;

import com.back.messages.LikeView;
import com.back.messages.Message;
import com.back.messengers.Group;
import com.back.messengers.Messenger;
import com.back.messengers.PV;

import com.back.messengers.Page;
import com.back.usersPackage.User;
import com.back.usersPackage.UserType;
import com.dataBase.DataBaseGetter;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ScnMain implements Initializable {
    private static ScnMain scnMain;

    public static ScnMain getScnMain() {
        return scnMain;
    }

    Group group1;


    @FXML
    private ListView<String> lstUsers, lstMessengerGroups;
    @FXML
    private Pane cpnMessengersList, cpnPageTitle, cpnGroupTitle, cpnPVTitle;
    @FXML
    private ImageView imgTitle, imgNewPost, imgViews, imgFollow, imgBlock;
    @FXML
    private Label lblTitle;
    @FXML
    private VBox vbxMessages;

    private List<PV> pvList;
    private List<Group> groupList;
    private Page myPage;
    private List<Page> pageList;

    @FXML
    public void listsRefresh() {
        pvList = DataBaseGetter.getInstance().getPVsOfUser(User.getLoggedInUser().getUserName());
        groupList = DataBaseGetter.getInstance().getGroupsOfUser(User.getLoggedInUser().getUserName());
        myPage = DataBaseGetter.getInstance().getPage(User.getLoggedInUser().getUserName());
        pageList = new ArrayList<>();
        pageList.add(myPage);
        for (String f : myPage.getFollowings()) {
            pageList.add(Page.openPage(f));
        }
        int index = lstUsers.getSelectionModel().getSelectedIndex();
        messengersListRefresh();
        if (index < lstUsers.getItems().size() && index >= 0) {
            lstUsers.getSelectionModel().select(index);
        }
        messageFiller();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        {
            ArrayList temp = new ArrayList();
            for (int i = 0; i < 3; i++) {
                temp.add("");
            }
            lstMessengerGroups.setItems(FXCollections.observableList(temp));
        }
        scnMain = this;
        // lstUsers.setItems(FXCollections.observableList(m));
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
        sender.setFont(new Font("calibri",20));
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
            ImageView tempIcn = new ImageView(FrontManager.getIcnReply());
            tempIcn.setFitWidth(icnSize);
            tempIcn.setFitHeight(icnSize);
            tempIcn.setLayoutX(i);
            tempIcn.setLayoutY(y);
            tempIcn.setCursor(Cursor.HAND);
            tempIcn.setId("d" + message.getKeyID());
            tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    replyMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
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

        {
            ImageView tempIcn = new ImageView(FrontManager.getIcnForward(message.getBooleanIsForwarded()));
            tempIcn.setFitWidth(icnSize);
            tempIcn.setFitHeight(icnSize);
            tempIcn.setLayoutX(i);
            tempIcn.setLayoutY(y);
            tempIcn.setCursor(Cursor.HAND);
            tempIcn.setId("g" + message.getKeyID());
            tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    forwardMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
                }
            });
            nodes.add(tempIcn);
            i += icnSize * 1.1;
        }

        if (message.getRepliedTo() >= 0) {
            ImageView tempIcn = new ImageView(FrontManager.getIcnReplied());
            tempIcn.setFitWidth(icnSize);
            tempIcn.setFitHeight(icnSize);
            tempIcn.setLayoutX(i);
            tempIcn.setLayoutY(y);
            tempIcn.setCursor(Cursor.HAND);
            tempIcn.setId("h" + message.getKeyID());
            tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getRepliedMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
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

            {
                ImageView tempIcn = new ImageView(FrontManager.getIcnViews(message.getLikes().size()));
                tempIcn.setFitWidth(icnSize);
                tempIcn.setFitHeight(icnSize);
                tempIcn.setLayoutX(i);
                tempIcn.setLayoutY(y);
                tempIcn.setCursor(Cursor.HAND);
                tempIcn.setId("k" + message.getKeyID());
                tempIcn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        showViewsMessageClick(((ImageView) mouseEvent.getSource()).getId().substring(1));
                    }
                });
                nodes.add(tempIcn);
            }
        }


        Label text = new Label(message.getText());
        text.setMouseTransparent(true);
        text.setWrapText(true);
        text.setPrefWidth(width * 0.8);
        text.setLayoutY(width * 0.13);
        text.setLayoutX(width * 0.16);
        text.setFont(new Font("calibri",15));

        nodes.add(text);


        Label sentTime = new Label(message.getSentTime());
        sentTime.setMouseTransparent(true);
        sentTime.setLayoutY(text.getLayoutY() + text.getHeight() + width * 0.02);
        sentTime.setLayoutX(width * 0.02);
        sentTime.setFont(new Font("calibri",12));
        nodes.add(sentTime);

        Label sentDate = new Label(message.getSentDate());
        sentDate.setMouseTransparent(true);
        sentDate.setLayoutY(text.getLayoutY() + text.getHeight() + sentTime.getHeight() + width * 0.05);
        sentDate.setLayoutX(width * 0.02);
        sentDate.setFont(new Font("calibri",12));

        nodes.add(sentDate);


        //   double h = Math.max(sentDate.getLayoutY() + sentDate.getHeight(), text.getLayoutY() + text.getHeight()) + width * 0.04;
        AnchorPane result = new AnchorPane();
        result.setPrefWidth(width);
        result.getChildren().addAll(nodes);
        result.setId(message.getStringKeyID());
        return result;
    }

    private AnchorPane postToPane(Message message) {
        message.viewedByLoggedInUser();
        return SceneManager.getInstance().postToAnchorPane(pageList.get(lstUsers.getSelectionModel().getSelectedIndex()), message);
    }

    private void messengersListRefresh() {
        Node c = lstUsers;

        for (Node child : cpnMessengersList.getChildren()) {
            if (child != null) {
                if (child.getId() != null) {
                    if (child.getId().equals("lstUsers")) {
                        c = child;
                        break;
                    }
                }
            }
        }
        cpnMessengersList.getChildren().clear();
        cpnMessengersList.getChildren().add(c);

        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 0) {
            ArrayList<String> times = new ArrayList<>();
            ArrayList<String> others = new ArrayList<>();
            ArrayList<Image> images = new ArrayList<>();
            for (PV pv : pvList) {
                if (pv.getMessages().size() > 0) {
                    Message m = pv.getMessages().get(pv.getMessages().size() - 1);
                    times.add(m.getSentDate());
                } else {
                    times.add("No Message");
                }
                String other;
                if (pv.getUser1().isUserNameEqual(User.getLoggedInUser().getUserName())) {
                    other = pv.getUser2().getUserName();
                } else {
                    other = pv.getUser1().getUserName();
                }
                Image image = DataBaseGetter.getInstance().getUserProfile(other);
                if (image == null) {
                    image = new Image(ScnSettings.nullUrl);
                }
                images.add(image);
                others.add(other);
            }
            messengersListRefresh(images, others, times);
        } else if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 1) {

            ArrayList<String> times = new ArrayList<>();
            ArrayList<Group> groups = new ArrayList<>();

            for (Group group : groupList) {
                if (group.getMessages().size() > 0) {
                    Message m = group.getMessages().get(group.getMessages().size() - 1);
                    times.add(m.getSentDate());
                } else {
                    times.add("No Message");
                }
                groups.add(group);
                messengerGroupListRefresh(groups, times);
            }
        } else if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 2) {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> times = new ArrayList<>();
            ArrayList<Image> images = new ArrayList<>();
            for (Page p : pageList) {
                names.add(p.getPageName());
                Image image = DataBaseGetter.getInstance().getUserProfile(p.getOwnerUserName());
                if (image == null) {
                    image = new Image(ScnSettings.nullUrl);
                }
                images.add(image);
                ArrayList<Message> posts = p.getPosts();
                if (posts.size() > 0) {
                    times.add(posts.get(posts.size() - 1).getSentDate());
                } else {
                    times.add("No Message");
                }
            }
            messengersListRefresh(images, names, times);
        }

    }

    private void messengerGroupListRefresh(ArrayList<Group> groups, ArrayList<String> times) {
        cpnMessengersList.setPrefHeight(lstUsers.getFixedCellSize() * (groups.size() + 1));
        lstUsers.setPrefHeight(lstUsers.getFixedCellSize() * (groups.size() + 1));
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < groups.size() && i < times.size(); i++) {
            temp.add("");
            Group group = groups.get(i);
            Image profile = DataBaseGetter.getInstance().getGroupProfile(group.getGroupID());
            if (profile == null) {
                profile = new Image(ScnSettings.nullUrl);
            }
            ImageView tempImageView = new ImageView(profile);
            Label unLabel = new Label(), dLabel = new Label();
            unLabel.setText(group.getGroupName());
            dLabel.setText(times.get(i));
            unLabel.setFont(new Font("Calibri", 25));
            dLabel.setFont(new Font("Calibri", 15));

            tempImageView.setFitWidth(lstUsers.getFixedCellSize() * 0.8);
            tempImageView.setFitHeight(lstUsers.getFixedCellSize() * 0.8);
            tempImageView.setLayoutY(lstUsers.getFixedCellSize() * (i + 0.1));
            tempImageView.setLayoutX(lstUsers.getFixedCellSize() * 0.1);
            unLabel.setLayoutX(lstUsers.getWidth() * 0.5);
            dLabel.setLayoutX(lstUsers.getWidth() * 0.5);
            unLabel.setLayoutY(lstUsers.getFixedCellSize() * (i + 0.1));
            dLabel.setLayoutY(lstUsers.getFixedCellSize() * (i + 0.5));
            tempImageView.setMouseTransparent(false);
            dLabel.setMouseTransparent(false);
            unLabel.setMouseTransparent(false);
            cpnMessengersList.getChildren().add(dLabel);
            cpnMessengersList.getChildren().add(unLabel);
            cpnMessengersList.getChildren().add(tempImageView);
        }
        lstUsers.setItems(FXCollections.observableList(temp));
    }

    private void messengersListRefresh(ArrayList<Image> images, ArrayList<String> names, ArrayList<String> times) {
        cpnMessengersList.setPrefHeight(lstUsers.getFixedCellSize() * (names.size() + 1));
        lstUsers.setPrefHeight(lstUsers.getFixedCellSize() * (names.size() + 1));
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < names.size() && i < times.size() && i < images.size(); i++) {
            temp.add("");
            ImageView tempImageView = new ImageView(images.get(i));
            Label unLabel = new Label(), dLabel = new Label();
            unLabel.setText(names.get(i));
            dLabel.setText(times.get(i));
            unLabel.setFont(new Font("Calibri", 25));
            dLabel.setFont(new Font("Calibri", 15));

            tempImageView.setFitWidth(lstUsers.getFixedCellSize() * 0.8);
            tempImageView.setFitHeight(lstUsers.getFixedCellSize() * 0.8);
            tempImageView.setLayoutY(lstUsers.getFixedCellSize() * (i + 0.1));
            tempImageView.setLayoutX(lstUsers.getFixedCellSize() * 0.1);
            unLabel.setLayoutX(lstUsers.getWidth() * 0.5);
            dLabel.setLayoutX(lstUsers.getWidth() * 0.5);
            unLabel.setLayoutY(lstUsers.getFixedCellSize() * (i + 0.1));
            dLabel.setLayoutY(lstUsers.getFixedCellSize() * (i + 0.5));
            tempImageView.setMouseTransparent(true);
            dLabel.setMouseTransparent(true);
            unLabel.setMouseTransparent(true);
            unLabel.setMaxWidth(lstUsers.getWidth()*0.48);
            cpnMessengersList.getChildren().add(dLabel);
            cpnMessengersList.getChildren().add(unLabel);
            cpnMessengersList.getChildren().add(tempImageView);
        }
        lstUsers.setItems(FXCollections.observableList(temp));


    }

    @FXML
    private void messageFiller() {
        if (lstUsers.getSelectionModel().getSelectedIndex() < 0) {
            return;
        }
        titleFiller();
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 0) {
            PV pv = pvList.get(lstUsers.getSelectionModel().getSelectedIndex());
            messageFiller(pv.getMessages());
            return;
        }

        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 1) {
            Group group = groupList.get(lstUsers.getSelectionModel().getSelectedIndex());
            messageFiller(group.getMessages());
            return;
        }

        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 2) {
            Page page = pageList.get(lstUsers.getSelectionModel().getSelectedIndex());
            postFiller(page.getPosts());
        }

    }

    private void messageFiller(List<Message> messages) {
        double l = 0;
        ArrayList<Pane> panes = new ArrayList<>();
        for (Message message : messages) {
            Pane temp = messageToPane(message);
            temp.setId(message.getStringKeyID());
            panes.add(temp);
            l += temp.getPrefHeight();
        }

        vbxMessages.getChildren().clear();
        vbxMessages.setPrefHeight(l);

        for (int i = panes.size() - 1; i >= 0; i--) {
            double w = vbxMessages.getWidth() - panes.get(i).getPrefWidth();
            if (messages.get(i).getSenderUserName().equals(User.getLoggedInUser().getUserName())) {
                w *= 0.99;
                panes.get(i).setStyle("-fx-background-color: rgb(44,215,54);");
            } else {
                w *= 0.01;
                panes.get(i).setStyle("-fx-background-color: rgb(30,203,234);");

            }
            AnchorPane temp=new AnchorPane();
            panes.get(i).setLayoutX(w);
            temp.getChildren().add(panes.get(i));
            panes.get(i).setLayoutX(w);
            vbxMessages.getChildren().add(temp);
            Label space=new Label("  ");
            space.setPrefWidth(vbxMessages.getWidth());
            space.setPrefHeight(20);
            if(i>0){
                vbxMessages.getChildren().add(space);
            }
            panes.get(i).setLayoutX(w);
        }

    }

    private void postFiller(List<Message> posts) {
        double l = 0;
        ArrayList<AnchorPane> panes = new ArrayList<>();
        for (Message post : posts) {
            AnchorPane temp = postToPane(post);
            temp.setId(post.getStringKeyID());
            panes.add(temp);
            l += temp.getPrefHeight();
        }

        vbxMessages.getChildren().clear();
        vbxMessages.setPrefHeight(l);

        for (int i = panes.size() - 1; i >= 0; i--) {
            double w = vbxMessages.getWidth() - panes.get(i).getPrefWidth();
            vbxMessages.getChildren().add(panes.get(i));
            panes.get(i).setLayoutX(w / 2);
        }
    }

    private void titleFiller() {
        try {
            if (lstUsers.getSelectionModel().getSelectedIndex() < 0) {
                imgTitle.setImage(null);
                lblTitle.setText("");
                cpnPVTitle.setVisible(false);
                cpnGroupTitle.setVisible(false);
                cpnPageTitle.setVisible(false);
                return;
            } else if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 0) {
                PV pv = pvList.get(lstUsers.getSelectionModel().getSelectedIndex());
                User otherUser;
                if (pv.getUser1().getUserName().equals(User.getLoggedInUser())) {
                    otherUser = pv.getUser2();
                } else {
                    otherUser = pv.getUser1();
                }
                Image image = DataBaseGetter.getInstance().getUserProfile(otherUser.getUserName());
                if (image == null) {
                    image = new Image(ScnSettings.nullUrl);
                }
                imgTitle.setImage(FrontManager.cropImage(image));
                lblTitle.setText(otherUser.getFirstName() + " " + otherUser.getLastName());
                cpnPVTitle.setVisible(true);
                cpnGroupTitle.setVisible(false);
                cpnPageTitle.setVisible(false);
                return;
            } else if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 1) {
                Group group = groupList.get(lstUsers.getSelectionModel().getSelectedIndex());
                Group.setOpenedGroup(group.getGroupID());

                Image image = DataBaseGetter.getInstance().getGroupProfile(group.getGroupID());
                if (image == null) {
                    image = new Image(ScnSettings.nullUrl);
                }
                imgTitle.setImage(FrontManager.cropImage(image));
                lblTitle.setText(group.getGroupName());
                cpnPVTitle.setVisible(false);
                cpnGroupTitle.setVisible(true);
                cpnPageTitle.setVisible(false);
                return;
            } else if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 2) {

                Page page = pageList.get(lstUsers.getSelectionModel().getSelectedIndex());
                page.viewThisPage();
                Image image = DataBaseGetter.getInstance().getUserProfile(page.getOwnerUserName());
                if (image == null) {
                    image = new Image(ScnSettings.nullUrl);
                }

                imgTitle.setImage(FrontManager.cropImage(image));
                lblTitle.setText(page.getPageName());
                boolean temp = User.getLoggedInUser().isUserNameEqual(page.getOwnerUserName());

                imgViews.setVisible(temp && User.getLoggedInUser().getUserType().equals(UserType.BUSINESS_USER));
                imgNewPost.setVisible(temp);
                imgFollow.setVisible(!temp);
                imgBlock.setVisible(!temp);
                imgFollow.setImage(new Image(FrontManager.getIcnFollow(page.getFollowers().contains(User.getLoggedInUser().getUserName()))));
                imgBlock.setImage(new Image(FrontManager.getIcnBlock(page.getBlocked().contains(User.getLoggedInUser().getUserName()))));
                cpnPVTitle.setVisible(false);
                cpnGroupTitle.setVisible(false);
                cpnPageTitle.setVisible(true);
                return;
            }
        } catch (Exception e) {

        }
    }

    @FXML
    private void openSettingScene() {
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewSettingsScene(), "Settings");
    }

    ///////////////////////////Search///////////////////////////////

    @FXML
    private void search() {
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewSearchScene(), "Search!");
    }


    //////////////// Message ///////////////////////////////////////

    private void likeMessageClick(String id) {
        DataBaseGetter.getInstance().getMessage(id).likeDislikeByLoggedInUser();
        listsRefresh();
    }

    private void deleteMessageClick(String id) {
        if (lstUsers.getSelectionModel().getSelectedIndex() < 0) {
            return;
        }
        int iD = Integer.parseInt(id);
        int index = lstUsers.getSelectionModel().getSelectedIndex();
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 0) {
            pvList.get(index).deleteMessage(iD);
        } else if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 1) {
            groupList.get(index).deleteMessage(iD);
        } else if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 2) {
            myPage.deletePost(iD);
        }
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

    private void forwardMessageClick(String id) {
        List<Messenger> messengers = new ArrayList<>();
        messengers.addAll(pvList);
        messengers.addAll(groupList);
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(messengers, true), "Forward Message");
        if (messengers.size() != 2) {
            return;
        }
        if (messengers.get(1) != null) {
            return;
        }
        messengers.get(0).forwardMessage(Integer.parseInt(id));
        listsRefresh();

    }

    private void getRepliedMessageClick(String id) {
        int iD = DataBaseGetter.getInstance().getMessage(id).getRepliedTo();
        if (iD < 0) {
            return;
        }
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewRepliedMessageScene(iD + ""), "");
    }

    private void fileMessageClick(String id) {
        Message message = DataBaseGetter.getInstance().getMessage(id);
        Blob b = DataBaseGetter.getInstance().getMessageFile(message);
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewFileShowScene(b, message.getFileName()), "File");
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

    private void showViewsMessageClick(String id) {
        Message m = DataBaseGetter.getInstance().getMessage(id);
        ArrayList<String> list = new ArrayList<>();
        for (LikeView like : m.getViews()) {
            list.add(like.getUserName());
        }
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(list, false), "Views");
    }

    private void replyMessageClick(String id) {
        if (lstUsers.getSelectionModel().getSelectedIndex() < 0) {
            return;
        }
        Message message = new Message(User.getLoggedInUser(), "", "", "", false, -1, false, new ArrayList<>(), new ArrayList<>());
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewNewMessageScene(message, DataBaseGetter.getInstance().getMessage(id), false), "Reply Message");
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 0) {
            pvList.get(lstUsers.getSelectionModel().getSelectedIndex()).sendMessage(message);
        } else if (lstMessengerGroups.getSelectionModel().getSelectedIndex() == 1) {
            groupList.get(lstUsers.getSelectionModel().getSelectedIndex()).sendMessage(message);
        }
        listsRefresh();
    }

    ////////////////////// Group ///////////////////////

    @FXML
    private void addNewGroup() {
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewNewGroupScene(), "New Group");
        ScnMain.getScnMain().listsRefresh();
    }

    @FXML
    private void groupSettings() {
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() != 1) {
            return;
        }
        if (lstUsers.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }
        Group group = groupList.get(lstUsers.getSelectionModel().getSelectedIndex());
        Boolean ban = false;
        for (String s : DataBaseGetter.getInstance().getbannedAccounts(group)) {
            if (s.equals(User.getLoggedInUser().getUserName())) {
                ban = true;
            }
        }
        if (ban) {
            StageManager.getInstance().showErrorDialog("You are banned");
        } else {
            StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewGroupSettingScene(), "Group Setting");
        }

        ScnMain.getScnMain().listsRefresh();
    }

    @FXML
    private void sendNewMessageGroup() {
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() != 1) {
            return;
        }
        if (lstUsers.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }
        Group group = groupList.get(lstUsers.getSelectionModel().getSelectedIndex());

        Boolean ban = false;
        for (String s : DataBaseGetter.getInstance().getbannedAccounts(group)) {
            if (s.equals(User.getLoggedInUser().getUserName())) {
                ban = true;
            }
        }
        if (ban) {
            StageManager.getInstance().showErrorDialog("You are banned");
        } else {
            Message message = new Message(User.getLoggedInUser(), "", "", "", false, -1, false, new ArrayList<>(), new ArrayList<>());
            StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewNewMessageScene(message, null, false), "New Message");
            group.sendMessage(message);
            listsRefresh();
        }

    }

    @FXML
    private void ban() {
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() != 1) {
            return;
        }
        if (lstUsers.getSelectionModel().getSelectedIndex() == -1) {
            return;

        }
        Group group = groupList.get(lstUsers.getSelectionModel().getSelectedIndex());

        List<String> userNames = new ArrayList<>();
        for (User member : group.getMembers()) {
            userNames.add(member.getUserName());
        }
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(userNames, true), "Ban" );
        if (userNames.size() != 2 ) {
            return;
        }
        if (userNames.get(1) != null) {
            return;
        }
        if (!(userNames.get(0).equals(group.getAdmin().getUserName())) ){
            group.banMember(group.getAdmin(), group, userNames.get(0));

        }

        ScnMain.getScnMain().listsRefresh();

    }

    @FXML
    private void remove() {
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() != 1) {
            return;
        }
        if (lstUsers.getSelectionModel().getSelectedIndex() == -1) {
            return;

        }
        Group group = groupList.get(lstUsers.getSelectionModel().getSelectedIndex());

        List<String> userNames = new ArrayList<>();
        for (User member : group.getMembers()) {
            userNames.add(member.getUserName());
        }
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(userNames, true), "Ban" );
        if (userNames.size() != 2 ) {
            return;
        }
        if (userNames.get(1) != null) {
            return;
        }
        if (!(userNames.get(0).equals(group.getAdmin().getUserName())) ){
            User user = DataBaseGetter.getInstance().getUser(userNames.get(0) );
            group.removeUser(group.getAdmin(), user,group);

        }
        ScnMain.getScnMain().listsRefresh();

    }


    /////////////////// PV //////////////////

    @FXML
    private void sendNewMessagePV() {
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() != 0) {
            return;
        }
        if (lstUsers.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }
        PV pv = pvList.get(lstUsers.getSelectionModel().getSelectedIndex());
        User user;
        if (pv.getUser1().isUserNameEqual(User.getLoggedInUser().getUserName())) {
            user = pv.getUser2();
        } else {
            user = pv.getUser1();
        }
        if (User.isLoggedUserBlocked(user.getUserName())) {
            StageManager.getInstance().showErrorDialog("You Are Blocked.");
            return;
        }
        Message message = new Message(User.getLoggedInUser(), "", "", "", false, -1, false, new ArrayList<>(), new ArrayList<>());
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewNewMessageScene(message, null, false), "New Message");
        pv.sendMessage(message);
        listsRefresh();
    }

    @FXML
    private void blockPV() {
        if (lstMessengerGroups.getSelectionModel().getSelectedIndex() != 0) {
            return;
        }
        if (lstUsers.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }
        PV pv = pvList.get(lstUsers.getSelectionModel().getSelectedIndex());
        User user;
        if (pv.getUser1().isUserNameEqual(User.getLoggedInUser().getUserName())) {
            user = pv.getUser2();
        } else {
            user = pv.getUser1();
        }
        User.blockUnBlockUser(user.getUserName());
    }

    //////////////////////////////////////

    public void setGroup1(Group group1) {
        this.group1 = group1;
    }

    ////////// Page ////////////////////
    @FXML
    private void newPost() {
        try {
            if (lstUsers.getSelectionModel().getSelectedIndex() == -1 ||
                    lstMessengerGroups.getSelectionModel().getSelectedIndex() != 2) {
                return;
            }
            Page page = pageList.get(lstUsers.getSelectionModel().getSelectedIndex());
            Message message = new Message(User.getLoggedInUser(), "", "", "", false, -1, false, new ArrayList<>(), new ArrayList<>());
            StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewNewMessageScene(message, null, false, false), "New Post");
            page.newPost(message);
            listsRefresh();
        } catch (Exception e) {

        }
    }

    @FXML
    private void pageView() {
        try {
            if (lstUsers.getSelectionModel().getSelectedIndex() == -1 ||
                    lstMessengerGroups.getSelectionModel().getSelectedIndex() != 2) {
                return;
            }
            Page page = pageList.get(lstUsers.getSelectionModel().getSelectedIndex());
            ArrayList<String> v = new ArrayList<>();
            for (LikeView l : page.getPageViews()) {
                v.add(l.getUserName() + " : " + l.getDate());
            }
            StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(v, false), "Views");
        } catch (Exception e) {

        }
    }

    @FXML
    private void pageFollow() {
        try {
            if (lstUsers.getSelectionModel().getSelectedIndex() == -1 ||
                    lstMessengerGroups.getSelectionModel().getSelectedIndex() != 2) {
                return;
            }
            Page page = pageList.get(lstUsers.getSelectionModel().getSelectedIndex());
            if (page.getFollowers().contains(User.getLoggedInUser().getUserName())) {
                page.unfollow();
            } else {
                page.follow();
            }
            titleFiller();
            // listsRefresh();
        } catch (Exception e) {

        }
    }

    @FXML
    private void pageBlock() {
        try {
            if (lstUsers.getSelectionModel().getSelectedIndex() == -1 ||
                    lstMessengerGroups.getSelectionModel().getSelectedIndex() != 2) {
                return;
            }
            Page page = pageList.get(lstUsers.getSelectionModel().getSelectedIndex());
            if (page.getBlocked().contains(User.getLoggedInUser().getUserName())) {
                page.unblock(User.getLoggedInUser().getUserName());
            } else {
                myPage.block(page.getOwnerUserName());
            }
            titleFiller();
            // listsRefresh();
        } catch (Exception e) {

        }
    }

    @FXML
    private void pageShowFollowers() {
        try {
            if (lstUsers.getSelectionModel().getSelectedIndex() == -1 ||
                    lstMessengerGroups.getSelectionModel().getSelectedIndex() != 2) {
                return;
            }
            Page page = pageList.get(lstUsers.getSelectionModel().getSelectedIndex());
            ArrayList<String> pn = new ArrayList<>();
            for (String f : page.getFollowers()) {
                pn.add(Page.openPage(f).getPageName());
            }
            StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(pn, false), "Followers");
            //  listsRefresh();
        } catch (Exception e) {

        }
    }

    @FXML
    void pageShowFollowings() {
        try {
            if (lstUsers.getSelectionModel().getSelectedIndex() == -1 ||
                    lstMessengerGroups.getSelectionModel().getSelectedIndex() != 2) {
                return;
            }
            Page page = pageList.get(lstUsers.getSelectionModel().getSelectedIndex());
            ArrayList<String> pn = new ArrayList<>();
            for (String f : page.getFollowings()) {
                pn.add(Page.openPage(f).getPageName());
            }
            StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(pn, false), "Followings");
            //       listsRefresh();
        } catch (Exception e) {

        }
    }

}
