package com.devmoroz.moneyme.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;

public class PhotoUtil {

    public static final File PICTURES_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    public static Bitmap makeBitmap(String photoFileName, ImageView targetImageView) {
        Bitmap b = null;

        int targetW = targetImageView.getWidth();
        int targetH = targetImageView.getHeight();

        File pictureFile = new File(PICTURES_DIR, photoFileName);
        if (pictureFile.exists()) {
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pictureFile.getPath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            b = BitmapFactory.decodeFile(pictureFile.getPath(), bmOptions);
        }

        return b;
    }
}
