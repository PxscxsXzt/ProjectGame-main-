package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class Destroyter {
    private double x, y;  
    private double dx;    
    private double dy = 0;  
    private BufferedImage image;
    private int health = 100; 
    private static final int MAX_SPEED = 3;  

    public Destroyter(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        loadImage();
        initializeMovement();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(getClass().getResource("/game/image/destroyter.png"));
        } catch (IOException e) {
            System.err.println("Error loading destroyter image: " + e.getMessage());
        }
    }

    private void initializeMovement() {
        Random random = new Random();
        dx = random.nextDouble() * MAX_SPEED * (random.nextBoolean() ? 1 : -1);

        if (dx == 0) dx = 1;
    }

    public void update() {
        x += dx;

        if (x < 0 || x > 1400 - getWidth()) {
            dx = -dx;
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = 50;
        int barHeight = 5;
        g2d.setColor(Color.RED);
        g2d.fillRect((int)x, (int)y - 10, barWidth, barHeight);
        g2d.setColor(Color.RED);
        g2d.fillRect((int)x, (int)y - 10, (int) (barWidth * (health / 100.0)), barHeight);
    }

    public void draw(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, (int)x, (int)y, 80, 60, null);  
            drawHealthBar(g2d); 
        }
    }

    // Method to return the bounding rectangle for collision detection
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, getWidth(), getHeight());
    }

    // Getters
    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getWidth() { return image != null ? image.getWidth() : 80; }
    public int getHeight() { return image != null ? image.getHeight() : 60; }
}
