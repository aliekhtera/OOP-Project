package com.back.messengers;


import com.dataBase.DataBaseGetter;

import java.util.ArrayList;
import java.util.Comparator;

class UserLevel implements Comparator<UserLevel> {
    private int level;
    private Page page;
    boolean constLevel;

    UserLevel(Page page, int l) {
        this.page = page;
        level = l;
        constLevel = false;
    }

    UserLevel(Page page, int l, boolean isConst) {
        this.page = page;
        level = l;
        constLevel = isConst;
    }

    public Page getPage() {
        return page;
    }

    public String getUserName() {
        return page.getOwnerUserName();
    }

    public int getLevel() {
        return level;
    }

    void levelUp(int i) {
        if (constLevel) {
            return;
        }
        level += i;
    }

    static int indexOfArray(ArrayList<UserLevel> userLevels, String un) {
        for (int i = 0; i < userLevels.size(); i++) {
            if (userLevels.get(i).getUserName().equals(un)) {
                return i;
            }
        }
        return -1;
    }

    public static void addNewSuggestLayer(ArrayList<Page> last, ArrayList<UserLevel> result, int num) {
        ArrayList<Page> temp = new ArrayList<>();
        for (Page p : last) {
            for (String f : p.getFollowers()) {
                Page page = DataBaseGetter.getInstance().getPage(f);
                if (page != null) {
                    int index = UserLevel.indexOfArray(result, f);
                    if (index == -1) {
                        result.add(new UserLevel(page, num));
                        temp.add(page);
                    } else {
                        result.get(index).levelUp(num);
                    }
                }
            }

            for (String f : p.getFollowings()) {
                Page page = DataBaseGetter.getInstance().getPage(f);
                if (page != null) {
                    int index = UserLevel.indexOfArray(result, f);
                    if (index == -1) {
                        result.add(new UserLevel(page, num));
                        temp.add(page);
                    } else {
                        result.get(index).levelUp(num);
                    }
                }
            }

        }

        last.clear();
        last.addAll(temp);
    }

    @Override
    public int compare(UserLevel o1, UserLevel o2) {
        if (o1.level > o2.level) {
            return 1;
        } else if (o1.level == o2.level) {
            return 0;
        } else {
            return -1;
        }
    }

}
