package com.joshi.islandproperties;

import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.joshi.islandproperties.list_folders.ImageAdapter;
import com.joshi.islandproperties.list_folders.ListFolderTask;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;


public class DetailActivity extends DropboxActivity {

    public String folderName;
    public RecyclerView mGridview;
    public static ProgressDialog mDialog;
    private String strDir;

    ImageAdapter mFilesAdapter;
    FileMetadata mSelectedFile;

    public static ImageView largeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFilesAdapter = new ImageAdapter(PicassoClient.getPicasso(), new ImageAdapter.Callback() {
            @Override
            public void onFolderClicked(FolderMetadata folder) {

            }

            @Override
            public void onFileClicked(FileMetadata file) {

            }
        });

        com.joshi.islandproperties.SearchActivity.fromDetail = true;
        Intent intent = getIntent();
        folderName = intent.getStringExtra(com.joshi.islandproperties.SearchActivity.EXTRA_MESSAGE);

        strDir = "/"+folderName;
        largeImage = (ImageView)findViewById(R.id.imageView_large_detail);

        mGridview = (RecyclerView) findViewById(R.id.gridview_detail);
        mGridview.setItemAnimator(new DefaultItemAnimator());
        mGridview.setHasFixedSize(true);
        //Layout manager for Recycler view
        mGridview.setLayoutManager(new GridLayoutManager(this, 3));

    }

    public void btnAddClicked(View view){
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Getting pictures...");
        mDialog.setCancelable(false);
        mDialog.show();

        if(getTenPhotos()){
            UploadPicturesActivity.isPhoto = true;
        }else{
            UploadPicturesActivity.isPhoto = false;
        }
        Log.d("strDir", strDir);
        Intent intent = new Intent(this, UploadPicturesActivity.class).putExtra("strDir", strDir);
        startActivity(intent);
    }

    boolean getTenPhotos(){

//        File images = Environment.getExternalStorageDirectory();

        File images = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (images.exists()) {
            File test1 = new File(images, "100MEDIA/");
            if (test1.exists()) {
                images = test1;
            } else {
                File test2 = new File(images, "100ANDRO/");
                if (test2.exists()) {
                    images = test2;
                } else {
                    File test3 = new File(images, "Camera/");
                    if (!test3.exists()) {
                        test3.mkdirs();
                    }
                    images = test3;
                }
            }
        }

        if (images == null) return false;
        File[] imagelist = images.listFiles(new FilenameFilter(){

            public boolean accept(File dir, String name)
            {
                return ((name.endsWith(".jpg"))||(name.endsWith(".png")));
            }
        });
        if (imagelist == null) return false;
        if (imagelist.length > 0) {
            /** The newest file comes first **/
            Arrays.sort(imagelist, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }
        int count = imagelist.length<10 ? imagelist.length : 10; //only latest 10 photos
        UploadPicturesActivity.mFiles = new String[count];

        for(int i= 0 ; i< count; i++)
        {
            UploadPicturesActivity.mFiles[i] = imagelist[i].getAbsolutePath();

        }
        UploadPicturesActivity.mUrls = new Uri[UploadPicturesActivity.mFiles.length];

        for(int i=0; i < UploadPicturesActivity.mFiles.length; i++)
        {
            UploadPicturesActivity.mUrls[i] = Uri.parse(UploadPicturesActivity.mFiles[i]);
        }

        return true;
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
                mGridview.setAdapter(mFilesAdapter);
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                Toast.makeText(DetailActivity.this,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(strDir);
    }
}
