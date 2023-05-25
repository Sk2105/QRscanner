package com.sgtech.qr_scanner.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.sgtech.qr_scanner.R;
import com.sgtech.qr_scanner.activities.MainActivity;
import com.sgtech.qr_scanner.activities.WebActivity;

public class SettingsFragment extends Fragment {
    SwitchCompat switchCompat;
    TextView policy, terms, shareBtn, rateUs;
    SharedPreferences preferences;
    String url = "https://play.google.com/store/apps/details?id=com.sgtech.qr_scanner";

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        intiView(view);
        preferences = requireActivity().getSharedPreferences("Editor", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (preferences.getBoolean(MainActivity.isVibrate, false)) {
            switchCompat.setChecked(preferences.getBoolean(MainActivity.isVibrate, false));
        }
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                editor.putBoolean(MainActivity.isVibrate, false);
                editor.apply();
            } else {
                editor.putBoolean(MainActivity.isVibrate, true);
                editor.apply();
            }
        });
        Intent i = new Intent(requireActivity(), WebActivity.class);
        policy.setOnClickListener(v -> {
            i.putExtra("title", "Privacy Policy");
            i.putExtra("web", "https://www.app-privacy-policy.com/live.php?token=dYtIlwHExW4zFSBnZHFck73YwkEJnNE6");
            startActivity(i);
        });
        terms.setOnClickListener(v -> {
            i.putExtra("title", "Terms & Conditions");
            i.putExtra("web", "https://www.app-privacy-policy.com/live.php?token=Ez38AsZtNbT5P8ywkKaZ0FUrMLxwW7Cw");
            startActivity(i);
        });
        shareBtn.setOnClickListener(v -> shareLink());
        rateUs.setOnClickListener(v -> rateUsApp());
        return view;
    }

    private void rateUsApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void shareLink() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(intent, "Share With Friends"));
    }

    private void intiView(View view) {
        switchCompat = view.findViewById(R.id.setVibration);
        policy = view.findViewById(R.id.policy);
        terms = view.findViewById(R.id.terms);
        shareBtn = view.findViewById(R.id.share);
        rateUs = view.findViewById(R.id.rateUs);
    }
}