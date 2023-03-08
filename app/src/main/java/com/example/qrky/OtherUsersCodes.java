package com.example.qrky;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OtherUsersCodes extends Fragment {
    // US 02.03.01: As a player, I want to be able to browse QR codes that other players have scanned.
    // Franco Bonilla
    private OtherUsersCodesViewModel mViewModel;

    public static OtherUsersCodes newInstance() {
        return new OtherUsersCodes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other_users_codes, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OtherUsersCodesViewModel.class);
        // TODO: Use the ViewModel
    }

}