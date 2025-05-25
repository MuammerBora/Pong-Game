/*
	arka plan: happy-relaxing-loop-275487.wav
	Easy: violin-win-5-185128.wav
	Normal: level-win-6416.wav
	Hard: orchestral-win-331233.wav
	game start: game-start-6104.wav
	game over(lost): negative-beeps-6008.wav

*/

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class SoundPlayer {

    // Ses dosyalarının yol isimleri
    public static final String BACKGROUND_MUSIC_PATH = "/Sounds/happy-relaxing-loop-275487.wav";
    public static final String GAME_START_PATH = "/Sounds/game-start-6104.wav";
    public static final String LEVEL_WIN_PATH = "/Sounds/level-win-6416.wav";
    public static final String NEGATIVE_BEEPS_PATH = "/Sounds/negative-beeps-6008.wav";
    public static final String ORCHESTRAL_WIN_PATH = "/Sounds/orchestral-win-331233.wav";
    public static final String PLASTIC_BOUNCE_PATH = "/Sounds/plastic-ball-bounce-14790.wav";
    public static final String VIOLIN_WIN_PATH = "/Sounds/violin-win-5-185128.wav";

    private static Clip backgroundClip;

    /**
     * Arka plan müziğini başlatır ve tekrar eder.
     */
    public static void playBackgroundMusic() {
        try {
            if (backgroundClip == null || !backgroundClip.isRunning()) {
                InputStream is = SoundPlayer.class.getResourceAsStream(BACKGROUND_MUSIC_PATH);
                if (is == null) {
                    System.out.println("Background müzik bulunamadı: " + BACKGROUND_MUSIC_PATH);
                    return;
                }
                byte[] audioBytes = audioToByteArray(is);
                ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(ais);
                backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundClip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Herhangi bir ses dosyasını yeni threadde çalar.
     * @param filePath Dosya yolu
     */
    public static void playSound(String filePath) {
        new Thread(() -> {
            try {
                InputStream is = SoundPlayer.class.getResourceAsStream(filePath);
                if (is == null) {
                    System.out.println("Ses dosyası bulunamadı: " + filePath);
                    return;
                }
                byte[] audioBytes = audioToByteArray(is);
                ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Özel fonksiyonlar
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
