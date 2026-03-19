package INF1009_P3_02;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardManager {

    private static final int MAX_ENTRIES = 10;

    private final String filePath;
    private final List<LeaderboardEntry> entries = new ArrayList<LeaderboardEntry>();

    public LeaderboardManager(String filePath) {
        this.filePath = filePath;
        load();
    }

    public List<LeaderboardEntry> getEntries() {
        return new ArrayList<LeaderboardEntry>(entries);
    }

    public void addEntry(String name, int score) {
        if (name == null || name.trim().isEmpty()) {
            name = "Player";
        }

        String trimmedName = name.trim();
        LeaderboardEntry existing = null;
        for (LeaderboardEntry entry : entries) {
            if (entry.name.equalsIgnoreCase(trimmedName)) {
                existing = entry;
                break;
            }
        }

        if (existing != null) {
            if (score > existing.score) {
                entries.remove(existing);
                entries.add(new LeaderboardEntry(trimmedName, score));
            }
        } else {
            entries.add(new LeaderboardEntry(trimmedName, score));
        }

        Collections.sort(entries, new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry a, LeaderboardEntry b) {
                return Integer.compare(b.score, a.score);
            }
        });

        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }

        save();
    }

    private void load() {
        entries.clear();

        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length != 2) continue;

                String name = parts[0];
                try {
                    int score = Integer.parseInt(parts[1]);
                    entries.add(new LeaderboardEntry(name, score));
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException e) {
            System.err.println("[Leaderboard] Failed to load leaderboard: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private void save() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(filePath, false));
            for (LeaderboardEntry entry : entries) {
                writer.println(entry.name + "|" + entry.score);
            }
        } catch (IOException e) {
            System.err.println("[Leaderboard] Failed to save leaderboard: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}

