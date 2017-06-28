package com.joshi.islandproperties;

import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.TextView;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity {

    private DropboxAPI<AndroidAuthSession> dropbox;
    //    private final static String FILE_DIR = "/DropboxSample/";
//    private final static String DROPBOX_NAME = "dropbox_prefs";
//    private final static String ACCESS_KEY = "2jczkyw2vzma3so";
//    private final static String ACCESS_SECRET = "gio4u7uy57vpzx1";
//    final static private Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
//    public static boolean isLoggedIn = false;
//    public static boolean endDLFiles = false;
//    public static boolean endDeleteFolder = false;

    private final String FILE_DIR = "/";
    public static String[] mfnames = null;

    public static ListView mListView;
    public static ArrayList<String> list;
//    public static StableArrayAdapter adapter;

    public static String deleteFolderPath;
    private  int mPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dropbox = MainActivity.dropbox;

        mListView = (ListView) findViewById(R.id.list);
        list = new ArrayList<String>();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        mfnames[position], Toast.LENGTH_LONG)
//                        .show();
                //show the delete text view
                TextView tvDelete = (TextView) findViewById(R.id.tv_delete);
                tvDelete.setVisibility(View.VISIBLE);
                mPosition = position;
                deleteFolderPath = list.get(position);
//                DeleteProperty(folderPath);
            }
        });

        //hide the delete text view
        TextView tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvDelete.setVisibility(View.INVISIBLE);
    }
//    public class StableArrayAdapter extends ArrayAdapter<String> {
//
//        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
//
//        public StableArrayAdapter(Context context, int textViewResourceId,
//                                  List<String> objects) {
//            super(context, textViewResourceId, objects);
//            for (int i = 0; i < objects.size(); ++i) {
//                mIdMap.put(objects.get(i), i);
//            }
//        }
//        @Override
//        public long getItemId(int position) {
//            String item = getItem(position);
//            return mIdMap.get(item);
//        }
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

        }
        return true;
    }

    protected void onResume() {
        super.onResume();

        DLFiles dlf = new DLFiles(SettingsActivity.this, dropbox,
                FILE_DIR, mfnames, mListView, "SettingsActivity" );
        dlf.execute();
    }
      /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */

    public void btnDeleteClicked(View view){
        view.setVisibility(View.INVISIBLE);
//        Toast.makeText(getApplicationContext(),
//                "btn clicked", Toast.LENGTH_LONG)
//                .show();
        DeleteProperty(deleteFolderPath, view);
    }

    void DeleteProperty(String folderPath, View view){

        DeleteFolder folder = new DeleteFolder(this, dropbox,
                folderPath, mListView, view, mPosition);

        folder.execute();
    }
}
