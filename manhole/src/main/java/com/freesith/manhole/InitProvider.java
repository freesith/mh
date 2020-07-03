package com.freesith.manhole;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.freesith.manhole.core.ManholeContext;
import com.freesith.manhole.history.ManholeHistory;

import java.io.File;

public class InitProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context instanceof Application) {
            ManholeContext.context = context;
            ((Application)context).registerActivityLifecycleCallbacks(new MoxLifeCallbacks(context.getPackageName()));
            Manhole.init();
            File dbFile = new File(context.getFilesDir() + File.separator + ManholeConstants.MOCK_DB_NAME);
            if (dbFile.exists()) {
                Manhole.getInstance().initMockDb(context, dbFile.getAbsolutePath());
            }
            ManholeHistory.INSTANCE.init(context);
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
