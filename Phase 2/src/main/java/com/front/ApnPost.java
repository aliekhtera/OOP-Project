package com.front;

import com.back.messages.LikeView;
import com.back.messages.Message;
import com.back.messengers.Group;
import com.back.messengers.Page;
import com.back.usersPackage.User;
import com.back.usersPackage.UserType;
import com.dataBase.DataBaseGetter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class ApnPost {
    Message post;
    Page page;
    @FXML
    ImageView imgDelete, imgLike, imgViews, imgPic;
    @FXML
    Label lblText, lblSender,lblTime;

    public void setClass(Message post, Page page, Blob blob) {
        this.page = page;
        this.post = post;
        imgLike.setImage(new Image(FrontManager.getIcnLiked(post.isLoggedUserLiked())));
        imgViews.setImage(new Image(FrontManager.getIcnViews(post.getViews().size())));
        try {
            imgPic.setImage(new Image(blob.getBinaryStream()));
        } catch (Exception e) {
        }
        lblText.setText(post.getText());
        lblSender.setText(page.getPageName());
        imgDelete.setVisible(post.getSender().isUserNameEqual(User.getLoggedInUser().getUserName()));
        imgViews.setVisible(post.getSender().isUserNameEqual(User.getLoggedInUser().getUserName()));
        lblText.setText(post.getText());
        lblTime.setText(post.getSentTime() +"   "+post.getSentDate());
    }

    @FXML
    private void delete() {
        page.deletePost(post.getKeyID());
        ScnMain.getScnMain().listsRefresh();
    }

    @FXML
    private void like() {
        post.likeDislikeByLoggedInUser();
        ScnMain.getScnMain().listsRefresh();
    }

    @FXML
    private void comments() {
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewCommentScene(page,post.getKeyID()),"Comments" );
        ScnMain.getScnMain().listsRefresh();
    }

    @FXML
    private void views() {
        List<String> views = new ArrayList<>();
        if (post.getSender().getUserType().equals(UserType.BUSINESS_USER)) {
            for (LikeView view : post.getViews()) {
                views.add(view.getDate() + "  " + view.getTime() + "  : (" + view.getUserName() + ")");
            }
        } else {
            for (LikeView view : post.getViews()) {
                views.add(view.getUserName());
            }
        }
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(views, false), "Views");
    }

    @FXML
    private void showLikes() {
        List<String> views = new ArrayList<>();
        if (post.getSender().getUserType().equals(UserType.BUSINESS_USER)) {
            for (LikeView view : post.getLikes()) {
                views.add(view.getDate() + "  " + view.getTime() + "  : (" + view.getUserName() + ")");
            }
        } else {
            for (LikeView view : post.getLikes()) {
                views.add(view.getUserName());
            }
        }
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewListShowScene(views, false), "Likes");

    }

    @FXML
    private void newComment(){
        Message message = new Message(User.getLoggedInUser(), "", "", "", false, -1, false, new ArrayList<>(), new ArrayList<>());
        StageManager.getInstance().openNewStage(SceneManager.getInstance().getNewNewMessageScene(message,post, false), "New Message");
        page.newComment(message,post.getKeyID());
    }

}
