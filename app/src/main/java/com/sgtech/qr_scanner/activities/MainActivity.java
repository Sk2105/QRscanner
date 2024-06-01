package com.sgtech.qr_scanner.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sgtech.qr_scanner.R;
import com.sgtech.qr_scanner.database.AppDataBase;

import com.sgtech.qr_scanner.adapter.SlideAdapter;
import com.sgtech.qr_scanner.fragments.HistoryFragment;
import com.sgtech.qr_scanner.fragments.ScanFragment;
import com.sgtech.qr_scanner.fragments.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    String[] permission = {Manifest.permission.CAMERA};
    public static String isVibrate = "isVibrate";
    ImageView scanImgId, historyImgId, settingImgId;
    LinearLayout scanLayout, historyLayout, settingLayout;
    TextView scanTxtId, historyTxtId, settingTxtId;
    ViewPager viewPager;
    List<Fragment> fragmentList = new ArrayList<>();
    List<TextView> textViews = new ArrayList<>();
    List<LinearLayout> linearLayouts = new ArrayList<>();
    List<ImageView> imageViews = new ArrayList<>();
    List<Integer> imgIdes = new ArrayList<>();
    List<Integer> imgBlueIdes = new ArrayList<>();
    int currentLayout = 0;
    AppDataBase appDataBase;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        addView();
        // check Permission
        checkPermission();
        //set ViewPage
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setBottomBar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scanLayout.setOnClickListener(v -> selectedItem(0));
        historyLayout.setOnClickListener(v -> selectedItem(1));
        settingLayout.setOnClickListener(v -> selectedItem(2));

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
            requestPermissions(permission, 1);
        }
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        scanImgId = findViewById(R.id.scanImgId);
        historyImgId = findViewById(R.id.historyImgId);
        settingImgId = findViewById(R.id.settingImgId);
        scanLayout = findViewById(R.id.scanPage);
        historyLayout = findViewById(R.id.history);
        settingLayout = findViewById(R.id.settingPage);
        scanTxtId = findViewById(R.id.scanTxtId);
        historyTxtId = findViewById(R.id.historyTxtId);
        settingTxtId = findViewById(R.id.settingTxtId);
        addList();

    }

    public void addView() {
        appDataBase = new AppDataBase(this);
        fragmentList.add(new ScanFragment());
        fragmentList.add(new HistoryFragment());
        fragmentList.add(new SettingsFragment());
        viewPager.setAdapter(new SlideAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(currentLayout);
    }

    public void selectedItem(int i) {
        if (currentLayout == i) {
            return;
        }
        viewPager.setCurrentItem(i);
        setBottomBar(i);
    }

    public void setBottomBar(int i) {
        for (int j = 0; j < linearLayouts.size(); j++) {
            if (j == i) {
                linearLayouts.get(j).setBackgroundResource(R.drawable.bottom_selected_shape);
                linearLayouts.get(j).startAnimation(getScaleAnimation());
                textViews.get(j).setVisibility(View.VISIBLE);
                imageViews.get(j).setImageResource(imgIdes.get(j));
                currentLayout = j;
            } else {
                linearLayouts.get(j).setBackgroundResource(R.drawable.item_bg);
                textViews.get(j).setVisibility(View.GONE);
                imageViews.get(j).setImageResource(imgBlueIdes.get(j));
            }

        }
    }

    public ScaleAnimation getScaleAnimation() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }

    public void addList() {
        textViews.add(scanTxtId);
        textViews.add(historyTxtId);
        textViews.add(settingTxtId);
        linearLayouts.add(scanLayout);
        linearLayouts.add(historyLayout);
        linearLayouts.add(settingLayout);
        imageViews.add(scanImgId);
        imageViews.add(historyImgId);
        imageViews.add(settingImgId);
        imgIdes.add(R.drawable.app_icon);
        imgIdes.add(R.drawable.history_icon);
        imgIdes.add(R.drawable.settings_icon);
        imgBlueIdes.add(R.drawable.app_icon_blue);
        imgBlueIdes.add(R.drawable.history_icon_blue);
        imgBlueIdes.add(R.drawable.settings_icon_blue);
    }

}