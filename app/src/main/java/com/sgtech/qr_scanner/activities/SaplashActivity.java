package com.sgtech.qr_scanner.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sgtech.qr_scanner.R;

public class SaplashActivity extends AppCompatActivity {
    String[] permission = {Manifest.permission.CAMERA};
    boolean startActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            startActivity = false;
            setContentView(R.layout.activity_saplash);
            checkPermissions();
        } else {
            startActivity = true;
            checkPermissions();
        }

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permission, 1);
            } else {
                ActivityCompat.requestPermissions(this, permission, 1);
            }
        } else {
            startActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                startActivity();
            } else {
                finish();
            }
        }
    }

    public void startActivity() {
        if (!startActivity) {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SaplashActivity.this, MainActivity.class));
                finish();
            }, 1500);
        } else {
            startActivity(new Intent(SaplashActivity.this, MainActivity.class));
            finish();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}