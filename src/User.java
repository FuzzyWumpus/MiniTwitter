import java.util.ArrayList;
import java.util.List;

/* User class: defines what a user is based on component.
 * users have timestamps for updates to their profile and creation
 * users can post tweets and follow other valid users.
*/
public class User implements Component {

    private String userID;
    private List<String> followers;
    private List<String> followings;
    private List<String> newsFeed;
    private TreeView treeView;
    private int messageCount;
    private int positiveMessageCount;
    private List<UserObserver> observers;
    private static final String[] positiveWords = { "good", "great", "awesome", "happy", "excellent" };
    private long creationTime;
    private long lastUpdateTime;

    public User(String ID, TreeView tV) {
        setUserID(ID);
        followers = new ArrayList<>();
        followings = new ArrayList<>();
        newsFeed = new ArrayList<>();
        treeView = tV;
        observers = new ArrayList<>();
        positiveMessageCount = 0;
        creationTime = System.currentTimeMillis();
        lastUpdateTime = 0;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public void addUser(String name) {
        // Not applicable to individual users
    }

    @Override
    public void addGroup(String name) {
        // Not applicable to individual users
    }

    public void accept(UserVisitor visitor) {
        visitor.visitUser(this);
    }

    public String getUserID() {

        return userID;

    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List getFollowings() {
        return followings;
    }

    public void follow(User userToFollow) {
        if (!followings.contains(userToFollow.getUserID()) && userToFollow.getUserID() != this.getUserID()) {
            followings.add(userToFollow.getUserID());
            userToFollow.addFollower(userID);
        } else {

        }
    }

    public void addFollower(String userID2) {
        if (!followers.contains(userID2)) {
            followers.add(userID2);
        }
    }

    public void registerObserver(UserObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(UserObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (UserObserver observer : observers) {
            observer.updateNewsFeed();
        }
    }

    // updatedFeed boolean is used to detect whether the postTweet is coming from
    // updateFollowersNewsFeed or from the userFrame
    // this way repeat messages across followers aren't counted multiple times.
    public void postTweet(String tweet, boolean updatedFeed) {
        if (updatedFeed == false) {
            messageCount++;
            if (isPositiveMessage(tweet)) {
                positiveMessageCount++;
            }
        }
        if (!newsFeed.contains(tweet)) {
            newsFeed.add(tweet);
            updateFollowersNewsFeed(tweet); // Update the news feed of all followers
            notifyObservers(); // Notify the observers about the new tweet
            lastUpdateTime = System.currentTimeMillis();

        }
    }

    // Checks message for positive words based on the positiveWords list. //TO DO
    // incorpate a smarter way to detect positive messages perhaps using AI?
    private boolean isPositiveMessage(String tweet) {
        for (String positiveWord : positiveWords) {
            if (tweet.contains(positiveWord)) {
                return true;
            }
        }
        return false;
    }

    // Updates each user that follows this user by posting the tweet in their News
    // Feed.
    public void updateFollowersNewsFeed(String tweet) {
        for (String follower : followers) {
            User followerUser = treeView.getUser(follower);
            if (followerUser != null) {
                followerUser.postTweet(tweet, true);
            }
        }
    }

    public int getMessageCount() {
        return messageCount;
    }

    public List<String> getNewsFeed() {
        return newsFeed;
    }

    public void updateNewsFeed() {
        newsFeed.clear();

        for (String following : followings) {
            User followingUser = treeView.getUser(following);
            if (followingUser != null) {
                List<String> followingNewsFeed = followingUser.getNewsFeed();
                newsFeed.addAll(followingNewsFeed);
            }
        }

        newsFeed.sort((tweet1, tweet2) -> {
            // sorts and compares the tweets so they appear in the correct order.
            String timestamp1 = tweet1.substring(tweet1.lastIndexOf("(") + 1, tweet1.lastIndexOf(")"));
            String timestamp2 = tweet2.substring(tweet2.lastIndexOf("(") + 1, tweet2.lastIndexOf(")"));
            return timestamp1.compareTo(timestamp2);
        });
    }

    public int getPositiveMessageCount() {
        return positiveMessageCount;
    }
}
