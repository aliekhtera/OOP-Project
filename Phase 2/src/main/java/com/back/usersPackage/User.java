package com.back.usersPackage;

import com.back.GeneralMethods;
import com.back.MethodReturns;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;
import javafx.scene.image.Image;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class User {
    static private User selectedUser;
    private static String[] securityQ = {"What city were you born?", "What was the first concert you attended?",
            "In what city did your parent meet?", "What was the model of your first car?"};
    private final String userName;
    private String passWord;
    private String firstName;
    private String lastName;
    private UserType userType;
    private String securityAnswers;
    private int securityQuestion;
    private ArrayList<String> blocked;
    static private User loggedInUser;


    public User(String userName, String passWord, String firstName, String lastName,
                UserType userType, String securityAnswers, int securityQuestion, ArrayList<String> blocked) {
        this.userName = userName;
        this.passWord = passWord;
        this.setFirstName(firstName);
        this.setUserType(userType);
        this.setLastName(lastName);
        this.securityAnswers = securityAnswers;
        this.securityQuestion = securityQuestion;
        this.blocked = new ArrayList<>(blocked);
    }

    public User(User user) {
        this.userName = user.userName;
        this.passWord = user.passWord;
        this.setFirstName(user.firstName);
        this.setUserType(user.userType);
        this.setLastName(user.lastName);
        this.securityAnswers = user.securityAnswers;
        securityQuestion = user.securityQuestion;
        this.blocked = new ArrayList<>(user.blocked);
    }

    // add new user to DB and login
    // return DONE if E.T goes well , DATABASE... if data base return error , BAD_INPUT if null username or pass, DUPLICATE if user with same username exists.
    public static MethodReturns signUpNewUser(String userName, String passWord, String firstName, String lastName, UserType userType, String sa, int sq) throws SQLException {
        if (!GeneralMethods.getInstance().notEmptyStrings(userName, passWord, sa)) {
            return MethodReturns.BAD_INPUT;
        }
        if (userName.length() >= 45 || passWord.length() < 8 || passWord.length() >= 45 || sa.length() >= 45) {
            return MethodReturns.BAD_INPUT;
        }
        firstName = GeneralMethods.getInstance().cutTo45Strings(firstName);
        lastName = GeneralMethods.getInstance().cutTo45Strings(lastName);
        sa = GeneralMethods.getInstance().cutTo45Strings(sa);

        if (DataBaseGetter.getInstance().getUser(userName) != null) {
            return MethodReturns.DUPLICATE;
        }
        User temp = new User(userName, passWord, firstName, lastName, userType, sa, sq, new ArrayList<>());
        return DataBaseSetter.getInstance().addNewUserToDataBase(temp);
    }

    // login user
    //return DONE if E.T goes well , BAD_INPUT if null username or pass , NO_SUCH_OBJECT if no such username or wrong pass
    public static MethodReturns loginUser(String userName, String passWord) throws SQLException {
        if (!GeneralMethods.getInstance().notEmptyStrings(userName, passWord)) {
            return MethodReturns.BAD_INPUT;
        }
        User tempUser = DataBaseGetter.getInstance().getUser(userName);
        if (tempUser == null) {
            return MethodReturns.NO_SUCH_OBJECT;
        }
        if (!tempUser.isPasswordEqual(passWord)) {
            return MethodReturns.NO_SUCH_OBJECT;
        }
        setLoggedInUser(tempUser);
        return MethodReturns.DONE;
    }

    public MethodReturns securityQuestionPassEdit(String securityAnswers, String password) {
        if (!GeneralMethods.getInstance().notEmptyStrings(userName, securityAnswers, password)) {
            return MethodReturns.BAD_INPUT;
        }
        User tempUser = this;

        /*if (tempUser == null) {
            return MethodReturns.NO_SUCH_OBJECT;
        }*/

        if (!tempUser.securityAnswers.equals(securityAnswers)) {
            return MethodReturns.NO_SUCH_OBJECT;
        }
        try {
            DataBaseSetter.getInstance().editPassWord(userName, password);
            //   User.setLoggedInUser(tempUser);
        } catch (Exception e) {
            return MethodReturns.UNKNOWN_DATABASE_ERROR;
        }
        return MethodReturns.DONE;
    }

    private void setFirstName(String firstName) {
        if (firstName != null) {
            this.firstName = firstName;
        }
    }

    private void setUserType(UserType userType) {
        if (userType != null) {
            this.userType = userType;
        }
    }

    private void setLastName(String lastName) {
        if (lastName != null) {
            this.lastName = lastName;
        }
    }

    public boolean isPasswordEqual(String passWord) {
        try {
            return this.passWord.equals(passWord);
        } catch (Exception e) {
            return false;
        }
    }

    private static void setLoggedInUser(User loggedInUser) {
        User.loggedInUser = new User(loggedInUser);
    }

    public static User getLoggedInUser() {
        if (loggedInUser == null) {
            return null;
        }
        return new User(loggedInUser);
    }

    public static void setSelectedUser(User selectedUser) {
        User.selectedUser = new User(selectedUser);
    }

    public static User getSelectedUser() {
        if (selectedUser == null) {
            return null;
        }
        return new User(selectedUser);
    }

    @Override
    public String toString() {
        return
                "(username, password, firstname, lastname, blocked ,securityq, securitya, usertype )" +
                        " VALUES ('" + userName + '\'' +
                        ", '" + passWord + '\'' +
                        ", '" + firstName + '\'' +
                        ", '" + lastName + '\'' +
                        ", '" + GeneralMethods.getInstance().textCompressor(blocked) + '\'' +
                        ", '" + securityQuestion + '\'' +
                        ", '" + securityAnswers + '\'' +
                        ", '" + userType +
                        "')";
    }

    public boolean isUserNameEqual(String u) {
        try {
            return u.equals(userName);
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getSecurityQuestion() {
        try {
            return securityQ[securityQuestion];
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isSecurityAnswerEqual(String s) {
        if (s == null) {
            return false;
        }
        return (s.equals(securityAnswers));
    }

    public static boolean blockUnBlockUser(String un) {
        if (User.getLoggedInUser().isUserNameEqual(un)) {
            return false;
        }
        User user = DataBaseGetter.getInstance().getUser(User.getLoggedInUser().getUserName());
        int index = user.blocked.indexOf(un);
        if (index < 0) {
            user.blocked.add(un);
        } else {
            user.blocked.remove(index);
        }
        return DataBaseSetter.getInstance().editUserBlock(user);
    }

    public static ArrayList<String> getSecurityQ() {
        return new ArrayList<>(Arrays.stream(securityQ).toList());
    }

    public ArrayList<String> getBlocked() {
        return blocked;
    }

    public static boolean isLoggedUserBlocked(String un) {
        User user = DataBaseGetter.getInstance().getUser(un);
        return user.blocked.contains(User.getLoggedInUser().getUserName());
    }

    public static boolean isUserBlocked(String un) {
        User user = DataBaseGetter.getInstance().getUser(User.getLoggedInUser().getUserName());
        return user.blocked.contains(un);
    }

    public String getSecurityAnswers() {
        return securityAnswers;
    }

    public String getPassWord() {
        return passWord;
    }

    public int getSecurityIndex() {
        return securityQuestion;
    }

    public static MethodReturns editUser(String userName, String passWord, String firstName, String lastName, String sa, int sq, File profile) {

        if (!GeneralMethods.getInstance().notEmptyStrings(userName, passWord, sa)) {
            return MethodReturns.BAD_INPUT;
        }
        if (userName.length() >= 45 || passWord.length() < 8 || passWord.length() >= 45 || sa.length() >= 45) {
            return MethodReturns.BAD_INPUT;
        }
        firstName = GeneralMethods.getInstance().cutTo45Strings(firstName);
        lastName = GeneralMethods.getInstance().cutTo45Strings(lastName);
        sa = GeneralMethods.getInstance().cutTo45Strings(sa);

        if (DataBaseGetter.getInstance().getUser(userName) == null) {
            return MethodReturns.NO_SUCH_OBJECT;
        }
        User temp = new User(userName, passWord, firstName, lastName, null, sa, sq, new ArrayList<>());

        return DataBaseSetter.getInstance().editAllUserFields(temp, profile);
    }

}
