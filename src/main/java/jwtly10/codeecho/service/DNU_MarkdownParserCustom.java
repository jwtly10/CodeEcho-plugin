package jwtly10.codeecho.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jwtly10.codeecho.model.SectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jwtly10.codeecho.model.SectionType.SecType;

/* This class is a custom markdown parser that parses markdown into sections of text and code blocks.
 * It uses a regular expression to match the code blocks and then separates the text and code blocks
 * into different sections.
 * This parser version tries to handle the case when code is being streamed and we have not closed
 * the code block yet. However it needs more work and instead we have a work around in place. TODO
 * */
public class DNU_MarkdownParserCustom {
    private static final Pattern pattern = Pattern.compile("(.*?)(```(\\w*)\\n(.*?))(?=```|$)", Pattern.DOTALL);

    public List<SectionType> parseMarkdown(String markdown) {
        List<SectionType> sections = new ArrayList<>();
        Matcher matcher = pattern.matcher(markdown);
        int start = 0;

        while (matcher.find(start)) {
            // Capturing text section before a code block
            String textSection = matcher.group(1);
            if (!textSection.isEmpty()) {
                sections.add(new SectionType(SecType.TEXT, textSection.trim()));
            }

            String language = matcher.group(3).isEmpty() ? null : matcher.group(3);
            String codeContent = matcher.group(4);

            // Check if the codeContent is not null, which means we have a code block
            if (codeContent != null && !codeContent.isEmpty()) {
                sections.add(new SectionType(SecType.CODE, codeContent.trim(), language));
            }

            // Move start to the end of the current match to find the next one
            start = matcher.end(2);
        }

        // If there's any text left after the last code block (or if no code block was found)
        // Add it as a text section
        if (start < markdown.length()) {
            String remainingText = markdown.substring(start).trim();
            if (!remainingText.isEmpty()) {
                sections.add(new SectionType(SecType.TEXT, remainingText));
            }
        }

        return sections;
    }

    // Placeholder for markdownToHtml function
    public static String markdownToHtml(String markdownText) {
        MutableDataSet options = new MutableDataSet();
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // TODO: Figure out how to horizontal scroll the code block instead of wrapping
        String css = "<style type=\"text/css\">"
                + "p { margin: 0; padding: 0; }"
                + "pre { background-color: #000000; padding: 2px 4px; }"
                + "pre, code { white-space: pre-wrap; word-wrap: break-word; }"
                + "</style>";
        String content = renderer.render(parser.parse(markdownText));
        return css + content;
    }
}