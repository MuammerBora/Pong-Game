import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import javax.swing.JOptionPane;

public class GamePanel extends JPanel {
    private boolean inMenu = true;
    private int selectedOption = 0; // 0: Single Player, 1: Two Players
    private Paddle leftPaddle, rightPaddle;
    private Ball ball;
    private Timer timer;
    private boolean wPressed = false, sPressed = false, upPressed = false, downPressed = false;
    private AI ai;
    private int aiDifficulty = 1; // Normal difficulty by default
    private boolean isSinglePlayer = false;
    private int playerScore = 0;
    private int aiScore = 0;
    private String endMessage = "";
    private boolean inDifficultySelection = false;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (inMenu) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        int response = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to exit the application?",
                                "Confirm Exit",
                                JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    } else {
                        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                            selectedOption = (selectedOption - 1 + 2) % 2;
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                            selectedOption = (selectedOption + 1) % 2;
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            if (selectedOption == 0) {
                                isSinglePlayer = true;
                                inDifficultySelection = true;
                                inMenu = false;
                                repaint();
                            } else {
                                isSinglePlayer = false;
                                startGame();
                            }
                        }
                    }
                } else if (inDifficultySelection) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        inMenu = true;
                        inDifficultySelection = false;
                        isSinglePlayer = false;
                        selectedOption = 0;
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                        aiDifficulty = (aiDifficulty - 1 + 3) % 3;
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                        aiDifficulty = (aiDifficulty + 1) % 3;
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        inDifficultySelection = false;
                        startGame();
                    }
                } else {
                    // During gameplay
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        if (endMessage.isEmpty()) {
                            timer.stop();
                            int response = JOptionPane.showConfirmDialog(null,
                                    "Are you sure you want to exit the game?",
                                    "Confirm Exit",
                                    JOptionPane.YES_NO_OPTION);
                            if (response == JOptionPane.YES_OPTION) {
                                inMenu = true;
                                inDifficultySelection = false;
                                selectedOption = 0;
                                isSinglePlayer = false;
                                playerScore = 0;
                                aiScore = 0;
                                repaint();
                            } else {
                                timer.start();
                            }
                        }
                    } else if (!endMessage.isEmpty()) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            playerScore = 0;
                            aiScore = 0;
                            endMessage = "";
                            startGame();
                        }
                    } else {
                        // Two player: left paddle (W/S), right paddle (UP/DOWN)
                        // Single player: right paddle (UP/DOWN)
                        if (!isSinglePlayer) {
                            if (e.getKeyCode() == KeyEvent.VK_W) wPressed = true;
                            if (e.getKeyCode() == KeyEvent.VK_S) sPressed = true;
                        }
                        if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = true;
                        if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!inMenu && !inDifficultySelection) {
                    if (!isSinglePlayer) {
                        if (e.getKeyCode() == KeyEvent.VK_W) wPressed = false;
                        if (e.getKeyCode() == KeyEvent.VK_S) sPressed = false;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = false;
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = false;
                }
            }
        });
    }

    private void startGame() {
        if (timer != null) {
            timer.stop();
        }

        int paddleWidth = 20, paddleHeight = 100, paddleSpeed = 8;
        leftPaddle = new Paddle(0, 250, paddleWidth, paddleHeight, paddleSpeed);
        rightPaddle = new Paddle(getWidth() - paddleWidth, 250, paddleWidth, paddleHeight, paddleSpeed);

        ball = new Ball(getWidth() / 2 - 20 / 2, getHeight() / 2 - 20 / 2, 20, 5, 5);
        if (isSinglePlayer) {
            ai = new AI(leftPaddle, ball, aiDifficulty); // AI always controls left paddle
        }

        inMenu = false;
        inDifficultySelection = false;
        SoundPlayer.playSound("/Sounds/game-start-6104.wav");

        timer = new Timer(16, e -> {
            // Two player: left paddle (W/S), right paddle (UP/DOWN)
            // Single player: right paddle (UP/DOWN), AI left paddle
            if (!isSinglePlayer) {
                if (wPressed) leftPaddle.moveUp();
                if (sPressed) leftPaddle.moveDown(getHeight());
            }
            if (upPressed) rightPaddle.moveUp();
            if (downPressed) rightPaddle.moveDown(getHeight());

            if (isSinglePlayer) {
                ai.move();
            }

            ball.move();

            // Score logic
            if (ball.x < 0) {
                // Ball went past left paddle (AI or Player 1)
                playerScore++;
                if (playerScore == 12) {
                    if (isSinglePlayer) {
                        endGame("Player Wins!");
                    } else {
                        endGame("Player 2 Wins!");
                    }
                }
                ball.reset(getWidth(), getHeight());
                SoundPlayer.playSound("/Sounds/negative-beeps-6008.wav");
            }
            if (ball.x > getWidth()) {
                // Ball went past right paddle (Player or Player 2)
                aiScore++;
                if (aiScore == 12) {
                    if (isSinglePlayer) {
                        endGame("AI Wins!");
                    } else {
                        endGame("Player 1 Wins!");
                    }
                }
                ball.reset(getWidth(), getHeight());
                SoundPlayer.playSound("/Sounds/negative-beeps-6008.wav");
            }

            if (ball.y <= 0 || ball.y >= getHeight() - ball.size) ball.reverseY();

            if (ball.x <= leftPaddle.x + leftPaddle.width &&
                    ball.y + ball.size >= leftPaddle.y &&
                    ball.y <= leftPaddle.y + leftPaddle.height) {
                SoundPlayer.playSound("/Sounds/plastic-ball-bounce-14790.wav");
                ball.reverseX();
                ball.increaseSpeed();
                ball.x = leftPaddle.x + leftPaddle.width;
            }
            if (ball.x + ball.size >= rightPaddle.x &&
                    ball.y + ball.size >= rightPaddle.y &&
                    ball.y <= rightPaddle.y + rightPaddle.height) {
                SoundPlayer.playSound("/Sounds/plastic-ball-bounce-14790.wav");
                ball.reverseX();
                ball.increaseSpeed();
                ball.x = rightPaddle.x - ball.size;
            }

            repaint();
        });
        timer.start();
    }

    private void endGame(String result) {
        timer.stop();
        endMessage = result;

        if (result.equals("Player Wins!")) {
            SoundPlayer.playSound("/Sounds/level-win-6416.wav");
        } else if (result.equals("Player 2 Wins!")) {
            SoundPlayer.playSound("/Sounds/orchestral-win-331233.wav");
        } else {
            SoundPlayer.playSound("/Sounds/negative-beeps-6008.wav");
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (inMenu) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("PONG", 340, 120);

            g.setFont(new Font("Arial", Font.PLAIN, 28));
            g.setColor(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
            g.drawString("Single Player", 320, 220);
            g.setColor(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
            g.drawString("Two Players", 320, 270);

            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(Color.LIGHT_GRAY);
            g.drawString("Use Up/Down or W/S to select, Enter to start", 230, 350);
        } else if (inDifficultySelection) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Select Difficulty", 280, 120);
            g.setFont(new Font("Arial", Font.PLAIN, 28));

            if (aiDifficulty == 0) g.setColor(Color.YELLOW);
            else g.setColor(Color.WHITE);
            g.drawString("Easy", 360, 220);

            if (aiDifficulty == 1) g.setColor(Color.YELLOW);
            else g.setColor(Color.WHITE);
            g.drawString("Normal", 340, 270);

            if (aiDifficulty == 2) g.setColor(Color.YELLOW);
            else g.setColor(Color.WHITE);
            g.drawString("Hard", 360, 320);

            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(Color.LIGHT_GRAY);
            g.drawString("Use Up/Down to select, Enter to start", 260, 400);
        } else {
            // Game screen
            g.setColor(Color.WHITE);
            for (int i = 0; i < getHeight(); i += 30) {
                g.fillRect(getWidth() / 2 - 2, i, 4, 20);
            }
            leftPaddle.draw(g);
            rightPaddle.x = getWidth() - rightPaddle.width;
            rightPaddle.draw(g);
            ball.draw(g);

            // Show scores
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            if (isSinglePlayer) {
                g.drawString("AI: " + aiScore, 50, 50);
                g.drawString("Player: " + playerScore, getWidth() - 150, 50);
            } else {
                g.drawString("Player 1: " + aiScore, 50, 50);
                g.drawString("Player 2: " + playerScore, getWidth() - 150, 50);
            }

            // Show end message
            if (!endMessage.isEmpty()) {
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.setColor(Color.YELLOW);
                g.drawString(endMessage, getWidth() / 2 - 120, getHeight() / 2);
                g.setFont(new Font("Arial", Font.PLAIN, 24));
                g.drawString("Press Enter to play again or Escape to return to menu.",
                        getWidth() / 2 - 250, getHeight() / 2 + 50);
            }
        }
    }
}

