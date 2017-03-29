package com.as.atlas.demofileexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_PERMISSION_FOR_CAMERA = 0;

    private static final int CODE_REQUEST_PATH = 1;
    private static final int CODE_TAKE_PHOTO_CODE = 2;
    private int count = 0;

    String FileName = "photo";

    String curFileName;
    EditText edittext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edittext = (EditText)findViewById(R.id.editText);

        PhotoUtil.FetchAll(this);

        isStoragePermissionGranted();
        isCameraPermissionGranted();


        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        Button capture = (Button) findViewById(R.id.cameraButton);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Here, the counter will be incremented each time, and the
                // picture taken by camera will be stored as 1.jpg,2.jpg
                // and likewise.
                File internalAppDir = new File (String.valueOf(getFilesDir())+ count +".jpg");

                count++;
                String file = dir + count +".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                    internalAppDir.createNewFile();
                }
                catch (IOException e)
                {
                    Log.d(TAG, "createNewFile Exception.");
                }

//                Uri outputFileUri = Uri.fromFile(newfile);
//                Uri outputFileUri = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName() + ".provider", newfile);

                Uri outputFileUri = Uri.fromFile(internalAppDir);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Log.d(TAG, "outputFileUri:" + outputFileUri);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, CODE_TAKE_PHOTO_CODE);
            }
        });
    }

    private void isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_FOR_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_FOR_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                Log.d(TAG, "PERMISSION_GRANTED");
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
                Log.d(TAG, "PERMISSION_DENIED");
            }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

//    public void takePhoto(View view) {  // Android will check parameter View view
//        Log.d(TAG, "takePhoto");
//    }

    public void getfile(View view){
        Intent intent1 = new Intent(this, FileChooser.class);
        startActivityForResult(intent1, CODE_REQUEST_PATH);
    }

    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        if (requestCode == CODE_REQUEST_PATH) {
            if (resultCode == RESULT_OK) {
                curFileName = data.getStringExtra("GetFileName");
                edittext.setText(curFileName);
            }
        } else if (requestCode == CODE_TAKE_PHOTO_CODE) {
            //Check if data in not null
            if (data != null) {
// 建立資料夾
                File imageStorageFolder =
                        new File(Environment.getExternalStorageDirectory() + File.separator + FileName);

                if (!imageStorageFolder.exists()) {
                    imageStorageFolder.mkdirs();
                    Log.d(TAG, "Folder created at: " + imageStorageFolder.toString());
                }
                //Check if data in not null
                if (data != null) {
                    String imageName = "image";
                    String fileNameExtension = ".jpg";
                    File sdCard = Environment.getExternalStorageDirectory();
                    String strImageStorageFolder = File.separator + FileName + File.separator;
                    File destinationFile =
                            new File(sdCard, strImageStorageFolder + imageName + fileNameExtension);
                    Log.d(TAG, "the destination for image file is: " + destinationFile);
                    if (data.getExtras() != null) {
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        int height = size.y;

                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
                        if (bitmap.getWidth() > bitmap.getHeight())
                            ScalePic(bitmap, height);
                        else
                            ScalePic(bitmap, width);
                        try {
                            FileOutputStream out = new FileOutputStream(destinationFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            Log.e(TAG, "ERROR:" + e.toString());
                        }

                    }
                }

            }
            Log.d("CameraDemo", "Pic saved");

        }
    }

    private void ScalePic(Bitmap bitmap,int phone)
    {
        //縮放比例預設為1
//            float mScale = 1 ;
//            //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
//            if(bitmap.getWidth() > phone )
//            {
//                //判斷縮放比例
//                mScale = (float)phone/(float)bitmap.getWidth();
//                Matrix mMat = new Matrix() ;
//                mMat.setScale(mScale, mScale);
//                Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
//                        0,
//                        0,
//                        bitmap.getWidth(),
//                        bitmap.getHeight(),
//                        mMat,AlarmCheckHelp
//                        false);
//                imageView.setImageBitmap(mScaleBitmap);
//            }
//            else imageView.setImageBitmap(bitmap);
    }

}
