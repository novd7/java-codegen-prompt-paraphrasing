import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordCounter {

    // Порядок букв в русском алфавите, где 'ё' идет сразу после 'е'
    private static final String RUSSIAN_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    public static void main(String[] args) throws IOException {
        // Читаем одну строку строго в UTF-8
        try (var reader = new InputStreamReader(System.in, StandardCharsets.UTF_8)) {
            StringBuilder inputBuilder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1 && c != '\n') {
                inputBuilder.append((char) c);
            }
            String input = inputBuilder.toString();

            // Находим все слова (непрерывные последовательности букв)
            Pattern pattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");
            Matcher matcher = pattern.matcher(input);

            Map<String, Integer> counts = new HashMap<>();
            boolean hasEnglish = false;
            boolean hasRussian = false;

            while (matcher.find()) {
                String word = matcher.group().toLowerCase(Locale.ROOT);
                counts.merge(word, 1, Integer::sum);

                // Определяем алфавит первого символа для глобальной проверки
                char first = word.charAt(0);
                if (first >= 'a' && first <= 'z') {
                    hasEnglish = true;
                } else if ((first >= 'а' && first <= 'я') || first == 'ё') {
                    hasRussian = true;
                }
            }

            // Сортируем и выводим в UTF-8
            try (var writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8)) {
                if (counts.isEmpty()) {
                    // Если слов нет, оставляем вывод пустым
                    return;
                }

                // Сортируем по убыванию частоты, затем по алфавиту с учетом языка
                List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
                        .sorted((e1, e2) -> {
                            int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
                            if (freqCompare != 0) {
                                return freqCompare;
                            }
                            return compareWords(e1.getKey(), e2.getKey(), hasEnglish, hasRussian);
                        })
                        .toList();

                for (Map.Entry<String, Integer> entry : sorted) {
                    writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
                }
            }
        }
    }

    /**
     * Сравнивает два слова согласно правилам задачи.
     * Если алфавиты разные — русские слова идут раньше английских.
     * Если алфавит один — сравниваем по правилам этого алфавита.
     */
    private static int compareWords(String w1, String w2, boolean hasEnglish, boolean hasRussian) {
        boolean w1Rus = isRussian(w1);
        boolean w2Rus = isRussian(w2);

        if (w1Rus && !w2Rus) {
            return -1;
        }
        if (!w1Rus && w2Rus) {
            return 1;
        }

        // Алфавиты совпадают
        int len = Math.min(w1.length(), w2.length());
        for (int i = 0; i < len; i++) {
            char c1 = w1.charAt(i);
            char c2 = w2.charAt(i);
            if (c1 != c2) {
                if (w1Rus) {
                    return Integer.compare(indexInRussian(c1), indexInRussian(c2));
                } else {
                    return Character.compare(c1, c2);
                }
            }
        }
        return Integer.compare(w1.length(), w2.length());
    }

    private static boolean isRussian(String word) {
        char first = word.charAt(0);
        return (first >= 'а' && first <= 'я') || first == 'ё';
    }

    private static int indexInRussian(char c) {
        // Приводим к нижнему регистру, так как слова уже в нем, но на всякий случай
        c = Character.toLowerCase(c);
        int idx = RUSSIAN_ALPHABET.indexOf(c);
        // В нормальной ситуации символ всегда найдется, но для защиты от NPE
        return idx == -1 ? Integer.MAX_VALUE : idx;
    }
}