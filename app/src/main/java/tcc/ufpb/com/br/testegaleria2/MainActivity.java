package tcc.ufpb.com.br.testegaleria2;

import android.media.MediaScannerConnection;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button buttonCamera, buttonGallery ;
    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public  static final int RequestPermissionCode  = 1 ;
    DisplayMetrics displayMetrics ;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageview);
        buttonCamera = (Button)findViewById(R.id.button2);
        buttonGallery = (Button)findViewById(R.id.button1);

        EnableRuntimePermission();

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClickImageFromCamera() ;

            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageFromGallery();

            }
        });

    }

    public void ClickImageFromCamera() {

        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStorageDirectory(),
                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);

        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        CamIntent.putExtra("return-data", true);

        startActivityForResult(CamIntent, 0);

    }

    public void GetImageFromGallery(){

        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {

            ImageCropFunction();

        }
        else if (requestCode == 2) {

            if (data != null) {

                uri = data.getData();

                ImageCropFunction();

            }
        }
        else if (requestCode == 1) {

            if (data != null) {
                try {
                    Bundle bundle = data.getExtras();
                    if(bundle != null) {
                        Bitmap bitmap = bundle.getParcelable("data");
                        imageView.setImageBitmap(bitmap);
                        saveImageToExternalStorage(bitmap);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void saveImageToExternalStorage(Bitmap finalBitmap) throws IOException {
        //String root =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File root = android.os.Environment.getExternalStorageDirectory();
        File myDir = new File(root + "/ABCdaForca");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 1000000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file1 = new File(myDir, fname);
        if (file1.exists())
            file1.delete();
        //file1.createNewFile();
        try {
            FileOutputStream out = new FileOutputStream(file1);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[] { file1.toString() },                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

    }

    public void ImageCropFunction() {

        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 3);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 1);

        } catch (ActivityNotFoundException e) {

        }
    }
    //Image Crop Code End Here

    public void EnableRuntimePermission(){

        if(Build.VERSION.SDK_INT > 23){

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                Toast.makeText(MainActivity.this,"Todas as permissões concedidas", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
            }

        }


    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;

        }

    }

}

