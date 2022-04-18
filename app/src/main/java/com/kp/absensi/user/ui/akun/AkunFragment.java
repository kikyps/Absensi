package com.kp.absensi.user.ui.akun;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kp.absensi.Preferences;
import com.kp.absensi.R;
import com.kp.absensi.common.EditProfile;

public class AkunFragment extends Fragment {

    TextView namaKaryawan, editProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_akun, container, false);
        namaKaryawan = root.findViewById(R.id.nama_karyawan);
        editProfile = root.findViewById(R.id.edit_profil);
        buttonListener();
        return root;
    }

    private void buttonListener(){
        editProfile.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), EditProfile.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showProfile();
    }

    public void showProfile(){
        String namaData = Preferences.getDataNama(getContext());
        namaKaryawan.setText(namaData);
    }
}