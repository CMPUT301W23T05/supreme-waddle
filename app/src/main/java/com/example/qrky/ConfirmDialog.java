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

/**
 * This ConfirmDialog extending the DialogFragment class. It is responsible for displaying a confirmation dialog with a title, a confirm button, and a cancel button.
 */
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

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *The onCreateView method is responsible for inflating the dialog's layout file and returning the root view. It returns the inflated dialog_confirm layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm, container, false);
    }
    /**
     *The onViewCreated method is called after the view is created and is responsible for initializing the dialog's UI components and setting up click listeners for the confirm and cancel buttons.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     */
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

    /**
     * The onCancel method is called when the dialog is canceled by the user and is responsible for setting the fragment result with a boolean value indicating that the cancel button was clicked.
     * @param dialog the dialog that was canceled will be passed into the
     *               method
     */
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Bundle result = new Bundle();
        result.putBoolean("confirm", false);
        getParentFragmentManager().setFragmentResult(
                ConfirmDialog.class.getSimpleName(), result);
        super.onCancel(dialog);
    }
}
