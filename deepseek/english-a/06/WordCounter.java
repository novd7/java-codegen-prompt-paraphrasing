import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

public class WordCounter {

    public static void main(String[] args) throws IOException {
        // Read one line from stdin in UTF-8
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();
        if (line == null) {
            return;
        }

        // Determine which alphabet is used based on the presence of Russian letters
        boolean russian = line.codePoints()
                .anyMatch(cp -> isRussianLetter(cp));

        // Extract words (consecutive letter sequences), lowercase them
        Map<String, Long> counts = new LinkedHashMap<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); ) {
            int cp = line.codePointAt(i);
            int cpLen = Character.charCount(cp);
            if (isLetter(cp)) {
                current.appendCodePoint(Character.toLowerCase(cp));
            } else {
                if (current.length() > 0) {
                    addWord(counts, current.toString());
                    current.setLength(0);
                }
            }
            i += cpLen;
        }
        if (current.length() > 0) {
            addWord(counts, current.toString());
        }

        if (counts.isEmpty()) {
            return; // empty output
        }

        // Build comparator: Russian uses custom order (ё after е), English uses standard a-z
        Comparator<String> alphabetical = russian
                ? WordCounter::compareRussian
                : WordCounter::compareEnglish;

        // Sort: by decreasing frequency, then alphabetically
        List<Map.Entry<String, Long>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int cmp = Long.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return alphabetical.compare(a.getKey(), b.getKey());
        });

        // Output in UTF-8
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        for (Map.Entry<String, Long> e : entries) {
            writer.write(e.getKey());
            writer.write(": ");
            writer.write(Long.toString(e.getValue()));
            writer.write("\n");
        }
        writer.flush();
    }

    private static void addWord(Map<String, Long> counts, String word) {
        counts.merge(word, 1L, Long::sum);
    }

    private static boolean isLetter(int cp) {
        if (cp >= 'A' && cp <= 'Z') return true;
        if (cp >= 'a' && cp <= 'z') return true;
        // Russian А-Я, а-я
        if (cp >= '\u0410' && cp <= '\u044F') return true;
        // Ё and ё
        if (cp == '\u0401' || cp == '\u0451') return true;
        return false;
    }

    private static boolean isRussianLetter(int cp) {
        return (cp >= '\u0410' && cp <= '\u044F') || cp == '\u0401' || cp == '\u0451';
    }

    // English alphabetical: a-z
    private static int compareEnglish(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            char ca = a.charAt(i);
            char cb = b.charAt(i);
            if (ca != cb) {
                return Character.compare(ca, cb);
            }
        }
        return Integer.compare(a.length(), b.length());
    }

    // Russian alphabetical: а б в г д е ё ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я
    private static int compareRussian(String a, String b) {
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            int ca = russianRank(a.charAt(i));
            int cb = russianRank(b.charAt(i));
            if (ca != cb) {
                return Integer.compare(ca, cb);
            }
        }
        return Integer.compare(a.length(), b.length());
    }

    private static int russianRank(char c) {
        // 'ё' comes immediately after 'е'
        if (c == '\u0451') return 6; // after е (index 5)
        if (c >= '\u0430' && c <= '\u0435') return c - '\u0430';         // а..е -> 0..5
        if (c >= '\u0436' && c <= '\u044F') return c - '\u0436' + 7;     // ж..я -> 7..32
        // fallback (should not happen)
        return 1000 + c;
    }
}