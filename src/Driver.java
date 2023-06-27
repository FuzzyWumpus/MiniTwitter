//Our main class that starts the program by an instance of the mainFrame.
public class Driver {
    public static void main(String[] args) throws Exception {
         MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.initialize();
    }
}
