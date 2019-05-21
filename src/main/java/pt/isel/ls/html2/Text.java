package pt.isel.ls.html2;

import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.Writer;

public class Text extends Node {

    private final String text;

    public Text(String text) {

        this.text = text;
    }

    @Override
    public void writeTo(Writer writer) throws IOException {
        writer.write(StringEscapeUtils.escapeHtml4(text));
    }
}
