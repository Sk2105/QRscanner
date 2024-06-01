package com.sgtech.qr_scanner.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.sgtech.qr_scanner.KeyClass;
import com.sgtech.qr_scanner.R;
import com.sgtech.qr_scanner.database.AppDataBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ResultActivity extends AppCompatActivity {

    TextView qrResult, qrType, dataTime;
    ImageView qrImage, qrTypeIcon;
    LinearLayout shareBtn, webBtn, copyBtn, saveBtn;
    String type = null;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    int dimens = 500;
    String title = null;
    Toolbar toolbar;
    AppDataBase appDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initViews();
        saveBtn.setOnClickListener(v -> {
            try {
                saveQR();
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Error : " + e, Toast.LENGTH_SHORT).show();
            }
        });
        shareBtn.setOnClickListener(v -> shareTxt());
        copyBtn.setOnClickListener(v -> copyTxt());
        webBtn.setOnClickListener(v -> searchWEB());

    }

    private void searchWEB() {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, title);
        startActivity(intent);
    }

    private void copyTxt() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setText(title);
    }

    private void shareTxt() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TEXT, title);
        Intent i = Intent.createChooser(intent, title);
        startActivity(i);
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT <= 32) {
            return (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    private void saveQR() throws FileNotFoundException {
        if (!checkPermission()) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Date date = new Date();
            Bitmap bit = bitmap;
            File fileDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)));
            File dir = new File(fileDir, "QR Scanner");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, "QR_Scanner" + date.getDate() + date.getTime() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bit.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Toast.makeText(this, "File Successfully Sava", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void getIntentData() {
        appDataBase = new AppDataBase(this);
        type = getIntent().getStringExtra(KeyClass.TYPE_VALUE);
        if (Objects.equals(type, KeyClass.URL_VALUE)) {
            title = getIntent().getStringExtra(KeyClass.URL_URL);
            qrResult.setText(title);
            qrType.setText("Url");
            qrTypeIcon.setImageResource(R.drawable.language_layer);
            qrgEncoder =
                    new QRGEncoder(title, null,
                            QRGContents.Type.TEXT,
                            dimens);
            bitmap = qrgEncoder.getBitmap(2);
            qrImage.setImageBitmap(bitmap);
        } else if (Objects.equals(type, KeyClass.TEXT_TYPE)) {
            title = getIntent().getStringExtra(KeyClass.TEXT_TITLE);
            qrgEncoder = new QRGEncoder(title, null,
                    QRGContents.Type.TEXT,
                    dimens);
            bitmap = qrgEncoder.getBitmap(2);
            qrImage.setImageBitmap(bitmap);
            qrResult.setText(title);
            qrType.setText("Text");
            qrTypeIcon.setImageResource(R.drawable.text_layer);
        } else if (Objects.equals(type, KeyClass.WIFI_TYPE)) {
            title = getIntent().getStringExtra(KeyClass.WIFI_SSID);
            qrgEncoder = new QRGEncoder(title, null,
                    QRGContents.Type.TEXT,
                    dimens);
            bitmap = qrgEncoder.getBitmap(2);
            qrImage.setImageBitmap(bitmap);
            qrResult.setText(title);
            qrType.setText("Wifi");
            qrTypeIcon.setImageResource(R.drawable.wifi_layer);
        }
        if (getIntent().getStringExtra("SEE_DATA").equals("NO")) {
            appDataBase.insertDATA(type, title);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @SuppressLint("RestrictedApi")
    private void initViews() {
        dataTime = findViewById(R.id.dataTime);
        dataTime.setText(new SimpleDateFormat("dd/MM/yyyy , HH:mm a", Locale.getDefault()).format(new Date()));
        qrResult = findViewById(R.id.qrResult);
        qrType = findViewById(R.id.dataType);
        qrImage = findViewById(R.id.qrImg);
        qrTypeIcon = findViewById(R.id.typeImg);
        toolbar = findViewById(R.id.toolbar);
        shareBtn = findViewById(R.id.shareContent);
        webBtn = findViewById(R.id.open);
        copyBtn = findViewById(R.id.copyContent);
        saveBtn = findViewById(R.id.saveImg);
        setSupportActionBar(toolbar);
        getIntentData();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


}