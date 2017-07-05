package com.joshi.islandproperties;

/**
 * Created by David on 1/5/2016.
 */
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.joshi.islandproperties.dropbox_classes.DropboxClientFactory;

public class UploadFileToDropbox extends AsyncTask<Void, Void, Boolean> {

    private String path;
    private Context context;
    private File file;
    private int count;
    private ProgressDialog mDialog = null;
    public static int nUploads;

    public UploadFileToDropbox(Context context,
                               String path, File ff, int cnt) {

        this.context = context.getApplicationContext();
        this.path = path;
        this.file = ff;
        this.count = cnt;


        if (this.count == -1){
            mDialog = new ProgressDialog(context);
            mDialog.setMessage("Uploading Photo");
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            FileInputStream fileInputStream = new FileInputStream(this.file);
            DropboxClientFactory.getClient().files().uploadBuilder(path)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(fileInputStream);
            return true;
        } catch (UploadErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        nUploads = nUploads + 1;

        if(this.count == -1)
            mDialog.dismiss();
        if (this.count == nUploads)
            UploadPicturesActivity.mDialog.dismiss();

        if (result) {
            if(this.count == -1)
                Toast.makeText(context, "Photo Uploaded Successfully!", Toast.LENGTH_LONG).show();
            else{
                String msg = String.format("Picture%d Uploaded Successfully!", nUploads);
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        } else {
            if(this.count == -1)
                Toast.makeText(context, "Failed to upload photo!", Toast.LENGTH_LONG).show();
            else {
                String msg = String.format("Failed to upload Picture%d!", nUploads);
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }
    }
}
