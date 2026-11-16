import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            line = reader.readLine();
        } catch (Exception e) {
            throw new UncheckedIOException("Failed to read input", e);
        }

        if (line == null || line.isEmpty()) {
            return;
        }

        // Регулярное выражение: только латиница и кириллица (включая Ё/ё). 
        // Небуквенные символы (цифры, знаки препинания, пробелы) служат разделителями.
        Pattern wordPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");
        Matcher matcher = wordPattern.matcher(line);

        Map<String, Integer> counts = new LinkedHashMap<>();
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        // Collator для русского языка: 'ё' идет после 'е'
        Collator ruCollator = Collator.getInstance(new Locale("ru", "RU"));
        ruCollator.setStrength(Collator.PRIMARY);

        // Collator для английского языка
        Collator enCollator = Collator.getInstance(new Locale("en", "US"));
        enCollator.setStrength(Collator.PRIMARY);

        Comparator<String> wordComparator = (w1, w2) -> {
            boolean isEn1 = isEnglish(w1);
            boolean isEn2 = isEnglish(w2);

            // Слова разных алфавитов не сравниваем между собой,
            // английские всегда идут раньше русских.
            if (isEn1 && !isEn2) {
                return -1;
            }
            if (!isEn1 && isEn2) {
                return 1;
            }

            // Внутри одного алфавита используем соответствующий Collator
            if (isEn1) {
                return enCollator.compare(w1, w2);
            } else {
                return ruCollator.compare(w1, w2);
            }
        };

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Comparator
                .comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed()
                .thenComparing(Map.Entry::getKey, wordComparator));

        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, Integer> entry : entries) {
            output.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append('\n');
        }

        // Вывод в UTF-8
        System.out.print(output.toString());
    }

    // Проверка, состоит ли слово только из английских букв
    private static boolean isEnglish(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c < 'a' || c > 'z') {
                return false;
            }
        }
        return true;
    }
}