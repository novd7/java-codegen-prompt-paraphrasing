import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordCount {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String line = reader.readLine();
        if (line == null) {
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isLetter(c)) {
                word.append(Character.toLowerCase(c));
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
            return compareAlphabetically(a.getKey(), b.getKey());
        });

        for (Map.Entry<String, Integer> e : entries) {
            out.print(e.getKey() + ": " + e.getValue() + "\n");
        }
        out.flush();
    }

    private static void addWord(Map<String, Integer> counts, String w) {
        counts.merge(w, 1, Integer::sum);
    }

    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я')
                || c == 'Ё' || c == 'ё';
    }

    private static int compareAlphabetically(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            int cmp = Integer.compare(weight(a.charAt(i)), weight(b.charAt(i)));
            if (cmp != 0) return cmp;
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int weight(char c) {
        switch (c) {
            case 'ё': return 'е' + 1;         // ё идёт сразу после е
            case 'е': return 'е';
            case 'а': case 'б': case 'в': case 'г': case 'д': case 'ж':
            case 'з': case 'и': case 'й': case 'к': case 'л': case 'м':
            case 'н': case 'о': case 'п': case 'р': case 'с': case 'т':
            case 'у': case 'ф': case 'х': case 'ц': case 'ч': case 'ш':
            case 'щ': case 'ъ': case 'ы': case 'ь': case 'э': case 'ю':
            case 'я':
                return c;
            default:
                return c;
        }
    }
}