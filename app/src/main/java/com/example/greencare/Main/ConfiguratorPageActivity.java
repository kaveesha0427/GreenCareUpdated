package com.example.greencare.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.greencare.Classifiers.Configuration;
import com.example.greencare.Classifiers.ModelClassifier;
import com.example.greencare.Classes.PredictionResult;
import com.example.greencare.R;

import com.example.greencare.utils.ImageUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ConfiguratorPageActivity extends AppCompatActivity {

    private static final String IMAGE_DIRECTORY = "/GreenCare";
    private final int GALLERY = 1;
    private final int CAMERA = 2;
    private ImageView plantIv;
    private Button selectImageBtn, classifyBtn;
    private ModelClassifier modelClassifier;

    private BottomNavigationView bottomNav;

    private static String TAG = "ConfiguratorPageActivity";


    public static Intent getIntent(Context packageContext) {
        return new Intent(packageContext, ConfiguratorPageActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conifigurator_page);
        plantIv = findViewById(R.id.plant_input_image_view);
        selectImageBtn = findViewById(R.id.input_type_choose_button);
        classifyBtn = findViewById(R.id.classify_button);
        bottomNav = findViewById(R.id.bottom_nav);

        requestMultiplePermissions();
        loadImageClassifier();
        setBtnListeners();
        setUpNav();
    }

    private void loadImageClassifier() {
        try {
            modelClassifier = ModelClassifier.classifierGenerator(getAssets(), Configuration.TFLITE_MODEL);
        } catch (IOException e) {
            Toast.makeText(this, "Couldn't load model", Toast.LENGTH_SHORT).show();
            System.out.println(e.getMessage());
        }
    }

    private void setBtnListeners() {
        selectImageBtn.setOnClickListener(v -> {
            showPictureDialog();
        });

        classifyBtn.setOnClickListener(v -> predictInputImage());
    }

    public void predictInputImage() {
        if (plantIv.getDrawable() != null) {
            Bitmap bitmapVal = ((BitmapDrawable) plantIv.getDrawable()).getBitmap();
            bitmapVal = ThumbnailUtils.extractThumbnail(bitmapVal, getScreenWidth(), getScreenWidth());
            bitmapVal = ImageUtils.prepareImageForClassification(bitmapVal);

            List<PredictionResult> resultList = modelClassifier.predictImage(bitmapVal);
            PredictionResult predictionResult = resultList.get(0);
            String confidence = String.format(Locale.getDefault(), "%.2f %%", predictionResult.predictionConfidence * 100);

            Intent intent = new Intent(getBaseContext(), ResultScreen.class);
            intent.putExtra("result", predictionResult.predictionLabel);
            intent.putExtra("bitmap", bitmapVal);
            intent.putExtra("confidence", confidence);

            startActivity(intent);

        } else {
            Toast.makeText(ConfiguratorPageActivity.this, "Choose an Image first", Toast.LENGTH_SHORT).show();
        }
    }


    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    saveImage(bitmap);
                    Toast.makeText(ConfiguratorPageActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    plantIv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ConfiguratorPageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            plantIv.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(ConfiguratorPageActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();

            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void setUpNav() {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), ConfiguratorPageActivity.class));
                    return true;
                case R.id.nav_account:
                    startActivity(new Intent(getApplicationContext(), UserProfilePage.class));
                    return true;
                case R.id.nav_history:
                    startActivity(new Intent(getApplicationContext(), HistoryPageActivity.class));
                    return true;
                case R.id.nav_feedback:
                    startActivity(new Intent(getApplicationContext(), UserFeedBackActivity.class));
                    return true;
            }
            return false;
        });
    }
}