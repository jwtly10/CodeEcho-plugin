package jwtly10.codeecho.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class ParserService {
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
