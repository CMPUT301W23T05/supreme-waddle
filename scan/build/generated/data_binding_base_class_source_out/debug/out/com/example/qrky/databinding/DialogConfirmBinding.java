// Generated by view binder compiler. Do not edit!
package com.example.qrky.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.qrky.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DialogConfirmBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btnCancel;

  @NonNull
  public final Button btnConfirm;

  @NonNull
  public final TextView tvTitle;

  private DialogConfirmBinding(@NonNull LinearLayout rootView, @NonNull Button btnCancel,
      @NonNull Button btnConfirm, @NonNull TextView tvTitle) {
    this.rootView = rootView;
    this.btnCancel = btnCancel;
    this.btnConfirm = btnConfirm;
    this.tvTitle = tvTitle;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogConfirmBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogConfirmBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_confirm, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogConfirmBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_cancel;
      Button btnCancel = ViewBindings.findChildViewById(rootView, id);
      if (btnCancel == null) {
        break missingId;
      }

      id = R.id.btn_confirm;
      Button btnConfirm = ViewBindings.findChildViewById(rootView, id);
      if (btnConfirm == null) {
        break missingId;
      }

      id = R.id.tv_title;
      TextView tvTitle = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle == null) {
        break missingId;
      }

      return new DialogConfirmBinding((LinearLayout) rootView, btnCancel, btnConfirm, tvTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
