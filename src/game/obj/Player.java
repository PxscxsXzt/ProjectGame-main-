package game.obj;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    public static final int WIDTH = 150;
    public static final int HEIGHT = 120;
    public static final int MAX_HEALTH = 100;

    private double x;
    private double y;
    private float angle = 0f;
    private int score = 0;
    private int health = MAX_HEALTH;

    private BufferedImage image;
    private ArrayList<Projectile> projectiles;

    // For damage cooldown and visual feedback
    private long lastDamageTime = 0;
    private final long DAMAGE_COOLDOWN = 1000; // 1 second cooldown
    private boolean isFlashing = false;
    private long flashStartTime = 0;
    private final long FLASH_DURATION = 500; // 0.5 seconds

    public Player() {
        loadImage();
        projectiles = new ArrayList<>();
    }

    // Load and scale the player image
    private void loadImage() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/game/image/bubble.png"));
        image = getScaledImage(icon.getImage(), WIDTH, HEIGHT);
    }

    // Utility method to scale the image
    private BufferedImage getScaledImage(Image img, int width, int height) {
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bimage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return bimage;
    }

    // Set the initial location of the player
    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Adjust the player's angle, ensuring it stays within 0-360 degrees
    public void changeAngle(float angle) {
        this.angle = (angle % 360 + 360) % 360;
    }

    // Fire a new projectile
    public void fire() {
        double projectileX = x + (WIDTH / 2);
        double projectileY = y + (HEIGHT / 2);
        projectiles.add(new Projectile(projectileX, projectileY, angle));
    }

    // Update projectiles, checking for collisions with destroyers and rocks
    public void updateProjectiles(ArrayList<Destroyter> destroyters, ArrayList<Rock> rocks) {
        synchronized (projectiles) {
            Iterator<Projectile> projectileIterator = projectiles.iterator();
            while (projectileIterator.hasNext()) {
                Projectile projectile = projectileIterator.next();
                projectile.update();

                boolean shouldRemoveProjectile = false;

                // Remove projectiles that go outside screen bounds
                if (projectile.getX() < 0 || projectile.getX() > 1400 || projectile.getY() < 0 || projectile.getY() > 900) {
                    projectileIterator.remove();
                    continue;
                }

                // Check collision with destroyers
                Iterator<Destroyter> destroyterIterator = destroyters.iterator();
                while (destroyterIterator.hasNext()) {
                    Destroyter destroyter = destroyterIterator.next();
                    if (projectile.collidesWith(destroyter.getBounds())) {
                        score += 1;
                        destroyterIterator.remove(); // Remove the Destroyter
                        shouldRemoveProjectile = true;
                        break;
                    }
                }

                // Check collision with rocks if no collision with destroyers
                if (!shouldRemoveProjectile) {
                    Iterator<Rock> rockIterator = rocks.iterator();
                    while (rockIterator.hasNext()) {
                        Rock rock = rockIterator.next();
                        if (projectile.collidesWith(rock.getBounds())) {
                            score += 2;
                            rockIterator.remove(); // Remove the Rock
                            shouldRemoveProjectile = true;
                            break;
                        }
                    }
                }

                if (shouldRemoveProjectile) {
                    projectileIterator.remove();
                }
            }
        }
    }

    // Update player's position within screen bounds
    public void update() {
        if (x < 0) x = 0;
        if (x > 1300 - WIDTH) x = 1300 - WIDTH;
        if (y < 0) y = 0;
        if (y > 900 - HEIGHT) y = 900 - HEIGHT;
    }

    // Draw player and projectiles
    public void draw(Graphics2D g2) {
        synchronized (projectiles) {
            AffineTransform oldTransform = g2.getTransform();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Handle flashing effect
            if (isFlashing) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - flashStartTime > FLASH_DURATION) {
                    isFlashing = false;
                } else {
                    // Alternate between visible and semi-transparent
                    if ((currentTime - flashStartTime) / 100 % 2 == 0) {
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    } else {
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    }
                }
            }

            g2.translate(x + WIDTH / 2, y + HEIGHT / 2);
            g2.rotate(Math.toRadians(angle));
            g2.drawImage(image, -WIDTH / 2, -HEIGHT / 2, null);
            g2.setTransform(oldTransform);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Reset opacity

            drawHealthBar(g2);

            for (Projectile projectile : projectiles) {
                projectile.draw(g2);
            }
        }
    }

    // Draw health bar above the player
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = 60;
        int barHeight = 5;
        int barX = (int) x + (WIDTH / 2) - (barWidth / 2);
        int barY = (int) y - 15;

        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(barX, barY, (int) (barWidth * (health / (double) MAX_HEALTH)), barHeight);
    }

    // Reduce health when taking damage, with cooldown to avoid constant damage
    public void takeDamage(int damage) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDamageTime > DAMAGE_COOLDOWN) {
            health -= damage;
            lastDamageTime = currentTime;
            isFlashing = true;
            flashStartTime = currentTime;
            if (health < 0) health = 0;
        }
    }

    // Set health directly (for effects like healing)
    public void setHealth(int health) {
        this.health = Math.min(health, MAX_HEALTH);
    }

    // Check if player is dead
    public boolean isDead() {
        return health <= 0;
    }

    // Getters for health, max health, and score
    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    public int getScore() {
        return score;
    }

    // Setter for score to reset or adjust the player's score
    public void setScore(int score) {
        this.score = score;
    }

    // Get bounds for collision detection
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, WIDTH, HEIGHT);
    }

    // Rotate player left
    public void rotateLeft() {
        angle -= 5;
    }

    // Rotate player right
    public void rotateRight() {
        angle += 5;
    }

    // Move vertically
    public void setDY(int dy) {
        this.y += dy;
    }

    // Move horizontally
    public void setDX(int dx) {
        this.x += dx;
    }

    // Get the player's x position
    public int getX() {
        return (int) x;
    }

    // Get the player's y position
    public int getY() {
        return (int) y;
    }
}
