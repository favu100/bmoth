package de.bmoth.app;

/*
 *Original Code from https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywords.java
 */

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Highlighter {
    private static final String[] START = new String[]{
        "MACHINE"
    };
    private static final String[] KEYWORDS = new String[]{
        "VARIABLES", "DEFINITIONS", "INVARIANT", "INITIALISATION", "OPERATIONS", "CONSTANTS", "PROPERTIES"
    };
    private static final String[] KEYWORDS2 = new String[]{
        "END", "SELECT", "THEN", "BEGIN", "WHERE", "ANY"
    };
    private static final String START_PATTERN = "\\b(" + String.join("|", START) + ")\\b";
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String KEYWORD2_PATTERN = "\\b(" + String.join("|", KEYWORDS2) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<START>" + START_PATTERN + ")"
            + "|(?<KEYWORD2>" + KEYWORD2_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private Highlighter() {
    }

    protected static StyleSpans<Collection<String>> computeHighlighting(String text) {
        String[] groups = new String[]{"START", "KEYWORD", "KEYWORD2", "PAREN", "BRACE", "BRACKET", "SEMICOLON", "STRING", "COMMENT"};
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
            = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = null; /* never happens */
            for (String group : groups) {
                if (matcher.group(group) != null) {
                    styleClass = group.toLowerCase();
                    break;
                }
            }
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
