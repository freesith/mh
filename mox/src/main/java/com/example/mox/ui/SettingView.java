package com.example.mox.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.mox.ManholeConstants;
import com.example.mox.Mox;
import com.example.mox.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingView extends LinearLayout implements View.OnClickListener {

    private EditText etPath;
    private Button btnEdit;
    private Button btnRefresh;

    //TODO 2019-11-21 by WangChao
    private String lastPath = "";
    private Context context;

    public SettingView(Context context) {
        super(context);
        init(context);
    }

    public SettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SettingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_setting, this);
        etPath = view.findViewById(R.id.etPath);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        btnEdit.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);


        etPath.setText(Mox.getInstance().sp.getString(ManholeConstants.KEY_DB_PATH));

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEdit) {

            if (etPath.isEnabled()) {
                String trim = etPath.getText().toString().trim();
                if (!lastPath.equals(trim)) {
                    //TODO 2019-11-21 by WangChao 保存
                    refresh();
                }
                etPath.setEnabled(false);
                btnEdit.setText("编辑");
            } else {
                etPath.setEnabled(true);
                etPath.setSelection(etPath.getText().length());
                btnEdit.setText("保存");
            }

        } else if (v.getId() == R.id.btnRefresh) {
            refresh();
        }
    }

    private void refresh() {
        String trim = etPath.getText().toString().trim();
        if (trim.startsWith("http")) {
            downloadDbFile(trim);
            Mox.getInstance().sp.put(ManholeConstants.KEY_DB_PATH, trim);
        } else if (trim.startsWith("/")) {
            copyDBFile(trim);
            Mox.getInstance().initDb(context, context.getFilesDir().getAbsolutePath() + File.separator + ManholeConstants.DB_NAME);
            Mox.getInstance().sp.put(ManholeConstants.KEY_DB_PATH, trim);
        }
    }

    private void  downloadDbFile(String url) {
        final Request request = new Request.Builder().url(url).header("Cache-Control","no-cache").get().build();
        OkHttpClient client = new OkHttpClient.Builder().build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"fail:" + e.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    int code = response.code();
                    if (code != 200 || response.body() == null) {
                        return;
                    }

                    InputStream inputStream = response.body().byteStream();
                    File dbFile = new File(context.getFilesDir(), ManholeConstants.DB_NAME);
                    if (!dbFile.exists()) {
                        try {
                            dbFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(dbFile);
                        byte[] buffer = new byte[4096];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer,0,len);
                        }
                        Mox.getInstance().initDb(context, context.getFilesDir().getAbsolutePath() + File.separator + ManholeConstants.DB_NAME);
                        post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "complete",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            }catch (IOException e) {

                            }
                        }
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            }catch (IOException e) {

                            }
                        }
                    }

                }
            }
        });

    }

    private void copyDBFile(String path) {
        File file = new File(path);
        if (file.exists()) {

            File dbFile = new File(context.getFilesDir(), ManholeConstants.DB_NAME);
            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(file);
                outputStream = new FileOutputStream(dbFile);
                FileUtils.copy(inputStream, outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }catch (IOException e) {

                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    }catch (IOException e) {

                    }
                }
            }

        }
    }
}
