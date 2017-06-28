package com.joshi.islandproperties;

import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;

import android.view.View;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;


public class DetailActivity extends AppCompatActivity {

    private DropboxAPI<AndroidAuthSession> dropbox;
    public String folderName;
    public static GridView mGridview;
    public static ProgressDialog mDialog;
    private String strDir;

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

        com.joshi.islandproperties.SearchActivity.fromDetail = true;
        Intent intent = getIntent();
        folderName = intent.getStringExtra(com.joshi.islandproperties.SearchActivity.EXTRA_MESSAGE);
//        Toast.makeText(getApplicationContext(),
//                        folderName, Toast.LENGTH_LONG)
//                        .show();
        dropbox = MainActivity.dropbox;

        strDir = "/" + folderName;
        ImageView iv = (ImageView)findViewById(R.id.imageView_large_detail);
//        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mGridview = (GridView) findViewById(R.id.gridview_detail);


        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                ImageView i = (ImageView)findViewById(R.id.imageView_large_detail);
                i.setImageDrawable(DownloadPicture.mDrawableList.get(position));
//                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }
        });

        //download pictures
        DownloadPicture download = new DownloadPicture(
                DetailActivity.this, dropbox, strDir, iv);
        download.execute();
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
        UploadPicturesActivity.strPropertyName = strDir;
        Intent intent = new Intent(this, UploadPicturesActivity.class);
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
}
