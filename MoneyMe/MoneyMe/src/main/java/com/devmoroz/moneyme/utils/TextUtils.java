package com.devmoroz.moneyme.utils;


import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

import com.devmoroz.moneyme.models.Todo;

import java.util.Locale;


public class TextUtils {

    public static String parseContent(Todo todo) {

        final int CONTENT_SUBSTRING_LENGTH = 300;
        String UNCHECKED_SYM = "[ ] ";
        String CHECKED_SYM = "[x] ";
        String UNCHECKED_ENTITY = "&EmptySmallSquare; ";
        String CHECKED_ENTITY = "&#x2713; ";

        // Defining title and content texts
        String contentText = limit(todo.getContent().trim(), 0, CONTENT_SUBSTRING_LENGTH, false, true);

        // Replacing checkmarks symbols with html entities
        Spanned contentSpanned;

        if (todo.isCheckList()) {
            contentText = contentText
                    .replace(CHECKED_SYM, CHECKED_ENTITY)
                    .replace(UNCHECKED_SYM, UNCHECKED_ENTITY)
                    .replace(System.getProperty("line.separator"), "<br/>");
            contentSpanned = Html.fromHtml(contentText);
        }else {
            contentSpanned = new SpannedString(contentText);
        }

        return contentSpanned.toString();
    }

    public static String limit(String value, int start, int length, boolean singleLine, boolean elipsize) {
        if (start > value.length()) {
            return null;
        }
        StringBuilder buf = new StringBuilder(value.substring(start));
        int indexNewLine = buf.indexOf(System.getProperty("line.separator"));
        int endIndex = singleLine && indexNewLine < length ? indexNewLine : length < buf.length() ? length : -1;
        if (endIndex != -1) {
            buf.setLength(endIndex);
            if (elipsize) {
                buf.append("...");
            }
        }
        return buf.toString();
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase(Locale.getDefault()) + string.substring(1,
                string.length()).toLowerCase(Locale.getDefault());
    }
}
