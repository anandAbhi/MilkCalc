package com.example.abhinav.milkcalc.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhinav.milkcalc.adapters.BillsAdapter;
import com.example.abhinav.milkcalc.databinding.FragmentBillsBinding;
import com.example.abhinav.milkcalc.pojo.Bill;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import timber.log.Timber;

public class BillsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        billsRef = database.getReference("bills");
        bills = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        billsRef.addChildEventListener(mChildEventListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBillsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BillsAdapter(bills);
        binding.recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);

        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation()));
    }

    @Override
    public void onPause() {
        super.onPause();
        billsRef.removeEventListener(mChildEventListener);
    }

    private BillsAdapter adapter;
    private FragmentBillsBinding binding;
    private DatabaseReference billsRef;
    private ArrayList<Bill> bills;

    private ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Bill value = dataSnapshot.getValue(Bill.class);
            value.serverID = dataSnapshot.getKey();
            Timber.d("Value is: %s %s", value.date, dataSnapshot.getKey());
            bills.add(value);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Bill value = dataSnapshot.getValue(Bill.class);
            value.serverID = dataSnapshot.getKey();
            Timber.d("Changed Value is: %s %s", value.date, dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Timber.d("Removed key is: %s", dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
