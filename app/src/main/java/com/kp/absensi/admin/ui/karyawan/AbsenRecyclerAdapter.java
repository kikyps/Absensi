package com.kp.absensi.admin.ui.karyawan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kp.absensi.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AbsenRecyclerAdapter extends RecyclerView.Adapter<AbsenRecyclerAdapter.MyViewHolder> implements Filterable {
    private final List<DataStore> AllList;
    public List<DataStore> FilteredList;
    Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
    String event = DataKaryawan.eventDate;

    public AbsenRecyclerAdapter(ArrayList<DataStore> mList, Context context) {
        this.context = context;
        this.AllList = mList;
        //this.FilteredList = mList;
        FilteredList = new ArrayList<>(mList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_absensi, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataStore storeUser = AllList.get(position);
        holder.tv_nama.setText(storeUser.getsNama());

        databaseReference.child(storeUser.getKey()).child("sAbsensi").child(event).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String kehadiran = snapshot.child("sKehadiran").getValue().toString();

                    if (kehadiran.equals("hadir")){
                        holder.tv_hadir.setText("Hadir");
                        holder.tv_hadir.setTextColor(Color.GREEN);
                    } else if (kehadiran.equals("izin")){
                        holder.tv_hadir.setText("Izin");
                        holder.tv_hadir.setTextColor(ContextCompat.getColor(context, R.color.orange));
                    }
                } else {
                    seleksiAbsen(holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.card_view.setOnClickListener(view -> {
            Intent intent = new Intent(context, RekapAbsen.class);
            intent.putExtra("idKaryawan", storeUser.getKey());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivities(new Intent[]{intent});
        });
    }

    private void seleksiAbsen(MyViewHolder holder){
        String curentDate = DataKaryawan.dateFormat.format(DataKaryawan.calendar.getTime());
        String tgglNow = DataKaryawan.dateFormat.format(new Date().getTime());
        if (curentDate.equals(tgglNow)){
            holder.tv_hadir.setText("Belum Absen");
            holder.tv_hadir.setTextColor(ContextCompat.getColor(context, R.color.orange));
        } else {
            holder.tv_hadir.setText("Tidak ada data absen!");
            holder.tv_hadir.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return AllList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String searchText = charSequence.toString().toLowerCase();
            List<DataStore> listFiltered = new ArrayList<>();

            if (searchText.isEmpty()) {
                listFiltered.addAll(FilteredList);
            } else {
                for (DataStore data : FilteredList) {
                    if (data.getsNama().toLowerCase().contains(searchText)) {
                        listFiltered.add(data);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = listFiltered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            AllList.clear();
            AllList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_nama, tv_hadir;
        CardView card_view;


        public MyViewHolder(@NonNull View iteView){
            super(iteView);

            tv_nama = iteView.findViewById(R.id.sNama);
            tv_hadir = iteView.findViewById(R.id.sHadir);
            card_view = iteView.findViewById(R.id.card_view);
        }
    }
}
