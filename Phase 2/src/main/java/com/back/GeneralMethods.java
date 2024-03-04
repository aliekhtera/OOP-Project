package com.back;

import java.util.ArrayList;

public class GeneralMethods {
    private static GeneralMethods generalMethods = new GeneralMethods();

    public static GeneralMethods getInstance() {
        return generalMethods;
    }

    private GeneralMethods() {
    }

    public boolean notEmptyStrings(String... strings) {
        for (String string : strings) {
            if (string == null) {
                return false;
            }
        }
        for (String string : strings) {
            if (string.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public String textCompressor(ArrayList<String> input) {
        if(input==null){
            return "";
        }
        String result = "";
        for (String s : input) {
            result += "$" + s + "$";
        }
        return result;
    }

    public ArrayList<String> textDecompressor(String input) { // $text$
        if(input==null){
            return new ArrayList<>();
        }
        ArrayList<String> result = new ArrayList<>();
        char[] inp = input.toCharArray();
        String temp = "";
        for (char c : inp) {
            if (c == '$') {
                if (!temp.isEmpty()) {
                    result.add(temp);
                    temp = "";
                }
            } else {
                temp += c;
            }
        }
        if (!temp.isEmpty()) {
            result.add(temp);
            temp = "";
        }
        return result;
    }

    public String cutTo45Strings(String inp) {
        if (inp == null) {
            return null;
        }
        try {
            if (inp.length() > 44) {
                return inp.substring(0, 44);
            }
        } catch (Exception e){
            return inp;
        }
        return inp;
    }
}
