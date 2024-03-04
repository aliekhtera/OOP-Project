package back.messengers;

import back.MethodReturns;
import back.messages.Message;
import back.usersPackage.User;
import dataBase.DataBaseGetter;
import dataBase.DataBaseSetter;

import java.util.ArrayList;
import java.util.Scanner;

public class Group extends Messenger {
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

        ArrayList<Group> result = DataBaseGetter.getInstance().getGroupsOfUser(userName);
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

    public static MethodReturns createNewGroupInDB(String name,String id) {
        User user=DataBaseGetter.getInstance().getUser(User.getLoggedInUser().getUserName());
        if (User.getLoggedInUser() == null) {
            return null;
        }
        ArrayList<User> members=new ArrayList<>();
        members.add(user);
        Group temp = new Group(members, new ArrayList<>(), user,  user, name, id,new ArrayList<>());
        return  DataBaseSetter.getInstance().addNewGroupToDataBase(temp);
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
    public MethodReturns forwardMessage(int messageID,Messenger messenger) {
        Message message=DataBaseGetter.getInstance().getMessage(Integer.toString(messageID));
        return messenger.sendMessage(message.getText(),null,true);
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
                System.out.println("This username already exists");
                return null;
            }
            else {
                group.getMembers().add(user);
                DataBaseSetter.getInstance().addNewMemberToGroup(group);
                System.out.println(user.getUserName() + " was added");
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

    public MethodReturns changeGroupID (User admin, Group group, String newID) {
        try {
            if (User.getLoggedInUser().getUserName().equals(group.getAdmin())) {
                DataBaseSetter.getInstance().changeGroupID(group, newID);
                return MethodReturns.DONE;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public MethodReturns banMember (User admin, Group group, String  username) {
        try {
            DataBaseSetter.getInstance().bannedAccounts(group, username);
            return MethodReturns.DONE;
        } catch (Exception e) {
            return null;
        }
    }


}
