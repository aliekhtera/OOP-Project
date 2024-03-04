package front;

import back.messengers.Group;
import back.messengers.Page;
import back.usersPackage.User;
import back.usersPackage.UserType;
import dataBase.DataBaseGetter;

import java.util.ArrayList;
import java.util.Scanner;

class Printer {
    private static Printer printer = new Printer();

    private Printer() {
    }

    private Scanner scanner = new Scanner(System.in);

    static Printer getInstance() {
        return printer;
    }

    public Scanner getScanner() {
        return scanner;
    }

    void start() {
        System.out.println("Enter 1 to login / 2 to sign up");
    }

    void done() {
        System.out.println("DONE!");
    }

    void login() {
        System.out.println("login");
    }

    void signup() {
        System.out.println("signup");
    }

    void securityQuestion(){
        System.out.println("Security Question:");
    }

    String securityAnswer(){
        String result;
        do {
            System.out.println("Your Answer: (1-45)");
            result= scanner.nextLine();
        }while (!(result.length()>0 && result.length()<45));
        return result;
    }

    void invalidInput() {
        System.out.println("Error : Invalid Input");
    }

    void doesntExist() {
        System.out.println("Check your username and password; If you didn't ");
    }

    void username() {
        System.out.println("Enter Username (1-45)");
    }

    void password() {
        System.out.println("Enter Password (8-45)");
    }

    void loginPass(){
        System.out.println("Enter Password Or Enter 1 For Security Question");
    }


    void firstName() {
        System.out.println("Enter firstName (1-45)");
    }

    void lastName() {
        System.out.println("Enter lastName (1-45)");
    }

    void error() {
        System.out.println("Error");
    }

    void badNum() {
        System.out.println("Enter a good number (1 or 2)");
    }

    void badNum(int min, int max) {
        System.out.printf("Enter a number between %d and %d", min, max);
    }

    void noAccount() {
        System.out.println("If you don't have account please Sign up");
        System.out.println("If you have account please check your username and password");
    }

    void badInput() {
        System.out.println("Error : Bad Input");
    }

    void noUserName() {
        System.out.println("No User With This User Name");
    }

    void duplicate() {
        System.out.println("There is an account with this username");
    }
    // PV

    void messageList() {
        System.out.println("Messa");
    }

    UserType userType() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(" 1) Normal User \n 2) Business User");
            String temp = scanner.nextLine();
            if (temp.equals("1")) {
                return UserType.NORMAL_USER;
            } else if (temp.equals("2")) {
                return UserType.BUSINESS_USER;
            }
        }
    }

    String stringsIndexGetter(String... input) {
        for (int i = 0; i < input.length; i++) {
            System.out.println(i + 1 + " ) " + input[i]);
        }
        while (true) {
            String s = scanner.nextLine();
            s = s.trim();
            try {
                int index = Integer.parseInt(s);
                if (index > 0 && index <= input.length) {
                    return input[index - 1];
                } else {
                    badNum(1, input.length);
                }
            } catch (Exception e) {
                badInput();
            }
        }
    }

    int stringArrayIndexGetter(ArrayList<String> input) {
        for (int i = 0; i < input.size(); i++) {
            System.out.println(i + 1 + " ) " + input.get(i));
        }
        while (true) {
            String s = scanner.nextLine();
            s = s.trim();
            try {
                int index = Integer.parseInt(s);
                if (index > 0 && index <= input.size()) {
                    return (index - 1);
                } else {
                    badNum(1, input.size());
                }
            } catch (Exception e) {
                badInput();
            }
        }
    }

    int stringArrayIndexGetter(ArrayList<String> input,ArrayList<String> adds) {
        for (int i = 0 ; i < input.size(); i++) {
            if(i%4==0 && i/4<adds.size()){
                System.out.println(adds.get(i/4));
            }
            System.out.println(i + 1 + " ) " + input.get(i));
        }
        while (true) {
            String s = scanner.nextLine();
            s = s.trim();
            try {
                int index = Integer.parseInt(s);
                if (index > 0 && index <= input.size()) {
                    return (index - 1);
                } else {
                    badNum(1, input.size());
                }
            } catch (Exception e) {
                badInput();
            }
        }
    }

    void printUser(User user) {
        if (user == null) {
            noUserName();
        }
        System.out.println("First Name :" + user.getFirstName());
        System.out.println("Last Name :" + user.getLastName());
        try {
            if (user.getUserType().equals(UserType.BUSINESS_USER)) {
                System.out.println("Business User");
            } else {
                System.out.println("Normal User");
            }
        } catch (Exception e) {
        }

    }

    void exitProgram() {
        System.out.println("Are You Sure?");
        if (stringsIndexGetter("YES", "NO").equals("YES")) {
            System.exit(0);
        }
    }

    void mainMenu() {
        System.out.println("Main Menu: ");
    }

    String getNewMessageText() {
        System.out.println("Please Enter Your Message");
        return scanner.nextLine();
    }

    void editPageName(){
        System.out.println("Please Enter New Name: (1-90)");
    }

    public void printInOneLine(ArrayList<String> inp,String nullString){
        String temp="";
        for (int i = 0; i < inp.size(); i++) {
            temp+=inp.get(i);
            if(i+1<inp.size()){
                temp+=" , ";
            }
        }
        if(temp.isEmpty()){
            System.out.println(nullString);
        }
        System.out.println(temp);
    }

    void printPageName(Page p){
        System.out.println("***********  "+p.getPageName()+"  ***********");
    }



    //////////////////////////////////////////////////////////////////
    void noGroup() {System.out.println("No Group With this name or ID ");}

    String addUser(Group group) {
        System.out.println("Enter Username");
        String username = scanner.nextLine();

       if (DataBaseGetter.getInstance().getMembers(group).contains(DataBaseGetter.getInstance().getUser(username))) {            System.out.println("There is this username in group");
            return null;
        } else {
            return username;
        }

    }

    String removeUser(Group group) {
        System.out.println("Enter Username");
        String username = scanner.nextLine();
        if (DataBaseGetter.getInstance().getMembers(group).contains(DataBaseGetter.getInstance().getUser(username))) {
            return username;
        } else {
            System.out.println(" There isn't this username in the group ");
            return null;
        }
    }

    void removed(String userName) {
        System.out.println(userName + " was removed ");
    }

    String newName() {
        System.out.println("Enter Name:");
        String s = scanner.nextLine();
        return s;
    }

    String newID() {
        System.out.println("Please Enter Group ID:");
        Boolean bool = false;
        String s = scanner.nextLine();

        for (Group group : DataBaseGetter.getInstance().groups()) {
            if (group.getGroupID().equals(s)) {
                bool = true;
            }
        }

        if (bool) {
            System.out.println("The ID exists");
            return null;
        } else {
            return s;
        }

    }

    String banned(Group group) {
        Boolean bool = false;
        System.out.println("Enter Username");
        String s = scanner.nextLine();
        group.banMember(group.getAdmin(), group, s);
        return null;
    }

    void bannedUser(String userName) {
        System.out.println( userName + " was banned");
    }

    public void thereIs () {
        System.out.println(" There is this username in the group ");
    }

    void ban() {
        System.out.println("Your Account Is Banned");
    }


    public void joinGroup () {
        System.out.println("Enter ID ");
        String ID = scanner.next();
        Group group = DataBaseGetter.getInstance().getGroup(ID);

        if (group != null) {
            Boolean bool = false;
            for (User member : DataBaseGetter.getInstance().getMembers(group)) {
                if (member.getUserName().equals(User.getLoggedInUser().getUserName())) {
                    bool = true;
                }
            }
            if (bool) {
                System.out.println("You have joind before");
            } else {
                group.addUser(group.getAdmin(), User.getLoggedInUser(), group);
                System.out.println("You joind ");
            }
        }
        else {
            System.out.println("Invalid ID");
        }
    }

}