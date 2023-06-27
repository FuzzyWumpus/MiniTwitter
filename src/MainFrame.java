import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
/* Admin control GUI, also it a singleton. Contains many buttons for anaylsis( user and group
 * counts, Valid IDs positive msg percentage, total messages and last updated user) also contains the tree view
 * which contains our root node and allows the user to create groups and users to selected nodes (unless its a user selected.)
 * finally it a has a user view button to show what the selected user would see. 
 */
public class MainFrame extends JFrame {
    final private Font mainFont = new Font("Segoe print", Font.BOLD, 17);
    private List<Button> buttons;
    JTextField name;
    private TreeView treeView;
    private JTree tree;
    private int userCount;
    private int groupCount;
    private DefaultMutableTreeNode rootNode;
    private int messageCount;
    private static MainFrame instance;

    private MainFrame() {
        // Private constructor to prevent instantiation from outside the class
    }

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    public void initialize() {

        rootNode = new DefaultMutableTreeNode("Root");
        JLabel output = new JLabel();
        name = new JTextField();
        name.setFont(mainFont);
        treeView = new TreeView();
        treeView.setPreferredSize(new Dimension(500, 500));
        tree = treeView.getTree();
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setOpaque(false);
        output.setFont(mainFont);
        messageCount = 0;

        rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

        JButton addUser = new JButton("Add User ");
        addUser.setFont(mainFont);
        addUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!name.getText().isEmpty()) {

                    treeView.addUser(name.getText());
                    userCount++;
                    name.setText("");
                }

            }
        });

        JButton addGroup = new JButton("Add Group");
        addGroup.setFont(mainFont);
        addGroup.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (treeView.getSelectedNode() != null && !name.getText().isEmpty()) {
                    groupCount++;
                    treeView.addGroup(name.getText());
                }
                name.setText("");
            }
        });

        JButton userView = new JButton("User View");
        userView.setFont(mainFont);
        userView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeView.getTree()
                        .getLastSelectedPathComponent();

                // Check if a node is selected
                if (selectedNode != null) {
                    try {
                        Object selectedObject = selectedNode.getUserObject();

                        User selectedUser = (User) selectedObject;
                        userFrame userFrame = new userFrame(rootNode, selectedUser, treeView); // Pass the rootNode and
                                                                                               // selectedUser to the
                                                                                               // userFrame constructor
                        userFrame.setVisible(true); // Make the userFrame visible
                    } catch (Exception ee) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Please select a user from the tree.",
                                "User Selection", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Please select a user from the tree.",
                            "User Selection", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JButton userTotal = new JButton("Show User Total ");
        userTotal.setFont(mainFont);
        userTotal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                output.setText("Total number of users: " + userCount);
            }
        });

        JButton groupTotal = new JButton("Show Group Total");
        groupTotal.setFont(mainFont);
        groupTotal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                output.setText("Total number of groups: " + groupCount);
            }
        });

        JButton messageTotal = new JButton("Show Message Total ");
        messageTotal.setFont(mainFont);
        messageTotal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int totalMessages = calculateTotalMessages(rootNode); // Calculate the total messages from the root node
                output.setText("Total number of messages: " + totalMessages);
            }
        });

        JButton positiveMSG = new JButton("Show Positive %");
        positiveMSG.setFont(mainFont);
        positiveMSG.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                double positivePercentage = calculatePositivePercentage(rootNode); // Calculate the percentage of
                                                                                   // messages with positive words
                output.setText("Percentage of messages with positive words: " + positivePercentage + "%");
            }
        });

        JButton validateID = new JButton("Valid IDs? (No dubs/no spaces)");
        validateID.setFont(mainFont);
        validateID.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean validIDs = validateIDs(rootNode); // Check if all IDs are valid
                if (validIDs) {
                    output.setText("All IDs are valid.");
                } else {
                    output.setText("Invalid IDs found.");
                }
            }
        });

        JButton lastUpdated = new JButton("Last Updated User");
        lastUpdated.setFont(mainFont);
        lastUpdated.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                output.setText("Last updated user is: " + findUserWithMostRecentUpdate()); // calls method from most
                                                                                           // recent updated user
                                                                                           // returns userID
            }
        });

        // User/Group Button Panel
        JPanel buttonsPanel = new JPanel();

        Border userT = BorderFactory.createTitledBorder("User/Group Management");
        buttonsPanel.setLayout(new GridLayout(2, 3, 6, 5));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(userT);
        buttonsPanel.add(addUser);
        buttonsPanel.add(name);
        buttonsPanel.add(addGroup);
        buttonsPanel.add(userView);

        // Analysis View Panel
        JPanel analysisView = new JPanel();
        Border analysisT = BorderFactory.createTitledBorder("Admin Analysis");
        analysisView.setBorder(analysisT);
        GridBagLayout layout = new GridBagLayout();
        analysisView.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        analysisView.setOpaque(false);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        analysisView.add(userTotal, c);
        c.gridx = 1;
        c.gridy = 0;
        analysisView.add(groupTotal, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 20;
        c.gridx = 0;
        c.gridy = 1;
        analysisView.add(messageTotal, c);
        c.gridx = 1;
        c.gridy = 1;
        analysisView.add(positiveMSG, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        analysisView.add(validateID, c);
        c.gridx = 1;
        c.gridy = 2;
        analysisView.add(lastUpdated, c);
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        analysisView.add(output, c);

        // Main Panel that contains all other panels.
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(128, 128, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(treeScrollPane, BorderLayout.CENTER);
        mainPanel.add(analysisView, BorderLayout.NORTH);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        pack();

        add(mainPanel);

        setTitle("Mini Twitter Admin View");
        setSize(550, 600);
        setMinimumSize(new Dimension(400, 400));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

    }

    private boolean validateIDs(DefaultMutableTreeNode node) {
        return validateUniqueIDs(node) && validateNoSpaceIDs(node);
    }

    private boolean validateUniqueIDs(DefaultMutableTreeNode node) {
        validateVistor visitor = new validateVistor();
        traverseTree(node, visitor);

        List<String> userIDs = visitor.getUserIDs();
        return userIDs.size() == new HashSet<>(userIDs).size();
    }

    private boolean validateNoSpaceIDs(DefaultMutableTreeNode node) {
        validateVistor visitor = new validateVistor();
        traverseTree(node, visitor);

        for (String userID : visitor.getUserIDs()) {
            if (userID.contains(" ")) {
                return false;
            }
        }

        return true;
    }

    private int calculateTotalMessages(DefaultMutableTreeNode node) {
        TotalMessageCountVisitor visitor = new TotalMessageCountVisitor();
        traverseTree(node, visitor);
        return visitor.getTotalMessageCount();
    }

    private double calculatePositivePercentage(DefaultMutableTreeNode node) {
        PositiveMessageCountVisitor visitor = new PositiveMessageCountVisitor();
        traverseTree(node, visitor);

        int totalMessageCount = calculateTotalMessages(node);
        if (totalMessageCount == 0) {
            return 0;
        } else {
            double positivePercentage = (double) visitor.getPositiveMessageCount() / totalMessageCount * 100;
            return positivePercentage;
        }
    }

    private void traverseTree(DefaultMutableTreeNode node, UserVisitor visitor) {
        Object userObject = node.getUserObject();
        if (userObject instanceof User) {
            User user = (User) userObject;
            user.accept(visitor);
        }

        Enumeration<?> children = node.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
            traverseTree(childNode, visitor);
        }
    }

    private class validateVistor implements UserVisitor {
        private List<String> userIDs;

        public validateVistor() {
            userIDs = new ArrayList<>();
        }

        @Override
        public void visitUser(User user) {
            userIDs.add(user.getUserID());
        }

        public List<String> getUserIDs() {
            return userIDs;
        }
    }

    private String findUserWithMostRecentUpdate() {
        String userID = "";
        long maxUpdateTime = 0;

        Enumeration<TreeNode> nodes = treeView.getRootNode().breadthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
            Object userObject = node.getUserObject();

            if (userObject instanceof User) {
                User user = (User) userObject;
                long lastUpdateTime = user.getLastUpdateTime();
                if (lastUpdateTime > maxUpdateTime) {
                    maxUpdateTime = lastUpdateTime;
                    userID = user.getUserID();
                }
            }
        }

        return userID;
    }

}
