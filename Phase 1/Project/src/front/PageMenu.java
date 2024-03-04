package front;

import back.MethodReturns;
import back.messages.LikeView;
import back.messages.Message;
import back.messengers.Page;
import back.usersPackage.User;
import back.usersPackage.UserType;
import dataBase.DataBaseGetter;

import java.util.ArrayList;

public class PageMenu extends Menu {
    static private PageMenu instance = new PageMenu();

    public static PageMenu getInstance() {
        return instance;
    }

    private PageMenu() {
    }

    @Override
    public void start() {

        String result1 = Printer.getInstance().stringsIndexGetter("Main Menu", "My Page","Suggested Pages" ,"Search");
        if (result1.equals("Main Menu")) {
            MainMenu.getInstance().start();
            return;
        }
        if (result1.equals("My Page")) {
            openPage(User.getLoggedInUser().getUserName());
        }
        if(result1.equals("Suggested Pages")){
            try {
                usersList(DataBaseGetter.getInstance().getPage(User.getLoggedInUser().getUserName()).getPageSuggest(), User.getLoggedInUser().getUserName());
            }catch (Exception e){
                Printer.getInstance().error();
                start();
            }
            return;
        }
        MainMenu.getInstance().search();
    }

    void openPage(String userName) {
        Page page = Page.openPage(userName);
        Page loggedPage = DataBaseGetter.getInstance().getPage(User.getLoggedInUser().getUserName());
        if (page == null) {
            Printer.getInstance().noUserName();
            return;
        }

        String f, b, result1;
        Printer.getInstance().printPageName(page);
        if (page.getBlocked().indexOf(User.getLoggedInUser().getUserName()) >= 0) {// blocked
            if (loggedPage.getBlocked().indexOf(userName) >= 0) {
                b = "Unblock";
            } else {
                b = "Block";
            }
            result1 = Printer.getInstance().stringsIndexGetter("Main Menu", b);
        } else {
            page.viewThisPage();
            ArrayList<String> posts = Message.postsToStringToPrint(page.getPosts());
            if (!userName.equals(User.getLoggedInUser().getUserName())) {//OtherUser
                if (page.getFollowers().indexOf(User.getLoggedInUser().getUserName()) < 0) {
                    f = "Follow";
                } else {
                    f = "Unfollow";
                }
                if (loggedPage.getBlocked().indexOf(userName) >= 0) {
                    b = "Unblock";
                } else {
                    b = "Block";
                }
                result1 = Printer.getInstance().stringsIndexGetter("Main Menu", f, "Followers", "Followings", "Posts", b);
            } else {
                if (User.getLoggedInUser().getUserType().equals(UserType.BUSINESS_USER)) {
                    result1 = Printer.getInstance().stringsIndexGetter("Main Menu", "Edit Name", "Followers", "Followings", "Posts", "Blocked", "Page Views");
                } else {
                    result1 = Printer.getInstance().stringsIndexGetter("Main Menu", "Edit Name", "Followers", "Followings", "Posts", "Blocked");
                }
            }
        }
        switch (result1) {
            case "Follow":
                page.follow();
                openPage(userName);
                return;
            case "Unfollow":
                page.unfollow();
                openPage(userName);
                return;
            case "Block":
                loggedPage.block(userName);
                openPage(userName);
                return;
            case "Unblock":
                loggedPage.unblock(userName);
                openPage(userName);
                return;
            case "Followers":
                usersList(page.getFollowers(), userName);
                return;
            case "Followings":
                usersList(page.getFollowings(), userName);
                return;
            case "Blocked":
                usersList(page.getBlocked(), userName);
                return;
            case "Edit Name": {
                Printer.getInstance().editPageName();
                MethodReturns a = loggedPage.editName(Printer.getInstance().getScanner().nextLine());
                if (a.equals(MethodReturns.DONE)) {
                    Printer.getInstance().done();
                } else if (a.equals(MethodReturns.UNKNOWN_DATABASE_ERROR)) {
                    Printer.getInstance().error();
                } else {
                    Printer.getInstance().badInput();
                }
                openPage(userName);
                return;
            }
            case "Posts":
                openPosts(userName);
                return;
            case "Main Menu":
                MainMenu.getInstance().start();
                return;
            case "Page Views":
                ArrayList<String> views = new ArrayList<>();
                for (LikeView pageView : page.getPageViews()) {
                    views.add(pageView.getUserName() + " / " + pageView.getDate() + " / " + pageView.getTime());
                }
                Printer.getInstance().printInOneLine(views, "No View!");
                openPage("User Name");
        }
    }

    private void usersList(ArrayList<String> input, String backUserName) {
        ArrayList<String> options = new ArrayList<>(input);
        options.add("Back");
        options.add("Main Menu");
        int index1 = Printer.getInstance().stringArrayIndexGetter(options);
        if (index1 == options.size() - 1) {
            MainMenu.getInstance().start();
        } else if (index1 == options.size() - 2) {
            openPage(backUserName);
        } else {
            openPage(options.get(index1));
        }
    }

    void openPosts(String userName) {
        Page page = DataBaseGetter.getInstance().getPage(userName);
        final boolean isSameUser = userName.equals(User.getLoggedInUser().getUserName());
        final boolean isBusiness = UserType.BUSINESS_USER.equals(User.getLoggedInUser().getUserType()) && isSameUser;
        ArrayList<String> options1 = new ArrayList<>(Message.postsToStringToPrint(page.getPosts()));
        Page myPage=DataBaseGetter.getInstance().getPage(User.getLoggedInUser().getUserName());
        ArrayList<String> adds=myPage.printableAdds(page.getPosts().size()/4);

        for (Message post : page.getPosts()) {
            post.viewedByLoggedInUser();
        }
        if (isSameUser) {
            options1.add("New Post");
        }
        options1.add("Back");
        options1.add("Main Menu");
        int index1 = Printer.getInstance().stringArrayIndexGetter(options1,adds);
        if (index1 == options1.size() - 1) {
            MainMenu.getInstance().start();
            return;
        }
        if (index1 == options1.size() - 2) {
            openPage(userName);
            return;
        }
        if (isSameUser && index1 == options1.size() - 3) {
            newPost(userName);
            openPosts(userName);
            return;
        }

        String result2;
        if (isSameUser) {
            if (isBusiness) {
                result2 = Printer.getInstance().stringsIndexGetter("Delete Post", "Views", "Likes ", "New Comment", "Show Comments", "Like / Dislike");
            } else {
                result2 = Printer.getInstance().stringsIndexGetter("Delete Post", "Likes", "New Comment", "Show Comments", "Like / Dislike");
            }
        } else {
            result2 = Printer.getInstance().stringsIndexGetter("Like / Dislike", "New Comment", "Show Comments");
        }
        Message selectedPost = page.getPosts().get(index1);

        switch (result2) {
            case "Like / Dislike": {
                selectedPost.likeDislikeByLoggedInUser();
                openPosts(userName);
                return;
            }
            case "New Comment": {
                newComment(userName, selectedPost.getKeyID());
                openPosts(userName);
                openComments(userName,selectedPost.getKeyID());
                return;
            }
            case "Show Comments":
                openComments(userName, selectedPost.getKeyID());
                return;
            case "Likes": {
                ArrayList<String> likes = new ArrayList<>();
                for (LikeView l : selectedPost.getLikes()) {
                    likes.add(l.getUserName());
                }
                Printer.getInstance().printInOneLine(likes, "No Like!");
                openPosts(userName);
                return;
            }

            case "Likes ": {
                ArrayList<String> likes = new ArrayList<>();
                for (LikeView l : selectedPost.getLikes()) {
                    likes.add(l.getUserName() + " / " + l.getDate() + " / " + l.getTime());
                }
                Printer.getInstance().printInOneLine(likes, "No Like!");
                openPosts(userName);
                return;
            }

            case "Delete Post":
                page.deletePost(selectedPost.getKeyID());
                openPosts(userName);
                return;
            case "Views": {
                ArrayList<String> views = new ArrayList<>();
                for (LikeView l : selectedPost.getViews()) {
                    views.add(l.getUserName() + " / " + l.getDate() + " / " + l.getTime());
                }
                Printer.getInstance().printInOneLine(views, "No Like!");
                openPosts(userName);
                return;
            }
        }
    }

    private void newPost(String userName) {
        Page page=DataBaseGetter.getInstance().getPage(userName);
        String text;
        do {
             text = Printer.getInstance().getNewMessageText();
        }while (text.isEmpty());
        page.newPost(text);
    }

    private void newComment(String userName, int postId) {
        Page page=DataBaseGetter.getInstance().getPage(userName);
        String text;
        do {
            text = Printer.getInstance().getNewMessageText();
        }while (text.isEmpty());
        page.newComment(text,postId);
    }

    private void openComments(String userName, int postId) {
        Page page = DataBaseGetter.getInstance().getPage(userName);
        if (page == null) {
            openPage(userName);
            return;
        }
        ArrayList<Message> comments = page.getPostComments(postId);
        ArrayList<String> options = Message.commentToStringToPrint(postId, comments);
        options.add("New Comment");
        options.add("Back");
        int index1 = Printer.getInstance().stringArrayIndexGetter(options);
        if (index1 == options.size() - 1) {
            openPosts(userName);
            return;
        }
        if (index1 == options.size() - 2) {
            newComment(userName, postId);
            openComments(userName,postId);
            return;
        }
        Message selectedComment = comments.get(index1);
        String result2;
        if (User.getLoggedInUser().isUserNameEqual(userName)) {
            result2 = Printer.getInstance().stringsIndexGetter("Like / Dislike","Likes","Views","Reply");
        } else {
            result2 = Printer.getInstance().stringsIndexGetter("Like / Dislike","Reply");
        }

        switch (result2){
            case "Like / Dislike":
            selectedComment.likeDislikeByLoggedInUser();
            openComments(userName,postId);
            return;
            case "Likes": {
                ArrayList<String> likes = new ArrayList<>();
                for (LikeView l : selectedComment.getLikes()) {
                    likes.add(l.getUserName());
                }
                Printer.getInstance().printInOneLine(likes, "No Like!");
                openComments(userName,postId);
                return;
            }
            case "Views": {
                ArrayList<String> views = new ArrayList<>();
                for (LikeView l : selectedComment.getViews()) {
                    views.add(l.getUserName());
                }
                Printer.getInstance().printInOneLine(views, "No Like!");
                openComments(userName,postId);
                return;
            }
            case "Reply":
                newComment(userName,selectedComment.getKeyID());
                openComments(userName,postId);
                return;
        }
        
    }


}
