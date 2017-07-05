package com.joshi.islandproperties;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;


public class AddNewProperty extends AppCompatActivity{

    private int ipaOrAddress;
    public String strPropertyName;
    String strCheck, strCamera;

    private boolean isEmpty = false;
    private boolean isEmpty1 = false;
    private boolean isEmpty2 = false;
    private boolean isEmpty3 = false;
    private boolean isEmpty4 = false;
    private boolean isEmpty5 = false;

    public static ProgressDialog mDialog;
    private AlertDialog.Builder alertDialog;

    //restore entered data
    private String strStNum;
    private String strStName;
    private String strCity;
    public static String strState;
    private String strZipcode;

    private String arrStates[];
    public static String arrPA[];

    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_property);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        ipaOrAddress = intent.getIntExtra(MainActivity.EXTRA_MESSAGE, -1);

        //hide iv and tv
        ImageView iv_check = (ImageView) this.findViewById(R.id.iv_check);
        iv_check.setVisibility(View.INVISIBLE);
        ImageView iv_camera = (ImageView) this.findViewById(R.id.iv_camera);
        iv_camera.setVisibility(View.INVISIBLE);
        TextView tv_check = (TextView) this.findViewById(R.id.tv_check);
        tv_check.setVisibility(View.INVISIBLE);
        TextView tv_camera = (TextView) this.findViewById(R.id.tv_camera);
        tv_camera.setVisibility(View.INVISIBLE);

        isEmpty = false;
        isEmpty1 = false;
        isEmpty2 = false;
        isEmpty3 = false;
        isEmpty4 = false;
        isEmpty5 = false;

        strStNum = "";
        strStName = "";
        strCity = "";
        strState = "";
        strZipcode = "";

        arrStates = new String[]{"Alabama-AL", "Alaska-AK", "Arizona-AZ",
                "Arkansas-AR", "California-CA", "Colorado-CO", "Connnecticut-CT",
        "Delaware-DE", "Florida-FL", "Georgia-GA", "Hawaii-HI",
        "Idaho-ID", "Illinois-IL", "Indiana-IN", "Iowa-IA",
        "Kansas-KS", "Kentucky-KY", "Louisiana-LA", "Maine-ME",
        "Maryland-MD", "Massachusetts-MA", "Michigan-MI", "Minnesota-MN",
        "Mississippi-MS", "Missouri-MO", "Montana-MT", "Nebraska-NE",
        "Nevada-NV", "New Hampshire-NH", "New Jersey-NJ", "New Mexico-NM",
        "New York-NY", "North Carolina-NC", "North Dakota-ND", "Ohio-OH",
        "Oklahoma-OK", "Oregon-OR", "Pennsylvania-PA", "Rhode Island-RI",
        "South Carolina-SC", "South Dakota-SD", "Tennessee-TN", "Texas-TX",
        "Utah-UT", "Vermont-VT", "Virginia-VA", "Washington-WA",
        "West Virginia-WV", "Wisconsin-WI", "Wyoming-WY"};

        arrPA = new String[]{"AL",     "AK",       "AZ",
        "AR",   "CA",   "CO",   "CT",
        "DE",   "FL",   "GA",   "HI",
        "ID",   "IL",   "IN",   "IA",
        "KS",   "KY",   "LA",   "ME",
        "MD",   "MA",   "MI",   "MN",
        "MS",   "MO",   "MT",   "NE",
        "NV",   "NH",   "NJ",   "NM",
        "NY",   "NC",   "ND",   "OH",
        "OK",   "OR",   "PA",   "RI",
        "SC",   "SD",   "TN",   "TX",
        "UT",   "VT",   "VA",   "WA",
        "WV",   "WI",   "WY"};


        showDialog();

        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }


    public void showDialog() {
        if (ipaOrAddress == 1) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.layout_ipa, null);
            final EditText input = (EditText) textEntryView.findViewById(R.id.editText);

            alertDialog = new AlertDialog.Builder(this);
            alertDialog.setView(textEntryView);

            input.setHint("Enter IPA #");
            alertDialog.setTitle("New IPA Property");
            alertDialog.setMessage("Please enter IPA number for the property. This will be used for the folder name.");

            if(isEmpty){
                input.setError("Please enter IPA Number");
            }
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String strIpa = input.getText().toString().trim();
                    if (strIpa.equals("")) {
                        isEmpty = true;
                        showDialog();

                    }

                    else {//Create a property with strPropertyName
                        strPropertyName = "/IPA-" + strIpa;
                        strCheck = "IPA Property # " + strIpa + " added successfully.";
                        strCamera = "Add Pics for IPA-" + strIpa;
//                        Toast.makeText(getApplicationContext(), strPropertyName, Toast.LENGTH_SHORT).show();
                        try {
                            createProperty();
                        } catch (Exception e) {
                            System.out.println("Something went wrong: " + e);
                        }
                    }
                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    dialog.cancel();
                    finish();
                }
            });
            alertDialog.show();
        } else {
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.layout_address, null);

            alertDialog = new AlertDialog.Builder(this);

            //spinner
            dropdown = (Spinner)textEntryView.findViewById(R.id.spinner1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, arrStates);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrStates);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(new CustomOnItemSelectedListener());

            alertDialog.setView(textEntryView);

            final EditText st_num = (EditText) textEntryView.findViewById(R.id.st_num);
            st_num.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                        // hide virtual keyboard
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(st_num.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });
            if(isEmpty1){
                st_num.setError("Please enter Street Number");
            }else{
                st_num.setText(strStNum);
            }

            final EditText st_name = (EditText) textEntryView.findViewById(R.id.st_name);
            st_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                        // hide virtual keyboard
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(st_name.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });
            if(isEmpty2){
                st_name.setError("Please enter Street Name");
            }else{
                st_name.setText(strStName);
            }

            final EditText city = (EditText) textEntryView.findViewById(R.id.city);
            city.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                        // hide virtual keyboard
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(city.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });
            if(isEmpty3){
                city.setError("Please enter City");
            }else{
                city.setText(strCity);
            }

//            final EditText state = (EditText) textEntryView.findViewById(R.id.state);
//            state.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
//                        // hide virtual keyboard
//                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(state.getWindowToken(), 0);
//                        return true;
//                    }
//                    return false;
//                }
//            });
//            if(isEmpty4){
//                state.setError("Please select State");
//            }else{
//                state.setText(strState);
//            }



            final EditText zipcode = (EditText) textEntryView.findViewById(R.id.zipcode);
            zipcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                        // hide virtual keyboard
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(zipcode.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });
            if(isEmpty5){
                zipcode.setError("Please enter 5-digit zip code");
            }else{
                zipcode.setText(strZipcode);
            }


            alertDialog.setTitle("New Address Property");
            alertDialog.setMessage("Please enter Address for the property. This will be used for the folder name.");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    strStNum = st_num.getText().toString().trim();
                    strStName = st_name.getText().toString().trim();
                    strCity = city.getText().toString().trim();
                    String temp = dropdown.getSelectedItem().toString().trim();
                    strZipcode = zipcode.getText().toString().trim();

                    if (strStNum.equals(""))
                        isEmpty1 = true;
                    else isEmpty1 = false;

                    if (strStName.equals(""))
                        isEmpty2 = true;
                    else isEmpty2 = false;

                    if (strCity.equals(""))
                        isEmpty3 = true;
                    else isEmpty3 = false;

                    if (temp.equals(""))
                        isEmpty4 = true;
                    else isEmpty4 = false;

                    if (strZipcode.equals("") || strZipcode.length()<5)
                        isEmpty5 = true;
                    else isEmpty5 = false;

                    if (isEmpty1 || isEmpty2 || isEmpty3 || isEmpty4 || isEmpty5) {
                        showDialog();
                        return;
                    } else {//Create a property with value
                        String strAddress = strStNum + " " + strStName + " " + strCity + " " + strState + " " + strZipcode;
                        strPropertyName = "/" + strStNum + " " + strStName + " " + strCity + " " + strState + " " + strZipcode;
                        strCheck = "Address Property # " + strAddress + " added successfully.";
                        strCamera = "Add Pics for " + strAddress;
//                        Toast.makeText(getApplicationContext(), strPropertyName, Toast.LENGTH_SHORT).show();
                        try {
                            createProperty();
                        } catch (Exception e) {
                            System.out.println("Something went wrong: " + e);
                        }
                    }

                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    dialog.cancel();
                    finish();
//                    Intent intent = new Intent(this, MainActivity.class);
//                    intent.putExtra(EXTRA_MESSAGE, ipaOrAddress);
//                    startActivity(intent);

                }
            });
            alertDialog.show();
        }
    }

    void createProperty(){

        CreateFolder folder = new CreateFolder(AddNewProperty.this,
                strPropertyName);
        folder.obj = this;
        folder.execute();
    }
    public void btnCameraClicked(View view){
        view.setEnabled(false);
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Getting pictures...");
        mDialog.setCancelable(false);
        mDialog.show();

        if(getTenPhotos()){
            UploadPicturesActivity.isPhoto = true;
        }else{
            UploadPicturesActivity.isPhoto = false;
        }
        Intent intent = new Intent(this, UploadPicturesActivity.class).putExtra("strDir", strPropertyName);
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

class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        AddNewProperty.strState = AddNewProperty.arrPA[pos];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}