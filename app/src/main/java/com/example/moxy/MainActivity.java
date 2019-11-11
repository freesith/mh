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

public class MainActivity extends Activity {
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
                Toast.makeText(MainActivity.this,"xxx",Toast.LENGTH_SHORT).show();
            }
        });



    }
}
