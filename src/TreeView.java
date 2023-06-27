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
/*Contains our logic for a custom Tree that uses users and groups instead of the standard JTree logic. */
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

        Font customFont = new Font("Monospaced", Font.PLAIN, 18);
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
        DefaultMutableTreeNode parentNode = getSelectedNode();

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
            errorMessage("Cannot add user to a user node.", "Error"); //Exception handler
        }
    }
    
    public void addGroup(String name) {
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode != null && (selectedNode == rootNode || isGroupNode(selectedNode))) {
            Group group = new Group("Group: " + name);
            addObject(group);
        } else {
            errorMessage("Cannot add group to a user node.", "Error"); //Exception handler
        }
    }

    public DefaultMutableTreeNode getSelectedNode() {
        TreePath selectedPath = tree.getSelectionPath();
        if (selectedPath != null) {
            return (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        }
        return null;
    }
    //Method for creating error pop messages.
    public static void errorMessage(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public JTree getTree() {
        return tree;
    }
    //makes custom fonts for the nodes
    public class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        private Font regularFont;
        private Font groupFont;

        public MyTreeCellRenderer(Font font) {
            regularFont = font;
            groupFont = regularFont.deriveFont(Font.BOLD);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

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

                setText(nodeData.getNodeName());
            } else if (userObject instanceof User) {
                User user = (User) userObject;
                setText(user.getUserID());
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
        return (userObject instanceof Group);
    }

    public User getUserFromSelectedNode() {
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode != null) {
            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) userObject;
                Object nodeObject = node.getUserObject();
                if (nodeObject instanceof NodeData) {
                    NodeData nodeData = (NodeData) nodeObject;
                    if (!nodeData.isGroup()) {
                        return (User) nodeObject;
                    }
                }
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