import javax.swing.JFrame;
import javax.sound.sampled.*;
import java.io.InputStream;
import java.io.IOException;

public class PongGame {
    public static void main(String[] args) {
        // Ana pencereyi oluştur
        JFrame frame = new JFrame("Pong Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Pencere kapanınca program bitsin
        frame.setResizable(false); // Boyut değişmesin
        frame.setSize(800, 600); // Pencere boyutu
        frame.add(new GamePanel()); // Oyun panelini ekle
        frame.setLocationRelativeTo(null); // Ortada aç
        frame.setVisible(true); // Göster

        try {
            // Ses dosyasını kaynaklardan yükle
            InputStream soundStream = PongGame.class.getResourceAsStream("/Sounds/your-sound-file.wav");

            // Ses dosyasını kontrol et
            if (soundStream == null) {
                System.out.println("Ses dosyası bulunamadı!");
                return;
            }

            // Ses dosyasını bir AudioInputStream'e dönüştür
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundStream);

            // Ses dosyasını bir Clip'e yükle
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Ses çal
            clip.start();

            // Ses bitene kadar bekle
            while (!clip.isRunning())
                Thread.sleep(10);

            while (clip.isRunning())
                Thread.sleep(10);

            clip.close();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            // Hata mesajlarını al ve yazdır
            System.out.println("Hata oluştu: ");
            e.printStackTrace();
        }
    }
}



