import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
//
public class userFrame extends JFrame implements UserObserver{

    final private Font mainFont = new Font("Segoe print", Font.BOLD, 14);
    JTextField tweet;
    JLabel userID;
    JList<String> followingList;
    JList<String> newsFeedList;
    private User selectedUser;
    private DefaultListModel<String> followingListModel;
    private DefaultListModel<String> newsFeedListModel;
    private TreeView treeView2;
    
    public userFrame(DefaultMutableTreeNode rootNode, User selectedUser, TreeView treeView) {
        userID = new JLabel(selectedUser.getUserID());
        userID.setFont(mainFont);
        this.selectedUser = selectedUser;
        followingListModel = new DefaultListModel<>();
        newsFeedListModel = new DefaultListModel<>();
        treeView2 = treeView;
        selectedUser.registerObserver(this);
        System.out.println(selectedUser.getNewsFeed());
         

        JButton followUser = new JButton("Follow User");
        followUser.setFont(mainFont);
        followUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = JOptionPane.showInputDialog(userFrame.this, "Enter the ID of the user to follow:", "Follow User", JOptionPane.PLAIN_MESSAGE);
                if (userName != null && !userName.isEmpty()) {
                    User userToFollow = treeView.getUser(userName);
                    if (userToFollow != null) {
                        selectedUser.follow(userToFollow);
                        List<String> followings = selectedUser.getFollowings();
                        DefaultListModel<String> model = (DefaultListModel<String>) followingList.getModel();
                        model.clear();
                        for (String following : followings) {
                            model.addElement(following);
                        }
                         userToFollow.addFollower(selectedUser.getUserID());

                        JOptionPane.showMessageDialog(userFrame.this, "You are now following the user: " + userName, "Follow User", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(userFrame.this, "User with ID '" + userName + "' does not exist.", "User Not Found", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton postTweet = new JButton("Post Tweet");
        postTweet.setFont(mainFont);
        postTweet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tweetText = tweet.getText();
                if (!tweetText.isEmpty()) {
                    selectedUser.postTweet(selectedUser.getUserID() + ": " + tweetText);
                    JOptionPane.showMessageDialog(userFrame.this, "Tweet posted successfully.", "Post Tweet", JOptionPane.INFORMATION_MESSAGE);
                    tweet.setText("");
                    updateNewsFeed();
                    
                 
                }
            }
        });

        JPanel IDPanel = new JPanel();
        IDPanel.setLayout(new FlowLayout());
        IDPanel.setOpaque(false);
        IDPanel.add(userID);

        JPanel tweetPanel = new JPanel();
        tweet = new JTextField();
        tweet.setFont(mainFont);
        tweetPanel.setLayout(new GridLayout(2, 2, 6, 5));
        tweetPanel.setOpaque(false);
        tweetPanel.add(postTweet);
        tweetPanel.add(tweet);

        followingList = new JList<>(followingListModel);
        followingList.setFont(mainFont);

        if (selectedUser != null) {
            // Populate the following list with the users the current user is following
            List<String> followings = selectedUser.getFollowings();
            for (String following : followings) {
                followingListModel.addElement(following);
            }
        }

        JPanel followingListPanel = new JPanel();
        followingListPanel.setLayout(new BorderLayout());
        JLabel label1 = new JLabel("List View (Current Following)");
        followingListPanel.add(label1, BorderLayout.NORTH);

        JScrollPane followingListScrollPane = new JScrollPane(followingList);
        followingListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        followingListScrollPane.setPreferredSize(new Dimension(150, 200));

        // Add the current following list to the GUI
        followingListPanel.add(followingListScrollPane, BorderLayout.CENTER);



        newsFeedList = new JList<>(newsFeedListModel);
        newsFeedList.setFont(mainFont);
        // Populate the news feed list with the initial tweets
         List<String> newsFeed = selectedUser.getNewsFeed();
            for (String tweet : newsFeed) {
        newsFeedListModel.addElement(tweet);
    }

        JPanel newsFeedPanel = new JPanel();
        newsFeedPanel.setLayout(new BorderLayout());
        JLabel label2 = new JLabel("List View (News Feed)");
        newsFeedPanel.add(label2, BorderLayout.NORTH);

        JScrollPane newsFeedScrollPane = new JScrollPane(newsFeedList);
        newsFeedScrollPane.setBorder(BorderFactory.createEmptyBorder());
        newsFeedScrollPane.setPreferredSize(new Dimension(150, 200));

       
        newsFeedPanel.add(newsFeedScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(128, 128, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(followUser, BorderLayout.SOUTH);
        mainPanel.add(tweetPanel, BorderLayout.CENTER);
        mainPanel.add(IDPanel, BorderLayout.NORTH);
        mainPanel.add(followingListPanel, BorderLayout.WEST);
        mainPanel.add(newsFeedPanel, BorderLayout.EAST);

        add(mainPanel);

        setTitle("User View");
        setSize(500, 600);
        setMinimumSize(new Dimension(300, 400));
        setVisible(true);
    }

    
@Override
        public void updateNewsFeed() {
        SwingUtilities.invokeLater(() -> {
            newsFeedListModel.clear();
            List<String> newsFeed = selectedUser.getNewsFeed();
            for (String tweet : newsFeed) {
                newsFeedListModel.addElement(tweet);
            }
        });
    }

    private static User getUserByID(DefaultMutableTreeNode node, String userID) {
        Object userObject = node.getUserObject();

        if (userObject instanceof User) {
            User user = (User) userObject;
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }

        // Check child nodes recursively
        Enumeration<TreeNode> children = node.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
            User foundUser = getUserByID(childNode, userID);
            if (foundUser != null) {
                return foundUser;
            }
        }

        return null;
    }
}