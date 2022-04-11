package com.example.plantleave;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private static final int REQCODE = 0x10;
    public final static int REQIMG = 1;
    private static final int PERSTORE = 0x20;
    private static final int CANCROP = 2;

    @BindView(R.id.imageView)ImageView imageView;

    private File imgFile =null;
    private Uri fUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        requestStoragePermission();
        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


    @OnClick(R.id.imageButton2)
    public void takeImage(View view) {
        createImageFile();
        fUri =Uri.fromFile( imgFile);
        Intent newIntent= new Intent( "android.media.action.IMAGE_CAPTURE" );
        newIntent.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
        startActivityForResult( newIntent, REQIMG);
    }

    @OnClick(R.id.imageButton)
    public void selectImage(View view) {
        Intent chooseIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseIntent, REQCODE);
    }

    @OnClick(R.id.button)
    public void upload(View view) {
        uploadImage( "http://10.0.2.2:8090/file/upload","outputFileUri");
        Intent newIntent=new Intent(MainActivity.this, ResultActivity.class);

        startActivity(newIntent);
    }

    public void uploadImage(String url, String imageUrl) {
        OkHttpClient okHttpClient = new OkHttpClient();
        if(fUri == null) {
            //
        }
        else {
            try {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (imgFile != null){
                    //Log.d("TAG", String.valueOf(imgFile));
                    builder.addFormDataPart("file",imgFile.getName(),
                            RequestBody.create(MediaType.parse
                            ("image*//*"), imgFile));
                }
                MultipartBody reqBody = builder.build();
                Request newRequest = new Request.Builder().url(url).post(reqBody).build();

                Call call = okHttpClient.newCall(newRequest);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ex){ex.printStackTrace();}
                    @Override
                    public void onResponse(Call call, Response response) throws IOException{
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsObj = new JSONObject(response.body().string());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (resCode != RESULT_OK) {
            //
            Log.d("TAG","exception");
        }
        else{
            switch (reqCode) {
                case REQIMG:
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType( fUri,"image/*");

                    intent.putExtra("crop", "true");
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX",500);
                    intent.putExtra("outputY",500);
                    intent.putExtra("aspectX",1);
                    intent.putExtra("aspectY",1);
                    intent.putExtra("return-data",false);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT,fUri);
                    Toast.makeText( MainActivity.this,"Crop", Toast.LENGTH_SHORT).show();

                    Intent newIntent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    newIntent.setData(fUri);

                    this.sendBroadcast(newIntent);
                    startActivityForResult(newIntent,CANCROP);

                    break;

                case CANCROP:
                    try {
                        Bitmap bm = BitmapFactory.decodeStream( getContentResolver().openInputStream(fUri) );
                        Toast.makeText(MainActivity.this,fUri.toString(),Toast.LENGTH_SHORT).show();

                        imageView.setImageBitmap(bm);
                    } catch(FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    break;

                case REQCODE:
                    Uri uri = data.getData();
                    fUri = data.getData();

                    Intent newIntent2 = new Intent("com.android.camera.action.CROP");

                    newIntent2.setDataAndType( uri,"image/*" );
                    newIntent2.putExtra("crop","true");
                    newIntent2.putExtra("scale",true);
                    newIntent2.putExtra("outputX", 500);
                    newIntent2.putExtra("outputY", 500);
                    newIntent2.putExtra("aspectX", 1);
                    newIntent2.putExtra("aspectY", 1);
                    newIntent2.putExtra("return-data", false);

                    createImageFile();
                    fUri = Uri.fromFile( imgFile);
                    newIntent2.putExtra(MediaStore.EXTRA_OUTPUT,fUri);
                    Toast.makeText(MainActivity.this,"Crop",Toast.LENGTH_SHORT).show();
                    startActivityForResult(newIntent2, CANCROP);
                    break;
            }
        }
    }

    private void requestStoragePermission() {
        int permissioned= ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("TAG","Begin"+permissioned);
        if (permissioned==PackageManager.PERMISSION_GRANTED){
            //
        }else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERSTORE);
        }
    }

    private String name;

    public void createImageFile() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        Date dte=new Date(System.currentTimeMillis());
        name=fmt.format(dte);
        //SD
        //File outputImage = new File(Environment.getExternalStorageDirectory(),"new.jpg");
        //DCIM
        File pth = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        imgFile=new File(pth,name+".jpg");
    }

}