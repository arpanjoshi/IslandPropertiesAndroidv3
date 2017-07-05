package com.joshi.islandproperties;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.joshi.islandproperties.dropbox_classes.DropboxClientFactory;

/*************************************************************************
 * Created by prateekarora on 04/07/17.
 */


public class LogoutFromDB extends AsyncTask<Void, Void, Boolean> {

    Context context;
    private final CallBack mCallback;
    ProgressDialog dialog;

    public LogoutFromDB(Context context, CallBack mCallback) {
        this.context = context;
        this.mCallback = mCallback;
    }

    public interface CallBack {
        void isUserLoggedOut(boolean status);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Logging out...");
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            DropboxClientFactory.getClient().auth().tokenRevoke();
            return true;
        } catch (DbxException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isLogout) {
        super.onPostExecute(isLogout);
        try {
            if (dialog != null)
                dialog.dismiss();
        } catch (Exception e) {

        }
        mCallback.isUserLoggedOut(isLogout);

        if (isLogout) {
            Toast.makeText(context, "Logged out successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Some error occured please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
}
