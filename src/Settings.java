/*import javax.swing.*;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Settings extends JPanel {
    private int selectedOption = 0; // 0: Paddle 1, 1: Paddle 2, 2: Ball
    private int selectedColorIndexPaddle1 = 0;
    private int selectedColorIndexPaddle2 = 0;
    private int selectedColorIndexBall = 0;

    private final Color[] colors = {
            Color.WHITE, Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA, Color.GREEN, Color.BLACK
    };

    public Settings() {
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    selectedOption = (selectedOption - 1 + 3) % 3;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    selectedOption = (selectedOption + 1) % 3;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (selectedOption == 0) {
                        selectedColorIndexPaddle1 = (selectedColorIndexPaddle1 - 1 + colors.length) % colors.length;
                    } else if (selectedOption == 1) {
                        selectedColorIndexPaddle2 = (selectedColorIndexPaddle2 - 1 + colors.length) % colors.length;
                    } else if (selectedOption == 2) {
                        selectedColorIndexBall = (selectedColorIndexBall - 1 + colors.length) % colors.length;
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (selectedOption == 0) {
                        selectedColorIndexPaddle1 = (selectedColorIndexPaddle1 + 1) % colors.length;
                    } else if (selectedOption == 1) {
                        selectedColorIndexPaddle2 = (selectedColorIndexPaddle2 + 1) % colors.length;
                    } else if (selectedOption == 2) {
                        selectedColorIndexBall = (selectedColorIndexBall + 1) % colors.length;
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // Ana menüye dön
                    // Bu kısmı GamePanel'de yönlendirme yaparak ayarlayabilirsiniz.
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Settings", 320, 50);

        // Paddle 1
        g.setColor(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
        g.drawString("Paddle 1 Color: ", 100, 150);
        g.setColor(colors[selectedColorIndexPaddle1]);
        g.fillRect(300, 130, 50, 50);

        // Paddle 2
        g.setColor(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
        g.drawString("Paddle 2 Color: ", 100, 250);
        g.setColor(colors[selectedColorIndexPaddle2]);
        g.fillRect(300, 230, 50, 50);

        // Ball
        g.setColor(selectedOption == 2 ? Color.YELLOW : Color.WHITE);
        g.drawString("Ball Color: ", 100, 350);
        g.setColor(colors[selectedColorIndexBall]);
        g.fillRect(300, 330, 50, 50);
    }

    public Color getPaddle1Color() {
        return colors[selectedColorIndexPaddle1];
    }

    public Color getPaddle2Color() {
        return colors[selectedColorIndexPaddle2];
    }

    public Color getBallColor() {
        return colors[selectedColorIndexBall];
    }
}
*/