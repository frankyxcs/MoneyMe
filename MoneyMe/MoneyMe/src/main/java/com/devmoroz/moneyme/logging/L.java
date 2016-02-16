package com.devmoroz.moneyme.logging;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class L {

    public static void m(String message) {
        Log.d("MoneyMe", "" + message);
    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }
    public static void T(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_LONG).show();
    }



    public static void appendLog(String text)
    {
        File dir = new File (Environment.getExternalStorageDirectory() + "/MoneyMe/Logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File logFile = new File(dir, "logcat.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
