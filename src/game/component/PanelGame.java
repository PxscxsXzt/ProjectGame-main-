package game.component;

import game.obj.Player;
import game.obj.Destroyter;
import game.obj.Rock;
import game.obj.Bom;
import game.obj.Blood;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;


public class PanelGame extends JComponent implements Runnable {
    private BufferedImage image;
    private BufferedImage backgroundImage;
    private BufferedImage backgroundImageLevel2;
    private BufferedImage backgroundImageLevel3;
    private Thread thread;
    private boolean running = false;
    private boolean gameOver = false;
    private boolean win = false; // Flag to indicate if the player has won
    private Key key;
    private final int FPS = 50;
    private final long TARGET_TIME = 1000000000 / FPS;

    private Player player;
    private ArrayList<Destroyter> destroyters;
    private ArrayList<Rock> rocks;
    private ArrayList<Bom> boms;
    private ArrayList<Blood> bloods;
    private static final int NUM_OBSTACLES = 10;
    private int level = 1;
    private int totalScore = 0;

    // Minimum safe distance for object spawn
    private static final int MIN_SAFE_DISTANCE = 100;
    private JButton exitButton;
    private JButton restartButton;

    public PanelGame() {
    this.setPreferredSize(new Dimension(1300, 900));
    setFocusable(true);
    requestFocusInWindow();
    initObjectGame();
    initKeyboard();
    loadBackground();
    start();

    // Initialize the buttons with text
    restartButton = new JButton("RESTART");
    exitButton = new JButton("EXIT");

    // Customize button appearance for larger size
    styleButton(restartButton);
    styleButton(exitButton);

    // Set the larger size and position of the buttons
    restartButton.setBounds(400, 600, 500, 80); // Wider and taller button
    exitButton.setBounds(400, 700, 500, 80);

    // Set ActionListeners for the buttons
    restartButton.addActionListener(e -> restartGame());  // RESTART
    exitButton.addActionListener(e -> System.exit(0));    // EXIT

    // Initially make the buttons invisible
    restartButton.setVisible(false);
    exitButton.setVisible(false);

    // Add buttons to the panel
    add(restartButton);
    add(exitButton);
}

// Method to style buttons with larger text, rounded corners, cream background, and shadow effect
private void styleButton(JButton button) {
    button.setBackground(new Color(245, 245, 220)); // Cream background color
    button.setForeground(Color.BLACK); // Black text color
    button.setFont(new Font("Arial", Font.BOLD, 36)); // Larger font size for visibility

    // Remove border and make background visible
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setContentAreaFilled(false); // Custom painting, so disable default content fill

    // Add custom painting to achieve rounded corners and shadow effect
    button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw shadow with larger offset for bigger button
            g2.setColor(new Color(0, 0, 0, 100)); // Semi-transparent black for shadow
            g2.fillRoundRect(8, 8, c.getWidth() - 8, c.getHeight() - 8, 50, 50); // Offset shadow further

            // Draw button background with larger rounded corners
            g2.setColor(button.getBackground());
            g2.fillRoundRect(0, 0, c.getWidth() - 8, c.getHeight() - 8, 50, 50);

            // Draw text
            FontMetrics fm = g2.getFontMetrics();
            int textX = (c.getWidth() - fm.stringWidth(button.getText())) / 2;
            int textY = (c.getHeight() + fm.getAscent()) / 2 - 4;
            g2.setColor(button.getForeground());
            g2.drawString(button.getText(), textX, textY);
        }
    });
}

    // Load different backgrounds for each level
    private void loadBackground() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/game/image/BK.jpg"));
            backgroundImageLevel2 = ImageIO.read(getClass().getResource("/game/image/BK2.jpg"));
            backgroundImageLevel3 = ImageIO.read(getClass().getResource("/game/image/BK3.jpg"));
        } catch (IOException e) {
            System.err.println("Error loading background images: " + e.getMessage());
        }
    }


    private void initObjectGame() {
        player = new Player();
        player.changeLocation(50, 150);

        destroyters = new ArrayList<>();
        rocks = new ArrayList<>();
        boms = new ArrayList<>();
        bloods = new ArrayList<>();

        Random rand = new Random();

        // Determine counts based on level
        int destroyterCount = (level >= 2) ? NUM_OBSTACLES * 2 : NUM_OBSTACLES;
        int rockCount = (level >= 2) ? NUM_OBSTACLES * 2 : NUM_OBSTACLES;
        int bomBloodCount = (level >= 2) ? 4 : 2; // Increase Bom and Blood count for level 2 and above

        // Add Destroyters
        for (int i = 0; i < destroyterCount; i++) {
            int x, y;
            do {
                x = rand.nextInt(1300 - 50);
                y = rand.nextInt(900 - 50);
            } while (isTooClose(x, y, player.getX(), player.getY()));
            destroyters.add(new Destroyter(x, y));
        }

        // Add Rocks
        for (int i = 0; i < rockCount; i++) {
            int x, y;
            do {
                x = rand.nextInt(1300 - 50);
                y = rand.nextInt(900 - 50);
            } while (isTooClose(x, y, player.getX(), player.getY()));
            rocks.add(new Rock(x, y));
        }

        // Add Boms and Bloods for levels 2 and above
        for (int i = 0; i < bomBloodCount; i++) {
            int x, y;
            do {
                x = rand.nextInt(1300 - 50);
                y = rand.nextInt(900 - 50);
            } while (isTooClose(x, y, player.getX(), player.getY()));
            boms.add(new Bom(x, y));

            do {
                x = rand.nextInt(1300 - 50);
                y = rand.nextInt(900 - 50);
            } while (isTooClose(x, y, player.getX(), player.getY()));
            bloods.add(new Blood(x, y));
        }
    }

    private boolean isTooClose(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return (dx * dx + dy * dy) < (MIN_SAFE_DISTANCE * MIN_SAFE_DISTANCE);
    }

    private void initKeyboard() {
        key = new Key();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver || win) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> key.setKEY_w(true);
                    case KeyEvent.VK_S -> key.setKEY_s(true);
                    case KeyEvent.VK_A -> key.setKEY_a(true);
                    case KeyEvent.VK_D -> key.setKEY_d(true);
                    case KeyEvent.VK_F -> key.setKEY_rotateLeft(true);
                    case KeyEvent.VK_R -> key.setKEY_rotateRight(true);
                    case KeyEvent.VK_ENTER -> player.fire();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> key.setKEY_w(false);
                    case KeyEvent.VK_S -> key.setKEY_s(false);
                    case KeyEvent.VK_A -> key.setKEY_a(false);
                    case KeyEvent.VK_D -> key.setKEY_d(false);
                    case KeyEvent.VK_F -> key.setKEY_rotateLeft(false);
                    case KeyEvent.VK_R -> key.setKEY_rotateRight(false);
                }
            }
        });
    }

    public void start() {
        if (thread == null || !running) {
            thread = new Thread(this);
            running = true;
            thread.start();
        }
    }
    private void restartGame() {
        gameOver = false;
        win = false;
        totalScore = 0;
        level = 1;
        initObjectGame();
        start();
        restartButton.setVisible(false);
        exitButton.setVisible(false);
        
    }

    @Override
    public void run() {
        while (running) {
            long startTime = System.nanoTime();

            if (!gameOver && !win) {
                updateGame();
            }
            repaint();

            long elapsed = System.nanoTime() - startTime;
            long wait = (TARGET_TIME - elapsed) / 1000000;

            if (wait < 0) {
                wait = 5;
            }

            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }

    private void levelUp() {
        totalScore += player.getScore(); // Add the current level's score to totalScore
        level++;
        player.setScore(0); // Reset the player's score for the new level
        destroyters.clear();
        rocks.clear();
        boms.clear();
        bloods.clear();
        initObjectGame(); // Reinitialize game objects for the next level
    }


    private void updateGame() {
        // Update player movement based on key states
        player.setDY(0);
        player.setDX(0);
        if (key.isKEY_w()) player.setDY(-2);
        if (key.isKEY_s()) player.setDY(2);
        if (key.isKEY_a()) player.setDX(-2);
        if (key.isKEY_d()) player.setDX(2);
        if (key.isKEY_rotateLeft()) player.rotateLeft();
        if (key.isKEY_rotateRight()) player.rotateRight();

        player.update();
        player.updateProjectiles(destroyters, rocks);

        handleCollisions();
        // Check win condition in level 3
        if (level == 3 && destroyters.isEmpty() && rocks.isEmpty()) {
            totalScore += player.getScore(); // Add level 3 score to totalScore
            win = true;
            running = false; // Stop the game loop
            return;
        }
        // Level up conditions
        if ((level == 1 && player.getScore() >= 20) || (level == 2 && player.getScore() >= 40)) {
            levelUp();
    }
}


    // Check for collisions and handle them
 private void handleCollisions() {
    Iterator<Destroyter> destroyterIterator = destroyters.iterator();
    while (destroyterIterator.hasNext()) {
        Destroyter destroyter = destroyterIterator.next();
        destroyter.update();
        if (destroyter.getBounds().intersects(player.getBounds())) {
            player.takeDamage(25); // Reduce health by 25
            destroyterIterator.remove();
            if (player.getHealth() <= 0) {
                triggerGameOver();
                return;
            }
        }
    }

    Iterator<Rock> rockIterator = rocks.iterator();
    while (rockIterator.hasNext()) {
        Rock rock = rockIterator.next();
        rock.update();
        if (rock.getBounds().intersects(player.getBounds())) {
            player.takeDamage(25);
            rockIterator.remove();
            if (player.getHealth() <= 0) {
                triggerGameOver();
                return;
            }
        }
    }

    Iterator<Bom> bomIterator = boms.iterator();
    while (bomIterator.hasNext()) {
        Bom bom = bomIterator.next();
        if (bom.getBounds().intersects(player.getBounds())) {
            totalScore = Math.max(0, totalScore - 5); // Ensure score does not go below zero
            bomIterator.remove();
        }
    }

    Iterator<Blood> bloodIterator = bloods.iterator();
    while (bloodIterator.hasNext()) {
        Blood blood = bloodIterator.next();
        if (blood.getBounds().intersects(player.getBounds())) {
            player.setHealth(Math.min(player.getHealth() + 25, player.getMaxHealth()));
            bloodIterator.remove();
        }
    }
}


    private void triggerGameOver() {
        totalScore += player.getScore();
        gameOver = true;
    }

    private void drawBackground(Graphics2D g2d) {
        if (level == 1 && backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else if (level == 2 && backgroundImageLevel2 != null) {
            g2d.drawImage(backgroundImageLevel2, 0, 0, getWidth(), getHeight(), null);
        } else if (level == 3 && backgroundImageLevel3 != null) {
            g2d.drawImage(backgroundImageLevel3, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(new Color(50, 50, 50));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
            image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        drawBackground(g2d);
        
        if (win) {
            drawWinScreen(g2d); // Display the win message if the player has won
        } else if (gameOver) {
            drawGameOverScreen(g2d);
        } else {
            drawGame(g2d);
            drawScore(g2d);
            drawLevel(g2d);
        }

        g2d.dispose();
        g.drawImage(image, 0, 0, null);
    }

// Draw the Win screen with buttons for Restart and Exit
private void drawWinScreen(Graphics2D g2d) {
    // Set font for "YOU WIN" text
    g2d.setFont(new Font("Arial", Font.BOLD, 80));
    g2d.setColor(Color.WHITE);
    String winText = "YOU WIN!";
    int winTextWidth = g2d.getFontMetrics().stringWidth(winText);
    int winTextX = (getWidth() - winTextWidth) / 2;
    g2d.drawString(winText, winTextX, 300); // Display "YOU WIN!" text at y=300

    // Display total score
    g2d.setFont(new Font("Arial", Font.BOLD, 40));
    String scoreText = "Total Score: " + totalScore;
    int scoreTextWidth = g2d.getFontMetrics().stringWidth(scoreText);
    int scoreTextX = (getWidth() - scoreTextWidth) / 2;
    g2d.drawString(scoreText, scoreTextX, 400); // Display score below win text

    // Set up buttons to be visible
    setupEndGameButtons();
}

// Draw the Game Over screen with buttons for Restart and Exit
private void drawGameOverScreen(Graphics2D g2d) {
    // Set font for "GAME OVER" text
    g2d.setFont(new Font("Arial", Font.BOLD, 80));
    g2d.setColor(Color.WHITE);
    String gameOverText = "GAME OVER";
    int gameOverTextWidth = g2d.getFontMetrics().stringWidth(gameOverText);
    int gameOverTextX = (getWidth() - gameOverTextWidth) / 2;
    g2d.drawString(gameOverText, gameOverTextX, 300); // Display "GAME OVER" text at y=300

    // Display total score
    g2d.setFont(new Font("Arial", Font.BOLD, 40));
    String scoreText = "Total Score: " + totalScore;
    int scoreTextWidth = g2d.getFontMetrics().stringWidth(scoreText);
    int scoreTextX = (getWidth() - scoreTextWidth) / 2;
    g2d.drawString(scoreText, scoreTextX, 400); // Display score below game over text

    // Set up buttons to be visible
    setupEndGameButtons();
}

// Helper method to set up buttons for End Game screens
private void setupEndGameButtons() {
    // Customize the Restart and Exit buttons for end-game screens
    restartButton.setText("RESTART");
    restartButton.setFont(new Font("Arial", Font.BOLD, 30));
    restartButton.setBounds((getWidth() - 200) / 2, 500, 200, 50); // Centered horizontally

    exitButton.setText("EXIT");
    exitButton.setFont(new Font("Arial", Font.BOLD, 30));
    exitButton.setBounds((getWidth() - 200) / 2, 570, 200, 50); // Centered horizontally below restart button

    // Make the buttons visible
    restartButton.setVisible(true);
    exitButton.setVisible(true);
}


    private void drawGame(Graphics2D g2d) {
        player.draw(g2d);

        // Use an Iterator to draw destroyters to avoid ConcurrentModificationException
        Iterator<Destroyter> destroyterIterator = new ArrayList<>(destroyters).iterator();
        while (destroyterIterator.hasNext()) {
            Destroyter destroyter = destroyterIterator.next();
            destroyter.draw(g2d);
        }

        // Use an Iterator to draw rocks to avoid ConcurrentModificationException
        Iterator<Rock> rockIterator = new ArrayList<>(rocks).iterator();
        while (rockIterator.hasNext()) {
            Rock rock = rockIterator.next();
            rock.draw(g2d);
        }

        // If level 2 or higher, draw Bom and Blood objects
        if (level >= 2) {
            Iterator<Bom> bomIterator = new ArrayList<>(boms).iterator();
            while (bomIterator.hasNext()) {
                Bom bom = bomIterator.next();
                bom.draw(g2d);
            }

            Iterator<Blood> bloodIterator = new ArrayList<>(bloods).iterator();
            while (bloodIterator.hasNext()) {
                Blood blood = bloodIterator.next();
                blood.draw(g2d);
            }
        }
    }


    private void drawScore(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Score: " + player.getScore(), 10, 30);
        g2d.drawString("Total Score: " + totalScore, 10, 60);
    }

    private void drawLevel(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Level: " + level, 10, 90);
    }
}
