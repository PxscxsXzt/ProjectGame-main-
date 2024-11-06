package game.main;

import game.component.MainMenu;
import game.component.PanelGame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class Main extends JFrame {

    private PanelGame panelGame;
    private MainMenu mainMenu;

    public Main() {
        init();
    }

    private void init() {
        setTitle("Bubble Destroyer");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelGame = new PanelGame();
        mainMenu = new MainMenu (new StartGameAction(), e -> System.exit(0));

        add(mainMenu, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                panelGame.start();
            }
        });
    }

    private void switchToGame() {
        remove(mainMenu);  // Remove main menu
        add(panelGame, BorderLayout.CENTER);  // Add game panel
        revalidate();
        repaint();
        panelGame.requestFocus();  // Set focus to game panel
    }

    private class StartGameAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switchToGame();  // Switch to the game when "Start Game" is clicked
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}
