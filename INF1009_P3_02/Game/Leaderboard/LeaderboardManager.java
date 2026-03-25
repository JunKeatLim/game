package INF1009_P3_02.Game.Leaderboard;

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
    private static final String MODE_ALL = "ALL";

    private final String filePath;
    private final List<LeaderboardEntry> entries = new ArrayList<LeaderboardEntry>();

    public LeaderboardManager(String filePath) {
        this.filePath = filePath;
        load();
    }

    public List<LeaderboardEntry> getEntries() {
        return new ArrayList<LeaderboardEntry>(entries);
    }

    public List<LeaderboardEntry> getEntriesByMode(String mode) {
        String normalizedMode = normalizeMode(mode);
        List<LeaderboardEntry> filtered = new ArrayList<LeaderboardEntry>();
        for (LeaderboardEntry entry : entries) {
            if (normalizedMode.equals(MODE_ALL) || normalizedMode.equals(entry.mode)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    public int getBestScoreForPlayerMode(String name, String mode) {
        if (name == null || name.trim().isEmpty()) {
            return Integer.MIN_VALUE;
        }
        String trimmedName = name.trim();
        String normalizedMode = normalizeMode(mode);
        int best = Integer.MIN_VALUE;
        for (LeaderboardEntry entry : entries) {
            if (entry.name.equalsIgnoreCase(trimmedName) && entry.mode.equals(normalizedMode)) {
                if (entry.score > best) {
                    best = entry.score;
                }
            }
        }
        return best;
    }

    public void addEntry(String name, int score, String mode) {
        if (name == null || name.trim().isEmpty()) {
            name = "Player";
        }

        String trimmedName = name.trim();
        String normalizedMode = normalizeMode(mode);
        LeaderboardEntry existing = null;
        for (LeaderboardEntry entry : entries) {
            if (entry.name.equalsIgnoreCase(trimmedName) && entry.mode.equals(normalizedMode)) {
                existing = entry;
                break;
            }
        }

        if (existing != null) {
            if (score > existing.score) {
                entries.remove(existing);
                entries.add(new LeaderboardEntry(trimmedName, score, normalizedMode));
            }
        } else {
            entries.add(new LeaderboardEntry(trimmedName, score, normalizedMode));
        }

        trimEntriesPerMode();

        save();
    }

    public void addEntry(String name, int score) {
        addEntry(name, score, MODE_ALL);
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
                if (parts.length != 2 && parts.length != 3) continue;

                String name = parts[0];
                try {
                    int score = Integer.parseInt(parts[1]);
                    String mode = parts.length == 3 ? parts[2] : MODE_ALL;
                    entries.add(new LeaderboardEntry(name, score, normalizeMode(mode)));
                } catch (NumberFormatException ignored) {
                }
            }
            trimEntriesPerMode();
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
                writer.println(entry.name + "|" + entry.score + "|" + entry.mode);
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

    private String normalizeMode(String mode) {
        if (mode == null) return MODE_ALL;
        String trimmed = mode.trim();
        if (trimmed.isEmpty()) return MODE_ALL;
        String upper = trimmed.toUpperCase();
        if ("EASY".equals(upper) || "MEDIUM".equals(upper) || "HARD".equals(upper) || MODE_ALL.equals(upper)) {
            return upper;
        }
        return MODE_ALL;
    }

    private void sortByScoreDesc(List<LeaderboardEntry> list) {
        Collections.sort(list, new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry a, LeaderboardEntry b) {
                return Integer.compare(b.score, a.score);
            }
        });
    }

    private void trimEntriesPerMode() {
        List<LeaderboardEntry> easy = new ArrayList<LeaderboardEntry>();
        List<LeaderboardEntry> medium = new ArrayList<LeaderboardEntry>();
        List<LeaderboardEntry> hard = new ArrayList<LeaderboardEntry>();
        List<LeaderboardEntry> all = new ArrayList<LeaderboardEntry>();

        for (LeaderboardEntry entry : entries) {
            if ("EASY".equals(entry.mode)) easy.add(entry);
            else if ("MEDIUM".equals(entry.mode)) medium.add(entry);
            else if ("HARD".equals(entry.mode)) hard.add(entry);
            else all.add(entry);
        }

        sortByScoreDesc(easy);
        sortByScoreDesc(medium);
        sortByScoreDesc(hard);
        sortByScoreDesc(all);

        entries.clear();
        entries.addAll(limit(easy));
        entries.addAll(limit(medium));
        entries.addAll(limit(hard));
        entries.addAll(limit(all));
    }

    private List<LeaderboardEntry> limit(List<LeaderboardEntry> list) {
        if (list.size() <= MAX_ENTRIES) {
            return list;
        }
        return new ArrayList<LeaderboardEntry>(list.subList(0, MAX_ENTRIES));
    }
}

