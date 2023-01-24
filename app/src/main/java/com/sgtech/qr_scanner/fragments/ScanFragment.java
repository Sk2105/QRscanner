package com.sgtech.qr_scanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static android.widget.Toast.LENGTH_SHORT;

import static com.sgtech.qr_scanner.KeyClass.*;
import static com.google.mlkit.vision.barcode.common.Barcode.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sgtech.qr_scanner.R;
import com.sgtech.qr_scanner.activities.MainActivity;
import com.sgtech.qr_scanner.activities.ResultActivity;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("ALL")
public class ScanFragment extends Fragment {
    PreviewView previewView;
    SharedPreferences preferences;
    Vibrator vibrator;
    boolean isVibratePhone;
    SeekBar zoomBar;
    Preview preview;
    CameraSelector selector;
    ImageView flashBtn, getImageBtn, scanView;
    TextView zoomTxt;
    Boolean isActive = false, cameraLoaded = false, flashMode = false;
    ExecutorService cameraExecutor;
    ImageCapture imageCapture;
    ImageAnalysis analysis;
    ProcessCameraProvider processCameraProvider;
    Bitmap bit;
    CameraControl cameraControl;
    ListenableFuture cameraProviderFuture;
    Handler handler;

    public ScanFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        intiViews(view);
        handler = new Handler(Looper.getMainLooper());
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.set_amin);
        scanView.setAnimation(animation);
        flashBtn.setImageResource(R.drawable.flash_of_icon);
        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity());
        cameraProviderFuture.addListener(() -> {
            try {
                bindPreview();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireActivity()));
        flashBtn.setOnClickListener(v -> {
            if (flashMode) {
                flashBtn.setImageResource(R.drawable.flash_of_icon);
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                cameraControl.enableTorch(false);
                flashMode = false;
            } else {
                flashBtn.setImageResource(R.drawable.flash_on_icon);
                cameraControl.enableTorch(true);
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_AUTO);
                flashMode = true;
            }
        });
        getImageBtn.setOnClickListener(v -> setGetFileImage());
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraLoaded) {
                    if (!isActive) {
                        bit = previewView.getBitmap();
                        isActive = true;

                        if (bit != null) {
                            InputImage inputImage = InputImage.fromBitmap(bit, 90);
                            scanBarCode(inputImage);
                        }
                    }

                }
                handler.postDelayed(this, 1500);
            }
        });

        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float pro = progress / 100f;
                    zoomTxt.setText((progress / 10f) + "x");
                    cameraControl.setLinearZoom(pro);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                zoomTxt.setVisibility(View.VISIBLE);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                zoomTxt.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    private void intiViews(View view) {
        previewView = view.findViewById(R.id.preView);
        getImageBtn = view.findViewById(R.id.getImage);
        flashBtn = view.findViewById(R.id.flashBtn);
        zoomBar = view.findViewById(R.id.zoomBar);
        zoomTxt = view.findViewById(R.id.zoomTxt);
        scanView = view.findViewById(R.id.scan_amin_bar);
        vibrator = (Vibrator) requireActivity().getSystemService(VIBRATOR_SERVICE);
    }

    @SuppressLint("RestrictedApi")
    private void bindPreview() throws ExecutionException, InterruptedException {

        preferences = requireActivity().getSharedPreferences("Editor", MODE_PRIVATE);
        isVibratePhone = preferences.getBoolean(MainActivity.isVibrate, true);
        processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
        processCameraProvider.unbindAll();
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        selector =
                new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        analysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageCapture =
                new ImageCapture.Builder()
                        .setCameraSelector(selector).setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setFlashType(ImageCapture.FLASH_TYPE_USE_TORCH_AS_FLASH)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build();
        zoomBar.setProgress(0);
        analysis.setAnalyzer(cameraExecutor, new MyImageAnalysis());
        cameraControl = processCameraProvider.bindToLifecycle(this, selector,
                preview,
                imageCapture, analysis).getCameraControl();
        isActive = false;
        cameraLoaded = true;
    }


    public static class MyImageAnalysis implements ImageAnalysis.Analyzer {

        public MyImageAnalysis() {

        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
        }


    }


    public void setGetFileImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap =
                            MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                    if (bitmap != null) {
                        bit = bitmap;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputImage inputImage = null;
                try {
                    inputImage = InputImage.fromFilePath(requireActivity(), uri);
                    scanBarCode(inputImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            Toast.makeText(requireActivity(), "No Read Barcode...", LENGTH_SHORT).show();
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onDestroy() {
        super.onDestroy();
        processCameraProvider.shutdown();
    }


    private void scanBarCode(InputImage inputImage) {
        BarcodeScanner barcodeScanner = BarcodeScanning.getClient();
        Task<List<Barcode>> result =
                barcodeScanner.process(inputImage).addOnSuccessListener(barcodes -> {
                    if (barcodes != null) {
                        try {
                            readerBarCodeData(barcodes);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnCompleteListener(task -> {
                }).addOnFailureListener(e -> {
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            bindPreview();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readerBarCodeData(List<Barcode> barcodes) throws ExecutionException, InterruptedException {
        Intent intent;
        bit = null;
        if (barcodes.isEmpty()) {
            isActive = false;
        } else {
            isActive = true;
            if (isVibratePhone) {
                vibrator.vibrate(100);
            }
            for (Barcode barcode : barcodes) {
                int valueType = barcode.getValueType();
                switch (valueType) {
                    case TYPE_WIFI:
                        intent = new Intent(requireActivity(), ResultActivity.class);
                        intent.putExtra("SEE_DATA", "NO");
                        intent.putExtra(TYPE_VALUE, WIFI_TYPE);
                        intent.putExtra(WIFI_SSID, "SSID : " + barcode.getWifi().getSsid() + "\n" +
                                "Password : " + barcode.getWifi().getPassword() + "\n"
                                + "Security : WAP" + barcode.getWifi().getEncryptionType());
                        startActivity(intent);
                        break;
                    case TYPE_URL:
                        intent = new Intent(requireActivity(), ResultActivity.class);
                        intent.putExtra("SEE_DATA", "NO");
                        intent.putExtra(TYPE_VALUE, URL_VALUE);
                        intent.putExtra(URL_TITLE, barcode.getUrl().getTitle());
                        intent.putExtra(URL_URL, barcode.getUrl().getUrl());
                        startActivity(intent);
                        break;
                    case TYPE_PHONE:
                        if (ContextCompat.checkSelfPermission(requireContext(),
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                            }
                        } else {
                            intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + barcode.getPhone().getNumber()));
                            startActivity(intent);
                        }
                        break;
                    case (TYPE_TEXT | TYPE_PRODUCT):
                        intent = new Intent(requireActivity(), ResultActivity.class);
                        intent.putExtra(TYPE_VALUE, TEXT_TYPE);
                        intent.putExtra("SEE_DATA", "NO");
                        intent.putExtra(TEXT_TITLE, barcode.getDisplayValue());
                        startActivity(intent);
                        break;
                    case TYPE_EMAIL:
                        intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(TYPE_VALUE, EMAIL_TYPE);
                        intent.putExtra(Intent.EXTRA_EMAIL, Objects.requireNonNull(barcode.getEmail()).getAddress());
                        intent.putExtra(Intent.EXTRA_SUBJECT, barcode.getEmail().getSubject());
                        intent.putExtra(Intent.EXTRA_TEXT, barcode.getEmail().getBody());
                        startActivity(Intent.createChooser(intent, "Send Email"));
                        break;
                    case TYPE_CONTACT_INFO:
                        intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                        intent.putExtra(ContactsContract.Intents.Insert.NAME, barcode.getContactInfo().getName().getFormattedName());
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, barcode.getContactInfo().getPhones().get(0).getNumber());
                        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.getContactInfo().getEmails().get(0).getAddress());
                        startActivity(intent);
                        break;

                    case TYPE_SMS:
                        String packageName = Telephony.Sms.getDefaultSmsPackage(requireActivity());
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.setPackage(packageName);
                        intent.putExtra(Intent.EXTRA_TEXT, barcode.getSms().getMessage());
                        startActivity(intent);
                        break;
                }
            }

        }

    }


}