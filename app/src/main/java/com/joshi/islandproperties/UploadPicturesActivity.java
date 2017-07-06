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
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UploadPicturesActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    public static File myDiscCacheFile;
    public static File tempFile;
    public static boolean isPhoto;
    public static ProgressDialog mDialog;
    public String strPropertyName;

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

        fileList = new ArrayList<String>();

        strPropertyName = getIntent().getStringExtra("strDir");

        //hide the upload text view
        TextView tvDelete = (TextView) findViewById(R.id.tv_upload);
        tvDelete.setVisibility(View.INVISIBLE);

        selectedElements = new boolean[10];
        nSelected = 0;
        if (isPhoto) {
            mGridview = (GridView) findViewById(R.id.gridview);
            mGridview.setAdapter(new ImageAdapter(this));

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
                    if (nSelected > 0) {
                        tvUpload.setVisibility(View.VISIBLE);
                    } else {
                        tvUpload.setVisibility(View.GONE);
                    }

                    //set largeView
                    ImageView iv = (ImageView) findViewById(R.id.imageView_large);
                    //url to bitmap
                    File image = new File(mFiles[position]);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap, iv.getWidth(), iv.getHeight(), true);
                    iv.setImageBitmap(bitmap);


                }
            });
        }
        if (AddNewProperty.mDialog != null) AddNewProperty.mDialog.dismiss();
        if (DetailActivity.mDialog != null) DetailActivity.mDialog.dismiss();
    }


    public void btnUploadClicked(View view) {
        if (fileList.size() == 0) {
            Toast.makeText(this, "Select images or take a photo, please", Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Uploading Pictures");
        mDialog.setCancelable(false);
        mDialog.show();

        String fullpath;
        if (nSelected != fileList.size()) {
            Toast.makeText(this, "Number of selects Error!", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < fileList.size(); i++) {
            tempFile = new File(fileList.get(i));
            String str;
            str = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            final String photoName = "img_" + str + ".jpg";
            fullpath = strPropertyName + photoName;
            UploadFileToDropbox proc = new UploadFileToDropbox(UploadPicturesActivity.this, fullpath,
                    tempFile, fileList.size());
            proc.execute();
        }
    }

    public void btnTakePhotoClicked(View view) {

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
                        String fullpath = strPropertyName + "/" + photoName;
                        UploadFileToDropbox upload = new UploadFileToDropbox(UploadPicturesActivity.this,
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

    private void saveFileInCache(String aFileName, Intent data) {
        Bitmap myImage = (Bitmap) data.getExtras().get("data");
        final String cachePath = this.getCacheDir().getPath();
        File myDiscCacheFilePath;

        myDiscCacheFilePath = new File(cachePath);
        myDiscCacheFile = new File(myDiscCacheFilePath + File.separator + aFileName);

        try {
            FileOutputStream out = new FileOutputStream(myDiscCacheFile);
            myImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Problem saving file in cache", Toast.LENGTH_SHORT).show();
        }

    }

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
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, 85, 85, true);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            return l;
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
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }
}
