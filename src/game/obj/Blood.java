package game.obj;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Blood {
    private int x, y;
    private BufferedImage image;

    public Blood(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(getClass().getResource("/game/image/blood.png"));
            if (image == null) {
                throw new IOException("Blood image not found at specified path.");
            }
        } catch (IOException e) {
            System.err.println("Error loading Blood image: " + e.getMessage());
        }
    }

    public void draw(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, x, y, 50, 50, null);
        } else {
            // Optional: Draw a placeholder if the image is missing
            g2d.setColor(java.awt.Color.RED);
            g2d.fillRect(x, y, 50, 50);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 50, 50);
    }
}
