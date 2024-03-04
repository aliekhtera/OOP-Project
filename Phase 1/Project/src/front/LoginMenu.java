package front;

import back.MethodReturns;
import back.usersPackage.User;
import back.usersPackage.UserType;
import dataBase.DataBaseGetter;

import java.util.ArrayList;
import java.util.Scanner;

public class LoginMenu extends Menu {

    static final private LoginMenu instance = new LoginMenu();

    private LoginMenu() {

    }

    public static LoginMenu getInstance() {
        return instance;
    }

    Scanner scanner = new Scanner(System.in);
    Printer printer = Printer.getInstance();

    @Override
    public void start() {
        printer.start();

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("1")) {
                login();
                return;
            } else if (input.equals("2")) {
                signup();
                return;
            } else {
                printer.badNum();
            }
        }
    }

    void login() {
        printer.login();
        printer.username();
        String username = scanner.nextLine();
        printer.loginPass();
        String password = scanner.nextLine();
        //class
        MethodReturns check;
        if (password.equals("1")) {
            User user = DataBaseGetter.getInstance().getUser(username);
            if (user == null) {
                Printer.getInstance().noUserName();
                start();
                return;
            }
            System.out.println(user.getSecurityQuestion());
            String sa = Printer.getInstance().securityAnswer();
            Printer.getInstance().password();
            String p = scanner.nextLine();
            try {
                if (p.length() < 8) {
                    Printer.getInstance().badInput();
                    start();
                    return;
                }
            } catch (Exception e) {
                Printer.getInstance().badInput();
                start();
                return;
            }
            check = User.securityQuestionPassEdit(username, sa, p);

        } else {
            try {
                check = User.loginUser(username, password);
            } catch (Exception e) {
                check = MethodReturns.UNKNOWN_DATABASE_ERROR;
            }
        }


        if (check.equals(MethodReturns.DONE)) {
            // next menu
            MainMenu.getInstance().start();
            return;
        } else if (check.equals(MethodReturns.NO_SUCH_OBJECT)) {
            printer.doesntExist();
            printer.noAccount();
            // printer.start();
            start();
            return;
        } else if (check.equals(MethodReturns.BAD_INPUT)) {
            printer.invalidInput();
        }else {
            Printer.getInstance().error();

        }
        start();
    }

    void signup() {
        printer.signup();
        printer.username();
        String username = scanner.nextLine();
        printer.password();
        String password = scanner.nextLine();
        printer.firstName();
        String fName = scanner.nextLine();
        printer.lastName();
        String lName = scanner.nextLine();
        UserType ut = printer.userType();
        Printer.getInstance().securityQuestion();
        int sq = Printer.getInstance().stringArrayIndexGetter(User.getSecurityQ());
        String sa = printer.securityAnswer();


        MethodReturns check;
        try {
            check = User.signUpNewUser(username, password, fName, lName, ut, sa, sq);
        } catch (Exception e) {
            check = MethodReturns.UNKNOWN_DATABASE_ERROR;
        }

        if (check.equals(MethodReturns.DONE)) {
            //next menu
            Printer.getInstance().done();
            start();
            return;
        } else if (check.equals(MethodReturns.BAD_INPUT)) {
            printer.badInput();
            start();
        } else if (check.equals(MethodReturns.UNKNOWN_DATABASE_ERROR)) {
            printer.error();
            System.out.println("aaaaaa");
            start();
        } else if (check.equals(MethodReturns.DUPLICATE)) {
            printer.duplicate();
            start();
        }

    }


    String login_signup(ArrayList<String> input) {
        for (int i = 0; i < input.size(); i++) {
            System.out.println(i + ")" + input.get(i));
        }
        while (true) {
            String s = scanner.nextLine();
            s = s.trim();
            try {
                int index = Integer.parseInt(s);
                if (index > 0 && index <= input.size()) {
                    return input.get(index - 1);
                } else {
                    printer.invalidInput();
                }
            } catch (Exception e) {
                printer.error();
                //repeat menu
            }
        }
    }

}
