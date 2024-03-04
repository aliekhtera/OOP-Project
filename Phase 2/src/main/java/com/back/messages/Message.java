package com.back.messages;

import com.back.GeneralMethods;
import com.back.MethodReturns;
import com.back.messengers.Messenger;
import com.back.usersPackage.User;
import com.back.usersPackage.UserType;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Message {
    protected int keyID;//-1 for null
    protected User sender;
    protected String text;
    private String fileName;
    protected String sentTime;
    protected String sentDate;
    protected boolean isEdited;
    protected int repliedTo; //id
    protected ArrayList<LikeView> views;
    protected ArrayList<LikeView> likes;
    protected boolean isForwarded;


    public Message(User sender, String text, String sentTime, String sentDate, boolean isEdited, int repliedTo, boolean isForwarded, ArrayList<LikeView> views, ArrayList<LikeView> likes) {
        this.keyID = -1;
        this.sender = sender;
        this.text = text;
        this.sentTime = sentTime;
        this.sentDate = sentDate;
        this.isEdited = isEdited;
        this.repliedTo = repliedTo;
        this.views = new ArrayList<>(views);
        this.likes = new ArrayList<>(likes);
        this.isForwarded = isForwarded;
        fileName = null;
    }

    private Message(Message m) {
        this.keyID = m.keyID;
        this.sender = m.sender;
        this.text = m.text;
        this.sentTime = m.sentTime;
        this.sentDate = m.sentDate;
        this.isEdited = m.isEdited;
        this.views = new ArrayList<>(m.views);
        this.likes = new ArrayList<>(m.likes);
        this.isForwarded = m.isForwarded;
        this.repliedTo = m.repliedTo;
    }

    public static ArrayList<String> messageArrayListToStringArray(ArrayList<Message> input) {
        ArrayList<String> result = new ArrayList<>();
        String temp;
        for (Message message : input) {
            temp = message.sender.getUserName() + " : ";
            if (message.isForwarded) {
                temp += " FORWARDED!";
            } else if (message.isEdited) {
                temp += " EDITED!";
            }
            if (message.repliedTo >= 0) {
                for (int i = 0; i < input.size(); i++) {
                    if (input.get(i).keyID == message.repliedTo) {
                        temp += " REPLIED " + (i + 1);
                        break;
                    }
                }
            }
            temp += "\n";
            temp += message.getText();
            temp += " \n";
            temp += message.sentDate + "  " + message.sentTime + "\n";
            result.add(temp);
        }
        return result;
    }

    public static Message newMessage(String text, Message repliedTo, boolean isForwarded) { // null OR with ID(DB)
        if (!GeneralMethods.getInstance().notEmptyStrings()) {
            return null;
        }
        if (User.getLoggedInUser() == null) {
            return null; /// No User
        }
        String time = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        String date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now());
        int rep = -1;
        if (repliedTo != null) {
            rep = repliedTo.getKeyID();
        }
        Message message = new Message(User.getLoggedInUser(), text, time, date, false, rep, isForwarded, new ArrayList<>(), new ArrayList<>());

        Message result = DataBaseSetter.getInstance().addNewMessageToDataBase(message);
        if (result == null) {
            return null;
        }
        if (result.keyID < 0) {
            return null;
        }
        return result;

    }

    public boolean editMessage(String text) {
        if (isForwarded) {
            return false;
        }
        isEdited = true;
        this.text = text;
        return DataBaseSetter.getInstance().editMessageText(this);
    }

    public String getSentTime() {
        return sentTime;
    }

    public String getSentDate() {
        return sentDate;
    }

    public String getSenderUserName() {
        try {
            return sender.getUserName();
        } catch (Exception e) {
            return null;
        }
    }

    public String getText() {
        return text;
    }

    public int getRepliedTo() {
        return repliedTo;
    }

    public ArrayList<LikeView> getLikes() {
        return likes;
    }

    public ArrayList<LikeView> getViews() {
        return views;
    }

    public User getSender() {
        return sender;
    }

    public String getIsEdited() {
        if (isEdited) {
            return "1";
        } else {
            return "0";
        }
    }

    public String getIsForwarded() {
        if (isForwarded) {
            return "1";
        } else {
            return "0";
        }
    }

    public void setKeyID(int keyID) {
        if (keyID < 0) {
            return;
        }
        this.keyID = keyID;
    }

    public int getKeyID() {
        return keyID;
    }

    public String getStringKeyID() {
        if (keyID < 0) {
            return "";
        }
        return Integer.toString(keyID);
    }

    public static void messageView(ArrayList<String> messageIDs) {
        for (String messageID : messageIDs) {
            Message temp = DataBaseGetter.getInstance().getMessage(messageID);
            if (temp != null) {
                temp.viewedByLoggedInUser();
            }
        }
    }


    public void likeDislikeByLoggedInUser() {
        int index = -1;
        for (int i = 0; i < likes.size() && index == -1; i++) {
            if (User.getLoggedInUser().isUserNameEqual(likes.get(i).getUserName())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            likes.add(new LikeView());
        } else {
            likes.remove(index);
        }
        DataBaseSetter.getInstance().editMessageLikes(this);
    }

    public void viewedByLoggedInUser() {
        if (sender.getUserName().equals(User.getLoggedInUser().getUserName())) {
            return;
        }
        for (LikeView view : views) {
            if (view.getUserName().equals(User.getLoggedInUser().getUserName())) {
                return;
            }
        }
        views.add(new LikeView());
        DataBaseSetter.getInstance().editMessageViews(this);
    }

    public void forwardMessage(Messenger messenger) {
        messenger.sendMessage(this.getText(), null, true);
    }

    public static MethodReturns editMessage(int messageID, String newText) {
        Message m = DataBaseGetter.getInstance().getMessage(Integer.toString(messageID));
        if (m == null) {
            return MethodReturns.NO_SUCH_OBJECT;
        }
        if (!m.getSender().getUserName().equals(User.getLoggedInUser().getUserName())) {
            return MethodReturns.NO_SUCH_OBJECT;
        }

        if (m.editMessage(newText)) {
            return MethodReturns.DONE;
        } else {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }

    }

    public static ArrayList<String> postsToStringToPrint(ArrayList<Message> inp) {
        String temp;
        ArrayList<String> result = new ArrayList<>();
        for (Message message : inp) {
            if (message != null) {
                temp = "";
                if (message.getSender().getUserType().equals(UserType.BUSINESS_USER)) {
                    temp += "AD! ";
                }
                for (LikeView like : message.likes) {

                }

                temp += "Likes = " + message.likes.size();
                temp += "  / Views = " + message.views.size();
                if (message.isLoggedUserLiked()) {
                    temp += "  " + "Liked By You!";
                }
                temp += "\n";
                temp += message.text + "\n";
                temp += message.sentDate + "  " + message.sentTime + "\n";
                result.add(temp);
            }
        }
        return result;
    }

    public static ArrayList<String> commentToStringToPrint(int postID, ArrayList<Message> comments) {
        String temp;
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Message> added = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            temp = "";
            Message comment = comments.get(i);
            if (comment != null) {
                if (comment.getRepliedTo() == postID) {
                    added.add(comment);
                    temp += "Likes = " + comment.likes.size();
                    temp += "  / Views = " + comment.views.size();
                    if (comment.isLoggedUserLiked()) {
                        temp += "  " + "Liked By You!";
                    }
                    temp += "\n";
                    temp += comment.text + "\n";
                    temp += comment.sentDate + "  " + comment.sentTime + "\n";
                    result.add(temp);
                } else {
                    int index = -1;
                    for (int i1 = 0; i1 < added.size() && index == -1; i1++) {
                        if (comment.getRepliedTo() == added.get(i1).getKeyID()) {
                            index = i1;
                        }
                    }
                    if (index != -1) {
                        added.add(comment);
                        temp += " REPLIED TO:" + (index + 1) + "  ";
                        temp += "Likes = " + comment.likes.size();
                        temp += "  / Views = " + comment.views.size();
                        if (comment.isLoggedUserLiked()) {
                            temp += "  " + "Liked By You!";
                        }
                        temp += "\n";
                        temp += comment.text + "\n";
                        temp += comment.sentDate + "  " + comment.sentTime + "\n";
                        result.add(temp);
                    }
                }

            }
        }
        return result;
    }

    public boolean isLoggedUserLiked() {
        for (LikeView like : likes) {
            if (User.getLoggedInUser().isUserNameEqual(like.getUserName())) {
                return true;
            }
        }
        return false;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setAllFields(Message m) {
        this.keyID = m.keyID;
        this.sender = m.sender;
        this.text = m.text;
        this.sentTime = m.sentTime;
        this.sentDate = m.sentDate;
        this.isEdited = m.isEdited;
        this.views = new ArrayList<>(m.views);
        this.likes = new ArrayList<>(m.likes);
        this.isForwarded = m.isForwarded;
        this.repliedTo = m.repliedTo;
        this.fileName = m.fileName;
    }

    public boolean getBooleanIsEdited(){
        return isEdited;
    }

    public boolean getBooleanIsForwarded(){
        return isForwarded;
    }


}
