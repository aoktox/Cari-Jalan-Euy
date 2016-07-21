package com.jsproject.carijalaneuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Bundle b = new Bundle();
        OkHttpClient client = new OkHttpClient();
        String url = MapsActivity.API_GET_TITIK;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Titik[] titiks = new Gson().fromJson(response.body().string(), Titik[].class);
                b.putSerializable("titik",titiks);
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                i.putExtras(b);
                startActivity(i);
                finish();
            }
        });
    }
}
