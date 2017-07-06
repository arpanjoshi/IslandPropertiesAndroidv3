
/**
 * Created by David on 1/8/2016.
 */
package com.joshi.islandproperties;

/**
 * Created by David on 1/5/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.joshi.islandproperties.dropbox_classes.DropboxClientFactory;
import com.joshi.islandproperties.interfaces.OnDeleteSuccess;


public class DeleteFolder extends AsyncTask<Void, Void, Boolean> {

    private String path;
    private Context context;
    private ListView mListView;
    private View mView;
    private int mPosition;

    private final ProgressDialog mDialog;

    public AddNewProperty obj;
    OnDeleteSuccess onDeleteSuccess;

    public DeleteFolder(Context context,
                        String path, int position, OnDeleteSuccess onDeleteSuccess) {
        this.context = context.getApplicationContext();
        this.path = path;
        this.onDeleteSuccess = onDeleteSuccess;
        this.mPosition = position;

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Deleting Directory");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            DropboxClientFactory.getClient().files().delete(path);
            return true;
        } catch (DeleteErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "Property deleted Sucesfully!",
                    Toast.LENGTH_LONG).show();
            onDeleteSuccess.onDeleteSuccess();
        } else {
            Toast.makeText(context, "Failed to delete property", Toast.LENGTH_LONG)
                    .show();
        }
        mDialog.dismiss();
    }
}
