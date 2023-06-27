public class TotalMessageCountVisitor implements UserVisitor {
    private int totalMessageCount = 0;

    @Override
    public void visitUser(User user) {
        totalMessageCount += user.getMessageCount();
    }

    public int getTotalMessageCount() {
        return totalMessageCount;
    }
}