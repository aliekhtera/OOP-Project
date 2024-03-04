package front;

import back.GeneralMethods;
import back.MethodReturns;
import back.messages.LikeView;
import back.messages.Message;
import back.messengers.Group;
import back.messengers.Messenger;
import back.usersPackage.User;
import dataBase.DataBaseGetter;

import java.util.ArrayList;

public class GroupMenu extends Menu {

    private static GroupMenu groupMenu = new GroupMenu();
    public static GroupMenu getInstance() {
        return groupMenu;
    }
    private GroupMenu() {
    }
    void openGroup(Group group1) {
        String un = User.getLoggedInUser().getUserName();
        ArrayList<String> strings  = DataBaseGetter.getInstance().getGroup(group1.getGroupID()).getBannedAccounts();
        Boolean bool = false;
        for (String string : strings) {
            if (string.equals(un)) {
                bool = true;
            }
        }
        if (bool) {
            System.out.println("Banned Account");
            start();
            return;
        }
        else {
            Group group = Group.openGroup(User.getLoggedInUser().getUserName(), group1.getGroupID());
            if (group == null) {
                Printer.getInstance().noGroup();
                return;
            }
            ArrayList<String> messages = Message.messageArrayListToStringArray(group.getMessages());


            messages.add("New Message");
            messages.add("Main Menu");
            if (User.getLoggedInUser().getUserName().equals(group.getAdmin().getUserName())) {
                messages.add("Add New Member");
                messages.add("Change Group Name");
                messages.add("Members");
            }


            int selected = Printer.getInstance().stringArrayIndexGetter(messages);

            for (Message message : group.getMessages()) {
                message.viewedByLoggedInUser();
            }

            if (User.getLoggedInUser().getUserName().equals(group.getAdmin().getUserName())) {
                if (selected == messages.size() - 5) {
                    if (group.sendMessage(Printer.getInstance().getNewMessageText(), null, false).equals(MethodReturns.DONE)) {
                        Printer.getInstance().done();
                    } else {
                        Printer.getInstance().error();
                    }
                    openGroup(group);
                }
                else if (selected == messages.size() - 4) {
                    MainMenu.getInstance().start();
                }
                else if (selected == messages.size() - 3) {
                    String username = Printer.getInstance().addUser(group);
                    if (username.equals(null)) {
                        openGroup(group);
                    } else {
                        User user = DataBaseGetter.getInstance().getUser(username);
                        group.addUser(group.getAdmin(), user, group);
                        openGroup(group);
                    }
                    openGroup(group);
                    return;
                }
                else if (selected == messages.size() - 2) {
                    String newName = Printer.getInstance().newName();
                    if (group.changeGroupName(group.getAdmin(), group, newName).equals(MethodReturns.DONE)) {
                        Printer.getInstance().done();
                    } else {
                        Printer.getInstance().error();
                    }
                    openGroup(group);
                }
                else if (selected == messages.size() - 1) {
                    ArrayList<String> members = new ArrayList<>();
                    for (User member : DataBaseGetter.getInstance().getMembers(group)) {
                        members.add(member.getUserName());
                    }
                    members.add("MainMenu");
                    int num = Printer.getInstance().stringArrayIndexGetter(members);
                    if (num != members.size()-1) {
                        User selectedUser = group.getMembers().get(num);
                        System.out.println(selectedUser.getUserName());
                        User user = DataBaseGetter.getInstance().getUser(members.get(num));

                        String result;
                        result = Printer.getInstance().stringsIndexGetter("Remove", "Ban", "MainMenu");
                        String temp;


                        switch (result) {
                            case "Remove":
                                group.removeUser(group.getAdmin(), user, group);
                                Printer.getInstance().removed(user.getUserName());
                                break;
                            case "Ban":
                                group.banMember(group.getAdmin(), group, user.getUserName());
                                Printer.getInstance().bannedUser(user.getUserName());
                                break;
                            case "MainMenu":
                                MainMenu.getInstance().start();
                                break;
                        }
                        openGroup(group);
                        return;
                    }
                    else {
                        MainMenu.getInstance().start();
                    }

                }
                else {
                    Message selectedM = group.getMessages().get(selected);
                    String result;
                    if (selectedM.getSender().equals(User.getLoggedInUser().getUserName())) {
                        result = Printer.getInstance().stringsIndexGetter("Edit", "Reply", "Forward", "Views", "Delete", "MainMenu");
                    } else {
                        result = Printer.getInstance().stringsIndexGetter("Reply", "Forward", "Delete", "MainMenu");
                    }
                    String temp;
                    switch (result) {
                        case "Edit":
                            temp = Printer.getInstance().getNewMessageText();
                            Message.editMessage(selectedM.getKeyID(), temp);
                            break;
                        case "Reply":
                            temp = Printer.getInstance().getNewMessageText();
                            group.sendMessage(temp, selectedM, false);
                            break;
                        case "Forward":
                            Messenger messenger = forwardMessage();
                            group.forwardMessage(selectedM.getKeyID(), messenger);
                            start();
                            break;
                        case "Views":
                            ArrayList<String> m = new ArrayList<>();
                            for (LikeView view : selectedM.getViews()) {
                                m.add(view.getUserName());
                            }
                            Printer.getInstance().printInOneLine(m, "No User!");
                            break;
                        case "Delete":
                            group.deleteMessage(selectedM.getKeyID());
                            break;
                        case "MainMenu":
                            MainMenu.getInstance().start();
                            return;
                    }
                    openGroup(group);
                    return;
                }
            }
            else {

                if (DataBaseGetter.getInstance().getGroup(group.getGroupID()).getBannedAccounts().contains(User.getLoggedInUser())) {
                    Printer.getInstance().ban();
                    start();
                } else {
                    if (selected == messages.size() - 1) {
                        MainMenu.getInstance().start();
                        return;
                    }
                    else if (selected == messages.size() - 2) {
                        if (group.sendMessage(Printer.getInstance().getNewMessageText(), null, false).equals(MethodReturns.DONE)) {
                            Printer.getInstance().done();
                        } else {
                            Printer.getInstance().error();
                        }
                        openGroup(group);
                        return;
                    }
                    else {
                        Message selectedM = group.getMessages().get(selected);
                        String result;
                        if (selectedM.getSender().equals(User.getLoggedInUser().getUserName())) {
                            result = Printer.getInstance().stringsIndexGetter("Edit", "Reply", "Forward", "Views", "Delete", "MainMenu");
                        } else {
                            result = Printer.getInstance().stringsIndexGetter("Reply", "Forward", "Delete", "MainMenu");
                        }
                        String temp;
                        switch (result) {
                            case "Edit":
                                temp = Printer.getInstance().getNewMessageText();
                                Message.editMessage(selectedM.getKeyID(), temp);
                                break;
                            case "Reply":
                                temp = Printer.getInstance().getNewMessageText();
                                group.sendMessage(temp, selectedM, false);
                                break;
                            case "Forward":
                                Messenger messenger = forwardMessage();
                                group.forwardMessage(selectedM.getKeyID(), messenger);
                                start();
                                break;
                            case "Views":
                                ArrayList<String> m = new ArrayList<>();
                                for (LikeView view : selectedM.getViews()) {
                                    m.add(view.getUserName());
                                }
                                Printer.getInstance().printInOneLine(m, "No User!");
                                break;
                            case "Delete":
                                group.deleteMessage(selectedM.getKeyID());
                                break;
                            case "MainMenu":
                                MainMenu.getInstance().start();
                                return;
                        }
                        start();
                    }
                }
            }
        }
    }

    @Override
    public void start() {

        ArrayList<Group> groups = Group.groupsOfUser();
        ArrayList<String> gn = new ArrayList<>();

        for (Group group : groups) {
            gn.add(group.getGroupName());
        }

        gn.add("New Group");
        gn.add("Main Menu");
        gn.add("Join Group");
        int index = Printer.getInstance().stringArrayIndexGetter(gn);

        if (index == gn.size() - 2) {
            MainMenu.getInstance().start();
            return;
        } else if (index == gn.size() - 3) {
            newGroup();
            return;
        } else if (index == gn.size() - 1) {
            Printer.getInstance().joinGroup();
            MainMenu.getInstance().start();
        } else {
            openGroup(groups.get(index));
        }
    }

    private void newGroup() {
        if(Group.createNewGroupInDB(Printer.getInstance().newName(), Printer.getInstance().newID()).equals(MethodReturns.DONE)){
            Printer.getInstance().done();
        }else {
            Printer.getInstance().error();
        }
        start();
    }

}


