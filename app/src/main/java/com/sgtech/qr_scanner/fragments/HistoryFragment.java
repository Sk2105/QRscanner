package com.sgtech.qr_scanner.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtech.qr_scanner.R;
import com.sgtech.qr_scanner.database.AppDataBase;
import com.sgtech.qr_scanner.database.DataModel;
import com.sgtech.qr_scanner.adapter.RecyclerAdapter;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<DataModel> dataModels;
    TextView emptyTxt;
    RecyclerAdapter adapter;
    //  AppDataBase appDataBase;

    public HistoryFragment() {
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        emptyTxt = view.findViewById(R.id.empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        initViews();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initViews() {
        AppDataBase appDataBase = new AppDataBase(requireContext());
        dataModels = appDataBase.fetchData();
        if (dataModels != null) {
            adapter = new RecyclerAdapter(requireContext(),dataModels);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            emptyTxt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


}