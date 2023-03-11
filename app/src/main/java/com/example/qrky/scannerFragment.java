package com.example.qrky;

import static com.google.firebase.firestore.FieldValue.arrayUnion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
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
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.Result;
import com.king.zxing.CaptureFragment;

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
    private Bitmap mIvPhotoBitmap;

    private String mCode;
    private GeoPoint mGeoPoint;
    private String mPhoto = "";
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private final FirebaseStorage mStorage =
            FirebaseStorage.getInstance("gs://qrky-e8bc8.appspot.com");

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
    private void goSaveLibrary(boolean isLocationRequired) {
        if (TextUtils.isEmpty(mCode)) {
            Toast.makeText(requireContext(), "Code not valid.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isLocationRequired && Objects.isNull(mGeoPoint)) {
            Toast.makeText(requireContext(), "Wait for location succeed.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Map<String, Object> map = new HashMap<>();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] encodedhash = digest.digest(
                mCode.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (int i = 0; i < encodedhash.length; i++) {
            String hex = Integer.toHexString(0xff & encodedhash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        if (mDb.collection("QR Codes")
                .document(hexString.toString())
                .get().isSuccessful(
        )) {
            mDb.collection("QR Codes")
                    .document(hexString.toString())
                    .update("playerID", arrayUnion("playerID"));
            return;
        }
        map.put("hash", hexString.toString());
        String UniqueName = makeName(hexString.toString());
        map.put("Name", UniqueName);
        if(isLocationRequired) {
            map.put("location", mGeoPoint);
        }
        map.put("photo", mPhoto);
        map.put("timestamp", Timestamp.now());
        Log.d("TAGTAG", "goSaveLibrary: ");
        mDb.collection("QR Codes").add(map);
        MainActivity activity = (MainActivity) requireActivity();
        activity.switchTab(R.id.libraryFragment);
    }


    private String makeName(String str) {
        String[] strArr = str.split("");
        String[] wordArr = new String[6];
        String result = null;

        if((strArr[0].equals("0"))){
            wordArr[0] = "Loud ";
        }else{
            wordArr[0] = "Quiet ";
        }
        if((strArr[1].equals("0"))){
            wordArr[1] = "Far";
        }else{
            wordArr[1] = "Near";
        }
        if((strArr[2].equals("0"))){
            wordArr[2] = "Fast";
        }else{
            wordArr[2] = "Slow";
        }
        if((strArr[3].equals("0"))){
            wordArr[3] = "Full";
        }else{
            wordArr[3] = "Empty";
        }
        if((strArr[4].equals("0"))){
            wordArr[4] = "Young";
        }else{
            wordArr[4] = "Old";
        }
        if((strArr[5].equals("0"))){
            wordArr[5] = "Strong";
        }else{
            wordArr[5] = "Weak";
        }
        for (int i = 0; i < 6; i++) {
            result = wordArr[i];
        }

        return result;
    }

    private void confirmTrackLocation() {
        getParentFragmentManager().setFragmentResultListener(
                ConfirmDialog.class.getSimpleName(), getViewLifecycleOwner(),
                (requestKey, result) -> {
                    Log.d("TAGTAG", "confirmTrackLocation: ");
                    boolean confirm = result.getBoolean("confirm", false);
                    if (confirm) {
                        useCameraForTakingPicture();
                        tryStartLocation(true);
                    } else {
                        getCameraScan().startCamera();
                        goSaveLibrary(false);
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
                Camera camera = mCameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(), cameraSelector, preview, mImageCapture);
            } catch (Exception ignored) {}
        }, ContextCompat.getMainExecutor(requireContext()));
    }

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
        mIvPhoto.setVisibility(View.VISIBLE);
    }

    UploadTask uploadTask;

    private void savePhoto(ImageProxy image) {
        LoadingDialog dialog = LoadingDialog.newInstance();
        dialog.setCancelable(false);
        dialog.showNow(getParentFragmentManager(), LoadingDialog.class.getSimpleName());
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        mIvPhotoBitmap = BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        mIvPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//        mIvPhotoBitmap.recycle();
//        mIvPhotoBitmap = BitmapFactory.decodeStream(bais);
        mIvPhoto.setImageBitmap(mIvPhotoBitmap);
        StorageReference sr = mStorage.getReference();
        mPhoto = "QRImages/" + UUID.randomUUID() + ".jpg";
        StorageReference imageSr = sr.child(mPhoto);
        uploadTask = imageSr.putBytes(clonedBytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            dialog.dismissNow();
            goSaveLibrary(true);
        }).addOnFailureListener(e -> {
            dialog.dismissNow();
            Log.e(TAG, "savePhoto: ", e);
            Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        if (uploadTask != null) {
            uploadTask.cancel();
        }
        if (mIvPhotoBitmap != null) {
            mIvPhotoBitmap.recycle();
            mIvPhotoBitmap = null;
        }
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