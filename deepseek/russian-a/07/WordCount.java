import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

public class WordCount {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();
        StringBuilder out = new StringBuilder();

        if (line != null) {
            // Определяем алфавит по первой букве
            boolean isRussian = false;
            boolean determined = false;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (Character.isLetter(c)) {
                    isRussian = isRussianLetter(c);
                    determined = true;
                    break;
                }
            }

            if (determined) {
                final boolean russian = isRussian;
                Map<String, Integer> counts = new HashMap<>();
                StringBuilder word = new StringBuilder();

                for (int i = 0; i <= line.length(); i++) {
                    char c = (i < line.length()) ? line.charAt(i) : ' ';
                    boolean isLetter = (i < line.length()) &&
                            (russian ? isRussianLetter(c) : isEnglishLetter(c));
                    if (isLetter) {
                        word.append(Character.toLowerCase(c));
                    } else {
                        if (word.length() > 0) {
                            String w = word.toString();
                            counts.merge(w, 1, Integer::sum);
                            word.setLength(0);
                        }
                    }
                }

                List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
                Comparator<Map.Entry<String, Integer>> cmp = (a, b) -> {
                    int byCount = Integer.compare(b.getValue(), a.getValue());
                    if (byCount != 0) return byCount;
                    return russian ? compareRussian(a.getKey(), b.getKey())
                                   : a.getKey().compareTo(b.getKey());
                };
                entries.sort(cmp);

                for (Map.Entry<String, Integer> e : entries) {
                    out.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
                }
            }
        }

        System.out.write(out.toString().getBytes(StandardCharsets.UTF_8));
        System.out.flush();
    }

    private static boolean isRussianLetter(char c) {
        return (c >= 'А' && c <= 'я') || c == 'Ё' || c == 'ё';
    }

    private static boolean isEnglishLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private static int compareRussian(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            int diff = russianIndex(a.charAt(i)) - russianIndex(b.charAt(i));
            if (diff != 0) return diff;
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int russianIndex(char c) {
        // Порядок: а..е (0..5), ё (6), ж..я (7..)
        if (c == 'ё') return 6;
        if (c >= 'а' && c <= 'е') return c - 'а';
        if (c >= 'ж' && c <= 'я') return c - 'а' + 1;
        return 1000;
    }
}