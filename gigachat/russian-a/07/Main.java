import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    // Русский алфавит в нижнем регистре, где 'ё' идет сразу после 'е'
    private static final char[] РУССКИЙ_АЛФАВИТ = {
            'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я'
    };

    public static void main(String[] args) throws IOException {
        // Читаем одну строку из System.in в UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            return;
        }

        // Регулярное выражение: одна или более букв (русских или английских)
        Pattern wordPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");
        Matcher matcher = wordPattern.matcher(line);

        Map<String, Integer> counts = new HashMap<>();
        Set<Character> usedAlphabets = new HashSet<>();

        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            counts.merge(word, 1, Integer::sum);
            if (!word.isEmpty()) {
                usedAlphabets.add(Character.toLowerCase(word.charAt(0)));
            }
        }

        if (counts.isEmpty()) {
            return;
        }

        // Определяем, какие алфавиты встретились
        boolean hasRussian = usedAlphabets.stream().anyMatch(ch -> ch >= 'а' && ch <= 'я' || ch == 'ё');
        boolean hasEnglish = usedAlphabets.stream().anyMatch(ch -> ch >= 'a' && ch <= 'z');

        Comparator<String> comparator = (s1, s2) -> {
            int freq1 = counts.get(s1);
            int freq2 = counts.get(s2);

            // 1. Сортировка по убыванию частоты
            if (freq1 != freq2) {
                return Integer.compare(freq2, freq1);
            }

            // 2. Если частоты равны, проверяем алфавиты
            boolean isRus1 = isRussian(s1);
            boolean isRus2 = isRussian(s2);

            // По условию алфавитный порядок проверяется отдельно для русских и английских слов.
            // Если слова из разных алфавитов, их относительный порядок не определен спецификацией.
            // Для детерминированности вывода сгруппируем их: сначала английские, затем русские.
            if (isRus1 != isRus2) {
                return Boolean.compare(isRus1, isRus2);
            }

            // 3. Внутри одного алфавита сравниваем по правилам этого алфавита
            if (isRus1) {
                return compareRussian(s1, s2);
            } else {
                return s1.compareTo(s2);
            }
        };

        // Сортируем и выводим в UTF-8
        List<String> sortedWords = counts.keySet().stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        for (String word : sortedWords) {
            writer.write(word + ": " + counts.get(word));
            writer.newLine();
        }
        writer.flush();
    }

    private static boolean isRussian(String word) {
        return word.codePoints()
                .anyMatch(cp -> (cp >= 'а' && cp <= 'я') || cp == 'ё' || cp == 'Ё');
    }

    private static int compareRussian(String s1, String s2) {
        int len = Math.min(s1.length(), s2.length());
        for (int i = 0; i < len; i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 != c2) {
                int idx1 = indexInRussianAlphabet(c1);
                int idx2 = indexInRussianAlphabet(c2);
                return Integer.compare(idx1, idx2);
            }
        }
        return Integer.compare(s1.length(), s2.length());
    }

    private static int indexInRussianAlphabet(char c) {
        // Приводим к нижнему регистру для поиска, так как в РУССКИЙ_АЛФАВИТ только строчные
        char lower = Character.toLowerCase(c);
        for (int i = 0; i < РУССКИЙ_АЛФАВИТ.length; i++) {
            if (РУССКИЙ_АЛФАВИТ[i] == lower) {
                return i;
            }
        }
        // Технически недостижимо при корректном вводе по условию
        return Character.codePointAt(new String(new char[]{c}), 0);
    }
}