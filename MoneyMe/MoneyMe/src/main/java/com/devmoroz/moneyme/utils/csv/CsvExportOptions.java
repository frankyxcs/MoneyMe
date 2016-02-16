package com.devmoroz.moneyme.utils.csv;

public class CsvExportOptions {

    public final char fieldSeparator;
    public final boolean includeHeader;
    public final boolean exportSplits;
    public final boolean writeUtfBom;

    public CsvExportOptions(char fieldSeparator, boolean includeHeader,
                            boolean exportSplits,boolean writeUtfBom) {
        this.fieldSeparator = fieldSeparator;
        this.includeHeader = includeHeader;
        this.exportSplits = exportSplits;
        this.writeUtfBom = writeUtfBom;
    }
}
