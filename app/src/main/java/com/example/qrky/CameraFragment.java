package com.example.qrky;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;

public class CameraFragment extends Fragment {

    private PreviewView mPreviewView;
    private ProcessCameraProvider mCameraProvider;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPreviewView = view.findViewById(R.id.preview_view);
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                mCameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
                Camera camera = mCameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(), cameraSelector, preview);
            } catch (Exception ignored) {}
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraProvider.unbindAll();
    }
}
