import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordCount {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();
        if (line == null) line = "";

        // Comparator for letters: case-insensitive, with 'ё' right after 'е' for Russian
        Comparator<Character> letterOrder = (a, b) -> {
            char ca = Character.toLowerCase(a);
            char cb = Character.toLowerCase(b);
            if (ca == cb) return 0;
            // Handle 'ё' vs 'е'
            if (ca == 'ё' && cb == 'е') return 1;
            if (ca == 'е' && cb == 'ё') return -1;
            // Compare via code points to keep 'ё' after 'е' in Russian.
            // For Russian letters, code points of 'е' (0x0435) and 'ё' (0x0451)
            // are already in the right order except for the above cases,
            // because 'ё' (0x0451) > 'ж' (0x0436)... wait, no:
            // 'е' = 0x0435, 'ж'=0x0436,...,'я'=0x044F, 'ё'=0x0451.
            // So natural code point order puts 'ё' after 'я'. We must fix that.
            // Correct Russian order: е, ё, ж, з, ...
            if (isRussianLower(ca) && isRussianLower(cb)) {
                int ia = russianIndex(ca);
                int ib = russianIndex(cb);
                return Integer.compare(ia, ib);
            }
            return Character.compare(ca, cb);
        };

        Comparator<String> wordOrder = (s1, s2) -> {
            int minLen = Math.min(s1.length(), s2.length());
            for (int i = 0; i < minLen; i++) {
                int cmp = letterOrder.compare(s1.charAt(i), s2.charAt(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(s1.length(), s2.length());
        };

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                word.append(c);
            } else {
                if (word.length() > 0) {
                    addWord(counts, word.toString());
                    word.setLength(0);
                }
            }
        }
        if (word.length() > 0) {
            addWord(counts, word.toString());
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return wordOrder.compare(a.getKey(), b.getKey());
        });

        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, Integer> e : entries) {
            out.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
        }
        System.out.write(out.toString().getBytes(StandardCharsets.UTF_8));
        System.out.flush();
    }

    static void addWord(Map<String, Integer> counts, String w) {
        String lower = w.toLowerCase(Locale.ROOT);
        counts.merge(lower, 1, Integer::sum);
    }

    static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я')
                || c == 'Ё' || c == 'ё';
    }

    static boolean isRussianLower(char c) {
        return (c >= 'а' && c <= 'я') || c == 'ё';
    }

    // Index in Russian alphabet: а=0, б=1, ..., е=5, ё=6, ж=7, ...
    static int russianIndex(char c) {
        if (c == 'ё') return 6;
        if (c >= 'а' && c <= 'е') return c - 'а';       // а..е -> 0..5
        if (c >= 'ж' && c <= 'я') return c - 'а' + 1;   // ж..я -> 7..32
        return -1;
    }
}