package game.component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;
import javax.swing.ImageIcon;
import java.awt.Font;

public class MainMenu extends JPanel {
    
    private JButton startButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private Image backgroundImage;
    private final int buttonWidth = 450; // Adjusted button width
    private final int buttonHeight = 280; // Adjusted button height

    public MainMenu(ActionListener startAction, ActionListener exitAction) {
        loadBackgroundImage();
        initUI(startAction, exitAction);
    }

    private void loadBackgroundImage() {
        try (InputStream imageStream = getClass().getResourceAsStream("/game/image/B.jpg")) {
            if (imageStream != null) {
                backgroundImage = ImageIO.read(imageStream);
            } else {
                System.err.println("Background image not found. Please check the path.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initUI(ActionListener startAction, ActionListener exitAction) {
        setLayout(null); // Use null layout for custom positioning

        // Add title label
        titleLabel = new JLabel("Bubble Destroyer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 85)); // Increased font size to 60
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBounds(300, 150, 800, 100); // Moved down and increased width

        // Create buttons with images
        startButton = createImageButton("/game/image/START.png", startAction, buttonWidth, buttonHeight);
        exitButton = createImageButton("/game/image/EXIT.png", exitAction, buttonWidth, buttonHeight);

        add(titleLabel);
        add(startButton);
        add(exitButton);
    }

    private JButton createImageButton(String imagePath, ActionListener action, int width, int height) {
        JButton button = new JButton();
        button.addActionListener(action);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        // Load and set the button image with scaling to fit the new button size
        try (InputStream imageStream = getClass().getResourceAsStream(imagePath)) {
            if (imageStream != null) {
                Image buttonImage = ImageIO.read(imageStream).getScaledInstance(width, height, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(buttonImage));
            } else {
                System.err.println("Button image not found at path: " + imagePath);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        button.setPreferredSize(new Dimension(width, height)); // Set preferred size for layout purposes
        button.setSize(width, height); // Set the button size directly
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Center the buttons dynamically
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int startX = (panelWidth - buttonWidth) / 2;
        int startY = (panelHeight - buttonHeight) / 2 + 20; // Adjust to move slightly below center

        int exitX = startX;
        int exitY = startY + buttonHeight + 10; // Only a 5-pixel gap between buttons

        startButton.setBounds(startX, startY, buttonWidth, buttonHeight);
        exitButton.setBounds(exitX, exitY, buttonWidth, buttonHeight);
    }
}
