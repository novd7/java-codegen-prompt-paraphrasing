import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordCounter {
    
    private static final String RUSSIAN_ORDER = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final Map<Character, Integer> RUSSIAN_INDEX = new HashMap<>();
    
    static {
        for (int i = 0; i < RUSSIAN_ORDER.length(); i++) {
            RUSSIAN_INDEX.put(RUSSIAN_ORDER.charAt(i), i);
        }
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
    
    private static int russianCompare(String a, String b) {
        int minLen = Math.min(a.length(), b.length());
        for (int i = 0; i < minLen; i++) {
            int ai = RUSSIAN_INDEX.getOrDefault(a.charAt(i), Integer.MAX_VALUE);
            int bi = RUSSIAN_INDEX.getOrDefault(b.charAt(i), Integer.MAX_VALUE);
            if (ai != bi) return Integer.compare(ai, bi);
        }
        return Integer.compare(a.length(), b.length());
    }
    
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(
            new java.io.OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        
        String line = reader.readLine();
        if (line == null) line = "";
        
        Map<String, Integer> counts = new HashMap<>();
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                current.append(Character.toLowerCase(c));
            } else {
                if (current.length() > 0) {
                    String word = current.toString();
                    counts.merge(word, 1, Integer::sum);
                    current.setLength(0);
                }
            }
        }
        if (current.length() > 0) {
            String word = current.toString();
            counts.merge(word, 1, Integer::sum);
        }
        
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getValue(), e1.getValue());
            if (cmp != 0) return cmp;
            // alphabetical
            String w1 = e1.getKey();
            String w2 = e2.getKey();
            boolean r1 = !w1.isEmpty() && RUSSIAN_INDEX.containsKey(w1.charAt(0));
            boolean r2 = !w2.isEmpty() && RUSSIAN_INDEX.containsKey(w2.charAt(0));
            if (r1 && r2) return russianCompare(w1, w2);
            return w1.compareTo(w2);
        });
        
        for (Map.Entry<String, Integer> e : entries) {
            writer.print(e.getKey() + ": " + e.getValue() + "\n");
        }
        writer.flush();
    }
}