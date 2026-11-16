import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordCounter {
    
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(
            new java.io.OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
        
        String line = reader.readLine();
        if (line == null) {
            return;
        }
        
        // Detect alphabet by scanning for the first letter
        boolean isRussian = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isRussianLetter(c)) {
                isRussian = true;
                break;
            } else if (isEnglishLetter(c)) {
                isRussian = false;
                break;
            }
        }
        
        // Count words
        Map<String, Integer> counts = new HashMap<>();
        int i = 0;
        int len = line.length();
        while (i < len) {
            // Skip non-letters
            while (i < len && !isLetter(line.charAt(i))) {
                i++;
            }
            if (i >= len) break;
            int start = i;
            while (i < len && isLetter(line.charAt(i))) {
                i++;
            }
            String word = line.substring(start, i).toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }
        
        // Sort
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        
        Collator collator = Collator.getInstance(isRussian ? new Locale("ru") : Locale.ENGLISH);
        collator.setStrength(Collator.PRIMARY);
        // For Russian, ensure ё sorts immediately after е
        if (isRussian) {
            collator = Collator.getInstance(new Locale("ru", "RU"));
            collator.setStrength(Collator.TERTIARY);
            collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        }
        
        final Collator finalCollator = collator;
        entries.sort((a, b) -> {
            int cmp = b.getValue().compareTo(a.getValue());
            if (cmp != 0) return cmp;
            return finalCollator.compare(a.getKey(), b.getKey());
        });
        
        for (Map.Entry<String, Integer> e : entries) {
            writer.print(e.getKey());
            writer.print(": ");
            writer.print(e.getValue());
            writer.print('\n');
        }
        writer.flush();
    }
    
    private static boolean isRussianLetter(char c) {
        return (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') || c == 'Ё' || c == 'ё';
    }
    
    private static boolean isEnglishLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
    
    private static boolean isLetter(char c) {
        return isRussianLetter(c) || isEnglishLetter(c);
    }
}