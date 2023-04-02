package com.example.qrky;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.Result;
import com.king.zxing.CaptureFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 *This code is implementing a barcode scanner functionality that allows the user to scan a barcode and capture an image.
 */
public class scannerFragment extends CaptureFragment {

    private static final String TAG = "SW.ScannerF";
    private ProcessCameraProvider mCameraProvider;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture mImageCapture;
    private ImageButton mIbTakePicture;
    private ImageButton mIbRetake;
    private ImageButton mIbSave;
    private LocationHelper mLocationHelper;
    private Database mDatabase;
    private boolean mLocationRequired;
    private String mCode;
    private GeoPoint mGeoPoint;
    private Bitmap mPhotoBitmap;

    /**
     *onViewCreated method initializes the necessary UI components and starts the location service.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIbTakePicture = view.findViewById(R.id.ib_take_picture);
        mIbTakePicture.setVisibility(View.GONE);
        mIbTakePicture.setOnClickListener(v -> takePicture());

        mIbRetake = view.findViewById(R.id.ib_retake);
        mIbRetake.setOnClickListener(v -> useCameraForTakingPicture());
        mIbSave = view.findViewById(R.id.ib_save);
        mIbSave.setOnClickListener(v -> goSaveLibrary());
        mLocationHelper = new LocationHelper(requireContext(), getViewLifecycleOwner());
        mDatabase = new Database();
    }

    /**
     *The onScanResultCallback method is called when a barcode is successfully scanned.
     * It stops the camera and confirms whether the user wants to track their location
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
     *goSaveLibrary method is responsible for saving the specified data to a database or
     * performing other actions related to storing data.
     */
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void goSaveLibrary() {
        if (TextUtils.isEmpty(mCode)) {
            Toast.makeText(requireContext(), "Code not valid.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mLocationRequired && Objects.isNull(mGeoPoint)) {
            Toast.makeText(requireContext(), "Wait for location succeed.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (Objects.isNull(mPhotoBitmap)) {
            Toast.makeText(requireContext(), "Photo is not taken.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        int size = mPhotoBitmap.getByteCount();
        while (size > 100 * 1024 && quality > 1) {
            quality -= 1;
            size = quality * size / 100;
        }
        mPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        InputStream photoInputStream = new ByteArrayInputStream(baos.toByteArray());
        mDatabase.goSaveLibrary(true, mCode, mGeoPoint, photoInputStream);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.switchTab(R.id.libraryFragment);
    }

    /**
     * confirmTrackLocation method shows a dialogue to the user and, based on their response,
     * either starts the camera to capture an image or saves the scanned barcode data to the database.
     */
    private void confirmTrackLocation() {
        getParentFragmentManager().setFragmentResultListener(
            ConfirmDialog.class.getSimpleName(), getViewLifecycleOwner(),
            (requestKey, result) -> {
                boolean confirm = result.getBoolean("confirm", false);
                Log.d("TAG", "confirmTrackLocation: " + confirm);
                if (confirm) {
                    mLocationRequired = true;
                    tryStartLocation(true);
                } else {
                    mLocationRequired = false;
                }
                useCameraForTakingPicture();
            });
        ConfirmDialog.newInstance(
                getString(R.string.confirm_track_location_title),
                getString(R.string.confirm_track_location_confirm),
                getString(R.string.confirm_track_location_cancel)
        ).show(getParentFragmentManager(), ConfirmDialog.class.getSimpleName());
    }

    /**
     * useCameraForTakingPicture method initializes the CameraX library and starts
     * the camera to capture an image.
     */
    private void useCameraForTakingPicture() {
        viewfinderView.setVisibility(View.GONE);
        mIbRetake.setVisibility(View.GONE);
        mIbSave.setVisibility(View.GONE);
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

    private void unUseCamera() {
        mIbRetake.setVisibility(View.VISIBLE);
        mIbSave.setVisibility(View.VISIBLE);
        mIbTakePicture.setVisibility(View.GONE);
        mCameraProvider.unbindAll();
    }

    /**
     * takePicture method captures an image using the initialized camera and saves the image
     * and the scanned barcode data to the database.
     */
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void takePicture() {
        if (mLocationRequired && Objects.isNull(mGeoPoint)) {
            Toast.makeText(requireContext(), "Wait for location succeed.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mImageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Log.d(TAG, "onCaptureSuccess: ");
                        unUseCamera();
                        savePhoto(image);
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
    }

    /**
     * savePhoto is to extract the image data from the ImageProxy object
     * @param image the imageProxy from camera
     */
    private void savePhoto(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = bytes.length / 640 / 480;
        Bitmap bitmap = BitmapFactory.decodeByteArray(
                bytes, 0, bytes.length, options);
        Matrix matrix = new Matrix();
        matrix.postRotate(image.getImageInfo().getRotationDegrees());
        if (mPhotoBitmap != null) mPhotoBitmap.recycle();
        mPhotoBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        buffer.clear();
    }

    /**
     * This code overrides the onDestroyView method of a fragment class
     */
    @Override
    public void onDestroyView() {
        if (mPhotoBitmap != null) {
            mPhotoBitmap.recycle();
            mPhotoBitmap = null;
        }
        super.onDestroyView();
    }

    /**
     * getLayoutId returns an integer representing the layout resource ID for
     * the associated ScannerFragment.
     *
     */
    @Override
    public int getLayoutId() {
        return R.layout.fragment_scanner;
    }

    /**
     * getPreviewViewId returns the resource ID of a preview view.
     *
     */
    @Override
    public int getPreviewViewId() {
        return R.id.preview_view;
    }

    /**
     * getViewfinderViewId returns the ID of the view that will be used as the
     * viewfinder for the QR code scanner.
     *
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
     * tryStartLocation attempts to start location updates if the required
     * location permissions are granted.
     * @param shouldRequest whether to request permission for location
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
     * startLocation defines a private method startLocation() which is used to
     * start retrieving the device's location.
     */
    private void startLocation() {
        mLocationHelper.startLocation(
                LocationManager.GPS_PROVIDER, new LocationHelper.LocationCallback() {
                    @Override
                    public void onSuccess(double latitude, double longitude) {
                        mLocationHelper.stopLocation();
                        mGeoPoint = new GeoPoint(latitude, longitude);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Location location = mLocationHelper.getNetwork();
                        if (location != null) {
                            mGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }
}