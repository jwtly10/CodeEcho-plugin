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

public class ParserService {
    private static final Pattern codeBlockPattern = Pattern.compile("```(\\w*)\\n(.*?)```", Pattern.DOTALL);

    /**
     * Converts a markdown string into an HTML string, for rendering in rich text formats
     * and consistent styling
     *
     * @param markdownText The markdown string to convert
     * @return The HTML string
     */
    public static String markdownToHtml(String markdownText) {
        MutableDataSet options = new MutableDataSet();
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        String css = "<style type=\"text/css\">"
                + "p { margin: 0; padding: 0;}"
                + "pre, code { white-space: pre-wrap; word-wrap: break-word; }"
                + "body { font-family: Arial; font-size: 11px;}"
                + "</style>";
        String content = renderer.render(parser.parse(markdownText));
        return css + content;
    }

    /**
     * Parses a markdown string into a list of SectionType objects, this allows
     * for the markdown to be split into sections of code and text
     * so the code can be formatted and rendered differently to the plain text
     *
     * @param markdown The markdown string to parse
     * @return A list of SectionType objects
     */
    public List<SectionType> parseMarkdownIntoSections(String markdown) {
        markdown = markdown.strip();
        List<SectionType> sections = new ArrayList<>();
        Matcher matcher = codeBlockPattern.matcher(markdown);

        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String text = markdown.substring(lastEnd, matcher.start());
                text = markdownToHtml(text);
                sections.add(new SectionType(SecType.TEXT, text));
            }

            String language = matcher.group(1).isEmpty() ? null : matcher.group(1);
            String codeContent = matcher.group(2);
            sections.add(new SectionType(SecType.CODE, codeContent, language));
            lastEnd = matcher.end();
        }

        if (lastEnd < markdown.length()) {
            String text = markdown.substring(lastEnd);
            text = markdownToHtml(text);
            sections.add(new SectionType(SecType.TEXT, text));
        }

        return sections;
    }
}
