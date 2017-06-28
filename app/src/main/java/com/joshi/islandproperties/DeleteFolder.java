
/**
 * Created by David on 1/8/2016.
 */
package com.joshi.islandproperties;

/**
 * Created by David on 1/5/2016.
 */

        import java.util.HashMap;
        import java.util.List;

        import android.app.ProgressDialog;
        import android.content.Context;

        import android.os.AsyncTask;
        import android.view.View;
        import android.widget.ArrayAdapter;

        import android.widget.ListView;

        import android.widget.Toast;

        import com.dropbox.client2.DropboxAPI;
        import com.dropbox.client2.exception.DropboxException;


public class DeleteFolder extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI<?> dropbox;
    private String path;
    private Context context;
    private ListView mListView;
    private View mView;
    private int mPosition;

    private final ProgressDialog mDialog;

    public AddNewProperty obj;

    public DeleteFolder(Context context, DropboxAPI<?> db,
                        String path, ListView listView, View view, int position) {
        this.context = context.getApplicationContext();
        this.dropbox = db;
        this.path = path;
        this.mListView = listView;
        this.mView = view;
        this.mPosition = position;

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Deleting Directory");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {


        try {

            dropbox.delete("/"+path);
            return true;

        } catch (DropboxException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "Property deleted Sucesfully!",
                    Toast.LENGTH_LONG).show();

            String item = SettingsActivity.deleteFolderPath;
            SettingsActivity.list.remove(item);

            StableArrayAdapter adapter = new StableArrayAdapter(context,
                    R.layout.lst_setting_item, SettingsActivity.list);

            SettingsActivity.mListView.setAdapter(adapter);
            SettingsActivity.mListView.invalidate();

        } else {
            Toast.makeText(context, "Failed to delete property", Toast.LENGTH_LONG)
                    .show();
        }
        mDialog.dismiss();
    }
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }
        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
