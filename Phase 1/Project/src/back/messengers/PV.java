package back.messengers;

import back.MethodReturns;
import back.messages.Message;
import back.usersPackage.User;
import dataBase.DataBaseGetter;
import dataBase.DataBaseSetter;
//import front.Printer;

import java.util.ArrayList;

public class PV extends Messenger {

    private final User user1;
    private final User user2;

    public PV(User u1, User u2, ArrayList<Message> m) {
        user1 = new User(u1);
        user2 = new User(u2);
        messages = new ArrayList<>(m);
    }

    public static ArrayList<User> usersPVsList() {
        if (User.getLoggedInUser() == null) {
            return new ArrayList<>();
        }
        String userName = User.getLoggedInUser().getUserName();
        ArrayList<PV> pvs = DataBaseGetter.getInstance().getPVsOfUser(userName);
        ArrayList<User> result = new ArrayList<>();
        for (PV pv : pvs) {
            if (pv.user1.isUserNameEqual(userName)) {
                result.add(pv.user2);
            } else if (pv.user2.isUserNameEqual(userName)) {
                result.add(pv.user1);
            }
        }
        return result;
    }

    public static PV openPV(String userName) { // null if users==null  Or PV
        return openPV(User.getLoggedInUser().getUserName(), userName);
    }


    public static PV openPV(String un1, String un2) {// null if users==null  Or PV
        User u1 = DataBaseGetter.getInstance().getUser(un1);
        User u2 = DataBaseGetter.getInstance().getUser(un2);
        if (u1 == null || u2 == null) {
            return null;
        }
        try {
            ArrayList<PV> pvs = DataBaseGetter.getInstance().getPVsOfUser(un1);
            for (int i = 0; i < pvs.size(); i++) {
                if ((pvs.get(i).user1.isUserNameEqual(un1) && pvs.get(i).user2.isUserNameEqual(un2))
                        || (pvs.get(i).user2.isUserNameEqual(un1) && pvs.get(i).user1.isUserNameEqual(un2))) {
                    return pvs.get(i);
                }
            }
            return createNewPVInDB(u1, u2);
        } catch (Exception e) {
            return null;
        }
    } // NULL

    private static PV createNewPVInDB(User u1, User u2) {
        if (u1 == null || u2 == null) {
            return null;
        }
        PV pv = new PV(u1, u2, new ArrayList<>());
        if (DataBaseSetter.getInstance().addNewPVToDataBase(pv).equals(MethodReturns.DONE)) {
            return pv;
        } else return null;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<String> getMessagesID() {
        ArrayList<String> result = new ArrayList<>();
        for (Message message : messages) {
            result.add(Integer.toString(message.getKeyID()));
        }

        return result;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

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
        return DataBaseSetter.getInstance().editMessagesOfPV(this);
    }

/*    @Override
    public MethodReturns editMessage(int messageID, String newText) {
        Message m=DataBaseGetter.getInstance().getMessage(Integer.toString(messageID));
        if(m==null){
            return MethodReturns.NO_SUCH_OBJECT;
        }
        if(m.getSender().getFirstName().equals(User.getLoggedInUser().))
            if(m.editMessage(newText)){
                return MethodReturns.DONE;
            }else {
                return MethodReturns.UNKNOWN_DATABASE_ERROR;
            }

    }*/

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
       return DataBaseSetter.getInstance().editMessagesOfPV(this);
    }

    @Override
    public MethodReturns forwardMessage(int messageID,Messenger messenger) {
        Message message=DataBaseGetter.getInstance().getMessage(Integer.toString(messageID));
        return messenger.sendMessage(message.getText(),null,true);
    }


}
