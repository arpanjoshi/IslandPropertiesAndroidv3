package com.joshi.islandproperties;

/**
 * Created by David on 1/10/2016.
 */
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.joshi.islandproperties.dropbox_classes.DropboxClientFactory;
import com.joshi.islandproperties.list_folders.ListFolderTask;


/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 */

public class DownloadPicture extends AsyncTask<Void, Long, Boolean> {

    private Context mContext;
    private final ProgressDialog mDialog;
    private DropboxAPI<?> mApi;
    private String mPath;
    private ImageView mView;
    public static ArrayList<Drawable>  mDrawableList;

    private FileOutputStream mFos;

    private boolean mCanceled;
    private Long mFileLen;
    private String mErrorMsg;

    // Note that, since we use a single file name here for simplicity, you
    // won't be able to use this code for two simultaneous downloads.
    private final static String IMAGE_FILE_NAME = "dbroulette.png";

    public DownloadPicture(Context context, DropboxAPI<?> api,
                           String dropboxPath, ImageView view) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();

        mApi = api;
        mPath = dropboxPath;
        mView = view;

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Downloading Image");
        mDialog.setCancelable(false);
//        mDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel",
//                new OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        mCanceled = true;
//                        mErrorMsg = "Canceled";
//
//                        // This will cancel the getThumbnail operation by
//                        // closing
//                        // its stream
//                        if (mFos != null) {
//                            try {
//                                mFos.close();
//                            } catch (IOException e) {
//                            }
//                        }
//                    }
//                });

        mDialog.show();

        mDrawableList = new ArrayList<Drawable>();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (mCanceled) {
                return false;
            }

            // Get the metadata for a directory
            DropboxAPI.Entry dirent = mApi.metadata(mPath, 1000, null, true, null);

            if (!dirent.isDir || dirent.contents == null) {
                // It's not a directory, or there's nothing in it
                mErrorMsg = "File or empty directory";
                return false;
            }

            // Make a list of everything in it that we can get a thumbnail for
            ArrayList<DropboxAPI.Entry> thumbs = new ArrayList<DropboxAPI.Entry>();
            for (DropboxAPI.Entry ent : dirent.contents) {
                if (ent.thumbExists) {
                    // Add it to the list of thumbs we can choose from
                    thumbs.add(ent);
                }
            }

            if (mCanceled) {
                return false;
            }

            if (thumbs.size() == 0) {
                // No thumbs in that directory
                mErrorMsg = "No pictures in that directory";
                return false;
            }

            for (int index=0; index<thumbs.size(); index++){
                DropboxAPI.Entry ent = thumbs.get(index);
                String path = ent.path;
                mFileLen = ent.bytes;

                String cachePath = mContext.getCacheDir().getAbsolutePath() + "/"
                        + IMAGE_FILE_NAME;
                try {
                    mFos = new FileOutputStream(cachePath);
                } catch (FileNotFoundException e) {
                    mErrorMsg = "Couldn't create a local file to store the image";
                    return false;
                }

                // This downloads a smaller, thumbnail version of the file. The
                // API to download the actual file is roughly the same.
                mApi.getThumbnail(path, mFos, DropboxAPI.ThumbSize.BESTFIT_960x640,
                        DropboxAPI.ThumbFormat.JPEG, null);
                if (mCanceled) {
                    return false;
                }

                Drawable mDrawable = Drawable.createFromPath(cachePath);
                mDrawableList.add(mDrawable);
            }

            // We must have a legitimate picture
            return true;

        } catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Download canceled";
        } catch (DropboxServerException e) {
            // Server-side exception. These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them. You may want to
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
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int percent = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
        mDialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mDialog.dismiss();
        if (result) {
            // Set the image now that we have it
            mView.setImageDrawable(mDrawableList.get(0));

            ImageAdapter_Detail adapter = new ImageAdapter_Detail(mContext);
            /*DetailActivity.mGridview.setAdapter(adapter);*/


        } else {
            // Couldn't download it, so show an error
            showToast(mErrorMsg);
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }

    private class ImageAdapter_Detail extends BaseAdapter {
        private Context mContext;

        public ImageAdapter_Detail(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mDrawableList.size();
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

            View gridView;
            ImageView imageView;
            if (convertView == null) {
                gridView = new View(mContext);

                // get layout from grid_item.xml
                gridView = inflater.inflate(R.layout.grid_item, null);

                imageView = (ImageView) gridView
                        .findViewById(R.id.grid_item_image);
                imageView.setImageDrawable(mDrawableList.get(position));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            } else {
                gridView = (View) convertView;
            }
            return gridView;
        }

    }

}
