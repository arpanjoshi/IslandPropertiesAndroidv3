package com.joshi.islandproperties.dropbox_classes;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.dropbox.core.android.Auth;
import com.pixplicity.easyprefs.library.Prefs;

/*************************************************************************
 * Created by prateekarora on 29/06/17.
 */


public abstract class DropboxActivity extends AppCompatActivity {

    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "6hmmdoun2b3wd5s";
    private final static String ACCESS_SECRET = "i11xke09s4jdggm";

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String accessToken = Prefs.getString("access-token", null);
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                Prefs.putString("access-token", accessToken);
                initAndLoadData(accessToken);
            }
        } else {
            initAndLoadData(accessToken);
        }

        String uid = Auth.getUid();
        String storedUid = prefs.getString("user-id", null);
        if (uid != null && !uid.equals(storedUid)) {
            prefs.edit().putString("user-id", uid).apply();
        }
    }

    private void initAndLoadData(String accessToken) {
        DropboxClientFactory.init(accessToken);
        PicassoClient.init(getApplicationContext(), DropboxClientFactory.getClient());
        loadData();
    }

    protected abstract void loadData();

    protected boolean hasToken() {
        if (Prefs.getString("access-token", null) == null) {
            return false;
        } else
            return true;
    }

    protected void revokeToken() {
        Prefs.putString("access-token", null);
    }
}
