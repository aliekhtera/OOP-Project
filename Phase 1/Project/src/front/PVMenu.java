package front;

import back.MethodReturns;
import back.messages.LikeView;
import back.messages.Message;
import back.messengers.Messenger;
import back.messengers.PV;
import back.usersPackage.User;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;

import java.util.ArrayList;
import java.util.Scanner;

class PVMenu extends Menu {
    private static PVMenu pvMenu = new PVMenu();

    private PVMenu() {
    }

    static PVMenu getPvMenu() {
        return pvMenu;
    }


    void openPV(String userName) {
        PV pv = PV.openPV(userName);
        if (pv == null) {
            Printer.getInstance().noUserName();
            return;
        }
        ArrayList<String> messages = Message.messageArrayListToStringArray(pv.getMessages());
        if(User.isUserBlocked(userName)){
            messages.add("Unblock");
        }else{
            messages.add("Block");
        }

        messages.add("New Message");
        messages.add("Main Menu");
        int selected = Printer.getInstance().stringArrayIndexGetter(messages);
        for (Message message : pv.getMessages()) {
            message.viewedByLoggedInUser();
        }
        if(selected==messages.size()-3){
            if(User.blockUnBlockUser(userName)){
                Printer.getInstance().done();
            }else{
                Printer.getInstance().error();
            }
            openPV(userName);
            return;
        }
        if (selected == messages.size() - 1) {
            MainMenu.getInstance().start();
            return;
        } else if (selected == messages.size() - 2) {
            if(User.isLoggedUserBlocked(userName)){
                Printer.getInstance().error();
            }else {
                if (pv.sendMessage(Printer.getInstance().getNewMessageText(), null, false).equals(MethodReturns.DONE)) {
                    Printer.getInstance().done();
                } else {
                    Printer.getInstance().error();
                }
            }
            openPV(userName);
            return;
        } else {

            Message selectedM = pv.getMessages().get(selected);
            String result;
            if (selectedM.getSender().equals(User.getLoggedInUser().getUserName())) {
                result = Printer.getInstance().stringsIndexGetter("Edit", "Reply", "Forward", "Views", "Delete", "MainMenu");
            } else {
                result = Printer.getInstance().stringsIndexGetter( "Reply", "Forward",  "Delete", "MainMenu");
            }
            String temp;
            switch (result) {
                case "Edit":
                    temp = Printer.getInstance().getNewMessageText();
                    Message.editMessage(selectedM.getKeyID(), temp);
                    break;
                case "Reply":
                    if(User.isLoggedUserBlocked(userName)){
                        Printer.getInstance().error();
                        openPV(userName);
                    }else {
                        temp = Printer.getInstance().getNewMessageText();
                        pv.sendMessage(temp, selectedM, false);
                    }
                    break;
                case "Forward":
                    Messenger messenger = forwardMessage();
                    pv.forwardMessage(selectedM.getKeyID(), messenger);
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
                    pv.deleteMessage(selectedM.getKeyID());
                    break;
                case "MainMenu":
                    MainMenu.getInstance().start();
                    return;
            }
            start();
        }
    }

    @Override
    public void start() {
        ArrayList<User> pvs = PV.usersPVsList();
        ArrayList<String> un = new ArrayList<>();
        for (User pv : pvs) {
            un.add(pv.getUserName());
        }
        un.add("Search");
        un.add("Main Menu");
        int index = Printer.getInstance().stringArrayIndexGetter(un);
        if (index == un.size() - 1) {
            MainMenu.getInstance().start();
            return;
        } else if (index == un.size() - 2) {
            MainMenu.getInstance().search();
            return;
        } else {
            openPV(pvs.get(index).getUserName());
        }
    }


}
