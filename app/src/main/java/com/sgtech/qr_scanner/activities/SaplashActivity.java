package com.sgtech.qr_scanner.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.sgtech.qr_scanner.R;

public class SaplashActivity extends AppCompatActivity {
    String[] permission = {Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saplash);
        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permission, 1);
            }else{
                ActivityCompat.requestPermissions(this,permission,1);
            }
        }else{
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
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SaplashActivity.this, MainActivity.class));
            finish();
        }, 1500);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}