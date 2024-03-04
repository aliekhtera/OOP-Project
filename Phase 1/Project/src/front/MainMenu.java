package front;

import back.usersPackage.User;
import dataBase.DataBaseGetter;

public class MainMenu extends Menu {
    private static MainMenu instance = new MainMenu();

    private MainMenu() {
    }

   public static MainMenu getInstance() {
        return instance;
    }

    @Override
    public void start() {
        Printer.getInstance().mainMenu();
        String result = Printer.getInstance().stringsIndexGetter("Exit","Search", "PVs", "Groups", "Page");
        switch (result) {
            case "Search":
                search();
                break;
            case "PVs":
                PVMenu.getPvMenu().start();
                break;
            case "Groups":
                GroupMenu.getInstance().start();
                break;
            case "Page":
                PageMenu.getInstance().start();
                break;
            case "Exit":
               Printer.getInstance().exitProgram();
               start();

                break;
        }
    }

    void search() {
        Printer.getInstance().username();
        String un = Printer.getInstance().getScanner().nextLine();
        User u = DataBaseGetter.getInstance().getUser(un);
        Printer.getInstance().printUser(u);
        String result;
        if (u == null) {
            result=Printer.getInstance().stringsIndexGetter("Search", "Main Menu");
        }else{
            result=Printer.getInstance().stringsIndexGetter("Open Pv","Open Page","Search","Main Menu");
        }
        switch (result){
            case "Search":
                search();
                break;
            case "Main Menu":
                MainMenu.getInstance().start();
                break;
            case "Open Pv":
                PVMenu.getPvMenu().openPV(u.getUserName());
                break;
            case "Open Page":
                PageMenu.getInstance().openPage(u.getUserName());
        }
    }


}
