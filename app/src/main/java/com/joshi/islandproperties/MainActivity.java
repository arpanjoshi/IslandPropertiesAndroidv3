package com.joshi.islandproperties;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.Session;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.joshi.islandproperties.dropbox_classes.DropboxActivity;
import com.joshi.islandproperties.dropbox_classes.DropboxClientFactory;
import com.joshi.islandproperties.dropbox_classes.GetCurrentAccountTask;


public class MainActivity extends DropboxActivity implements LogoutFromDB.CallBack {

    public static DropboxAPI<AndroidAuthSession> dropbox;
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        ImageButton button = (ImageButton) findViewById(R.id.imageView_logout);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getBackground().setColorFilter(0x88000088, PorterDuff.Mode.MULTIPLY);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getBackground().clearColorFilter();
                    if (hasToken()) {

                        /*new LogoutFromDB(MainActivity.this, MainActivity.this).execute();*/
                        // Revoking Token
                        revokeToken();
                        v.setEnabled(false);
                        v.setVisibility(View.GONE);
                        ImageButton ib = (ImageButton) findViewById(R.id.imageView_settings);
                        ib.setVisibility(View.GONE);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }

                return true;
            }
        });
        if (!hasToken()) {
            button.setEnabled(false);
            button.setVisibility(View.GONE);
        }

        button = (ImageButton) findViewById(R.id.imageView_settings);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    v.getBackground().setColorFilter(0x88000088, PorterDuff.Mode.MULTIPLY);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    v.getBackground().clearColorFilter();
                    if (!hasToken()) {
                        Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key));
                    } else {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                }

                return true;
            }
        });
        if (!hasToken()) {
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
        if (hasToken()) {
            ImageView iv = (ImageView) findViewById(R.id.imageView_logout);
            iv.setEnabled(true);
            iv.setVisibility(View.VISIBLE);

            iv = (ImageView) findViewById(R.id.imageView_settings);
            iv.setEnabled(true);
            iv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                Log.d("Logged In", "Success");
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
            }
        }).execute();
    }

    public void btnIpaClicked(View view) {
        if (!hasToken()) {
            // Old Code of V1
            /*dropbox.getSession().startAuthentication(MainActivity.this);*/
            Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key));
        } else {

            ipaOrAddress = 1;
            Intent intent = new Intent(this, AddNewProperty.class);
            intent.putExtra(EXTRA_MESSAGE, ipaOrAddress);
            startActivity(intent);
        }
    }

    public void btnAddressClicked(View view) {
        if (!hasToken()) {
            // Old DropBox Code of V1
            /*dropbox.getSession().startAuthentication(MainActivity.this);*/
            Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key));
        } else {
            ipaOrAddress = 0;
            Intent intent = new Intent(this, AddNewProperty.class);
            intent.putExtra(EXTRA_MESSAGE, ipaOrAddress);
            startActivity(intent);

        }
    }

    public void btnSearchClicked(View view) {
        if (!hasToken()) {
            // Old DropBox Code of V1
            /*dropbox.getSession().startAuthentication(MainActivity.this);*/
            Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key));
        } else {
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

    @Override
    public void isUserLoggedOut(boolean status) {
        /*if (status)
            revokeToken();*/
    }
}
