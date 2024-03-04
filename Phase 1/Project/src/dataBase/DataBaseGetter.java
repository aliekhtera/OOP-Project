package dataBase;

import back.GeneralMethods;
import back.MethodReturns;
import back.messages.LikeView;
import back.messages.Message;
import back.messengers.Group;
import back.messengers.PV;
import back.messengers.Page;
import back.usersPackage.User;
import back.usersPackage.UserType;

import javax.xml.transform.sax.SAXResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataBaseGetter {
    DataBaseGetter() {
    }

    private static DataBaseGetter instance = new DataBaseGetter();

    public static DataBaseGetter getInstance() {
        return instance;
    }

    public User getUser(String userName) {
        try {
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery("SELECT * FROM tbl_users WHERE username =" + "'" + userName + "'" + " ;");
            resultSet.next();
            String u = resultSet.getString("username");
            String p = resultSet.getString("password");
            String f = resultSet.getString("firstname");
            String l = resultSet.getString("lastname");
            String t = resultSet.getString("usertype");
            String sa = resultSet.getString("securitya");
            int sq = resultSet.getInt("securityq");
            ArrayList<String> blocked = GeneralMethods.getInstance().textDecompressor(resultSet.getString("blocked"));
            resultSet.close();
            return new User(u, p, f, l, UserType.valueOf(t), sa, sq, blocked);
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<String> getBusinessUserNames() {
        ArrayList<String> result = new ArrayList<>();
        try {
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery("SELECT * FROM tbl_users WHERE usertype = 'BUSINESS_USER' ;");
            while (true) {
                resultSet.next();
                result.add(resultSet.getString("username"));
            }
        } catch (Exception e) {

        }
        return result;
    }

    public ArrayList<PV> getPVsOfUser(String userName) {
        ArrayList<PV> result = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tbl_pvs WHERE (username1 = " + "'" + userName + "'" + " OR username2 = '" + userName + "') ;";
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery(sql);
            while (true) {
                resultSet.next();
                User u1 = getUser(resultSet.getString("username1"));
                User u2 = getUser(resultSet.getString("username2"));
                ArrayList<Message> messages = new ArrayList<>();
                ArrayList<String> messageId = GeneralMethods.getInstance().textDecompressor(resultSet.getString("messages"));
                for (String s : messageId) {
                    Message temp = getMessage(s);
                    if (temp != null) {
                        messages.add(temp);
                    }
                }
                if (u1 != null && u2 != null) {
                    PV pv = new PV(u1, u2, messages);
                    result.add(pv);
                }
            }
        } catch (SQLException e) {
            return result;
        }
    }

    public Message getMessage(String id) {
        try {
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery("SELECT * FROM tbl_message WHERE idtbl_message  = " + id + " ;");
            resultSet.next();
            User sender = getUser(resultSet.getString("sender"));
            String text = resultSet.getString("text");
            String sentTime = resultSet.getString("senttime");
            String sentDate = resultSet.getString("sentdate");
            boolean isEdited = resultSet.getBoolean("isedited");
            boolean isF = resultSet.getBoolean("isforwarded");
            int rep = resultSet.getInt("repliedto");
            ArrayList<String> sLike = GeneralMethods.getInstance().textDecompressor(resultSet.getString("likes"));
            ArrayList<String> sView = GeneralMethods.getInstance().textDecompressor(resultSet.getString("views"));
            ArrayList<LikeView> likes = new ArrayList<>();
            ArrayList<LikeView> views = new ArrayList<>();
            for (String s : sLike) {
                likes.add(new LikeView(s));
            }
            for (String s : sView) {
                views.add(new LikeView(s));
            }
            Message result = new Message(sender, text, sentTime, sentDate, isEdited, rep, isF, views, likes);
            result.setKeyID(Integer.parseInt(id));
            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    public Page getPage(String ownerUserName) {
        try {
            String sql = "SELECT * FROM tbl_pages WHERE username = '" + ownerUserName + "' ;";
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery(sql);
            resultSet.next();

            String name = resultSet.getString("name");
            String post = resultSet.getString("posts");
            String followers = resultSet.getString("followers");
            String blocked = resultSet.getString("blocked");
            String comment = resultSet.getString("comments");
            ArrayList<String> pv = GeneralMethods.getInstance().textDecompressor(resultSet.getString("pageview"));

            ArrayList<String> sPost = GeneralMethods.getInstance().textDecompressor(post);
            ArrayList<String> sComment = GeneralMethods.getInstance().textDecompressor(comment);
            ArrayList<Message> posts = new ArrayList<>();
            ArrayList<Message> comments = new ArrayList<>();
            ArrayList<LikeView> pageView = new ArrayList<>();
            for (String s : pv) {
                pageView.add(new LikeView(s));
            }


            for (String s : sPost) {
                posts.add(DataBaseGetter.getInstance().getMessage(s));
            }
            for (String s : sComment) {
                comments.add(DataBaseGetter.getInstance().getMessage(s));
            }
            ArrayList<String> following = DataBaseGetter.getInstance().getPageFollowing(ownerUserName);

            return new Page(ownerUserName, name, posts, comments, GeneralMethods.getInstance().textDecompressor(blocked),
                    GeneralMethods.getInstance().textDecompressor(followers), following, pageView);
        } catch (Exception e) {
            return null;
        }
    }

    private ArrayList<String> getPageFollowing(String userName) {
        ArrayList<String> result = new ArrayList<>();
        try {
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery("SELECT * FROM tbl_pages ;");
            while (true) {
                resultSet.next();
                if (0 <= GeneralMethods.getInstance().textDecompressor(resultSet.getString("followers")).indexOf(userName)) {
                    result.add(resultSet.getString("username"));
                }
            }
        } catch (Exception e) {
            return result;
        }

    }



    public ArrayList<Group> getGroupsOfUser(String userName) {
        ArrayList<Group> result = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tbl_group";
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery(sql);
            while (true) {
                resultSet.next();
                ArrayList<User> members = new ArrayList<>();
                ArrayList<String> users = GeneralMethods.getInstance().textDecompressor(resultSet.getString("users"));
                if (users.contains(userName)) {
                    for (String user : users) {
                        User temp = getUser(user);
                        if (temp != null) {
                            members.add(temp);
                        }
                    }
                    User admin = getUser(resultSet.getString("admin"));
                    String groupName = resultSet.getString("groupName");
                    String groupID = resultSet.getString("groupID");
                    ArrayList<String> banned = GeneralMethods.getInstance().textDecompressor(resultSet.getString("bannedAccounts"));
                    ArrayList<Message> messages = new ArrayList<>();
                    ArrayList<String> messageId = GeneralMethods.getInstance().textDecompressor(resultSet.getString("messages"));

                    for (String s : messageId) {
                        Message temp = getMessage(s);
                        if (temp != null) {
                            messages.add(temp);
                        }
                    }
                    if (users != null && admin != null && groupName != null && groupID != null) {
                        Group group = new Group(members, messages, admin, User.getLoggedInUser(), groupName, groupID, banned);
                        result.add(group);
                    }
                }
            }
        } catch (SQLException e) {
            return result;
        }

    }

    public Group getGroup(String groupId) {
        try {

            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery("SELECT * FROM tbl_group WHERE groupID =" + "'" + groupId + "'" + " ;");
            //      ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery(sql);
            while (true) {
                resultSet.next();
                User admin = getUser(resultSet.getString("admin"));
                String groupName = resultSet.getString("groupName");
                String groupID = resultSet.getString("groupID");
                ArrayList<User> members = new ArrayList<>();
                ArrayList<String> users = GeneralMethods.getInstance().textDecompressor(resultSet.getString("users"));
                ArrayList<String> banned = GeneralMethods.getInstance().textDecompressor(resultSet.getString("bannedAccounts"));
                ArrayList<Message> messages = new ArrayList<>();
                ArrayList<String> messageId = GeneralMethods.getInstance().textDecompressor(resultSet.getString("messages"));

                for (String s : messageId) {
                    Message temp = getMessage(s);
                    if (temp != null) {
                        messages.add(temp);
                    }
                }
                for (String user : users) {
                    User temp = getUser(user);
                    if (temp != null) {
                        members.add(temp);
                    }
                }

                if (users != null && admin != null && groupName != null && groupID != null) {
                    Group group = new Group(members, messages, admin, User.getLoggedInUser(), groupName, groupID, banned);
                    return group;
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<User> getMembers(Group group) {
        ArrayList<User> result = new ArrayList<>();
        try {

            String sql = "SELECT * FROM tbl_group WHERE (groupID = " + "'" + group.getGroupID() + "') ;";
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery(sql);
            while (true) {
                resultSet.next();

                ArrayList<User> members = new ArrayList<>();
                ArrayList<String> users = GeneralMethods.getInstance().textDecompressor(resultSet.getString("users"));

                for (String user : users) {
                    User user1 = getUser(user);
                    members.add(user1);
                }

                return members;

            }
        } catch (SQLException e) {
            return result;
        }

    }

    public MethodReturns editMessagesOfGroup(Group group) {
        String m = GeneralMethods.getInstance().textCompressor(group.getMessagesID());
        try {
            DataBaseManager.getInstance().getStatement().execute("UPDATE tbl_group SET messages = '" + m +
                    "' WHERE  ((groupID = '" + group.getGroupID() + "' )) ;");

            return MethodReturns.DONE;

        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
    }

    public ArrayList<Group> groups() {
        ArrayList<Group> result = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tbl_group";
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery(sql);
            while (true) {
                resultSet.next();
                User admin = getUser(resultSet.getString("admin"));
                String groupName = resultSet.getString("groupName");
                String groupID = resultSet.getString("groupID");
                ArrayList<User> members = new ArrayList<>();
                ArrayList<String> users = GeneralMethods.getInstance().textDecompressor(resultSet.getString("users"));
                ArrayList<String> banned = GeneralMethods.getInstance().textDecompressor(resultSet.getString("bannedAccounts"));
                ArrayList<Message> messages = new ArrayList<>();
                ArrayList<String> messageId = GeneralMethods.getInstance().textDecompressor(resultSet.getString("messages"));

                for (String s : messageId) {
                    Message temp = getMessage(s);
                    if (temp != null) {
                        messages.add(temp);
                    }
                }
                for (String user : users) {
                    User temp = getUser(user);
                    if (temp != null) {
                        members.add(temp);
                    }
                }

                if (users != null && admin != null && groupName != null && groupID != null) {
                    Group group = new Group(members, messages, admin, User.getLoggedInUser(), groupName, groupID, banned);
                    result.add(group);

                }
            }
        } catch (SQLException e) {
            return result;
        }
    }



    public ArrayList<String> getbannedAccounts(Group group) {
        ArrayList<String> result = new ArrayList<>();
        try {

            String sql = "SELECT * FROM tbl_group WHERE (groupID = " + "'" + group.getGroupID() + "') ;";
            ResultSet resultSet = DataBaseManager.getInstance().getStatement().executeQuery(sql);
            while (true) {
                resultSet.next();
                ArrayList<String> users = GeneralMethods.getInstance().textDecompressor(resultSet.getString("bannedAccounts"));
                return users;
            }
        } catch (SQLException e) {
            return result;
        }
    }


}
