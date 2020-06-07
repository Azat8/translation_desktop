package mkh.azat.frames;

import javax.swing.*;

public class MainFrame {
    public MainFrame() {}

    public void render() {
        JFrame frame = new JFrame("Main frame");
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setSize(400, 500);
    }
}