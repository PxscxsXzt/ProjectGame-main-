package game.component;

public class Key {
    private boolean KEY_w;
    private boolean KEY_s;
    private boolean KEY_a;
    private boolean KEY_d;
    private boolean KEY_rotateLeft;
    private boolean KEY_rotateRight;

    // Getter and Setter for KEY_w
    public boolean isKEY_w() {
        return KEY_w;
    }

    public void setKEY_w(boolean KEY_w) {
        this.KEY_w = KEY_w;
    }

    // Getter and Setter for KEY_s
    public boolean isKEY_s() {
        return KEY_s;
    }

    public void setKEY_s(boolean KEY_s) {
        this.KEY_s = KEY_s;
    }

    // Getter and Setter for KEY_a
    public boolean isKEY_a() {
        return KEY_a;
    }

    public void setKEY_a(boolean KEY_a) {
        this.KEY_a = KEY_a;
    }

    // Getter and Setter for KEY_d
    public boolean isKEY_d() {
        return KEY_d;
    }

    public void setKEY_d(boolean KEY_d) {
        this.KEY_d = KEY_d;
    }

    // Getter and Setter for KEY_rotateLeft
    public boolean isKEY_rotateLeft() {
        return KEY_rotateLeft;
    }

    public void setKEY_rotateLeft(boolean KEY_rotateLeft) {
        this.KEY_rotateLeft = KEY_rotateLeft;
    }

    // Getter and Setter for KEY_rotateRight
    public boolean isKEY_rotateRight() {
        return KEY_rotateRight;
    }

    public void setKEY_rotateRight(boolean KEY_rotateRight) {
        this.KEY_rotateRight = KEY_rotateRight;
    }

    @Override
    public String toString() {
        return "Key{" +
                "KEY_w=" + KEY_w +
                ", KEY_s=" + KEY_s +
                ", KEY_a=" + KEY_a +
                ", KEY_d=" + KEY_d +
                ", KEY_rotateLeft=" + KEY_rotateLeft +
                ", KEY_rotateRight=" + KEY_rotateRight +
                '}';
    }
}
