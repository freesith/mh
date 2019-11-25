package com.example.mox.ui;

import android.content.Context;
import android.os.Build;
import android.os.FileUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.mox.ManholeConstants;
import com.example.mox.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

        } else if (trim.startsWith("/")) {
            copyDBFile(trim);
        }
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
