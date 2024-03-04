package com.back.messengers;

import com.back.GeneralMethods;
import com.back.MethodReturns;
import com.back.messages.Message;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Group extends Messenger {

    static private String  selectedGroup;
    static private String  openedGroup;
    private int keyID;//-1 for null
    private ArrayList<User> members;
    private ArrayList <Message> messages;
    User admin;
    User user;
    String groupName;
    String groupID;
    ArrayList<String> bannedAccounts = new ArrayList<>();

    Scanner scanner = new Scanner(System.in);

    public Group(ArrayList<User> members, ArrayList<Message> messages, User admin, User user, String groupName, String groupID, ArrayList<String> bannedAccounts) {
        this.keyID = -1;
        this.members = members;
        this.messages = messages;
        this.admin = admin;
        this.user = user;
        this.groupName = groupName;
        this.groupID = groupID;
        this.bannedAccounts = bannedAccounts;
    }

    public static ArrayList<Group> groupsOfUser() {
        if (User.getLoggedInUser() == null) {
            return new ArrayList<>();
        }
        String userName = User.getLoggedInUser().getUserName();

        ArrayList<Group> groups = DataBaseGetter.getInstance().getGroupsOfUser(userName);
        ArrayList<Group> result = new ArrayList<>();

        for (Group group : groups) {
            if (group.getMembers().contains(User.getLoggedInUser())) {
                result.add(group);
            }
        }
        return result;
    }

    public static ArrayList<User> members(Group group) {

        if (group.getGroupID() == null) {
            return new ArrayList<>();
        }
        String userName = User.getLoggedInUser().getUserName();

        ArrayList<User> members = DataBaseGetter.getInstance().getMembers(group);
        ArrayList<User> result = new ArrayList<>();

        for (User member : members) {
            if (group.getMembers().contains(member)) {
                result.add(member);
            }
        }
        return result;

    }

    /*
        ArrayList<User>  = DataBaseGetter.getInstance().getUser(userName);
        ArrayList<User> result = new ArrayList<>();
        for (int i = 0; i < group.getMembers().size(); i++) {
            if (group.getMembers().contains()) {
                result.add(group.getMembers().get());
            } else if (pv.user2.isUserNameEqual(userName)) {
                result.add(pv.user1);
            }
        }

        return result;
    }
*/
    ///// open group
    public static Group openGroup(String userName, String groupID) {

        User user = DataBaseGetter.getInstance().getUser(userName);
        Group group = DataBaseGetter.getInstance().getGroup(groupID);

        if (user == null || group == null) {
            return null;
        }

        try {
            ArrayList<Group> groups = DataBaseGetter.getInstance().getGroupsOfUser(userName);
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).getGroupID().equals(groupID)) {
                    return groups.get(i);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private static Group createNewGroupInDB(Group group, User user) {
        if (user == null) {
            return null;
        }
        Group temp = new Group(group.getMembers(), group.getMessages(), user,  user, group.getGroupName(), group.getGroupID(), group.bannedAccounts);
        if (DataBaseSetter.getInstance().addNewGroupToDataBase(group).equals(MethodReturns.DONE)) {
            return group;
        } else return null;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupID() {
        return groupID;
    }

    public User getAdmin() {
        return admin;
    }

    public int getKeyID() {
        return keyID;
    }

    public String getStringKeyID() {
        if (keyID < 0) {
            return null;
        }
        return Integer.toString(keyID);
    }

    public ArrayList<String> getMessagesID() {
        ArrayList<String> result = new ArrayList<>();
        for (Message message : messages) {
            result.add(Integer.toString(message.getKeyID()));
        }

        return result;
    }

    public void setKeyID(int keyID) {
        this.keyID = keyID;
    }

    public ArrayList<String> getBannedAccounts() {
        return bannedAccounts;
    }

    /////////////////////////Message////////////////////////////////////////////////////////////////////////////

    @Override
    public MethodReturns sendMessage(String text, Message rep ,boolean isForwarded ) {
        Message temp=Message.newMessage(text, rep , isForwarded);
        if (temp == null) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
        int newMessage = temp.getKeyID();
        if (newMessage == -1) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
        messages.add(temp);
        return DataBaseSetter.getInstance().editMessagesOfGroup(this);
    }

    @Override
    public MethodReturns deleteMessage(int messageID) {
        int index=-1;
        for (int i = 0; i < messages.size(); i++) {
            if(messages.get(i).getKeyID()==messageID){
                index=i;
                break;
            }
        }
        if(index==-1){
            return MethodReturns.NO_SUCH_OBJECT;
        }
        messages.remove(index);
        return DataBaseSetter.getInstance().editMessagesOfGroup(this);
    }

    @Override
    public MethodReturns forwardMessage(int messageID) {
        Message message=DataBaseGetter.getInstance().getMessage(Integer.toString(messageID));
        return sendMessage(message.getText(),null,true);
    }

    public MethodReturns sendMessage(Message message){
        if(message.getKeyID()<0){
            return MethodReturns.BAD_INPUT;
        }
        for (Message m : messages) {
            if(m.getKeyID()==message.getKeyID()){
                return MethodReturns.DUPLICATE;
            }
        }
        messages.add(message);
        return DataBaseSetter.getInstance().editMessagesOfGroup(this);
    }


    ///////////////////////////////////Admin////////////////////////////////////////////////////////////////////

    public MethodReturns addUser(User admin, User user, Group group) {
        Boolean bool = false;
        try {
            for (User member : group.getMembers()) {
                if (member.getUserName().equals(user.getUserName())) {
                    bool = true;
                }
            }

            if (bool) {
                return null;
            }
            else {
                group.getMembers().add(user);
                DataBaseSetter.getInstance().addNewMemberToGroup(group);
                return MethodReturns.DONE;
            }

        }
        catch (Exception e) {
            return null;
        }
    }

    public MethodReturns removeUser (User admin, User user, Group group) {
        try {
            ArrayList<String > members = new ArrayList<>();
            for (User member : group.getMembers()) {
                if(!(member.getUserName().equals(user.getUserName())))  {
                    members.add(member.getUserName());
                }
            }
            DataBaseSetter.getInstance().removeMemberFromGroup(group, members);
            return MethodReturns.DONE;
        }
        catch (Exception e) {
            return null;
        }
    }

    public MethodReturns changeGroupName (User admin, Group group, String newName) {
        try {
            DataBaseSetter.getInstance().changeGroupName(group, newName);
            return MethodReturns.DONE;
        }
        catch (Exception e) {
            return null;
        }
    }

    public MethodReturns banMember (User admin, Group group, String  username) {
        try {
            DataBaseSetter.getInstance().bannedAccounts(group, username);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return null;
        }
    }

    public static MethodReturns editGroup(String GroupName, ArrayList<User> members, File profile, String groupID) {

        if (!GeneralMethods.getInstance().notEmptyStrings(GroupName)) {
            return MethodReturns.BAD_INPUT;
        }

        GroupName = GeneralMethods.getInstance().cutTo45Strings(GroupName);


        if (DataBaseGetter.getInstance().getGroup(groupID) == null) {
            return MethodReturns.NO_SUCH_OBJECT;
        }
        Group group = DataBaseGetter.getInstance().getGroup(groupID);
        ArrayList<String > strings = new ArrayList<>();
        for (User member : group.getMembers()) {
            strings.add(member.getUserName());
        }
        String mem = GeneralMethods.getInstance().textCompressor(strings);
        String m = GeneralMethods.getInstance().textCompressor(group.getMessagesID());
        String ban = GeneralMethods.getInstance().textCompressor(group.getBannedAccounts());
        Group temp = new Group(members, group.getMessages(), group.getAdmin(), User.getLoggedInUser(), GroupName, groupID, group.getBannedAccounts() );

        return DataBaseSetter.getInstance().editAllGroupFields(temp, profile);
    }


    @Override
    public String toString() {
        return "Group : " +groupName+" ("+groupID+")";
    }

    public static String getSelectedGroup() {
        return selectedGroup;
    }

    public static void setSelectedGroup(String selectedGroup) {
        Group.selectedGroup = selectedGroup;
    }

    public static void setOpenedGroup(String openedGroup) {
        Group.openedGroup = openedGroup;
    }

    public static String getOpenedGroup() {
        return openedGroup;
    }
}
