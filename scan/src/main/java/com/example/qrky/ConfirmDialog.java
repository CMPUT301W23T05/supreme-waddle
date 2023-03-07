package com.example.qrky;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialog extends DialogFragment {

    private TextView tvTitle;
    private Button btnConfirm;
    private Button btnCancel;

    public static ConfirmDialog newInstance(String title, String confirm, String cancel) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("confirm", confirm);
        bundle.putString("cancel", cancel);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tv_title);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnCancel = view.findViewById(R.id.btn_cancel);

        btnConfirm.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putBoolean("confirm", true);
            getParentFragmentManager().setFragmentResult(
                    ConfirmDialog.class.getSimpleName(), result);
            dismissNow();
        });

        btnCancel.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putBoolean("confirm", false);
            getParentFragmentManager().setFragmentResult(
                    ConfirmDialog.class.getSimpleName(), result);
            dismissNow();
        });

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title", "");
            String confirm = args.getString("confirm", "");
            String cancel = args.getString("cancel", "");

            tvTitle.setText(title);
            btnConfirm.setText(confirm);
            btnCancel.setText(cancel);
        }
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Bundle result = new Bundle();
        result.putBoolean("confirm", false);
        getParentFragmentManager().setFragmentResult(
                ConfirmDialog.class.getSimpleName(), result);
        super.onCancel(dialog);
    }
}
