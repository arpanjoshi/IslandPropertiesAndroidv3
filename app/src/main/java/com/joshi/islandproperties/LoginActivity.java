package com.joshi.islandproperties;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.TokenPair;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Button button = (Button)findViewById(R.id.buttonLogin);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    v.setBackgroundColor(Color.BLUE);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

//                    v.getBackground().setColorFilter(0xff33b5e5, PorterDuff.Mode.MULTIPLY);
                    v.setBackgroundColor(0xff33b5e5);
                    MainActivity.dropbox.getSession().startAuthentication(LoginActivity.this);
                }

                return true;
            }
        });
    }

//    public void btnLoginClicked(View view){
//        MainActivity.dropbox.getSession().startAuthentication(this);
//
//    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = MainActivity.dropbox.getSession();
//        session.setAccessTokenPair(pair);
        if (session.authenticationSuccessful()) {
            try {
                Intent indent = new Intent(this, MainActivity.class);
                MainActivity.isLoggedIn = true;
                startActivity(indent);
                session.finishAuthentication();

            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
