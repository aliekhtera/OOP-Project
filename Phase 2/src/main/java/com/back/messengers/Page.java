package com.back.messengers;

import com.back.MethodReturns;
import com.back.messages.LikeView;
import com.back.messages.Message;
import com.back.usersPackage.User;
import com.dataBase.DataBaseGetter;
import com.dataBase.DataBaseSetter;

import java.util.*;


public class Page {
   // public static ArrayList<Page> tempPages;
    final private String ownerUserName;
    private String pageName;
    final private ArrayList<Message> posts;
    final private ArrayList<Message> comments;
    final private ArrayList<String> blocked;
    final private ArrayList<String> followers;
    final private ArrayList<String> followings;
    final private ArrayList<LikeView> pageViews;

    public Page(String owner, String pageName, ArrayList<Message> posts, ArrayList<Message> comments, ArrayList<String> blocked
            , ArrayList<String> followers, ArrayList<String> followings, ArrayList<LikeView> pageViews) {
        this.ownerUserName = owner;
        this.pageName = pageName;
        this.posts = new ArrayList<>(posts);
        this.comments = new ArrayList<>(comments);
        this.blocked = new ArrayList<>(blocked);
        this.followers = new ArrayList<>(followers);
        this.followings = new ArrayList<>(followings);
        this.pageViews = new ArrayList<>(pageViews);
    }

    public ArrayList<String> getFollowings() {
        return new ArrayList<>(followings);
    }

    public ArrayList<Message> getPosts() {
        return new ArrayList<>(posts);
    }

    public String getPageName() {
        return pageName;
    }

    public ArrayList<Message> getComments() {
        return new ArrayList<>(comments);
    }

    public ArrayList<String> getBlocked() {
        return new ArrayList<>(blocked);
    }

    public ArrayList<String> getFollowers() {
        return new ArrayList<>(followers);
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public static Page openPage(String userName) {
        if (DataBaseGetter.getInstance().getUser(userName) == null) {
            return null;
        }
        return DataBaseGetter.getInstance().getPage(userName);
    }

    public boolean newPost(String text) {
        Message temp = Message.newMessage(text, null, false);
        if (temp == null) {
            return false;
        }
        posts.add(temp);
        return DataBaseSetter.getInstance().editPagePosts(this).equals(MethodReturns.DONE);
    }

    public boolean newPost(Message post) {
        if (post == null) {
            return false;
        }
        if(post.getKeyID()<0){
            return false;
        }
        posts.add(post);
        return DataBaseSetter.getInstance().editPagePosts(this).equals(MethodReturns.DONE);
    }

    public boolean newComment(Message temp, int id) {
        if (temp == null) {
            return false;
        }
        comments.add(temp);
        return DataBaseSetter.getInstance().editPageComments(this).equals(MethodReturns.DONE);
    }

    public boolean deletePost(int messageID) {
        int index = -1;
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getKeyID() == messageID) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }
        posts.remove(index);
        return DataBaseSetter.getInstance().editPagePosts(this).equals(MethodReturns.DONE);
    }

  /*  public boolean deleteComment(int messageID) {
        int index = -1;
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getKeyID() == messageID) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }
        comments.remove(index);
        return DataBaseSetter.getInstance().editPageComments(this).equals(MethodReturns.DONE);
    }*/

    public void block(String un) {
        if (ownerUserName.equals(un)) {
            return;
        }
        if (blocked.contains(un)) {
            return;
        }
        blocked.add(un);
        DataBaseSetter.getInstance().editPageBlock(this);
    }

    public void follow() {
        String un = Objects.requireNonNull(User.getLoggedInUser()).getUserName();
        if (ownerUserName.equals(un)) {
            return;
        }
        if (followers.contains(un)) {
            return;
        }
        followers.add(un);
        DataBaseSetter.getInstance().editPageFollowers(this);
    }

    public void unfollow() {
        String un = User.getLoggedInUser().getUserName();
        int index = followers.indexOf(un);
        if (index >= 0) {
            followers.remove(index);
            DataBaseSetter.getInstance().editPageFollowers(this);
        }
    }

    public void unblock(String un) {
        int index = blocked.indexOf(un);
        if (index >= 0) {
            blocked.remove(index);
            DataBaseSetter.getInstance().editPageBlock(this);
        }
    }

    public MethodReturns editName(String newText) {
        if (newText == null) {
            return MethodReturns.BAD_INPUT;
        }
        if (newText.length() > 0 && newText.length() < 90) {
            pageName = newText;
            return DataBaseSetter.getInstance().editPageName(this);
        }
        return MethodReturns.BAD_INPUT;
    }

    public void viewThisPage() {
        LikeView temp = new LikeView();
        if (User.getLoggedInUser().isUserNameEqual(ownerUserName)) {
            return;
        }
        for (LikeView pageView : pageViews) {
            if (pageView.getUserName().equals(User.getLoggedInUser().getUserName()) && pageView.getDate().equals(temp.getDate())) {
                return;
            }
        }
        pageViews.add(temp);
        DataBaseSetter.getInstance().editPageViews(this);
    }

    public ArrayList<LikeView> getPageViews() {
        return new ArrayList<>(pageViews);
    }

    public ArrayList<Message> getPostComments(int postID) {
        ArrayList<Message> result = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            Message comment = comments.get(i);
            if (comment != null) {
                if (comment.getRepliedTo() == postID) {
                    result.add(comment);
                } else {
                    int index = -1;
                    for (int i1 = 0; i1 < result.size() && index == -1; i1++) {
                        if (comment.getRepliedTo() == result.get(i1).getKeyID()) {
                            index = i1;
                        }
                    }
                    if (index != -1) {
                        result.add(comment);
                    }
                }

            }
        }
        return result;
    }

    private ArrayList<UserLevel> pageSuggest(int num) {
        ArrayList<UserLevel> result = new ArrayList<>();
        ArrayList<Page> temp = new ArrayList<>();
        result.add(new UserLevel(this, 0, true));
        for (String f : followers) {
            Page page = DataBaseGetter.getInstance().getPage(f);
            if (page != null) {
                int index = UserLevel.indexOfArray(result, f);
                if (index == -1) {
                    result.add(new UserLevel(page, 0, true));
                    temp.add(page);
                } else {
                    result.get(index).levelUp(num);
                }
            }
        }

        for (String f : followings) {
            Page page = DataBaseGetter.getInstance().getPage(f);
            if (page != null) {
                int index = UserLevel.indexOfArray(result, f);
                if (index == -1) {
                    result.add(new UserLevel(page, num / 2, false));
                    temp.add(page);
                } else {
                    result.get(index).levelUp(num);
                }
            }
        }

        for (int i = num; i > 0; i--) {
            UserLevel.addNewSuggestLayer(temp, result, i);
        }

        return result;
    }

    public ArrayList<String> getPageSuggest() {
        ArrayList<String> result = new ArrayList<>();
        List<UserLevel> input1 = pageSuggest(5);
        input1.sort(new UserLevel(null, 0));
        for (int i = 0; i < input1.size(); i++) {
            if (input1.get(0).getLevel() == 0) {
                input1.remove(0);
            } else {
                break;
            }
        }
        for (int i = input1.size() - 1, j = 0; i >= 0 && j < 10; j++, i--) {
            result.add(input1.get(i).getUserName());
        }
        return result;
    }

    public ArrayList<String> printableAdds(int max) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<AddLevel> allAddLevels = new ArrayList<>();
        ArrayList<UserLevel> suggestedUserNames = pageSuggest(5);

        {
            ArrayList<Message> allAds = new ArrayList<>();
            ArrayList<String> bUNs = DataBaseGetter.getInstance().getBusinessUserNames();
            try {
                bUNs.remove(User.getLoggedInUser().getUserName());
            } catch (Exception e) {
            }

            for (String s : bUNs) {
                allAds.addAll(DataBaseGetter.getInstance().getPage(s).getPosts());
            }
            for (Message ad : allAds) {
                allAddLevels.add(new AddLevel(ad, 0));
            }
        }

        for (AddLevel addLevel : allAddLevels) {
            for (UserLevel sun : suggestedUserNames) {
                for (LikeView like : addLevel.getMessage().getLikes()) {
                    try {
                        if (like.getUserName().equals(sun.getUserName())) {
                            addLevel.levelUp(3 * sun.getLevel());
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
                for (LikeView view : addLevel.getMessage().getViews()) {
                    try {
                        if (view.getUserName().equals(sun.getUserName())) {
                            addLevel.levelUp(sun.getLevel());
                            break;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        allAddLevels.sort(new AddLevel(null, 0));
        for (int i = allAddLevels.size() - 1, j = 0; i >= 0 && j < max; i--, j++) {
            allAddLevels.get(i).getMessage().viewedByLoggedInUser();
            String temp = "ADVERTISEMENT! \n" + allAddLevels.get(i).getMessage().getSender().getUserName() + " : " + allAddLevels.get(i).getMessage().getText();
            result.add(temp);
        }
        return result;
    }


}

