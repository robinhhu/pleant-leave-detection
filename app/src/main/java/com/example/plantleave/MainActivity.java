package com.example.plantleave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.View;
import android.os.Bundle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_GALLERY = 0x10;
    public final static int REQUEST_IMAGE_CAPTURE = 1;
    private static final int STORAGE_PERMISSION = 0x20;
    private static final int CROP_PHOTO = 2;

    @BindView(R.id.imageView)ImageView imageView;// imageView

    private File imageFile = null;
    private Uri outputFileUri = null;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //takePhotos = (ImageButton) findViewById(R.id.imageButton2);
        ButterKnife.bind(this);

        requestStoragePermission();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    //start
    @OnClick(R.id.imageButton2)
    public void takeImage(View view) {
        createImageFile();
        outputFileUri = Uri.fromFile(imageFile);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @OnClick(R.id.imageButton)
    public void selectImage(View view) {
        Intent selectIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(selectIntent, REQUEST_CODE_GALLERY);
    }

    @OnClick(R.id.button)
    public void upload(View view) {
        Intent intent=new Intent(MainActivity.this,ResultActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.d("TAG","canceled or other exception!");
        }
        else{
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(outputFileUri, "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("scale", true);

                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);

                    intent.putExtra("outputX", 400);
                    intent.putExtra("outputY", 400);
                    intent.putExtra("return-data", false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    Toast.makeText(MainActivity.this, "Crop", Toast.LENGTH_SHORT).show();
                    //broadcast
                    Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intentBc.setData(outputFileUri);
                    //displayImage(outputFileUri);
                    this.sendBroadcast(intentBc);
                    startActivityForResult(intent, CROP_PHOTO);
                    break;

                case CROP_PHOTO:
                    try {
                        Log.v("MainActivity","Log.v输入日志信息");
                        //displayImage(outputFileUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(outputFileUri));
                        Toast.makeText(MainActivity.this, outputFileUri.toString(), Toast.LENGTH_SHORT).show();
                        Log.v("MainActivity",outputFileUri.toString());
                        imageView.setImageBitmap(bitmap);
                    } catch(FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case REQUEST_CODE_GALLERY:
                    outputFileUri = data.getData();
                    displayImage(outputFileUri);
//                    Intent intentSelect = new Intent();
//                    intentSelect.setAction(Intent.ACTION_GET_CONTENT);
//                    intentSelect.setType("image/*");
//                    startActivityForResult(Intent.createChooser(intentSelect, "Select Picture"),1);
                    //intentSelect.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    break;
            }
        }
    }

    private void requestStoragePermission() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("TAG","Begin" + hasCameraPermission);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED){

            Log.e("TAG", "Permission granted");
        }else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("TAG", "Ask for permission");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Log.e("TAG","Permission granted");
            }else {

            }
        }

    }

    private String name;
    public void createImageFile() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        name = format.format(date);
        //SD
        //File outputImage = new File(Environment.getExternalStorageDirectory(),"test.jpg");
        //DCIM
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        imageFile = new File(path, name + ".jpg");
    }

    //display image
    private void displayImage(Uri imageUri) {
        try{
            Glide.with(this)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .transform(new CenterCrop(this))
                    .into(imageView);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}