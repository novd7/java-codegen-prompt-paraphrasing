import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    // Порядок русских букв: а, б, в, г, д, е, ё, ж, з, и, й, к, л, м, н, о, п, р, с, т, у, ф, х, ц, ч, ш, щ, ъ, ы, ь, э, ю, я
    private static final String RUSSIAN_ORDER = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    public static void main(String[] args) {
        String line = readLineFromStdin();
        if (line == null || line.isEmpty()) {
            return;
        }

        // Регулярное выражение для букв одного из двух алфавитов.
        // Символ | внутри класса [] не работает как ИЛИ, поэтому перечисляем все буквы.
        Pattern wordPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");
        Matcher matcher = wordPattern.matcher(line);

        Map<String, Integer> counts = new HashMap<>();
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
                .sorted(getComparator())
                .toList();

        String output = sorted.stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n")) + "\n";

        // Вывод в UTF-8
        System.out.print(output);
    }

    private static String readLineFromStdin() {
        Charset cs = Charset.defaultCharset();
        try {
            cs = Console.charset();
        } catch (Exception ignored) {
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, cs))) {
            return reader.readLine();
        } catch (Exception e) {
            throw new UncheckedIOException(new java.io.IOException(e));
        }
    }

    private static Comparator<Map.Entry<String, Integer>> getComparator() {
        return (e1, e2) -> {
            // 1. Сортировка по убыванию количества
            int freqCompare = Integer.compare(e2.getValue(), e1.getValue());
            if (freqCompare != 0) {
                return freqCompare;
            }

            String w1 = e1.getKey();
            String w2 = e2.getKey();

            boolean isRus1 = isRussian(w1);
            boolean isRus2 = isRussian(w2);

            // 2. Сортировка по алфавиту внутри одного языка
            if (isRus1 && isRus2) {
                return compareRussian(w1, w2);
            }
            if (!isRus1 && !isRus2) {
                return w1.compareTo(w2);
            }

            // 3. Разные алфавиты: по условию задачи они не смешиваются во входных данных,
            // но для детерминизма выведем английские слова раньше русских.
            return isRus1 ? 1 : -1;
        };
    }

    private static boolean isRussian(String word) {
        // Если есть хотя бы одна буква из расширенного русского диапазона, считаем слово русским
        return word.codePoints().anyMatch(cp -> (cp >= 0x0430 && cp <= 0x044F) || cp == 0x0451);
    }

    private static int compareRussian(String w1, String w2) {
        int len = Math.min(w1.length(), w2.length());
        for (int i = 0; i < len; i++) {
            char c1 = w1.charAt(i);
            char c2 = w2.charAt(i);
            if (c1 != c2) {
                int idx1 = RUSSIAN_ORDER.indexOf(c1);
                int idx2 = RUSSIAN_ORDER.indexOf(c2);
                // В строке RUSSIAN_ORDER буквы уже стоят в нужном порядке
                return Integer.compare(idx1, idx2);
            }
        }
        return Integer.compare(w1.length(), w2.length());
    }
}