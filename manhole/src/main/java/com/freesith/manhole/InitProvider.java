package com.freesith.manhole;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class InitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context instanceof Application) {
            ((Application)context).registerActivityLifecycleCallbacks(new MoxLifeCallbacks());
            Mox.init();
            Mox.getInstance().sp = new Sp(context);
            File dbFile = new File(context.getFilesDir() + File.separator + ManholeConstants.DB_NAME);
            if (dbFile.exists()) {
                Mox.getInstance().initDb(context, dbFile.getAbsolutePath());
            }
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
