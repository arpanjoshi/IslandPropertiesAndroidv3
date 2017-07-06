package com.joshi.islandproperties;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.joshi.islandproperties.dropbox_classes.DropboxActivity;
import com.joshi.islandproperties.dropbox_classes.DropboxClientFactory;
import com.joshi.islandproperties.dropbox_classes.PicassoClient;
import com.joshi.islandproperties.interfaces.OnDeleteSuccess;
import com.joshi.islandproperties.list_folders.FilesAdapter;
import com.joshi.islandproperties.list_folders.ListFolderTask;

import java.util.ArrayList;


public class SettingsActivity extends DropboxActivity implements OnDeleteSuccess {

    private final String FILE_DIR = "/";
    public static String[] mfnames = null;

    public RecyclerView mListView;
    public static ArrayList<String> list;

    public static String deleteFolderPath;
    private int mPosition;

    FilesAdapter mFilesAdapter;
    FileMetadata mSelectedFile;

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

        mListView = (RecyclerView) findViewById(R.id.list_settings);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setHasFixedSize(true);
        //Layout manager for Recycler view
        mListView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<String>();

        mFilesAdapter = new FilesAdapter(PicassoClient.getPicasso(), new FilesAdapter.Callback() {
            @Override
            public void onFolderClicked(FolderMetadata folder) {
                deleteFolderPath = folder.getPathLower();
                TextView tvDelete = (TextView) findViewById(R.id.tv_delete);
                tvDelete.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFileClicked(FileMetadata file) {

            }
        });

        //hide the delete text view
        TextView tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvDelete.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                Toast.makeText(SettingsActivity.this,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute("");
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */

    public void btnDeleteClicked(View view) {
        view.setVisibility(View.INVISIBLE);
        DeleteProperty(deleteFolderPath);
    }

    void DeleteProperty(String folderPath) {
        DeleteFolder folder = new DeleteFolder(this,
                folderPath, mPosition, this);
        folder.execute();
    }

    @Override
    public void onDeleteSuccess() {
        loadData();
    }
}
