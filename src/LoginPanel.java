import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginPanel extends JPanel {
    private JTextField usernameInput;
    private JButton startButton;
    private ActionListener startButtonListener;
    private JFrame parentFrame; // Çıkış onayı için ana pencereye referans

    public LoginPanel() {
        // Ana pencereyi bulmak için
        SwingUtilities.invokeLater(() -> {
            this.parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        });

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Kullanıcı adı etiketi
        JLabel usernameLabel = new JLabel("Player Name:");
        usernameLabel.setForeground(Color.WHITE); // Yazı rengini beyaz yapalım
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);

        // Kullanıcı adı giriş alanı
        usernameInput = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameInput, gbc);

        // Başla butonu
        startButton = new JButton("Enter");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(startButton, gbc);

        startButton.addActionListener(e -> {
            String playerName = usernameInput.getText();
            if (!playerName.trim().isEmpty()) {
                // Oyuncu adını GamePanel'e iletmek için ActionListener'ı tetikliyoruz
                startButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, playerName));
            } else {
                // İsim girilmemişse uyarı ver
                JOptionPane.showMessageDialog(parentFrame, "Please enter your name to start the game.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Enter tuşu için dinleyici ekleme
        usernameInput.addKeyListener(new KeyAdapter() { // Dinleyiciyi usernameInput'a ekledik
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startButton.doClick(); // Enter'a basınca butona tıklanmış gibi yap
                }
            }
        });

        // Esc tuşu için dinleyici ekleme (Pencerenin kendisine eklendi)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    confirmExit();
                }
            }
        });

        // Panelin görünüm ayarları
        setBackground(Color.BLACK); // Arka plan rengini siyah yapalım
        setFocusable(true);
        requestFocusInWindow(); // Pencere göründüğünde bu panele odaklan
    }

    public void setStartButtonListener(ActionListener listener) {
        this.startButtonListener = listener;
    }

    // confirmExit metodu LoginPanel içinde tanımlanmıştı, burada da kalsın
    private void confirmExit() {
        if (parentFrame != null) {
            int result = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0); // Ana pencere bulunamazsa direkt çıkış yap
        }
    }
}