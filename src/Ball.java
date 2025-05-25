public class Ball {
    public int x, y;
    public int size;
    public int speedX, speedY;
    public int initialSpeedX, initialSpeedY;
    public Ball(int x, int y, int size, int speedX, int speedY) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speedX = speedX;
        this.speedY = speedY;
        this.initialSpeedX = speedX;
        this.initialSpeedY = speedY;
    }
    public void move() {
        x += speedX;
        y += speedY;
    }
    public void reverseX() {
        speedX = -speedX;
    }
    public void reverseY() {
        speedY = -speedY;
    }
    public void reset(int panelWidth, int panelHeight) {
        x = panelWidth / 2 - size / 2;
        y = panelHeight / 2 - size / 2;
// Hızı başa döndür
        if (speedX > 0) speedX = initialSpeedX;
        else speedX = -initialSpeedX;
        if (speedY > 0) speedY = initialSpeedY;
        else speedY = -initialSpeedY;
        reverseX();
    }
    public void draw(java.awt.Graphics g) {
        g.fillOval(x, y, size, size);
    }
    public void increaseSpeed() {
        if (speedX > 0) speedX++;
        else speedX--;
        if (speedY > 0) speedY++;
        else speedY--;
    }
}

