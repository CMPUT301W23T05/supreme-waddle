// Generated by view binder compiler. Do not edit!
package com.example.qrky.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.example.qrky.R;
import java.lang.NullPointerException;
import java.lang.Override;

public final class FragmentNearbyCodesListBinding implements ViewBinding {
  @NonNull
  private final RecyclerView rootView;

  @NonNull
  public final RecyclerView list;

  private FragmentNearbyCodesListBinding(@NonNull RecyclerView rootView,
      @NonNull RecyclerView list) {
    this.rootView = rootView;
    this.list = list;
  }

  @Override
  @NonNull
  public RecyclerView getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentNearbyCodesListBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentNearbyCodesListBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_nearby_codes_list, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentNearbyCodesListBinding bind(@NonNull View rootView) {
    if (rootView == null) {
      throw new NullPointerException("rootView");
    }

    RecyclerView list = (RecyclerView) rootView;

    return new FragmentNearbyCodesListBinding((RecyclerView) rootView, list);
  }
}
