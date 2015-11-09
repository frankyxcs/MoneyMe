package com.devmoroz.moneyme.utils.csv;


import android.nfc.FormatException;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Csv {
    public static class Reader {
        private BufferedReader reader;

        private char delimiter = ';';
        private boolean ignoreSpaces = true;
        private boolean ignoreEmptyLines = false;

        public Reader(java.io.Reader reader) { this.reader = new BufferedReader(reader); }

        public  List<String> readLine() {
            String line;
            try {
                line = reader.readLine();
            } catch (java.io.IOException e) { throw new IOException(e); }
            if (line == null) return null;
            if (!ignoreSpaces) line = removeLeadingSpaces(line);
            if (ignoreEmptyLines && line.length() == 0) return readLine();

            List<String> result = new ArrayList<>();

            while (line != null) {
                String token = "";
                int nextDelimiterIndex = line.indexOf(delimiter);

                if (nextDelimiterIndex == -1) {
                    token += line;
                    line = null;
                } else {
                    token += line.substring(0, nextDelimiterIndex);
                    line = line.substring(nextDelimiterIndex + 1, line.length());
                }

                result.add(format(token));
            }

            return result;
        }

        public void close() {
            try {
                reader.close();
            } catch (java.io.IOException e) { throw new IOException(e); }
        }

        private String format(String s) {
            String result = s;
            if (!ignoreSpaces) result = result.trim();
            if (result.endsWith("\"")) result = result.substring(0, result.length() - 2);
            if (result.startsWith("\"")) result = result.substring(1, result.length() - 1);
            return result;
        }

        private String removeLeadingSpaces(String s) { return s.replaceFirst(" +", ""); }

        public Reader delimiter(char delimiter) { this.delimiter = delimiter; return this; }
        public Reader ignoreSpaces(boolean ignoreSpaces) { this.ignoreSpaces = ignoreSpaces; return this; }
        public Reader ignoreEmptyLines(boolean ignoreEmptyLines) { this.ignoreEmptyLines = ignoreEmptyLines; return this; }
    }

    public static class Writer {
        private Appendable appendable;

        private char delimiter = ';';

        private boolean first = true;

        public Writer(String fileName) { this(new File(fileName)); }
        public Writer(File file) {
            try {
                appendable = new FileWriter(file);
            } catch (java.io.IOException e) { throw new IOException(e); }
        }
        public Writer(Appendable appendable) { this.appendable = appendable; }

        public Writer value(String value) {
            if (!first) string("" + delimiter);
            string(escape(value));
            first = false;
            return this;
        }

        public Writer newLine() {
            first = true;
            return string("\n");
        }

        public Writer comment(String comment) {
            if (!first) throw new FormatException("invalid csv: misplaced comment");
            return string("#").string(comment).newLine();
        }

        public Writer flush() {
            try {
                if (appendable instanceof Flushable) {
                    Flushable flushable = (Flushable) appendable;
                    flushable.flush();
                }
            } catch (java.io.IOException e) { throw new IOException(e); }
            return this;
        }

        public void close() {
            try {
                if (appendable instanceof Closeable) {
                    Closeable closeable = (Closeable) appendable;
                    closeable.close();
                }
            } catch (java.io.IOException e) { throw new IOException(e); }
        }

        private Writer string(String s) {
            try {
                appendable.append(s);
            } catch (java.io.IOException e) { throw new IOException(e); }
            return this;
        }

        private String escape(String value) {
            if (value == null) return "";
            if (value.length() == 0) return "\"\"";

            boolean needQuoting = value.startsWith(" ") || value.endsWith(" ") || (value.startsWith("#") && first);
            if (!needQuoting) {
                for (char ch : new char[]{'\"', '\\', '\r', '\n', '\t', delimiter}) {
                    if (value.indexOf(ch) != -1) {
                        needQuoting = true;
                        break;
                    }
                }
            }

            String result = value.replace("\"", "\"\"");
            if (needQuoting) result = "\"" + result + "\"";
            return result;
        }

        public Writer delimiter(char delimiter) { this.delimiter = delimiter; return this; }
    }

    public static class Exception extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public Exception() { }
        public Exception(String message) { super(message); }
        public Exception(String message, Throwable cause) { super(message, cause); }
        public Exception(Throwable cause) { super(cause); }
    }

    public static class IOException extends Exception {
        private static final long serialVersionUID = 1L;
        public IOException() { }
        public IOException(String message) { super(message); }
        public IOException(String message, Throwable cause) { super(message, cause); }
        public IOException(Throwable cause) { super(cause); }
    }

    public static class FormatException extends Exception {
        private static final long serialVersionUID = 1L;
        public FormatException() { }
        public FormatException(String message) { super(message); }
        public FormatException(String message, Throwable cause) { super(message, cause); }
        public FormatException(Throwable cause) { super(cause); }
    }
}
