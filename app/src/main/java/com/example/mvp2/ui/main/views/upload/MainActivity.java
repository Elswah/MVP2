package com.example.mvp2.ui.main.views.upload;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mvp2.BuildConfig;
import com.example.mvp2.R;
import com.example.mvp2.ui.main.model.PojoResponse;
import com.example.mvp2.ui.main.model.Status;
import com.example.mvp2.ui.main.utils.FileCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements UploadInterface.View {
    public static final String TAG = "MainActivity";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    UploadActivityPresenter presenter;
    FileCompressor mCompressor;
    @BindView(R.id.btn_choose)
    Button choose;
    @BindView(R.id.btn_upload)
    Button uploading;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.img)
    ImageView img;
    Status status;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // bind the view using butterknife
        ButterKnife.bind(this);
        mCompressor = new FileCompressor(this);

        status = new Status();


    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter = new UploadActivityPresenter(this);

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }


    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Create file with current timestamp name
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    /**
     * Get real file path from URI
     *
     * @param contentUri
     * @return
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @OnClick(R.id.btn_choose)
    public void chooseImage() {
        selectImage();

    }

    @OnClick(R.id.btn_upload)
    public void upload() {
        try {
            if (!uploading.getText().equals("cancel")) {
                status.setStatus("upload");
                presenter.uploadBtnClicked(status.getStatus(), mPhotoFile.getAbsolutePath());
            } else {
                status.setStatus("cancel");
                presenter.uploadBtnClicked(status.getStatus(), mPhotoFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error Select File");
        }
    }

    @OnClick(R.id.img)
    public void showFullImage() {
        presenter.imageClicked();
    }

    @Override
    public void setStatus(String status) {
        if (status.equals("upload")) {
            uploading.setText("cancel");
        } else if (status.equals("cancel")) {

        } else if (status.equals("Done")) {
            progressBar.setVisibility(View.GONE);
            uploading.setVisibility(View.GONE);
            choose.setVisibility(View.GONE);
        }

    }


    @Override
    public void selectFileFirst() {
        Toast.makeText(this, "Select File First", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getResponse(PojoResponse response) {
        //Picasso.get().load(response.getThumbnail().trim()).into(img);
        url = response.getUrl().trim();
        Picasso.get()
                .load(response.getThumbnail().trim())
                .resize(100, 100)
                .centerCrop()
                .into(img);
        uploading.setText("upload");
    }

    @Override
    public void errorUploading(Call<PojoResponse> call, Throwable t) {
        if (call.isCanceled()) {
            Toast.makeText(this, "Upload canceled", Toast.LENGTH_LONG).show();
            uploading.setText("upload");
        } else {
            Toast.makeText(this, "error in uploading", Toast.LENGTH_LONG).show();
            uploading.setText("upload");
        }
    }

    @Override
    public void setProgressPercent(int percent) {
        progressBar.setProgress(percent);
    }

    @Override
    public void setProgressFinished() {
        // set the progress
        progressBar.setProgress(100);
        /*Toast.makeText(this, "Upload finished", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                progressBar.setProgress(0);
            }
        }, 2000);
        // uploadProgress.setProgress(0);*/

        // thread is used to change the progress value
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(0);
            }
        });
        thread.start();
    }

    @Override
    public void showFullImageInFragment() {
        Bundle bundle = new Bundle();
        if (url != null && !url.isEmpty()) {
            bundle.putString(DialogFragment.key, url);
            DialogFragment dialogFragment = new DialogFragment();
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getSupportFragmentManager(), "DialogFragment");
        }
    }


}
