import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JOptionPane;

public class TreeView extends JPanel {

    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    protected DefaultMutableTreeNode lastSelectedNode;
    private Component root;
    public TreeView() {
        rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        JScrollPane treeView = new JScrollPane(tree);
        treeView.setOpaque(false);
        treeView.getViewport().setOpaque(false);
        treeView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        Border treeViewT = BorderFactory.createTitledBorder("Tree View");
        treeView.setBorder(treeViewT);

        Font customFont = new Font(tree.getFont().getName(), Font.BOLD, tree.getFont().getSize() + 2);
        tree.setCellRenderer(new MyTreeCellRenderer(customFont));

        add(treeView);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = getSelectedNode();
            if (selectedNode != null && isGroupNode(selectedNode)) {
                lastSelectedNode = selectedNode;
            }
        });
    }

    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = lastSelectedNode;

        if (parentNode == null) {
            parentNode = rootNode;
        }

        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        return childNode;
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    public void addUser(String name) {
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode != null && (selectedNode == rootNode || isGroupNode(selectedNode))) {
            addObject(new User(name, this)); // Create a new User instance with the provided name
        } else {
            errorMessage("Cannot add user to a user node.", "Error");
        }
    }

    public void addGroup(String name) {
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode != null && (selectedNode == rootNode || isGroupNode(selectedNode))) {
            addObject(new NodeData("Group: " + name, true));
        } else {
            errorMessage("Cannot add group to a user node.", "Error");
        }
    }

    public DefaultMutableTreeNode getSelectedNode() {
        TreePath selectedPath = tree.getSelectionPath();
        if (selectedPath != null) {
            return (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        }
        return null;
    }

    public static void errorMessage(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public JTree getTree() {
        return tree;
    }

    public class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    private Font regularFont;
    private Font groupFont;

    public MyTreeCellRenderer(Font font) {
        regularFont = font;
        groupFont = font.deriveFont(Font.BOLD); // Bold font style for groups
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (userObject instanceof NodeData) {
            NodeData nodeData = (NodeData) userObject;

            if (nodeData.isGroup()) {
                // Customize group nodes with bold font
                setFont(groupFont);
            } else {
                // Regular nodes use regular font
                setFont(regularFont);
            }
        } else if (userObject instanceof User) {
            User user = (User) userObject;
            setText(user.getUserID()); // Set the IDname as the text of the tree node
            setFont(regularFont);
        }

        return this;
    }
}





    public boolean doesUserExist(String username) {
        return doesUserExist(rootNode, username);
    }

    private boolean doesUserExist(DefaultMutableTreeNode node, String username) {
        if (node == null) {
            return false;
        }

        // Check if the current node represents a user
        Object userObject = node.getUserObject();
        if (userObject instanceof User && ((User) userObject).getUserID().equals(username)) {
            return true;
        }

        // Check child nodes recursively
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            if (doesUserExist(childNode, username)) {
                return true;
            }
        }

        return false;
    }

    private boolean isGroupNode(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        return (userObject instanceof NodeData) && ((NodeData) userObject).isGroup();
    }

    public User getUserFromSelectedNode() {
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode != null) {
            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof User) {
                return (User) userObject;
            }
        }
        return null;




    }

    private static class NodeData {
        private String nodeName;
        private boolean isGroup;

        public NodeData(String nodeName, boolean isGroup) {
            this.nodeName = nodeName;
            this.isGroup = isGroup;
        }

        public String getNodeName() {
            return nodeName;
        }

        public boolean isGroup() {
            return isGroup;
        }

        @Override
        public String toString() {
            return nodeName;
        }
    }





    public User getUser(String userName) {
    return getUser(rootNode, userName);
}

private User getUser(DefaultMutableTreeNode node, String userName) {
    if (node == null) {
        return null;
    }

    Object userObject = node.getUserObject();
    if (userObject instanceof User && ((User) userObject).getUserID().equals(userName)) {
        return (User) userObject;
    }

    for (int i = 0; i < node.getChildCount(); i++) {
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
        User foundUser = getUser(childNode, userName);
        if (foundUser != null) {
            return foundUser;
        }
    }

    return null;
}
}