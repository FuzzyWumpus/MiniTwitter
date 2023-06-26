
public class PositiveMessageCountVisitor implements UserVisitor {
    private int positiveMessageCount = 0;

    @Override
    public void visitUser(User user) {
        positiveMessageCount += user.getPositiveMessageCount();
    }

    public int getPositiveMessageCount() {
        return positiveMessageCount;
    }
}