package form;

import javax.swing.*;
import java.awt.*;

public class WelcomeLoading extends JFrame {
    private JPanel Main;
    private JPanel textpanel;
    private JPanel logopanel;

    public WelcomeLoading() {
        setTitle("Welcome");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1440,900));
        setLocationRelativeTo(null);
        setContentPane(Main);
        setResizable(false);
        setVisible(true);
    }

}
