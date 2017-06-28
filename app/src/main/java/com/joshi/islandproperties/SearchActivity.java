package com.joshi.islandproperties;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {

    private DropboxAPI<AndroidAuthSession> dropbox;

    private final String FILE_DIR = "/";
    public static String[] mfnames = null;

    public static ListView mListView;
    public static ArrayList<String> list;
//    public static StableArrayAdapter adapter;

//    public static String deleteFolderPath;
//    private  int mPosition;
    public static boolean fromDetail = false;

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dropbox = MainActivity.dropbox;

        mListView = (ListView) findViewById(R.id.list_search);
        list = new ArrayList<String>();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, mfnames[position]);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(),
//                        mfnames[position], Toast.LENGTH_LONG)
//                        .show();
                //show the delete text view
//                TextView tvDelete = (TextView) findViewById(R.id.tv_delete);
//                tvDelete.setVisibility(View.VISIBLE);
//                mPosition = position;
//                deleteFolderPath = mfnames[position];
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

        }
        return true;
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
    protected void onResume() {
        super.onResume();

//        if (!fromDetail){
        list.clear();
            DLFiles dlf = new DLFiles(SearchActivity.this, dropbox,
                    FILE_DIR, mfnames, mListView, "SearchActivity");
            dlf.execute();
//        }
    }
}
