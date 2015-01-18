import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiWordTokenizer implements Enumeration<String> {
    public static final String[] STOP_WORDS = new String[] { "this", "the", "that",
            "are", "was", "will", "and", "not", "all" };
    public static final Set<String> STOP_WORDS_SET = new HashSet<String>(Arrays.asList(STOP_WORDS));

    private Pattern wordPattern;
    private Matcher wordMatcher;

    private boolean hasNextWord = false;
    private String nextWord = null;

    private void next() {
        while (hasNextWord = wordMatcher.find()) {
            nextWord = wordMatcher.group();
            nextWord = nextWord.toLowerCase();
            if (!Character.isLetter(nextWord.charAt(0)) ||
                    Character.isDigit(nextWord.charAt(0)) ||
                    STOP_WORDS_SET.contains(nextWord) ||
                    nextWord.contains("_") ||
                    nextWord.length() < 3) {
                continue;
            }
            break;
        }
    }

    public WikiWordTokenizer(String source) {
        // would match all letters and all digits from any languages
        // (and of course some word combining characters like _)
        // work since Java 7.
        this(source, Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS));

    }

    public WikiWordTokenizer(String source, Pattern pattern) {
        wordPattern = pattern;
        wordMatcher = wordPattern.matcher(source);
        next();
    }

    @Override
    public boolean hasMoreElements() {
        return hasNextWord;
    }

    @Override
    public String nextElement() {
        if (!hasNextWord) throw new NoSuchElementException();
        String element = nextWord;
        next();
        return element;
    }
}