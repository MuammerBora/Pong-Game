public class AI {
    private Paddle paddle;
    private Ball ball;
    private int difficulty; // 0: Easy, 1: Normal, 2: Hard

    public AI(Paddle paddle, Ball ball, int difficulty) {
        this.paddle = paddle;
        this.ball = ball;
        this.difficulty = difficulty;
    }

    public void move() {
        int targetY = ball.y + ball.size / 2 - paddle.height / 2; // Topun merkezine göre hedef y konumu
        int speed = difficulty == 0 ? 2 : (difficulty == 1 ? 4 : 6); // Zorluk seviyesine göre hız

        if (paddle.y + paddle.height / 2 < targetY) {
            paddle.moveDown(GameConstants.PANEL_HEIGHT); // Paddle'ı aşağı hareket ettir
        } else if (paddle.y + paddle.height / 2 > targetY) {
            paddle.moveUp(); // Paddle'ı yukarı hareket ettir
        }
    }
}