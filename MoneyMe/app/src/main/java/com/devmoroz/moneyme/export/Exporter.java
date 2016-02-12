package com.devmoroz.moneyme.export;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.devmoroz.moneyme.MoneyApplication;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class Exporter implements Closeable {

    protected final Context mContext;
    private final OutputStream outputStream;


    protected Exporter(Context context,OutputStream outputStream) {
        this.mContext = context;
        this.outputStream = outputStream;
    }

    @Override public void close() throws IOException {
        outputStream.close();
    }

    public void exportData() throws Exception {
        generateExport(outputStream);
    }

    public abstract void generateExport(OutputStream outputStream) throws Exception;

    protected abstract String getExtension();
}
