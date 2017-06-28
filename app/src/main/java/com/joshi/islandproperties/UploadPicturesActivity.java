package com.joshi.islandproperties;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;

import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class UploadPicturesActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
//    private Uri fileUri;
    public  static File myDiscCacheFile;
    public static File tempFile;
    public static boolean isPhoto;
    public static ProgressDialog mDialog;
    public static String strPropertyName;

    public static Uri[] mUrls;
    public static String[] mFiles;
    ArrayList<String> fileList;

    private boolean selectedElements[];
    private GridView mGridview;
    int nSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pictures);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getPhotoDialog = new ProgressDialog(this);
//        getPhotoDialog.setMessage("Getting Photos");
//        getPhotoDialog.show();

        //get the last 10 photos
        fileList = new ArrayList<String>();

        //hide the upload text view
        TextView tvDelete = (TextView) findViewById(R.id.tv_upload);
        tvDelete.setVisibility(View.INVISIBLE);

        selectedElements = new boolean[10];
        nSelected = 0;
        if(isPhoto) {
            mGridview = (GridView) findViewById(R.id.gridview);
            mGridview.setAdapter(new ImageAdapter(this));
//            mGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
//            mGridview.setMultiChoiceModeListener(new MultiChoiceModeListener());


            mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    if (selectedElements[position] == false) {

                        v.setBackgroundColor(Color.YELLOW);
                        selectedElements[position] = true;
                        nSelected++;
                        fileList.add(mFiles[position]);

                    } else {
                        v.setBackgroundColor(Color.WHITE);
                        selectedElements[position] = false;
                        nSelected--;
                        fileList.remove(mFiles[position]);

                    }

                    //set upload button visiblity
                    TextView tvUpload = (TextView) findViewById(R.id.tv_upload);
                    if(nSelected>0){
                        tvUpload.setVisibility(View.VISIBLE);
//                        tvUpload.setEnabled(true);
                    }else{
                        tvUpload.setVisibility(View.GONE);
//                        tvUpload.setEnabled(false);
                    }

                    //set largeView
                    ImageView iv = (ImageView) findViewById(R.id.imageView_large);
                    //url to bitmap
                    File image = new File(mFiles[position]);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap,iv.getWidth(),iv.getHeight(),true);
                    iv.setImageBitmap(bitmap);


                }
            });
        }
        if(AddNewProperty.mDialog != null) AddNewProperty.mDialog.dismiss();
        if(DetailActivity.mDialog != null) DetailActivity.mDialog.dismiss();
    }


    public void btnUploadClicked(View view){
//        Toast.makeText(UploadPicturesActivity.this, "Upload clicked", Toast.LENGTH_SHORT).show();
        if (fileList.size()==0){
            Toast.makeText(this, "Select images or take a photo, please", Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Uploading Pictures");
        mDialog.setCancelable(false);
        mDialog.show();

        String fullpath;
        if(nSelected != fileList.size()){
            Toast.makeText(this, "Number of selects Error!", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i=0; i<fileList.size(); i++){
            tempFile = new File(fileList.get(i));

            String filename = String.format("/upload%d.jpg", i);
            fullpath = strPropertyName + filename;
            UploadFileToDropbox proc = new UploadFileToDropbox(UploadPicturesActivity.this, MainActivity.dropbox, fullpath,
                    tempFile, fileList.size());
            proc.execute();
        }
    }
    public void btnTakePhotoClicked(View view){

        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String str;
                str = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                final String photoName = "img_" + str + ".jpg";
                saveFileInCache(photoName, data);

                //confirm the upload
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("One photo taken. Are you sure to upload it?");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Upload the photo
                        String fullpath = AddNewProperty.strPropertyName + "/" + photoName;
                        UploadFileToDropbox upload = new UploadFileToDropbox(UploadPicturesActivity.this, MainActivity.dropbox,
                                fullpath, myDiscCacheFile, -1);//-1 shows photo uploading
                        upload.execute();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();
                    }
                });
                alertDialog.show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
            }
        }
    }
    private void saveFileInCache(String aFileName, Intent data){
        Bitmap myImage = (Bitmap) data.getExtras().get("data");
        final String cachePath = this.getCacheDir().getPath();
        File myDiscCacheFilePath;

        myDiscCacheFilePath = new File(cachePath);
        myDiscCacheFile = new File(myDiscCacheFilePath + File.separator + aFileName);

        try{
            FileOutputStream out = new FileOutputStream(myDiscCacheFile);
            myImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.close();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Problem saving file in cache", Toast.LENGTH_SHORT).show();
        }

    }
//    private class ImageAdapter extends BaseAdapter {
//        private Context mContext;
//
//        public ImageAdapter(Context c) {
//            mContext = c;
//        }
//
//        public int getCount() { return mUrls.length;  }
//
//        public Object getItem(int position) {
//            return mUrls[position];
//        }
//
//        public long getItemId(int position) {
//            return position;
//        }
//
//        // create a new ImageView for each item referenced by the Adapter
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inflater = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            View gridView;
//
//            if (convertView == null) {
//                gridView = new View(mContext);
//
//                // get layout from mobile.xml
//                gridView = inflater.inflate(R.layout.grid_item, null);
//
//                ImageView imageView = (ImageView) gridView
//                        .findViewById(R.id.grid_item_image);
//                imageView.setImageURI(mUrls[position]);
//
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            } else {
//                gridView = (View) convertView;
//            }
//            return gridView;
//        }
//
//    }
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        // Constructor
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mUrls.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            CheckableLayout l;

            if (convertView == null) {
                View gridView = new View(mContext);
                gridView = inflater.inflate(R.layout.grid_item, null);
               
//                imageView.setImageURI(mUrls[position]);

                l = new CheckableLayout(UploadPicturesActivity.this);
                l.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,
                        GridView.LayoutParams.WRAP_CONTENT));
                l.addView(gridView);

            } else {
                l = (CheckableLayout) convertView;
                ImageView imageView = (ImageView) l
                        .findViewById(R.id.grid_item_image);
                imageView.setImageDrawable(null);

            }
            ImageView imageView = (ImageView) l
                    .findViewById(R.id.grid_item_image);

            //url to bitmap
            File image = new File(mFiles[position]);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap,85,85,true);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            return l;

    //        CheckableLayout l;
    //        ImageView i;
    //
    //        if (convertView == null) {
    //            i = new ImageView(UploadPicturesActivity.this);
    //            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
    //            i.setLayoutParams(new ViewGroup.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
    //                    GridView.LayoutParams.MATCH_PARENT));
    //            l = new CheckableLayout(UploadPicturesActivity.this);
    //            l.setLayoutParams(new GridView.LayoutParams(95,
    //                    95));
    //            l.addView(i);
    //        } else {
    //            l = (CheckableLayout) convertView;
    //            i = (ImageView) l.getChildAt(0);
    //        }
    //
    //        i.setImageResource(mThumbIds[position]);
    //
    //        return l;

        }
    }

    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener{

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int selectCount = mGridview.getCheckedItemCount();
            //show the delete text view
            TextView tvUpload = (TextView) findViewById(R.id.tv_upload);
            tvUpload.setVisibility(View.VISIBLE);
            if (selectCount==0){
                tvUpload.setVisibility(View.GONE);
            }
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }
            ImageView iv = (ImageView) findViewById(R.id.imageView_large);
            //url to bitmap
            File image = new File(mFiles[position]);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap,iv.getWidth(),iv.getHeight(),true);
            iv.setImageBitmap(bitmap);

//            iv.setImageURI(mUrls[position]);


            //  add to or remove from fileList
            if (checked){
                fileList.add(mFiles[position]);
            }else{
                fileList.remove(mFiles[position]);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Select Items");
            mode.setSubtitle("One item selected");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
    public class CheckableLayout extends FrameLayout implements Checkable {
        private boolean mChecked;

        public CheckableLayout(Context context) {
            super(context);
        }

        public void setChecked(boolean checked) {
            mChecked = checked;
            setBackgroundColor(checked ? Color.YELLOW : Color.WHITE);
//            setBackgroundDrawable(checked ?
//                    getResources().getDrawable(R.drawable.photo_frame_small)
//                    : null);
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }
}
