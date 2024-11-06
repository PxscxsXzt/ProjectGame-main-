package game.obj;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bom {
    private int x, y;
    private BufferedImage image;

    public Bom(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(getClass().getResource("/game/image/bom.png")); // Bom image
        } catch (IOException e) {
            System.err.println("Error loading Bom image: " + e.getMessage());
        }
    }

    public void draw(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, x, y, 50, 50, null); // Adjust size as needed
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 50, 50); // Match the dimensions of the draw method
    }
}
