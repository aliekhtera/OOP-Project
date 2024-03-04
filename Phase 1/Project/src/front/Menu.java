package front;

import back.messengers.Messenger;
import back.messengers.PV;
import back.usersPackage.User;

import java.util.ArrayList;

public abstract class Menu {
    public abstract void start();

    Messenger forwardMessage(){
        String result=Printer.getInstance().stringsIndexGetter("PV","Group");
        if(result.equals("PV")){
            ArrayList<User> us =PV.usersPVsList();
            ArrayList<String> un=new ArrayList<>();
            for (User u : us) {
                un.add(u.getUserName());
            }
            int index=Printer.getInstance().stringArrayIndexGetter(un);
            PV pv=PV.openPV(un.get(index));
            return pv;
        }else{

        }
        return null;
    }
}
