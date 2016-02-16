package com.devmoroz.moneyme.helpers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.eventBus.OnPermissionRequestedListener;
import com.tbruyelle.rxpermissions.RxPermissions;

public class PermissionsHelper {

    public static void requestPermission(Activity activity, String permission, int rationaleDescription, View
            messageView, OnPermissionRequestedListener onPermissionRequestedListener) {

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                Snackbar.make(messageView, rationaleDescription, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, view -> {
                            requestPermissionExecute(activity, permission, onPermissionRequestedListener, messageView);
                        })
                        .show();
            } else {
                requestPermissionExecute(activity, permission, onPermissionRequestedListener, messageView);
            }
        } else {
            onPermissionRequestedListener.onPermissionGranted();
        }
    }


    private static void requestPermissionExecute(Activity activity, String permission, OnPermissionRequestedListener
            onPermissionRequestedListener, View messageView) {
        RxPermissions.getInstance(activity)
                .request(permission)
                .subscribe(granted -> {
                    if (granted) {
                        onPermissionRequestedListener.onPermissionGranted();
                    } else {
                        String msg = activity.getString(R.string.permission_not_granted) + ": " + permission;
                        Snackbar.make(messageView, msg, Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
