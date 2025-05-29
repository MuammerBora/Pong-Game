import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import java.net.URI;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap; // HashMap için eklendi

public class GamePanel extends JPanel {

    private boolean inMenu = true; // Ana menüde miyiz?
    private int selectedOption = 0; // Ana menü seçeneği (0: Single Player, 1: Two Players, 2: Achievements, 3: Settings)
    private Paddle leftPaddle, rightPaddle;
    private Ball ball;
    private Timer timer;
    private boolean wPressed = false, sPressed = false, upPressed = false, downPressed = false;
    private AI ai;
    private int aiDifficulty = 1; // 0: Easy, 1: Normal, 2: Hard
    private boolean isSinglePlayer = false;
    private int playerScore = 0;
    private int aiScore = 0;
    private String endMessage = ""; // Oyun sonu mesajı
    private boolean inDifficultySelection = false; // Zorluk seçme ekranında mı
    private boolean inAchievements = false; // Başarımlar ekranında mıyız?
    private Rectangle settingsButtonBounds; // Ana menüdeki SETTINGS seçeneği için tıklama alanı



    // Ayarlar menüsü durumları
    private boolean inSettings = false; // Ayarlar menüsünde miyiz?
    private boolean inColorSettings = false; // Renk Ayarları alt menüsünde miyiz?
    private int selectedSettingsOption = 0; // Ayarlar menüsü seçeneği (0: Color, 1: Sound)
    private int selectedColorSettingOption = 0; // Renk Ayarları seçeneği (0: Background, 1: Ball, 2: Paddle 1, 3: Paddle 2)

    private String playerName; // Oturum açan oyuncunun adı
    private GameRecords gameRecords; // Skorları yönetmek için GameRecords nesnesi

    private static final String GITHUB_URL = "https://github.com/MuammerBora/Pong-Game";
    private Rectangle githubLinkBounds;

    // settingsButtonBounds artık sağ alttaki yazı için kullanılmayacak
    // private Rectangle settingsButtonBounds; // Ayarlar butonu için sınır

    private Rectangle colorSettingsBounds; // Ana Ayarlar menüsündeki Color Settings seçeneği için
    private Rectangle soundSettingsBounds; // Ana Ayarlar menüsündeki Sound Settings seçeneği için

    // Renk Ayarları değişkenleri
    private Color backgroundColor = Color.BLACK;
    private Color ballColor = Color.WHITE;
    private Color paddle1Color = Color.WHITE; // Paddle 1 (Sol Paddle - AI veya Player 1)
    private Color paddle2Color = Color.WHITE; // Paddle 2 (Sağ Paddle - Player veya Player 2)

    // Renk seçenekleri için harita ve liste (çizim ve seçim için)
    private static final Map<String, Color> COLOR_OPTIONS_MAP = new HashMap<>();
    private static final List<String> COLOR_OPTIONS_NAMES = new ArrayList<>();

    static {
        // Renk seçeneklerini static initializer block içinde doldur
        COLOR_OPTIONS_MAP.put("White", Color.WHITE);
        COLOR_OPTIONS_MAP.put("Red", Color.RED);
        COLOR_OPTIONS_MAP.put("Blue", Color.BLUE);
        COLOR_OPTIONS_MAP.put("Black", Color.BLACK);
        COLOR_OPTIONS_MAP.put("Green", Color.GREEN);
        COLOR_OPTIONS_MAP.put("Orange", Color.ORANGE);
        COLOR_OPTIONS_MAP.put("Purple", new Color(128, 0, 128)); // Mor için RGB değeri
        COLOR_OPTIONS_MAP.put("Yellow", Color.YELLOW);
        COLOR_OPTIONS_MAP.put("Cyan", Color.CYAN);
        COLOR_OPTIONS_MAP.put("Magenta", Color.MAGENTA);
        COLOR_OPTIONS_MAP.put("Turquoise", new Color(64, 224, 208)); // Turkuaz için RGB
        COLOR_OPTIONS_NAMES.addAll(COLOR_OPTIONS_MAP.keySet());
        // İstenirse renk isimlerini alfabetik sıralayabiliriz
        // Collections.sort(COLOR_OPTIONS_NAMES);
    }


    public GamePanel(String playerName) {
        this.playerName = playerName;
        gameRecords = new GameRecords();

        setBackground(Color.BLACK); // Varsayılan arka plan rengi başlangıçta siyah
        setFocusable(true);

        // Panelin görünür olduğunda odaklanması için ComponentListener ekle
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                requestFocusInWindow();
            }
        });


        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                // ======= SETTINGS MODU =======
                if (inSettings) {
                    // --- COLOR SETTINGS ALT MENÜSÜ ---
                    if (inColorSettings) {
                        switch (keyCode) {
                            case KeyEvent.VK_ESCAPE:
                                SoundPlayer.playClickSound();
                                inColorSettings = false;
                                selectedColorSettingOption = 0;
                                repaint();
                                break;
                            case KeyEvent.VK_UP:
                                SoundPlayer.playClickSound();
                                selectedColorSettingOption = (selectedColorSettingOption + 3) % 4;
                                repaint();
                                break;
                            case KeyEvent.VK_DOWN:
                                SoundPlayer.playClickSound();
                                selectedColorSettingOption = (selectedColorSettingOption + 1) % 4;
                                repaint();
                                break;
                            case KeyEvent.VK_LEFT:
                            case KeyEvent.VK_RIGHT:
                                SoundPlayer.playClickSound();
                                Color currentColor = getColorForOption(selectedColorSettingOption);
                                String currentColorName = getColorName(currentColor);
                                int index = COLOR_OPTIONS_NAMES.indexOf(currentColorName);
                                int newIndex = (keyCode == KeyEvent.VK_LEFT)
                                        ? (index - 1 + COLOR_OPTIONS_NAMES.size()) % COLOR_OPTIONS_NAMES.size()
                                        : (index + 1) % COLOR_OPTIONS_NAMES.size();
                                setColorForOption(selectedColorSettingOption, COLOR_OPTIONS_MAP.get(COLOR_OPTIONS_NAMES.get(newIndex)));
                                repaint();
                                break;
                        }
                    }
                    // --- ANA SETTINGS MENÜSÜ ---
                    else {
                        switch (keyCode) {
                            case KeyEvent.VK_ESCAPE:
                                SoundPlayer.playClickSound();
                                inSettings = false;
                                selectedSettingsOption = 0;
                                inMenu = true;
                                if (!SoundPlayer.backgroundMusicIsPlaying()) {
                                    SoundPlayer.playMainMenuMusic();
                                }
                                repaint();
                                break;
                            case KeyEvent.VK_UP:
                            case KeyEvent.VK_W:
                                SoundPlayer.playClickSound();
                                selectedSettingsOption = (selectedSettingsOption + 1) % 2;
                                repaint();
                                break;
                            case KeyEvent.VK_DOWN:
                            case KeyEvent.VK_S:
                                SoundPlayer.playClickSound();
                                selectedSettingsOption = (selectedSettingsOption + 1) % 2;
                                repaint();
                                break;
                            case KeyEvent.VK_ENTER:
                                SoundPlayer.playClickSound();
                                if (selectedSettingsOption == 0) {
                                    inColorSettings = true;
                                    selectedColorSettingOption = 0;
                                    repaint();
                                } else if (selectedSettingsOption == 1) {
                                    System.out.println("Sound Settings clicked!"); // Henüz işlevsel değil
                                }
                                break;
                        }
                    }
                    return; // Diğer kontrolleri atla
                }

                // ======= ANA MENÜ =======
                if (inMenu) {
                    switch (keyCode) {
                        case KeyEvent.VK_ESCAPE:
                            confirmExit();
                            break;
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_W:
                            SoundPlayer.playClickSound();
                            selectedOption = (selectedOption + 3) % 4;
                            repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_S:
                            SoundPlayer.playClickSound();
                            selectedOption = (selectedOption + 1) % 4;
                            repaint();
                            break;
                        case KeyEvent.VK_ENTER:
                            SoundPlayer.playClickSound();
                            switch (selectedOption) {
                                case 0: // Single Player
                                    isSinglePlayer = true;
                                    inMenu = false;
                                    inDifficultySelection = true;
                                    repaint();
                                    break;
                                case 1: // Two Players
                                    isSinglePlayer = false;
                                    startGame();
                                    break;
                                case 2: // Achievements
                                    inMenu = false;
                                    inAchievements = true;
                                    repaint();
                                    break;
                                case 3: // Settings
                                    inMenu = false;
                                    inSettings = true;
                                    selectedSettingsOption = 0;
                                    repaint();
                                    break;
                            }
                            break;
                    }
                    return;
                }

                // ======= ZORLUK SEÇİMİ =======
                if (inDifficultySelection) {
                    switch (keyCode) {
                        case KeyEvent.VK_ESCAPE:
                            SoundPlayer.playClickSound();
                            inDifficultySelection = false;
                            inMenu = true;
                            isSinglePlayer = false;
                            selectedOption = 0;
                            if (!SoundPlayer.backgroundMusicIsPlaying()) {
                                SoundPlayer.playMainMenuMusic();
                            }
                            repaint();
                            break;
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_W:
                            SoundPlayer.playClickSound();
                            aiDifficulty = (aiDifficulty + 2) % 3;
                            repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_S:
                            SoundPlayer.playClickSound();
                            aiDifficulty = (aiDifficulty + 1) % 3;
                            repaint();
                            break;
                        case KeyEvent.VK_ENTER:
                            SoundPlayer.playClickSound();
                            inDifficultySelection = false;
                            startGame();
                            break;
                    }
                    return;
                }

                // ======= BAŞARIMLAR =======
                if (inAchievements) {
                    if (keyCode == KeyEvent.VK_ESCAPE) {
                        SoundPlayer.playClickSound();
                        inAchievements = false;
                        inMenu = true;
                        selectedOption = 2;
                        if (!SoundPlayer.backgroundMusicIsPlaying()) {
                            SoundPlayer.playMainMenuMusic();
                        }
                        repaint();
                    }
                    return;
                }

                // ======= OYUN İÇİ =======
                if (keyCode == KeyEvent.VK_ESCAPE) {
                    if (endMessage.isEmpty()) {
                        timer.stop();
                        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit the game?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            inMenu = true;
                            inDifficultySelection = false;
                            selectedOption = 0;
                            isSinglePlayer = false;
                            playerScore = 0;
                            aiScore = 0;
                            endMessage = "";
                            leftPaddle.speed = GameConstants.PADDLE_SPEED;
                            rightPaddle.speed = GameConstants.PADDLE_SPEED;
                            ball.speedX = ball.initialSpeedX;
                            ball.speedY = ball.initialSpeedY;
                            SoundPlayer.stopBackgroundMusic();
                            SoundPlayer.playMainMenuMusic();
                            repaint();
                        } else {
                            timer.start();
                        }
                    }
                } else if (!endMessage.isEmpty()) {
                    if (keyCode == KeyEvent.VK_ENTER) {
                        playerScore = 0;
                        aiScore = 0;
                        endMessage = "";
                        startGame();
                    }
                } else {
                    // Oyuncu kontrolleri
                    if (!isSinglePlayer) {
                        if (keyCode == KeyEvent.VK_W) wPressed = true;
                        if (keyCode == KeyEvent.VK_S) sPressed = true;
                        if (keyCode == KeyEvent.VK_UP) upPressed = true;
                        if (keyCode == KeyEvent.VK_DOWN) downPressed = true;
                    } else {
                        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) upPressed = true;
                        if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) downPressed = true;
                    }
                }
            }


            @Override
            public void keyReleased(KeyEvent e) {
                if (!inMenu && !inDifficultySelection && !inAchievements && !inSettings && endMessage.isEmpty()) { // Ayarlar menüsündeyken tuş bırakma kontrolü yapma
                    if (!isSinglePlayer) {
                        if (e.getKeyCode() == KeyEvent.VK_W) wPressed = false;
                        if (e.getKeyCode() == KeyEvent.VK_S) sPressed = false;
                        if (e.getKeyCode() == KeyEvent.VK_UP) upPressed = false;
                        if (e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = false;
                    } else {
                        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) upPressed = false;
                        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) downPressed = false;
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (inMenu) { // Sadece ana menüdeyken tıklama olaylarını işle
                    if (githubLinkBounds != null && githubLinkBounds.contains(e.getPoint())) {
                        SoundPlayer.playClickSound();
                        openGitHubLink();
                    }
                    if (settingsButtonBounds != null && settingsButtonBounds.contains(e.getPoint())) {
                        SoundPlayer.playClickSound();
                        inMenu = false;
                        inSettings = true;
                        selectedSettingsOption = 0;
                        repaint();
                    }
                    if (githubLinkBounds != null && githubLinkBounds.contains(e.getPoint())) {
                        SoundPlayer.playClickSound();
                        openGitHubLink();
                    }
                    // Sağ alttaki Settings butonunun tıklama algılamasını kaldırdık
                }
                // Ayarlar menüsündeyken tıklama olaylarını işle (buraya eklenecek)
                else if (inSettings) {

                    if (inColorSettings) { // Renk Ayarları alt menüsündeyken
                        // Renk seçeneklerine tıklama algılama buraya gelecek
                        // Şimdilik sadece menüye dönüş ve alt menüye giriş tıklamaları
                    } else { // Ana Ayarlar menüsündeyken (Color, Sound)
                        // YENİ EKLENEN: Seçenek sınırlarını kullanarak tıklama kontrolü
                        if (colorSettingsBounds != null && colorSettingsBounds.contains(e.getPoint())) { // Color Settings tıklandı mı?
                            SoundPlayer.playClickSound();
                            inColorSettings = true;
                            selectedColorSettingOption = 0;
                            repaint();
                        }
                        if (soundSettingsBounds != null && soundSettingsBounds.contains(e.getPoint())) { // Sound Settings tıklandı mı?
                            SoundPlayer.playClickSound();
                            // Ses Ayarları mantığı buraya gelecek
                            System.out.println("Sound Settings clicked via mouse!");
                        }
                    }
                }
            }
        });
    }

    // GamePanel'in menü durumunda olup olmadığını döndüren metod
    public boolean isInMenu() {
        return inMenu;
    }

    // Seçilen renk ayarı seçeneğine göre mevcut rengi döndürür
    private Color getColorForOption(int option) {
        switch (option) {
            case 0: return backgroundColor;
            case 1: return ballColor;
            case 2: return paddle1Color;
            case 3: return paddle2Color;
            default: return Color.WHITE; // Varsayılan
        }
    }

    // Seçilen renk ayarı seçeneği için yeni rengi ayarlar
    private void setColorForOption(int option, Color color) {
        switch (option) {
            case 0: backgroundColor = color; break;
            case 1: ballColor = color; break;
            case 2: paddle1Color = color; break;
            case 3: paddle2Color = color; break;
        }
        // Arka plan rengi değişirse, JPanel'in setBackground metodunu da çağırmalıyız
        if (option == 0) {
            setBackground(backgroundColor);
        }
    }

    // Renk nesnesinden renk adını döndürür (ters arama)
    private String getColorName(Color color) {
        for (Map.Entry<String, Color> entry : COLOR_OPTIONS_MAP.entrySet()) {
            if (entry.getValue().equals(color)) {
                return entry.getKey();
            }
        }
        return "Unknown"; // Bulunamazsa
    }


    // ... (startGame metodu) ...
    private void startGame() {
        if (timer != null) {
            timer.stop();
        }

        int paddleWidth = GameConstants.PADDLE_WIDTH;
        int paddleHeight = GameConstants.PADDLE_HEIGHT;
        int paddleSpeed = GameConstants.PADDLE_SPEED;
        int ballSize = GameConstants.BALL_SIZE;
        int ballSpeed = GameConstants.BALL_SPEED;

        leftPaddle = new Paddle(0, (GameConstants.PANEL_HEIGHT / 2) - (paddleHeight / 2), paddleWidth, paddleHeight, paddleSpeed);
        rightPaddle = new Paddle(GameConstants.PANEL_WIDTH - paddleWidth, (GameConstants.PANEL_HEIGHT / 2) - (paddleHeight / 2), paddleWidth, paddleHeight, paddleSpeed);

        leftPaddle.speed = GameConstants.PADDLE_SPEED;
        rightPaddle.speed = GameConstants.PADDLE_SPEED;

        ball = new Ball(GameConstants.PANEL_WIDTH / 2 - ballSize / 2, GameConstants.PANEL_HEIGHT / 2 - ballSize / 2, ballSize, ballSpeed, ballSpeed);

        ball.speedX = ball.initialSpeedX;
        ball.speedY = ball.initialSpeedY;


        if (isSinglePlayer) {
            ai = new AI(leftPaddle, ball, aiDifficulty);
        }

        inMenu = false;
        inDifficultySelection = false;
        inAchievements = false;
        inSettings = false; // Oyuna girildiğinde Ayarlar menüsünde değiliz
        inColorSettings = false; // Oyuna girildiğinde Renk Ayarlarında değiliz


        SoundPlayer.playGameStart();

        SoundPlayer.stopBackgroundMusic();
        SoundPlayer.playGameMusic();


        timer = new Timer(16, e -> {
            if (!isSinglePlayer) {
                if (wPressed) leftPaddle.moveUp();
                if (sPressed) leftPaddle.moveDown(GameConstants.PANEL_HEIGHT);
            }
            if (upPressed) rightPaddle.moveUp();
            if (downPressed) rightPaddle.moveDown(GameConstants.PANEL_HEIGHT);


            if (isSinglePlayer) {
                ai.move();
            }

            ball.move();

            if (ball.x < 0) {
                playerScore++;
                SoundPlayer.playGoalSound(); // Gol sesi
                if (playerScore >= 12) {
                    if (isSinglePlayer) {
                        endGame("Player Wins!");
                        gameRecords.updateWins(playerName, aiDifficulty);
                    } else {
                        endGame("Player 2 Wins!");
                    }
                } else {
                    ball.reset(GameConstants.PANEL_WIDTH, GameConstants.PANEL_HEIGHT);
                }

            }
            if (ball.x > GameConstants.PANEL_WIDTH) {
                aiScore++;
                SoundPlayer.playGoalSound(); // Gol sesi
                if (aiScore >= 12) {
                    if (isSinglePlayer) {
                        endGame("AI Wins!");
                        gameRecords.updateLosses(playerName, aiDifficulty);
                    } else {
                        endGame("Player 1 Wins!");
                    }
                } else {
                    ball.reset(GameConstants.PANEL_WIDTH, GameConstants.PANEL_HEIGHT);
                }
            }

            if (ball.y <= 0 || ball.y >= GameConstants.PANEL_HEIGHT - ball.size) {
                ball.reverseY();
                SoundPlayer.playPlasticBounce();
            }

            if (ball.x <= leftPaddle.x + leftPaddle.width &&
                    ball.y + ball.size >= leftPaddle.y &&
                    ball.y <= leftPaddle.y + leftPaddle.height) {
                SoundPlayer.playPlasticBounce();
                ball.reverseX();
                ball.increaseSpeed();
                ball.x = leftPaddle.x + leftPaddle.width;
            }
            if (ball.x + ball.size >= rightPaddle.x &&
                    ball.y + ball.size >= rightPaddle.y &&
                    ball.y <= rightPaddle.y + rightPaddle.height) {
                SoundPlayer.playPlasticBounce();
                ball.reverseX();
                ball.increaseSpeed();
                ball.x = rightPaddle.x - ball.size;
            }

            repaint();
        });
        timer.start();
    }

    // ... (endGame metodu) ...
    private void endGame(String result) {
        timer.stop();
        endMessage = result;

        if (result.contains("Wins!")) {
            if (isSinglePlayer) {
                if (aiDifficulty == 0) SoundPlayer.playViolinWin();
                else if (aiDifficulty == 1) SoundPlayer.playLevelWin();
                else SoundPlayer.playOrchestralWin();
            } else {
                SoundPlayer.playOrchestralWin();
            }
        } else {
            SoundPlayer.playNegativeBeeps(); // Kaybetme sesi
        }

        gameRecords.saveRecordsToFile();

        repaint();
    }


    private void confirmExit() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            int response = JOptionPane.showConfirmDialog(parentFrame,
                    "Are you sure you want to exit the application?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                SoundPlayer.stopBackgroundMusic();
                System.exit(0);
            }
        } else {
            SoundPlayer.stopBackgroundMusic();
            System.exit(0);
        }
    }

    private void openGitHubLink() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(GITHUB_URL));
            } else {
                JOptionPane.showMessageDialog(this, "Cannot open GitHub link. Please visit:\n" + GITHUB_URL, "Info", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Cannot open GitHub link. Please visit: " + GITHUB_URL);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening GitHub link: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // setBackground(backgroundColor) burada etkili olur
        GameConstants.PANEL_WIDTH = getWidth();
        GameConstants.PANEL_HEIGHT = getHeight();

        // Arka plan rengini çiz (JPanel setBackground ile de ayarlanabilir, ancak bu daha manuel kontrol sağlar)
        g.setColor(backgroundColor);
        g.fillRect(0, 0, GameConstants.PANEL_WIDTH, GameConstants.PANEL_HEIGHT);


        if (inMenu) { // Ana menüyü çiz
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            FontMetrics fm = g.getFontMetrics(g.getFont());
            int titleWidth = fm.stringWidth("PONG");
            g.drawString("PONG", (getWidth() - titleWidth) / 2, 150);

            g.setFont(new Font("Arial", Font.PLAIN, 36));
            int optionY = 250;
            int optionSpacing = 60;

            g.setColor(selectedOption == 0 ? Color.YELLOW : Color.WHITE);
            fm = g.getFontMetrics(g.getFont());
            int spWidth = fm.stringWidth("Single Player");
            g.drawString("Single Player", (getWidth() - spWidth) / 2, optionY);

            g.setColor(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
            fm = g.getFontMetrics(g.getFont());
            int tpWidth = fm.stringWidth("Two Players");
            g.drawString("Two Players", (getWidth() - tpWidth) / 2, optionY + optionSpacing);

            g.setColor(selectedOption == 2 ? Color.YELLOW : Color.WHITE);
            fm = g.getFontMetrics(g.getFont());
            int achWidth = fm.stringWidth("Achievements");
            g.drawString("Achievements", (getWidth() - achWidth) / 2, optionY + 2 * optionSpacing);

            // Settings seçeneği
            g.setColor(selectedOption == 3 ? Color.YELLOW : Color.WHITE);
            fm = g.getFontMetrics(g.getFont());
            int settingsWidth = fm.stringWidth("Settings");
            int settingsX = (getWidth() - settingsWidth) / 2;
            int settingsY = optionY + 3 * optionSpacing;
            g.drawString("Settings", settingsX, settingsY);

// Settings seçeneğine fare ile tıklanabilmesi için sınır tanımla
            settingsButtonBounds = new Rectangle(settingsX, settingsY - fm.getAscent(), settingsWidth, fm.getHeight());


            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.LIGHT_GRAY);
            String hint = "Use Up/Down or W/S to select, Enter to start";
            fm = g.getFontMetrics(g.getFont());
            int hintWidth = fm.stringWidth(hint);
            g.drawString(hint, (getWidth() - hintWidth) / 2, getHeight() - 50);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.CYAN);
            String githubText = "GitHub Link";
            fm = g.getFontMetrics(g.getFont());
            int githubTextWidth = fm.stringWidth(githubText);
            int githubTextHeight = fm.getHeight();
            int githubTextX = 20;
            int githubTextY = getHeight() - 20;
            g.drawString(githubText, githubTextX, githubTextY);

            githubLinkBounds = new Rectangle(githubTextX, githubTextY - githubTextHeight, githubTextWidth, githubTextHeight);

            // Sağ alttaki Ayarlar yazısını ve buton sınırını kaldırdık
            // g.setFont(new Font("Arial", Font.PLAIN, 20));
            // g.setColor(Color.YELLOW);
            // String settingsText = "SETTINGS"; // Sağ alttaki Ayarlar yazısı (buton olarak da kullanılıyor)
            // fm = g.getFontMetrics(g.getFont());
            // int settingsTextWidth = fm.stringWidth(settingsText);
            // int settingsTextHeight = fm.getHeight();
            // int settingsTextX = getWidth() - settingsTextWidth - 20;
            // int settingsTextY = getHeight() - 20;
            // g.drawString(settingsText, settingsTextX, settingsTextY);

            // settingsButtonBounds = new Rectangle(settingsTextX, settingsTextY - settingsTextHeight, settingsTextWidth, settingsTextHeight);


        } else if (inSettings) { // Ayarlar menüsünü çiz
            if (inColorSettings) { // Renk Ayarları alt menüsünü çiz
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics fm = g.getFontMetrics(g.getFont());
                String title = "Color Settings";
                int titleWidth = fm.stringWidth(title);
                g.drawString(title, (getWidth() - titleWidth) / 2, 150);

                g.setFont(new Font("Arial", Font.PLAIN, 30));
                int optionY = 250;
                int optionSpacing = 50;
                String[] colorOptions = {"Background", "Ball", "Paddle-1", "Paddle-2"};

                // Renk seçeneklerinin çizileceği X koordinatı
                int colorOptionX = getWidth() / 2 + 50; // Renk swatch ve isminin çizileceği X koordinatı

                for (int i = 0; i < colorOptions.length; i++) {
                    g.setColor(selectedColorSettingOption == i ? Color.YELLOW : Color.WHITE);
                    fm = g.getFontMetrics(g.getFont());
                    int optionWidth = fm.stringWidth(colorOptions[i]);
                    g.drawString(colorOptions[i], getWidth() / 4 - optionWidth / 2, optionY + i * optionSpacing); // Sol tarafa hizala ve ortala

                    // Mevcut rengi ve renk seçeneklerini çiz
                    g.setColor(getColorForOption(i)); // Mevcut renk
                    g.fillRect(colorOptionX - 40, optionY + i * optionSpacing - fm.getAscent(), 30, 30); // Küçük renk kutusu çiz

                    g.setColor(Color.WHITE); // Yazı rengini beyaz yap
                    g.drawString(getColorName(getColorForOption(i)), colorOptionX, optionY + i * optionSpacing); // Mevcut renk adı
                }

                // İpuçları
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.setColor(Color.LIGHT_GRAY);
                String hint = "Use Up/Down to select item, Left/Right to change color, Escape to return";
                fm = g.getFontMetrics(g.getFont());
                int hintWidth = fm.stringWidth(hint);
                g.drawString(hint, (getWidth() - hintWidth) / 2, getHeight() - 50);


            } else { // Ana Ayarlar menüsünü çiz (Color, Sound)
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics fm = g.getFontMetrics(g.getFont());
                String title = "Settings";
                int titleWidth = fm.stringWidth(title);
                g.drawString(title, (getWidth() - titleWidth) / 2, 150);

                g.setFont(new Font("Arial", Font.PLAIN, 30));
                int optionY = 250;
                int optionSpacing = 50;
                String[] settingsOptions = {"Color Settings", "Sound Settings"};

                for (int i = 0; i < settingsOptions.length; i++) {
                    g.setColor(selectedSettingsOption == i ? Color.YELLOW : Color.WHITE);
                    fm = g.getFontMetrics(g.getFont());
                    int optionWidth = fm.stringWidth(settingsOptions[i]);
                    int optionX = (getWidth() - optionWidth) / 2; // Seçeneğin X pozisyonu (ortalanmış)
                    int currentOptionY = optionY + i * optionSpacing; // Seçeneğin Y pozisyonu (baselinesı)
                    g.drawString(settingsOptions[i], optionX, currentOptionY);

                    // Seçenek sınırlarını hesapla ve ata
                    if (i == 0) { // Color Settings
                        colorSettingsBounds = new Rectangle(optionX, currentOptionY - fm.getAscent(), optionWidth, fm.getHeight());
                    } else if (i == 1) { // Sound Settings
                        soundSettingsBounds = new Rectangle(optionX, currentOptionY - fm.getAscent(), optionWidth, fm.getHeight());
                    }
                }

                // İpuçları
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.setColor(Color.LIGHT_GRAY);
                String hint = "Use Up/Down or W/S to select, Enter to open, Escape to return to menu";
                fm = g.getFontMetrics(g.getFont());
                int hintWidth = fm.stringWidth(hint);
                g.drawString(hint, (getWidth() - hintWidth) / 2, getHeight() - 50);
            }


        } else if (inDifficultySelection) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics fm = g.getFontMetrics(g.getFont());
            String title = "Select Difficulty";
            int titleWidth = fm.stringWidth(title);
            g.drawString(title, (getWidth() - titleWidth) / 2, 150);

            g.setFont(new Font("Arial", Font.PLAIN, 30));
            int diffY = 250;
            int diffSpacing = 50;

            g.setColor(aiDifficulty == 0 ? Color.YELLOW : Color.WHITE);
            fm = g.getFontMetrics(g.getFont());
            int easyWidth = fm.stringWidth("Easy");
            g.drawString("Easy", (getWidth() - easyWidth) / 2, diffY);

            g.setColor(aiDifficulty == 1 ? Color.YELLOW : Color.WHITE);
            fm = g.getFontMetrics(g.getFont());
            int normalWidth = fm.stringWidth("Normal");
            g.drawString("Normal", (getWidth() - normalWidth) / 2, diffY + diffSpacing);

            g.setColor(aiDifficulty == 2 ? Color.YELLOW : Color.WHITE);
            fm = g.getFontMetrics(g.getFont());
            int hardWidth = fm.stringWidth("Hard");
            g.drawString("Hard", (getWidth() - hardWidth) / 2, diffY + 2 * diffSpacing);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.LIGHT_GRAY);
            String hint = "Use Up/Down to select, Enter to start";
            fm = g.getFontMetrics(g.getFont());
            int hintWidth = fm.stringWidth(hint);
            g.drawString(hint, (getWidth() - hintWidth) / 2, getHeight() - 50);

        } else if (inAchievements) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics fm = g.getFontMetrics(g.getFont());
            String title = "Achievements";
            int titleWidth = fm.stringWidth(title);
            g.drawString(title, (getWidth() - titleWidth) / 2, 80);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.YELLOW);
            int padding = 40;
            int playerColWidth = fm.stringWidth("Player") + 50;
            int scoreColWidth = 100;

            int col1X = padding;
            int col2X = col1X + playerColWidth;
            int col3X = col2X + scoreColWidth;
            int col4X = col3X + scoreColWidth;


            g.drawString("Player", col1X, 150);
            g.drawString("Easy", col2X, 150);
            g.drawString("Normal", col3X, 150);
            g.drawString("Hard", col4X, 150);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            int startY = 200;
            int rowHeight = 30;

            GameRecords currentRecords = new GameRecords();
            List<String> sortedPlayers = new ArrayList<>(currentRecords.getAllRecords().keySet());
            Collections.sort(sortedPlayers);


            int currentRow = 0;
            for (String player : sortedPlayers) {
                GameRecords.DifficultyStats stats = currentRecords.getAllRecords().get(player);
                if (stats != null) {
                    g.drawString(player, col1X, startY + currentRow * rowHeight);
                    g.drawString(stats.getEasyWins() + "W-" + stats.getEasyLosses() + "L", col2X, startY + currentRow * rowHeight);
                    g.drawString(stats.getNormalWins() + "W-" + stats.getNormalLosses() + "L", col3X, startY + currentRow * rowHeight);
                    g.drawString(stats.getHardWins() + "W-" + stats.getHardLosses() + "L", col4X, startY + currentRow * rowHeight);
                    currentRow++;
                }
            }

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.LIGHT_GRAY);
            String hint = "Press Escape to return to menu";
            fm = g.getFontMetrics(g.getFont());
            int hintWidth = fm.stringWidth(hint);
            g.drawString(hint, (getWidth() - hintWidth) / 2, getHeight() - 50);

        } else { // Oyun ekranı
            // Arka plan rengi paintComponent başında çizildiği için burada tekrar çizmeye gerek yok

            // Orta çizgiyi çiz (beyaz kalsın)
            g.setColor(Color.WHITE);
            for (int i = 0; i < GameConstants.PANEL_HEIGHT; i += 30) {
                g.fillRect(GameConstants.PANEL_WIDTH / 2 - 2, i, 4, 20);
            }

            // Paddle ve topu ayarlanmış renklerle çiz
            g.setColor(paddle1Color); // Paddle 1 rengi
            leftPaddle.draw(g);
            g.setColor(paddle2Color); // Paddle 2 rengi
            rightPaddle.x = GameConstants.PANEL_WIDTH - rightPaddle.width;
            rightPaddle.draw(g);
            g.setColor(ballColor); // Top rengi
            ball.draw(g);

            // Skorları göster (beyaz kalsın)
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            if (isSinglePlayer) {
                g.drawString("AI: " + aiScore, 50, 50);
                g.drawString("Player (" + playerName + "): " + playerScore, GameConstants.PANEL_WIDTH - 250, 50);
            } else {
                g.drawString("Player 1: " + aiScore, 50, 50);
                g.drawString("Player 2: " + playerScore, GameConstants.PANEL_WIDTH - 150, 50);
            }

            if (!endMessage.isEmpty()) {
                g.setFont(new Font("Arial", Font.BOLD, 48));
                g.setColor(Color.YELLOW);
                FontMetrics fm = g.getFontMetrics(g.getFont());
                int msgWidth = fm.stringWidth(endMessage);
                g.drawString(endMessage, (GameConstants.PANEL_WIDTH - msgWidth) / 2, GameConstants.PANEL_HEIGHT / 2);

                g.setFont(new Font("Arial", Font.PLAIN, 24));
                String continueHint = "Press Enter to play again or Escape to return to menu.";
                fm = g.getFontMetrics(g.getFont());
                int hintWidth = fm.stringWidth(continueHint);
                g.drawString(continueHint, (GameConstants.PANEL_WIDTH - hintWidth) / 2, GameConstants.PANEL_HEIGHT / 2 + 50);
            }
        }
    }

    // JPanel pencereden kaldırıldığında çağrılır
    @Override
    public void removeNotify() {
        super.removeNotify();
        // Panel kaldırıldığında müziği durdur
        SoundPlayer.stopBackgroundMusic();
    }
}
