// Generated by view binder compiler. Do not edit!
package com.example.qrky.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.qrky.R;
import com.king.zxing.ViewfinderView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentScannerBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final ImageButton ibTakePicture;

  @NonNull
  public final PreviewView previewView;

  @NonNull
  public final ViewfinderView viewfinderView;

  private FragmentScannerBinding(@NonNull FrameLayout rootView, @NonNull ImageButton ibTakePicture,
      @NonNull PreviewView previewView, @NonNull ViewfinderView viewfinderView) {
    this.rootView = rootView;
    this.ibTakePicture = ibTakePicture;
    this.previewView = previewView;
    this.viewfinderView = viewfinderView;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentScannerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentScannerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_scanner, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentScannerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ib_take_picture;
      ImageButton ibTakePicture = ViewBindings.findChildViewById(rootView, id);
      if (ibTakePicture == null) {
        break missingId;
      }

      id = R.id.preview_view;
      PreviewView previewView = ViewBindings.findChildViewById(rootView, id);
      if (previewView == null) {
        break missingId;
      }

      id = R.id.viewfinder_view;
      ViewfinderView viewfinderView = ViewBindings.findChildViewById(rootView, id);
      if (viewfinderView == null) {
        break missingId;
      }

      return new FragmentScannerBinding((FrameLayout) rootView, ibTakePicture, previewView,
          viewfinderView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}