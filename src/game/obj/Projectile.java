package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Projectile {
    private double x;
    private double y;
    private final float angle;
    private final float speed = 10f;
    private final int size = 8;

    public Projectile(double x, double y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int) x, (int) y, size, size);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    // Original method: Checks collision with another object using individual coordinates and dimensions
    public boolean collidesWith(int objX, int objY, int objWidth, int objHeight) {
        Rectangle projectileBounds = new Rectangle((int) x, (int) y, size, size);
        Rectangle objectBounds = new Rectangle(objX, objY, objWidth, objHeight);
        return projectileBounds.intersects(objectBounds);
    }

    // Overloaded method: Checks collision with another object using a Rectangle
    public boolean collidesWith(Rectangle objectBounds) {
        Rectangle projectileBounds = new Rectangle((int) x, (int) y, size, size);
        return projectileBounds.intersects(objectBounds);
    }

    // Helper method to get the bounds of the projectile
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, size, size);
    }
}
