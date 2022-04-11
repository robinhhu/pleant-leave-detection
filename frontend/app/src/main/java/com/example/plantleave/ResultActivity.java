package com.example.plantleave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {
    @BindView(R.id.resultContent)TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        ButterKnife.bind(this);
        getResult("http://10.0.2.2:8090/file/info/1");
    }

    @OnClick(R.id.returnButton)
    public void upload( View view) {
        Intent newIntent = new Intent(ResultActivity.this, MainActivity.class);
        startActivity(newIntent);
    }

    public void getResult(String url) {
        OkHttpClient client =new OkHttpClient();
        Request req= new Request.Builder().url(url).build();
        Call call=client.newCall(req);
        call.enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e){e.printStackTrace();}
            @Override
            public void onResponse(Call call, Response res) throws IOException {
                if(res.isSuccessful()) {
                    try {
                        JSONObject jsobj = new JSONObject(res.body().string());
                        String text = jsobj.get("result").toString();
                        textView.setText(text);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}