package com.joshi.islandproperties;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.Toast;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.joshi.islandproperties.dropbox_classes.DropboxActivity;
import com.joshi.islandproperties.dropbox_classes.DropboxClientFactory;
import com.joshi.islandproperties.dropbox_classes.PicassoClient;
import com.joshi.islandproperties.list_folders.FilesAdapter;
import com.joshi.islandproperties.list_folders.ListFolderTask;

import java.util.ArrayList;


public class SearchActivity extends DropboxActivity {

    private final String FILE_DIR = "/";
    public static String[] mfnames = null;

    public RecyclerView mListView;
    public static ArrayList<String> list;
//    public static StableArrayAdapter adapter;

//    public static String deleteFolderPath;
//    private  int mPosition;
    public static boolean fromDetail = false;

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";


    FilesAdapter mFilesAdapter;
    FileMetadata mSelectedFile;

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

        mFilesAdapter = new FilesAdapter(PicassoClient.getPicasso(), new FilesAdapter.Callback() {
            @Override
            public void onFolderClicked(FolderMetadata folder) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, folder.getName());
                startActivity(intent);
            }

            @Override
            public void onFileClicked(FileMetadata file) {
                Log.d("file meta data", file.toString());
            }
        });

        mListView = (RecyclerView) findViewById(R.id.list_search);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setHasFixedSize(true);
        //Layout manager for Recycler view
        mListView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<String>();

        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, mfnames[position]);
                startActivity(intent);
            }
        });*/
    }
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

    }

    @Override
    protected void loadData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();

        new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult result) {
                dialog.dismiss();
                mFilesAdapter.setFiles(result.getEntries());
                mListView.setAdapter(mFilesAdapter);
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                Toast.makeText(SearchActivity.this,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute("");
    }
}
