package com.example.moxy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mox.Mox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    OkHttpClient okHttpClient;
    public static final String TAG = "xxx";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            InputStream inputStream = getAssets().open("moc.db");
            File file = new File(getFilesDir() + "/moc.db");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream  fileOutputStream = new FileOutputStream(file);


            byte[] buffer = new byte[1024];
            int readBytes = 0;
            while ((readBytes = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, readBytes);
            }
            inputStream.close();
            fileOutputStream.close();

            Log.d(TAG, "onCreate: start init DB");
            Mox.getInstance().initDb(this.getApplicationContext(), file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request1();
            }
        });


        okHttpClient = new OkHttpClient.Builder().build();

    }


    private void request1 () {
        okHttpClient.newCall(new Request.Builder().url("http://www.baidu.com").get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d(TAG, "onResponse: response = " + string);
            }
        });
    }

}
