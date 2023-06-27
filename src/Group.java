import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class Group extends DefaultMutableTreeNode implements Component {
  private String name;
  private List<Component> children;
  private long creationTime;

  public Group(String name) {
    super(name);
    creationTime = System.currentTimeMillis();
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  @Override
  public void addUser(String name) {
    // Handled in TreeView
  }

  @Override
  public void addGroup(String name) {
    // Handled in TreeView
  }

}
