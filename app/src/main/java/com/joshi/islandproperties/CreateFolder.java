package com.joshi.islandproperties;

/**
 * Created by David on 1/5/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;


public class CreateFolder extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI<?> dropbox;
    private String path;
    private Context context;
    private final ProgressDialog mDialog;
    public AddNewProperty obj;
    String strError;

    public CreateFolder(Context context, DropboxAPI<?> dropbox,
                               String path) {
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Creating Folder");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            dropbox.createFolder(path);
            return true;
        }  catch (DropboxException e) {
            e.printStackTrace();
            strError = e.toString();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mDialog.dismiss();
        if (result) {

            ImageView iv_check = (ImageView) obj.findViewById(R.id.iv_check);
            iv_check.setVisibility(View.VISIBLE);
            ImageView iv_camera = (ImageView) obj.findViewById(R.id.iv_camera);
            iv_camera.setVisibility(View.VISIBLE);
            iv_camera.setEnabled(true);
            TextView tv_check = (TextView) obj.findViewById(R.id.tv_check);
            tv_check.setText(obj.strCheck);
            tv_check.setTextColor(Color.WHITE);
            tv_check.setVisibility(View.VISIBLE);
            TextView tv_camera = (TextView) obj.findViewById(R.id.tv_camera);
            tv_camera.setText(obj.strCamera);
            tv_camera.setTextColor(Color.WHITE);
            tv_camera.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(context, strError, Toast.LENGTH_LONG)
                    .show();
        }
    }
}
