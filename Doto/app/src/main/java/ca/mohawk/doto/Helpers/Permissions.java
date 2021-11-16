package ca.mohawk.doto.Helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class Permissions {
    Context context;
     public Permissions(Context context){
         this.context=context;
     }
    public boolean checkPermissionWrite() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public boolean requestPermissionWrite() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
              return true;
        }else{
             return false;
        }
    }

    public boolean checkPermissionRead() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public boolean requestPermissionRead() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;
        }
    }

    public boolean checkPermissionCamera() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public boolean requestPermissionCamera() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;
        }
    }
}
