package com.joshi.islandproperties;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/*************************************************************************
 * Created by prateekarora on 05/07/17.
 */


public class DownloadFileTask extends AsyncTask<Void, Void, File> {

    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;
    String name;
    String lowerPath;
    String rev;

    public interface Callback {
        void onDownloadComplete(File result);
        void onError(Exception e);
    }

    DownloadFileTask(Context context, DbxClientV2 dbxClient, Callback callback,
                     String name, String lowerPath, String rev) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
        this.name = name;
        this.lowerPath = lowerPath;
        this.rev = rev;
    }


    @Override
    protected void onPostExecute(File result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDownloadComplete(result);
        }
    }

    @Override
    protected File doInBackground(Void... params) {
        File file = null;
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            file = new File(path, name);

            // Make sure the Downloads directory exists.
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    mException = new RuntimeException("Unable to create directory: " + path);
                }
            } else if (!path.isDirectory()) {
                mException = new IllegalStateException("Download path is not a directory: " + path);
            }

            // Download the file.
            try (OutputStream outputStream = new FileOutputStream(file)) {
                mDbxClient.files().download(lowerPath, rev)
                        .download(outputStream);
            } catch (DownloadErrorException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            }

            // Tell android about the file
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            mContext.sendBroadcast(intent);

        } catch (IOException e) {
            mException = e;
        }

        return file;
    }
}