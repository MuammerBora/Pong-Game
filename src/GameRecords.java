import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GameRecords {
    // Dosya adı sabit olarak tanımlandı
    private static final String FILENAME = "game-records.txt";
    // Oyuncu adı ve zorluk seviyelerine göre istatistikleri tutacak harita
    private Map<String, DifficultyStats> playerRecords;

    // Zorluk seviyelerine göre galibiyet ve mağlubiyetleri tutan iç sınıf
    public static class DifficultyStats implements Serializable {
        private static final long serialVersionUID = 1L; // Serileştirme ID'si

        private int easyWins;
        private int easyLosses;
        private int normalWins;
        private int normalLosses;
        private int hardWins;
        private int hardLosses;

        public DifficultyStats() {
            // Başlangıçta tüm skorlar 0
            this.easyWins = 0;
            this.easyLosses = 0;
            this.normalWins = 0;
            this.normalLosses = 0;
            this.hardWins = 0;
            this.hardLosses = 0;
        }

        // Getter metodları
        public int getEasyWins() { return easyWins; }
        public int getEasyLosses() { return easyLosses; }
        public int getNormalWins() { return normalWins; }
        public int getNormalLosses() { return normalLosses; }
        public int getHardWins() { return hardWins; }
        public int getHardLosses() { return hardLosses; }


        // Skor güncelleme metodları
        public void addEasyWin() { easyWins++; }
        public void addEasyLoss() { easyLosses++; }
        public void addNormalWin() { normalWins++; }
        public void addNormalLoss() { normalLosses++; }
        public void addHardWin() { hardWins++; }
        public void addHardLoss() { hardLosses++; }

        @Override
        public String toString() {
            return easyWins + "W-" + easyLosses + "L, " +
                    normalWins + "W-" + normalLosses + "L, " +
                    hardWins + "W-" + hardLosses + "L";
        }
    }

    public GameRecords() {
        playerRecords = new HashMap<>();
        loadRecordsFromFile(); // Nesne oluştuğunda kayıtları yükle
    }

    // Tek oyunculu modda galibiyet güncelle
    public void updateWins(String playerName, int difficulty) {
        // Eğer oyuncu yoksa yeni bir giriş oluştur
        playerRecords.putIfAbsent(playerName, new DifficultyStats());
        // Zorluğa göre ilgili galibiyeti artır
        DifficultyStats stats = playerRecords.get(playerName);
        if (difficulty == 0) stats.addEasyWin();
        else if (difficulty == 1) stats.addNormalWin();
        else stats.addHardWin();
        saveRecordsToFile(); // Güncellemeden sonra dosyaya kaydet
    }

    // Tek oyunculu modda mağlubiyet güncelle
    public void updateLosses(String playerName, int difficulty) {
        // Eğer oyuncu yoksa yeni bir giriş oluştur
        playerRecords.putIfAbsent(playerName, new DifficultyStats());
        // Zorluğa göre ilgili mağlubiyeti artır
        DifficultyStats stats = playerRecords.get(playerName);
        if (difficulty == 0) stats.addEasyLoss();
        else if (difficulty == 1) stats.addNormalLoss();
        else stats.addHardLoss();
        saveRecordsToFile(); // Güncellemeden sonra dosyaya kaydet
    }


    // Tüm oyuncu kayıtlarını döndürür
    public Map<String, DifficultyStats> getAllRecords() {
        return playerRecords;
    }

    // Kayıtları dosyadan yükler (deserialize)
    private void loadRecordsFromFile() {
        try {
            File file = new File(FILENAME);
            // Dosya varsa ve boş değilse yükle
            if (file.exists() && file.length() > 0) {
                // ObjectInputStream ile nesneyi dosyadan oku
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                // Haritayı dosyadan oku ve playerRecords'a ata
                playerRecords = (Map<String, DifficultyStats>) ois.readObject();
                ois.close();
                fis.close();
            }
        } catch (FileNotFoundException e) {
            // Dosya bulunamazsa (ilk çalıştırma olabilir), hata verme, boş harita ile devam et
            System.out.println("Game records file not found. Starting with empty records.");
        } catch (IOException | ClassNotFoundException e) {
            // Diğer IO veya sınıf hatlarında hata izini yazdır
            e.printStackTrace();
        }
    }

    // Kayıtları dosyaya kaydeder (serialize)
    public void saveRecordsToFile() {
        try {
            // ObjectOutputStream ile nesneyi dosyaya yaz
            FileOutputStream fos = new FileOutputStream(FILENAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // playerRecords haritasını dosyaya yaz
            oos.writeObject(playerRecords);
            oos.close();
            fos.close();
            System.out.println("Game records saved successfully.");
        } catch (IOException e) {
            // IO hatalarında hata izini yazdır
            e.printStackTrace();
        }
    }
}