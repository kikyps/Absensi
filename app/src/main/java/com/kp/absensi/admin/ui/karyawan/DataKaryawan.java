package com.kp.absensi.admin.ui.karyawan;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DataKaryawan extends Fragment {

    AbsenRecyclerAdapter absenRecyclerAdapter;
    ArrayList<DataStore> listUser = new ArrayList<>();
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText filterData;

    private Context mContext;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rekap_absen, container, false);
        layoutbinding(root);
        listenerAction();
        showData();
        return root;
    }

    private void layoutbinding(View root) {
        recyclerView = root.findViewById(R.id.rv_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        filterData = root.findViewById(R.id.filter_data);
        swipeRefreshLayout = root.findViewById(R.id.swiper);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.purple_500));
        Collections.sort(listUser, DataStore.dataStoreComparator);
    }

    private void listenerAction(){
        filterData.addTextChangedListener(filter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            showData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private TextWatcher filter = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            absenRecyclerAdapter.getFilter().filter(charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mContext == null)
            mContext = context.getApplicationContext();
    }

    private void showData(){
        databaseReference.child("user").orderByChild("sStatus").equalTo("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        DataStore storeUser = item.getValue(DataStore.class);
                        storeUser.setKey(item.getKey());
                        listUser.add(storeUser);
                    }
                }
                absenRecyclerAdapter = new AbsenRecyclerAdapter(listUser, mContext);
                recyclerView.setAdapter(absenRecyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}