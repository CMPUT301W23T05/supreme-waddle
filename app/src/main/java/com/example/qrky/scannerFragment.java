package com.example.qrky;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.Result;
import com.king.zxing.CaptureFragment;

import java.nio.ByteBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 *
 */
public class scannerFragment extends CaptureFragment {

    private static final String TAG = "SW.ScannerF";
    private ProcessCameraProvider mCameraProvider;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture mImageCapture;
    private ImageButton mIbTakePicture;
    private LocationHelper mLocationHelper;
    private ImageView mIvPhoto;
    private Database mDatabase;
    private String mCode;
    private GeoPoint mGeoPoint;
    byte[] bytes;


    /**
     *
     * @param view The View returned by {@link @onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIbTakePicture = view.findViewById(R.id.ib_take_picture);
        mIvPhoto = view.findViewById(R.id.iv_photo);
        mIbTakePicture.setVisibility(View.GONE);
        mIbTakePicture.setOnClickListener(v -> takePicture());
        mLocationHelper = new LocationHelper(requireContext(), getViewLifecycleOwner());
        mDatabase = new Database();
        startLocation();
    }

    /**
     *
     * @param result
     * @return
     */
    @Override
    public boolean onScanResultCallback(Result result) {
        if (!TextUtils.isEmpty(result.getText())) {
            mCode = result.getText();
            getCameraScan().stopCamera();
            confirmTrackLocation();
            return true;
        }
        return super.onScanResultCallback(result);
    }

    /**
     *
     * @param isLocationRequired
     */
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void goSaveLibrary(boolean isLocationRequired, byte[] bytes) {
        if (TextUtils.isEmpty(mCode)) {
            Toast.makeText(requireContext(), "Code not valid.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLocationRequired && Objects.isNull(mGeoPoint)) {
            Toast.makeText(requireContext(), "Wait for location succeed.", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabase.goSaveLibrary(true, mCode, mGeoPoint, bytes);
    }

    private void confirmTrackLocation() {
    getParentFragmentManager().setFragmentResultListener(
            ConfirmDialog.class.getSimpleName(), getViewLifecycleOwner(),
            (requestKey, result) -> {
                Log.d("TAG", "confirmTrackLocation: ");
                boolean confirm = result.getBoolean("confirm", false);
                if (confirm) {
                    useCameraForTakingPicture();
                    tryStartLocation(true);
                } else {
                    getCameraScan().startCamera();
                    goSaveLibrary(false, bytes);
                }
            });
        ConfirmDialog.newInstance(
                getString(R.string.confirm_track_location_title),
                getString(R.string.confirm_track_location_confirm),
                getString(R.string.confirm_track_location_cancel)
        ).show(getParentFragmentManager(), ConfirmDialog.class.getSimpleName());
    }

    /**
     *
     */
    private void useCameraForTakingPicture() {
        viewfinderView.setVisibility(View.GONE);
        mIbTakePicture.setVisibility(View.VISIBLE);
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                mCameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                mImageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                mCameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(), cameraSelector, preview, mImageCapture);
            } catch (Exception ignored) {}
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    /**
     *
     */
    private void takePicture() {
        if (Objects.isNull(mGeoPoint)) {
            Toast.makeText(requireContext(), "Wait for location succeed.", Toast.LENGTH_SHORT).show();
            return;
        }

        mImageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Log.d(TAG, "onCaptureSuccess: ");
                        savePhoto(image);

                        mCameraProvider.unbindAll();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "onError: ", exception);
                    }
                });
        getRootView().postDelayed(() -> {
            getRootView().setForeground(new ColorDrawable(Color.WHITE));
            getRootView().postDelayed(() -> getRootView().setForeground(null), 50);
        }, 100);
        goSaveLibrary(true, bytes);
        mIvPhoto.setVisibility(View.VISIBLE);
    }

    private void savePhoto(ImageProxy image) {

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        Bitmap mIvPhotoBitmap = BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
        mIvPhoto.setImageBitmap(mIvPhotoBitmap);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     *
     * @return
     */

    @Override
    public int getLayoutId() {
        return R.layout.fragment_scanner;
    }

    /**
     *
     * @return
     */

    @Override
    public int getPreviewViewId() {
        return R.id.preview_view;
    }

    /**
     *
     * @return
     */
    @Override
    public int getViewfinderViewId() {
        return R.id.viewfinder_view;
    }

    final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private final ActivityResultLauncher<String[]> mMultiplePermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> tryStartLocation(false));

    /**
     *
     * @param shouldRequest
     */
    private void tryStartLocation(boolean shouldRequest) {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           if (shouldRequest)  mMultiplePermissionLauncher.launch(PERMISSIONS);
        } else {
            startLocation();
        }
    }

    /**
     *
     */
    private void startLocation() {
        mLocationHelper.startLocation(
                LocationManager.GPS_PROVIDER, new LocationHelper.LocationCallback() {
                    @Override
                    public void onSuccess(double latitude, double longitude) {
                        mLocationHelper.stopLocation();
                        mGeoPoint = new GeoPoint(latitude, longitude);
                        Toast.makeText(requireContext(), "Location Succeed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Location location = mLocationHelper.getNetwork();
                        if (location != null) {
                            mGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            Toast.makeText(requireContext(), "Location Succeed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Location Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}