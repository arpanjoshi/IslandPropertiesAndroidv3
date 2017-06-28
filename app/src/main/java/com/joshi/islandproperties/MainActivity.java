package com.joshi.islandproperties;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

import com.dropbox.client2.session.TokenPair;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static DropboxAPI<AndroidAuthSession> dropbox;
//    private final static String FILE_DIR = "/DropboxSample/";
    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "6hmmdoun2b3wd5s";
    private final static String ACCESS_SECRET = "i11xke09s4jdggm";
    final static private Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
    public static boolean isLoggedIn;
    private int ipaOrAddress;

    public final static String EXTRA_MESSAGE = "MESSAGE";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        setSupportActionBar(toolbar);

        loggedIn(false);
        AndroidAuthSession session;
        AppKeyPair pair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);

        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(pair, ACCESS_TYPE, token);
        } else {
            session = new AndroidAuthSession(pair, ACCESS_TYPE);
        }
        dropbox = new DropboxAPI<AndroidAuthSession>(session);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        ImageButton button = (ImageButton)findViewById(R.id.imageView_logout);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // change color
//                    v.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
//                   v.setBackgroundResource(R.drawable.logout_android_down);
                    v.getBackground().setColorFilter(0x88000088, PorterDuff.Mode.MULTIPLY);

                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // set to normal color
//                    v.setBackgroundResource(R.drawable.logout_android);
                    v.getBackground().clearColorFilter();
                    if (isLoggedIn) {

                        dropbox.getSession().unlink();
                        loggedIn(false);
                        v.setEnabled(false);
                        v.setVisibility(View.GONE);
                        ImageButton ib = (ImageButton)findViewById(R.id.imageView_settings);
                        ib.setVisibility(View.GONE);
//                        Toast.makeText(MainActivity.this,
//                                "Logged out successfully!",
//                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                }

                return true;
            }
        });
        if(!isLoggedIn){
            button.setEnabled(false);
            button.setVisibility(View.GONE);
        }

        button = (ImageButton)findViewById(R.id.imageView_settings);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    v.getBackground().setColorFilter(0x88000088, PorterDuff.Mode.MULTIPLY);

                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {

                    v.getBackground().clearColorFilter();
                    if (!isLoggedIn) {
                        dropbox.getSession().startAuthentication(MainActivity.this);
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                }

                return true;
            }
        });
        if(!isLoggedIn){
            button.setEnabled(false);
            button.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = dropbox.getSession();
//        session.setAccessTokenPair(pair);
        if (session.authenticationSuccessful()) {
            try {
                loggedIn(true);


                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();

            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (isLoggedIn){
            ImageView iv = (ImageView)findViewById(R.id.imageView_logout);
            iv.setEnabled(true);
            iv.setVisibility(View.VISIBLE);

            iv = (ImageView)findViewById(R.id.imageView_settings);
            iv.setEnabled(true);
            iv.setVisibility(View.VISIBLE);
        }
    }

    public void loggedIn(boolean isLogged) {
        isLoggedIn = isLogged;

    }

    public void btnIpaClicked(View view){
        if (!isLoggedIn) {
            dropbox.getSession().startAuthentication(MainActivity.this);

//            dropbox.getSession().startOAuth2Authentication(MainActivity.this);
        }
        else {

            ipaOrAddress = 1;
            Intent intent = new Intent(this, AddNewProperty.class);
            intent.putExtra(EXTRA_MESSAGE, ipaOrAddress);
            startActivity(intent);
        }
    }

    public void btnAddressClicked(View view) {
        if (!isLoggedIn) {
            dropbox.getSession().startAuthentication(MainActivity.this);
        }
        else {
            ipaOrAddress = 0;
            Intent intent = new Intent(this, AddNewProperty.class);
            intent.putExtra(EXTRA_MESSAGE, ipaOrAddress);
            startActivity(intent);

        }
    }

    public void btnSearchClicked(View view) {
        if (!isLoggedIn) {
            dropbox.getSession().startAuthentication(MainActivity.this);
        }
        else {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        }
    }

    public void btnSettingsClicked(View view) {

    }

    public void btnLogoutClicked(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.joshi.islandproperties/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.joshi.islandproperties/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
