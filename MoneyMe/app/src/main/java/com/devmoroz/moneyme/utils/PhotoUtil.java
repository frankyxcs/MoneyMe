package com.devmoroz.moneyme.utils;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

    public static String checkExistAndTakePath(String photoName){
        File pictureFile = new File(PICTURES_DIR, photoName);
        if (pictureFile.exists()) {
            return  pictureFile.getPath();
        }
        return null;
    }

    public static String extractImageUrlFromGallery(Context context, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        String fileName = cursor.getString(columnIndex);
        cursor.close();

        return fileName;
    }

    public static void setImageWithPicasso(Context context, String path, ImageView target){
        String photoPath = "file://" + path;
        Picasso.with(context).load(photoPath).fit().into(target);
    }
}
