package com.back.messengers;

import com.back.messages.Message;

import java.util.Comparator;

public class AddLevel implements Comparator<AddLevel> {
    private int level;
    private Message message;


    AddLevel(Message message, int l) {
        this.message = message;
        level = l;
    }

    public int getLevel() {
        return level;
    }

    public Message getMessage() {
        return message;
    }

    void levelUp(int i) {
        level += i;
    }


    @Override
    public int compare(AddLevel o1, AddLevel o2) {
        if (o1.level> o2.level){
            return 1;
        }else if(o1.level==o2.level){
            return 0;
        }
        return -1;
    }
}
