
public class Paddle {
    public int x, y;
    public int width, height;
    public int speed;
    public Paddle(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }
    public void moveUp() {
        y -= speed;
        if (y < 0) y = 0;
    }
    public void moveDown(int panelHeight) {
        if (y + height + speed <= panelHeight) {
            y += speed;
        } else {
            y = panelHeight - height;
        }
    }
    public void draw(java.awt.Graphics g) {
        g.fillRect(x, y, width, height);
    }
}