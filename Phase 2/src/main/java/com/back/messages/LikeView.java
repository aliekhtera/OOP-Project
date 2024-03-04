package com.back.messages;

import com.back.usersPackage.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LikeView {
    private String userName;
    private String date;
    private String time;

    public LikeView() {
        userName = User.getLoggedInUser().getUserName();
        time = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now());
    }

    LikeView(String userName, String date, String time) {
        this.userName = userName;
        this.date = date;
        this.time = time;
    }

    public LikeView(String compressed) {
        char[] in = compressed.toCharArray();
        String temp = "";
        ArrayList<String> r = new ArrayList<>();
        for (char c : in) {
            if (c == '%') {
                r.add(temp);
                temp = "";
            } else {
                temp += c;
            }
        }
        this.userName = r.get(0);
        this.date = r.get(1);
        this.time = r.get(2);
    }

    public String getUserName() {
        if (userName == null) {
            return "";
        }
        return userName;
    }

    public String getDate() {
        if (date == null) {
            return "";
        }
        return date;
    }

    public String getTime() {
        if (time == null) {
            return "";
        }
        return time;
    }

    public String compressThisClass() {
        return userName + "%" + date + "%" + time + "%";
    }
}
