package com.joshi.islandproperties;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import android.widget.ListView;
import android.widget.TextView;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 1/8/2016.
 */
public class DLFiles extends AsyncTask<Void, Long, String[]> {

    private Context mContext;
    private final ProgressDialog mDialog;
    private DropboxAPI<?> mApi;
    private String mPath;

    private String mErrorMsg;
    private String[] fnames;

    private ListView listView1;
    private String mWhatActivity;

    public DLFiles(Context context, DropboxAPI<?> api,
                   String dropboxPath, String[] efnames, ListView listView, String whatActivity) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();
        fnames = efnames;
        mApi = api;
        mPath = dropboxPath;
        mWhatActivity = whatActivity;

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Opening Directory");
        mDialog.setCancelable(false);
        mDialog.show();

        listView1 = listView;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        // Get the metadata for a directory

        try {
            ArrayList<String> filenames = new ArrayList<String>();
            DropboxAPI.Entry dirent;
            dirent = mApi.metadata(mPath, 100, null, true, null);
            for (DropboxAPI.Entry ent : dirent.contents) {
                if (ent.isDir) {
                    //Add it to the filenames we can choose from
                    filenames.add(ent.fileName());
                }
//                else {
//                    filenames.add(ent.fileName());
//                }
            }
            fnames = filenames.toArray(new String[filenames.size()]);

        } catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Download canceled";
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                // too many entries to return
            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                // can't be thumbnailed
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
            } else {
                // Something else
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
        }
        return fnames;
    }

//    @Override
//    protected void onProgressUpdate(Long... progress) {
//        int percent = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
//        mDialog.setProgress(percent);
//    }

    @Override
    protected void onPostExecute(String[] result) {

        try {
            if (result != null) {
                if (mWhatActivity.equals("SettingsActivity") ){
                    SettingsActivity.mfnames  = result;

                    mDialog.dismiss();

                    for (int i = 0; i < fnames.length; ++i) {
                        SettingsActivity.list.add(fnames[i]);
                    }

                    StableArrayAdapter adapter = new StableArrayAdapter(mContext,
                            R.layout.lst_setting_item, SettingsActivity.list);
                    SettingsActivity.mListView.setAdapter(adapter);
                }
                if (mWhatActivity.equals("SearchActivity") ){
                    SearchActivity.mfnames  = result;

                    mDialog.dismiss();

                    for (int i = 0; i < fnames.length; ++i) {
                        SearchActivity.list.add(fnames[i]);
                    }

                    SearchListAdapter adapter = new SearchListAdapter(mContext, SearchActivity.list);
                    SearchActivity.mListView.setAdapter(adapter);
                }
            }
//            else
//                showToast("result==null");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
    private class SearchListAdapter extends BaseAdapter {

        private  LayoutInflater inflater=null;
        private Context c;
        List<String> lstData;
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public SearchListAdapter(Context context, List<String> objects) {

            c = context;
            lstData = objects;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return lstData.size();
        }

        @Override
        public Object getItem(int position) {
            return lstData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi=convertView;
            if(convertView==null)
            {
                vi = inflater.inflate(R.layout.lst_search_item, null);
            }

            TextView title = (TextView)vi.findViewById(R.id.lst_serarch_text); // title

            // Setting all values in listview
            title.setText(lstData.get(position));

            return vi;
        }
    }
}