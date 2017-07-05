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

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.joshi.islandproperties.dropbox_classes.DropboxClientFactory;


public class CreateFolder extends AsyncTask<Void, Void, Boolean> {

    private String path;
    private Context context;
    private final ProgressDialog mDialog;
    public AddNewProperty obj;
    String strError;

    public CreateFolder(Context context,
                        String path) {
        this.context = context.getApplicationContext();
        this.path = path;

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Creating Folder");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            DropboxClientFactory.getClient().files().createFolder(path);
            return true;
        } catch (CreateFolderErrorException e) {
            e.printStackTrace();
            strError = e.toString();
        } catch (DbxException e) {
            e.printStackTrace();
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
