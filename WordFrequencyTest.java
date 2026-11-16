import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WordFrequencyTest {
    private record TestCase(String name, String input, String expected) {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: java WordFrequencyTest.java <program.java>");
        }

        List<TestCase> tests = List.of(
                new TestCase("countsCyrillicIgnoringCase", "Кот, пёс! КОТ." + "\n", "кот: 2\nпёс: 1\n"),
                new TestCase("countsEnglishIgnoringCase", "Apple APPLE apple" + "\n", "apple: 3\n"),
                new TestCase("prioritizesFrequency", "ant Zoo zoo ZOO ant" + "\n", "zoo: 3\nant: 2\n"),
                new TestCase("breaksTiesWithinFrequencyGroups", "zoo bee ant bee zoo" + "\n", "bee: 2\nzoo: 2\nant: 1\n"),
                new TestCase("sortsEnglishAlphabetically", "Zoo bee Ant" + "\n", "ant: 1\nbee: 1\nzoo: 1\n"),
                new TestCase("sortsEnglishPrefixes", "zoo atom at" + "\n", "at: 1\natom: 1\nzoo: 1\n"),
                new TestCase("sortsRussianAlphabetically", "жук ЁЖ ель я" + "\n", "ель: 1\nёж: 1\nжук: 1\nя: 1\n"),
                new TestCase("sortsYoAfterAllYeWords", "ж ея ё е" + "\n", "е: 1\nея: 1\nё: 1\nж: 1\n"),
                new TestCase("keepsYeAndYoDistinct", "всё все ВСЁ" + "\n", "всё: 2\nвсе: 1\n"),
                new TestCase("handlesYoInsideWords", "ёж жд ёл еж" + "\n", "еж: 1\nёж: 1\nёл: 1\nжд: 1\n"),
                new TestCase("splitsAtDigitsAndPunctuation", "a1b_A—b!" + "\n", "a: 2\nb: 2\n"),
                new TestCase("splitsAtRepeatedSeparators", "!?кот--\t  пёс...кот?!" + "\n", "кот: 2\nпёс: 1\n"),
                new TestCase("splitsAtCurrencySymbol", "cat$dog" + "\n", "cat: 1\ndog: 1\n"),
                new TestCase("acceptsSingleLetter", "Ё" + "\n", "ё: 1\n"),
                new TestCase("acceptsSingleWord", "JAVA" + "\n", "java: 1\n"),
                new TestCase("handlesEmptyLine", "" + "\n", ""),
                new TestCase("handlesWhitespaceOnly", " \t  " + "\n", ""),
                new TestCase("handlesDigitsAndSymbolsOnly", "123_! $" + "\n", ""),
                new TestCase("countsManyOccurrences", "word ".repeat(1000) + "end" + "\n", "word: 1000\nend: 1\n"),
                new TestCase("readsOnlyFirstLine", "first\nsecond\n", "first: 1\n"),
                new TestCase("acceptsLineWithoutFinalNewline", "Кот", "кот: 1\n"),
                new TestCase("acceptsWindowsLineEndings", "first\r\nsecond\r\n", "first: 1\n"));

        int passed = 0;
        for (TestCase test : tests) {
            String failure = run(args[0], test);
            if (failure == null) {
                passed++;
                System.out.println("PASS " + test.name());
            } else {
                System.out.println("FAIL " + test.name() + " — " + failure);
            }
        }

        System.out.printf("%d/%d passed%n", passed, tests.size());
        if (passed != tests.size()) {
            System.exit(1);
        }
    }

    private static String run(String source, TestCase test) throws Exception {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process process = new ProcessBuilder(java, source)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        try (var input = process.getOutputStream()) {
            input.write(test.input().getBytes(UTF_8));
        }
        if (!process.waitFor(15, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            return "timed out";
        }
        try (var output = process.getInputStream()) {
            String actual = new String(output.readAllBytes(), UTF_8);
            if (process.exitValue() != 0) {
                return "exit code " + process.exitValue();
            }
            return actual.equals(test.expected()) ? null
                    : "expected [" + visible(test.expected()) + "], got [" + visible(actual) + "]";
        }
    }

    private static String visible(String value) {
        return value.replace("\r", "\\r").replace("\n", "\\n");
    }
}
