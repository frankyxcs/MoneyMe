package com.devmoroz.moneyme.utils;


import android.content.Context;
import com.dropbox.core.v2.DbxFiles;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


public class PicassoClient {

    private static Picasso sPicasso;

    public static void init(Context context, DbxFiles files) {

        // Configure picasso to know about special thumbnail requests
        sPicasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(context))
                .addRequestHandler(new FileThumbnailRequestHandler(files))
                .build();
    }


    public static Picasso getPicasso() {
        return sPicasso;
    }
}
