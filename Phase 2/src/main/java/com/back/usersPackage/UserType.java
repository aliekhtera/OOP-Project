package com.back.usersPackage;

import java.util.ArrayList;
import java.util.List;

public enum UserType {
    NORMAL_USER,
    BUSINESS_USER;

    public static List toList() {
        ArrayList result = new ArrayList<>();
        result.add("Normal User");
        result.add("Business User");
        return result;
    }
    public static UserType indexToUserType(int index){
        if(index==0){
            return UserType.NORMAL_USER;
        }
        if(index==1){
            return UserType.BUSINESS_USER;
        }
        return null;
    }
}
