package com.dataBase;

import com.back.GeneralMethods;
import com.back.MethodReturns;
import com.back.messages.LikeView;
import com.back.messages.Message;
import com.back.messengers.Group;
import com.back.messengers.PV;
import com.back.messengers.Page;
import com.back.usersPackage.User;
import javafx.scene.image.Image;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBaseSetter {
    private DataBaseSetter() {
    }

    private static DataBaseSetter dataBaseSetter = new DataBaseSetter();

    public static DataBaseSetter getInstance() {
        return dataBaseSetter;
    }

    private String insertInToStatement(String tableName, String... nameValueNameValue) {
        String result = "INSERT INTO " + tableName + " ( ";
        List<String> temp = Arrays.stream(nameValueNameValue).toList();
        List<String> names = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        for (int i = 0; i + 1 < temp.size(); i += 2) {
            names.add(temp.get(i));
            vals.add(temp.get(i + 1));
        }
        for (int i = 0; i < names.size(); i++) {
            result += names.get(i);
            if (i + 1 < names.size()) {
                result += " , ";
            }
        }
        result += " )  VALUES ( ";
        for (int i = 0; i < vals.size(); i++) {
            result += "'";
            result += vals.get(i);
            result += "'";
            if (i + 1 < vals.size()) {
                result += " , ";
            }
        }
        result += " ) ;";
        return result;
    }

    public MethodReturns addNewUserToDataBase(User user) {
        try {
            DataBaseManager.getInstance().getStatement().execute("INSERT INTO tbl_users " + user.toString() + ";");
            addEmptyPageToDataBase(user);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    // be hich vagh usertype ro ezafe nakonim
    public MethodReturns editAllUserFields(User user, File image) {
        try {
            if (image == null) {
                String sql = "UPDATE tbl_users Set " +
                        "password= '" + user.getPassWord() + "' " +
                        ", firstname= '" + user.getFirstName() + "' " +
                        " , lastname= '" + user.getLastName() + "' " +
                        " , securityq= " + user.getSecurityIndex() + " " +
                        " , securitya= '" + user.getSecurityAnswers() + "' " +
                        " , blocked= '" + GeneralMethods.getInstance().textCompressor(user.getBlocked()) + "' " +
                        "WHERE username = '" + user.getUserName() + "' ;";
                DataBaseManager.getInstance().getStatement().execute(sql);
            } else {
                FileInputStream fis = new FileInputStream(image);

                String sql = "UPDATE tbl_users Set " +
                        "password= '" + user.getPassWord() + "' " +
                        ", firstname= '" + user.getFirstName() + "' " +
                        ", lastname= '" + user.getLastName() + "' " +
                        ", securityq= " + user.getSecurityIndex() + " " +
                        ", securitya= '" +  user.getSecurityAnswers() + "' " +
                        ", blocked= '" + GeneralMethods.getInstance().textCompressor(user.getBlocked()) + "' " +
                        ", profile= ? " +
                        "WHERE username = '" + user.getUserName() + "' ;";

                PreparedStatement ps = DataBaseManager.getInstance().getPreparedStatement(sql);
                ps.setBinaryStream(1, fis, (int) image.length());
                ps.execute();
            }
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public Message addNewMessageToDataBase(Message message) {
        try {
            ArrayList<String> l = new ArrayList<>();
            ArrayList<String> v = new ArrayList<>();
            for (LikeView like : message.getLikes()) {
                l.add(like.compressThisClass());
            }
            for (LikeView view : message.getViews()) {
                v.add(view.compressThisClass());
            }
            String sql = insertInToStatement("tbl_message", "text", message.getText(), "isforwarded", message.getIsForwarded()
                    , "senttime", message.getSentTime(), "sentdate", message.getSentDate(), "sender", message.getSenderUserName()
                    , "repliedto", Integer.toString(message.getRepliedTo()), "isedited", message.getIsEdited()
                    , "likes", GeneralMethods.getInstance().textCompressor(l), "views", GeneralMethods.getInstance().textCompressor(v));


            DataBaseManager.getInstance().getStatement().execute(sql);
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery("SELECT * FROM tbl_message ORDER BY idtbl_message DESC LIMIT 1 ;");
            resultSet.next();
            int id = resultSet.getInt("idtbl_message");
            message.setKeyID(id);
            return message;

        } catch (Exception e) {
            return null;
        }
    }

    public MethodReturns addNewPVToDataBase(PV pv) {
        try {
            String m = GeneralMethods.getInstance().textCompressor(pv.getMessagesID());
            String sql = insertInToStatement("tbl_pvs", "userName1", pv.getUser1().getUserName(), "userName2", pv.getUser2().getUserName(), "messages", m);
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns editMessagesOfPV(PV newPv) {
        String m = GeneralMethods.getInstance().textCompressor(newPv.getMessagesID());
        try {
            DataBaseManager.getInstance().getStatement().execute("UPDATE tbl_pvs SET messages = '" + m +
                    "' WHERE  ((username1 = '" + newPv.getUser1().getUserName() + "' AND username2 = '" + newPv.getUser2().getUserName() +
                    "') OR (username1 = '" + newPv.getUser2().getUserName() + "' AND username2 = '" + newPv.getUser1().getUserName() + "' )) ;");

            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public void editMessageViews(Message message) {
        try {
            ArrayList<String> v = new ArrayList<>();
            for (LikeView view : message.getViews()) {
                v.add(view.compressThisClass());
            }
            String sql = "UPDATE tbl_message SET views =" + "'" + GeneralMethods.getInstance().textCompressor(v) + "' WHERE idtbl_message = '" + message.getKeyID() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
        } catch (Exception e) {
            return;
        }
    }

    public void editMessageLikes(Message message) {
        try {
            ArrayList<String> l = new ArrayList<>();
            for (LikeView like : message.getLikes()) {
                l.add(like.compressThisClass());
            }
            String sql = "UPDATE tbl_message SET likes =" + "'" + GeneralMethods.getInstance().textCompressor(l) + "' WHERE idtbl_message = '" + message.getKeyID() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
        } catch (Exception e) {
            return;
        }
    }

    public boolean editMessageText(Message message) {
        try {
            String sql = "UPDATE tbl_message SET text =" + "'" + message.getText() + "' WHERE idtbl_message = '" + message.getKeyID() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
            sql = "UPDATE tbl_message SET isEdited = '1' WHERE idtbl_message = '" + message.getKeyID() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void addEmptyPageToDataBase(User user) {
        String sql = insertInToStatement("tbl_pages", "username", user.getUserName()
                , "posts", "", "followers", "", "blocked", "", "comments", "", "name", user.getFirstName() + " " + user.getLastName());
        try {
            DataBaseManager.getInstance().getStatement().execute(sql);
        } catch (Exception e) {
        }
    }

    public MethodReturns editPagePosts(Page page) {
        ArrayList<String> messages = new ArrayList<>();
        for (Message post : page.getPosts()) {
            messages.add(post.getStringKeyID());
        }

        String sql = "UPDATE tbl_pages SET posts =" + "'" + GeneralMethods.getInstance().textCompressor(messages) + "' WHERE username = '" + page.getOwnerUserName() + "' ;";
        try {
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }

    }

    public MethodReturns editPageComments(Page page) {
        ArrayList<String> messages = new ArrayList<>();
        for (Message comment : page.getComments()) {
            messages.add(comment.getStringKeyID());
        }

        String sql = "UPDATE tbl_pages SET comments =" + "'" + GeneralMethods.getInstance().textCompressor(messages) + "' WHERE username = '" + page.getOwnerUserName() + "' ;";
        try {
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns editPageBlock(Page page) {
        String sql = "UPDATE tbl_pages SET blocked =" + "'" + GeneralMethods.getInstance().textCompressor(page.getBlocked()) + "' WHERE username = '" + page.getOwnerUserName() + "' ;";
        try {
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns editPageFollowers(Page page) {
        String sql = "UPDATE tbl_pages SET followers =" + "'" + GeneralMethods.getInstance().textCompressor(page.getFollowers()) + "' WHERE username = '" + page.getOwnerUserName() + "' ;";
        try {
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns editPageName(Page page) {
        String sql = "UPDATE tbl_pages SET name =" + "'" + page.getPageName() + "' WHERE username = '" + page.getOwnerUserName() + "' ;";
        try {
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public void editPassWord(String un, String pass) throws SQLException {
        String sql = "UPDATE tbl_users SET password =" + "'" + pass + "' WHERE username = '" + un + "' ;";
        DataBaseManager.getInstance().getStatement().execute(sql);

    }

    public MethodReturns editPageViews(Page page) {
        ArrayList<String> a = new ArrayList<>();
        ArrayList<LikeView> b = page.getPageViews();
        for (LikeView likeView : b) {
            a.add(likeView.compressThisClass());
        }
        String sql = "UPDATE tbl_pages SET pageview =" + "'" + GeneralMethods.getInstance().textCompressor(a) + "' WHERE username = '" + page.getOwnerUserName() + "' ;";
        try {
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public boolean editUserBlock(User user) {
        try {
            String sql = "UPDATE tbl_users SET blocked =" + "'" + GeneralMethods.getInstance().textCompressor(user.getBlocked()) + "' WHERE username = '" + user.getUserName() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public MethodReturns editMessageFile(String id,File file){
        try {
            FileInputStream fis = new FileInputStream(file);

            String sql = "UPDATE tbl_message Set " +
                    "fileformat = '" + file.getName().substring(file.getName().indexOf('.')) + "' " +
                    ", file = ? " +
                    " WHERE idtbl_message  = '" + id + "' ;";

            PreparedStatement ps = DataBaseManager.getInstance().getPreparedStatement(sql);
            ps.setBinaryStream(1, fis, (int) file.length());
            ps.execute();
            return MethodReturns.DONE;
        }catch (Exception e){
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MethodReturns addNewGroupToDataBase(Group group) {
        try {
            String ban = GeneralMethods.getInstance().textCompressor(group.getBannedAccounts());
            String m = GeneralMethods.getInstance().textCompressor(group.getMessagesID());
            ArrayList<String> mem = new ArrayList<>();
            for (User member : group.getMembers()) {
                mem.add(member.getUserName());
            }
            String members = GeneralMethods.getInstance().textCompressor(mem);
            String sql = insertInToStatement("tbl_group", "users", members, "admin", group.getAdmin().getUserName(), "messages", m, "groupName", group.getGroupName(), "groupID", group.getGroupID(), "bannedAccounts", ban);
            DataBaseManager.getInstance().getStatement().execute(sql);

            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery("SELECT * FROM tbl_group ORDER BY idtbl_group DESC LIMIT 1 ;");
            resultSet.next();
            int id = resultSet.getInt("idtbl_group");
            group.setKeyID(id);

            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns editMessagesOfGroup(Group newGroup) {
        String ban = GeneralMethods.getInstance().textCompressor(newGroup.getBannedAccounts());
        String m = GeneralMethods.getInstance().textCompressor(newGroup.getMessagesID());
        ArrayList<String> mem = new ArrayList<>();
        for (User member : newGroup.getMembers()) {
            mem.add(member.getUserName());
        }
        String members = GeneralMethods.getInstance().textCompressor(mem);
        try {

            DataBaseManager.getInstance().getStatement().execute("UPDATE tbl_group SET messages = '" + m +
                    "' WHERE  ((groupID = '" + newGroup.getGroupID() + "' AND groupName = '" + newGroup.getGroupName() + "' AND admin = '" + newGroup.getAdmin().getUserName() + "' AND users = '" + members + "' AND bannedAccounts = '" + ban + "' )) ;");

            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns addNewMemberToGroup(Group newGroup) {
        ArrayList<String> mem = new ArrayList<>();
        for (User member : newGroup.getMembers()) {
            mem.add(member.getUserName());
        }
        String members = GeneralMethods.getInstance().textCompressor(mem);
        try {
            String sql = "UPDATE tbl_group SET users =" + "'" + members + "' WHERE groupID = '" + newGroup.getGroupID() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns changeGroupName(Group newGroup, String newName) {
        try {
            String sql = "UPDATE tbl_group SET groupName =" + "'" + newName + "' WHERE groupID = '" + newGroup.getGroupID() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns removeMemberFromGroup(Group newGroup, ArrayList<String> members) {

        String mem = GeneralMethods.getInstance().textCompressor(members);
        try {
            String sql = "UPDATE tbl_group SET users =" + "'" + mem + "' WHERE groupID = '" + newGroup.getGroupID() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns bannedAccounts(Group newGroup, String userName) {
        ArrayList<String > bans = new ArrayList<>();
        for (String bannedAccount : DataBaseGetter.getInstance().getGroup(newGroup.getGroupID()).getBannedAccounts()) {
            bans.add(bannedAccount);
        }
        bans.add(userName);
        String ban = GeneralMethods.getInstance().textCompressor(bans);
        try {
            String sql = "UPDATE tbl_group SET bannedAccounts =" + "'" + ban + "' WHERE groupID = '" + newGroup.getGroupID() + "' ;";
            DataBaseManager.getInstance().getStatement().execute(sql);
            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public MethodReturns editAllGroupFields(Group newGroup, File image) {
        try {
            if (image == null) {
                ArrayList<String > strings = new ArrayList<>();
                for (User member : newGroup.getMembers()) {
                    strings.add(member.getUserName());
                }
                String mem = GeneralMethods.getInstance().textCompressor(strings);
                String m = GeneralMethods.getInstance().textCompressor(newGroup.getMessagesID());
                String ban = GeneralMethods.getInstance().textCompressor(newGroup.getBannedAccounts());
                String sql = "UPDATE tbl_group Set " +
                        "groupName= '" + newGroup.getGroupName() + "' " +
                        ", users= '" + mem + "' " +
                        " , admin= '" + newGroup.getAdmin() + "' " +
                        " , messages= " + m + " " +
                        " , bannedAccounts= '" + ban + "' " +
                        "WHERE groupID = '" + newGroup.getGroupID() + "' ;";
                DataBaseManager.getInstance().getStatement().execute(sql);
            } else {
                FileInputStream fis = new FileInputStream(image);

                ArrayList<String > strings = new ArrayList<>();
                for (User member : newGroup.getMembers()) {
                    strings.add(member.getUserName());
                }
                String mem = GeneralMethods.getInstance().textCompressor(strings);
                String m = GeneralMethods.getInstance().textCompressor(newGroup.getMessagesID());
                String ban = GeneralMethods.getInstance().textCompressor(newGroup.getBannedAccounts());

                String sql = "UPDATE tbl_group Set " +
                        "groupName= '" + newGroup.getGroupName() + "' " +
                        ", users= '" + mem + "' " +
                        " , admin= '" + newGroup.getAdmin() + "' " +
                        " , messages= " + m + " " +
                        " , bannedAccounts= '" + ban + "' " +
                        ", profile= ? " +
                        "WHERE groupID = '" + newGroup.getGroupID() + "' ;";

                PreparedStatement ps = DataBaseManager.getInstance().getPreparedStatement(sql);
                ps.setBinaryStream(1, fis, (int) image.length());
                ps.execute();
            }
            return MethodReturns.DONE;
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }



}
