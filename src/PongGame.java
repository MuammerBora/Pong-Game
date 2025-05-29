import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PongGame extends JFrame {
    private LoginPanel loginPanel;
    private GamePanel gamePanel;

    public PongGame() {
        setTitle("Pong Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> confirmExit());
        gameMenu.add(exitMenuItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        // Uygulama başladığında LoginPanel'i göster
        loginPanel = new LoginPanel();
        loginPanel.setStartButtonListener(e -> {
            // LoginPanel'den GamePanel'e geçerken
            // LoginPanel'i kaldır ve GamePanel'i ekle
            getContentPane().remove(loginPanel);
            startGame((String) e.getActionCommand());
        });
        add(loginPanel, BorderLayout.CENTER);

        setVisible(true);
        // Uygulama başlangıcında herhangi bir müzik çalma komutu yok
    }

    private void startGame(String playerName) {
        // GamePanel'i oluştur ve ana menü durumunda başlat
        gamePanel = new GamePanel(playerName); // GamePanel constructor inMenu = true olarak başlatıyor
        getContentPane().add(gamePanel, BorderLayout.CENTER); // GamePanel'i ekle
        revalidate();
        repaint();
        gamePanel.requestFocusInWindow(); // Oyun paneline odaklanmayı sağlıyoruz

        // GamePanel eklendikten sonra ve menü durumundayken ana menü müziğini başlat
        // Bu, LoginPanel'den sonra ana menünün ilk göründüğünde müziğin başlamasını sağlar.
        if (gamePanel.isInMenu()) { // GamePanel'in menüde olup olmadığını kontrol et
            SoundPlayer.playMainMenuMusic();
        }
    }


    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            SoundPlayer.stopBackgroundMusic(); // Uygulama kapanırken müziği durdur
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PongGame::new);
    }
}