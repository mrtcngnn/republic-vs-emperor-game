import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class GameOverPopUp extends JFrame {

    private String message;

    public GameOverPopUp(String message) {
        setLayout(new BorderLayout());
        this.message = message;
        JLabel result = new JLabel(message);
        add(result, BorderLayout.CENTER);
        setTitle("GAME OVER");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

}
