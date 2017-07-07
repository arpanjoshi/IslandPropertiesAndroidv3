package com.joshi.islandproperties;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.dropbox.core.android.Auth;
import com.joshi.islandproperties.dropbox_classes.DropboxActivity;

public class LoginActivity extends DropboxActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.buttonLogin);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(Color.BLUE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(0xff33b5e5);
                    Auth.startOAuth2Authentication(LoginActivity.this, getString(R.string.app_key));
                }

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void loadData() {

    }

}
