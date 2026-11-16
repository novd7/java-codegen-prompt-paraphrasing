import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    // Регулярное выражение для поиска слов (непрерывные последовательности букв)
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-zА-Яа-яЁё]+");

    public static void main(String[] args) throws IOException {
        // Читаем одну строку из System.in в UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            return;
        }

        // Подсчитываем слова
        Map<String, Integer> counts = new HashMap<>();
        Matcher matcher = WORD_PATTERN.matcher(line);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase(StandardCharsets.UTF_8);
            counts.merge(word, 1, Integer::sum);
        }

        if (counts.isEmpty()) {
            return;
        }

        // Сортируем и выводим
        List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
                .sorted(getEntryComparator())
                .collect(Collectors.toList());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        for (Map.Entry<String, Integer> entry : sorted) {
            writer.write(entry.getKey() + ": " + entry.getValue());
            writer.newLine();
        }
        writer.flush();
    }

    private static Comparator<Map.Entry<String, Integer>> getEntryComparator() {
        return Comparator.<Map.Entry<String, Integer>>comparingInt((Map.Entry<String, Integer> e) -> -e.getValue())
                .thenComparing(Main::compareWordsAlphabetically);
    }

    // Сравнение слов по правилам задачи (с учетом специфики 'ё' и разделения алфавитов)
    private static int compareWordsAlphabetically(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
        String w1 = e1.getKey();
        String w2 = e2.getKey();

        boolean isCyr1 = isCyrillic(w1);
        boolean isCyr2 = isCyrillic(w2);

        // Если оба слова из одного алфавита — сравниваем по правилам этого алфавита
        if (isCyr1 && isCyr2) {
            return compareCyrillic(w1, w2);
        }
        if (!isCyr1 && !isCyr2) {
            return w1.compareTo(w2);
        }

        // Если слова из разных алфавитов, но частоты равны (этот метод вызывается только при равных частотах),
        // для детерминизма выведем русские слова раньше английских.
        return isCyr1 ? -1 : 1;
    }

    private static boolean isCyrillic(String s) {
        // Проверяем наличие хотя бы одного символа кириллицы
        return s.codePoints().anyMatch(cp -> cp >= 0x0400 && cp <= 0x04FF);
    }

    // Сортировка русских слов с учетом правила: е < ё < ж
    private static int compareCyrillic(String s1, String s2) {
        int len = Math.min(s1.length(), s2.length());
        for (int i = 0; i < len; i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 == c2) continue;

            int order1 = getCyrillicOrder(c1);
            int order2 = getCyrillicOrder(c2);

            if (order1 != order2) {
                return Integer.compare(order1, order2);
            }
        }
        return Integer.compare(s1.length(), s2.length());
    }

    // Возвращает порядковый номер буквы в русском алфавите, где 'ё' идет после 'е'
    private static int getCyrillicOrder(char c) {
        switch (c) {
            case 'а': return 1;
            case 'б': return 2;
            case 'в': return 3;
            case 'г': return 4;
            case 'д': return 5;
            case 'е': return 6;
            case 'ё': return 7;
            case 'ж': return 8;
            case 'з': return 9;
            case 'и': return 10;
            case 'й': return 11;
            case 'к': return 12;
            case 'л': return 13;
            case 'м': return 14;
            case 'н': return 15;
            case 'о': return 16;
            case 'п': return 17;
            case 'р': return 18;
            case 'с': return 19;
            case 'т': return 20;
            case 'у': return 21;
            case 'ф': return 22;
            case 'х': return 23;
            case 'ц': return 24;
            case 'ч': return 25;
            case 'ш': return 26;
            case 'щ': return 27;
            case 'ъ': return 28;
            case 'ы': return 29;
            case 'ь': return 30;
            case 'э': return 31;
            case 'ю': return 32;
            case 'я': return 33;
            default: return 0; // На случай, если в строке окажутся другие символы (хотя по условию слова состоят только из букв)
        }
    }
}