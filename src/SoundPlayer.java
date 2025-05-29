import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException; // Bu import satırının olduğundan emin olalım

public class SoundPlayer {

    // Ses dosyalarının yol isimleri
    public static final String MAIN_MENU_MUSIC_PATH = "/Sounds/MainMenu.wav"; // Ana menü müziği
    public static final String GAME_MUSIC_PATH = "/Sounds/OST-Pong-pong-Game.wav"; // Oyun müziği
    public static final String CLICK_SOUND_PATH = "/Sounds/ClickButton.wav"; // Tıklama sesi

    public static final String GAME_START_PATH = "/Sounds/game-start-6104.wav";
    public static final String LEVEL_WIN_PATH = "/Sounds/level-win-6416.wav"; // Normal zorluk kazanma sesi
    public static final String NEGATIVE_BEEPS_PATH = "/Sounds/Failed.wav.wav"; // Kaybetme sesi
    public static final String ORCHESTRAL_WIN_PATH = "/Sounds/orchestral-win-331233.wav"; // Hard zorluk kazanma sesi
    public static final String PLASTIC_BOUNCE_PATH = "/Sounds/plastic-ball-bounce-14790.wav"; // Top sekme sesi
    public static final String VIOLIN_WIN_PATH = "/Sounds/violin-win-5-185128.wav"; // Easy zorluk kazanma sesi
    public static final String GOAL_SOUND_PATH = "/Sounds/goal.wav";

    private static Clip backgroundClip; // Arka plan müziği için tek klip nesnesi

    /**
     * Belirtilen ses dosyasını arka plan müziği olarak başlatır ve tekrar eder.
     * Eğer başka bir arka plan müziği çalıyorsa onu durdurur.
     * @param filePath Çalınacak ses dosyasının yolu
     */
    public static void playBackgroundMusic(String filePath) {
        // Eğer çalan bir arka plan müziği varsa durdur
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close(); // Kaynakları serbest bırak
        }
        try {
            InputStream is = SoundPlayer.class.getResourceAsStream(filePath);
            if (is == null) {
                System.out.println("Background müzik bulunamadı: " + filePath);
                return;
            }
            byte[] audioBytes = audioToByteArray(is);
            if (audioBytes == null) {
                System.out.println("Ses dosyası byte dizisine dönüştürülemedi veya dönüştürme sırasında hata oluştu: " + filePath);
                return;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(ais);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // Müziği sürekli tekrar et
            backgroundClip.start(); // Müziği başlat
            //System.out.println("Background müzik çalıyor: " + filePath); // Debug çıktısı
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // Bu spesifik istisnaları yakala
            e.printStackTrace();
            System.out.println("Background müzik çalma hatası: " + filePath + " - " + e.getMessage()); // Debug çıktısı
        } catch (Exception e) {
            // Diğer genel istisnaları yakala
            e.printStackTrace();
            System.out.println("Genel hata oluştu: " + e.getMessage()); // Debug çıktısı
        }
    }

    /**
     * Çalan arka plan müziğini durdurur.
     */
    public static void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
            //System.out.println("Background müzik durduruldu."); // Debug çıktısı
        }
    }


    /**
     * Belirtilen ses dosyasını bir kez çalar (sound effect).
     * @param filePath Çalınacak ses dosyasının yolu
     */
    public static void playSound(String filePath) {
        // Ses efektlerini çalmak için yeni bir Thread kullanmak,
        // UI Thread'inin bloke olmasını önler.
        new Thread(() -> {
            try { // Try-with-resources burada InputStream ve AudioInputStream'i otomatik kapatır
                InputStream is = SoundPlayer.class.getResourceAsStream(filePath);
                if (is == null) {
                    System.out.println("Ses dosyası bulunamadı: " + filePath);
                    return;
                }
                // AudioInputStream de AutoCloseable olduğu için try-with-resources içinde kullanılabilir
                try (AudioInputStream ais = AudioSystem.getAudioInputStream(is)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(ais);
                    clip.start();
                    // Klip bitince kaynakları serbest bırak (isteğe bağlı ama iyi pratik)
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }


            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                // Bu spesifik istisnaları yakala
                e.printStackTrace();
                System.out.println("Ses dosyası çalma hatası: " + filePath + " - " + e.getMessage()); // Debug çıktısı
            } catch (Exception e) {
                // Diğer genel istisnaları yakala
                e.printStackTrace();
                System.out.println("Genel hata oluştu: " + e.getMessage()); // Debug çıktısı
            }
        }).start();
    }
    public static boolean backgroundMusicIsPlaying() {
        // backgroundClip null değilse ve çalıyorsa true döner
        return backgroundClip != null && backgroundClip.isRunning();
    }

    // Yeni özel fonksiyonlar

    /**
     * Ana menü müziğini başlatır.
     */
    public static void playMainMenuMusic() {
        playBackgroundMusic(MAIN_MENU_MUSIC_PATH);
    }

    /**
     * Oyun müziğini başlatır.
     */
    public static void playGameMusic() {
        playBackgroundMusic(GAME_MUSIC_PATH);
    }

    /**
     * Tıklama sesini çalar.
     */
    public static void playClickSound() {
        playSound(CLICK_SOUND_PATH);
    }


    // Mevcut özel fonksiyonlar

    public static void playGameStart() {
        playSound(GAME_START_PATH);
    }

    public static void playLevelWin() {
        playSound(LEVEL_WIN_PATH);
    }

    public static void playNegativeBeeps() {
        playSound(NEGATIVE_BEEPS_PATH);
    }

    public static void playOrchestralWin() {
        playSound(ORCHESTRAL_WIN_PATH);
    }

    public static void playPlasticBounce() {
        playSound(PLASTIC_BOUNCE_PATH);
    }

    public static void playViolinWin() {
        playSound(VIOLIN_WIN_PATH);
    }
    public static void playGoalSound() {
        playSound(GOAL_SOUND_PATH);
    }

    /**
     * InputStream'den byte dizisine dönüştürür.
     */
    private static byte[] audioToByteArray(InputStream is) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Byte dizisine dönüştürme hatası: " + e.getMessage()); // Debug çıktısı
            return null;
        }
    }
}