package com.devmoroz.moneyme.utils;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devmoroz.moneyme.logging.L;
import com.squareup.picasso.Callback;
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

    public static String checkExistAndTakePath(String photoName) {
        File pictureFile = new File(PICTURES_DIR, photoName);
        if (pictureFile.exists()) {
            return pictureFile.getAbsolutePath();
        }
        return null;
    }

    public static String extractImageUrlFromGallery(Context context, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String fileName = "";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        fileName = cursor.getString(columnIndex);
                    }
                } catch (Exception e) {

                }
            } else {
                fileName = selectedImage.getLastPathSegment();
            }
        } catch (SecurityException e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    public static void setImageWithPicasso(Context context, String path, ImageView target) {
        if (!path.contains("file:")) {
            Picasso.with(context).load("file://" + path).fit().into(target);
        } else {
            Picasso.with(context).load(path).fit().into(target);
        }
    }

    public static void setImageWithGlide(Context context, Uri imageUri, ImageView targetImage) {
        Glide.with(context).load(imageUri).centerCrop().crossFade().into(targetImage);
    }
}
