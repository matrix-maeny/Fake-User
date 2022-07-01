package com.matrix_maeny.fakeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.matrix_maeny.fakeuser.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;



/*
* "results": [
        {
            "gender": "female",
            "name": {
                "title": "Ms",
                "first": "Andréa",
                "last": "Louis"
            },
            "location": {
                "street": {
                    "number": 5236,
                    "name": "Rue Barrier"
                },
                "city": "Saint-Pierre",
                "state": "Côtes-D'Armor",
                "country": "France",
                "postcode": 70580,
                "coordinates": {
                    "latitude": "-3.4759",
                    "longitude": "93.8893"
                },
                "timezone": {
                    "offset": "+8:00",
                    "description": "Beijing, Perth, Singapore, Hong Kong"
                }
            },
            "email": "andrea.louis@example.com",
            "login": {
                "uuid": "19dd86d3-a682-4599-b993-2e4b1fc1477e",
                "username": "bluecat917",
                "password": "mikey1",
                "salt": "zGluLM0K",
                "md5": "994cc3c3cc9b2fbdd634cfab5ab6edae",
                "sha1": "53b9f7a329b34e6718a9caf5fe10f71aa793b399",
                "sha256": "dc77c843fdff3d711de5e6ac0c4c5324699840d4acc0a58c0ab7cfbe12e6d771"
            },
            "dob": {
                "date": "1959-02-20T15:21:32.583Z",
                "age": 63
            },
            "registered": {
                "date": "2014-04-25T04:58:22.962Z",
                "age": 8
            },
            "phone": "05-71-24-49-55",
            "cell": "06-78-64-76-88",
            "id": {
                "name": "INSEE",
                "value": "2NNaN47371376 41"
            },
            "picture": {
                "large": "https://randomuser.me/api/portraits/women/7.jpg",
                "medium": "https://randomuser.me/api/portraits/med/women/7.jpg",
                "thumbnail": "https://randomuser.me/api/portraits/thumb/women/7.jpg"
            },
            "nat": "FR"
        }
    ],
    "info": {
        "seed": "ec208b8471693d29",
        "results": 1,
        "page": 1,
        "version": "1.3"
    }
* */


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RequestQueue requestQueue;

    private String gender = null, title = null, firstName = null, lastName = null;
    private String email = null, phone = null, cell = null;

    private String streetName = null;
    private int streetNum = -1;

    private String city = null, state = null, country = null;
    private int postCode = -1;

    private String longitudes = null, latitudes = null;

    private String offset = null, description = null;

    private String uuid = null, username = null, password = null, salt = null, md5 = null, sha1 = null, sha256 = null;

    private String dateD = null;
    private int ageD = -1;

    private String dateR = null;
    private int ageR = -1;

    private String idName = null, idValue = null;

    private String thumbnail = null;
    private String nat = null;

    final Handler handler = new Handler();
    private ProgressDialog progressDialog;

    private String fullDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
    }

    private void initialize() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                // go to about activity
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                break;
            case R.id.refresh_data:
                // refresh data
                refreshData();
                break;
            case R.id.share_info:
                shareInfo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        new Thread(){
            public void run(){
                getDetails();
            }
        }.start();
    }


    private void getDetails() {

        progressDialog.setMessage("Creating...");
        handler.post(() -> progressDialog.show());
        requestQueue.getCache().clear();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String url = "https://randomuser.me/api/";

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONObject object;
                try {
                    JSONArray array = response.getJSONArray("results");
                    object = array.getJSONObject(0);

                } catch (Exception e) {
                    e.printStackTrace();
                    showToastH("Some error occurred");
                    object = null;
                }

                if (object != null) {
                    try {
                        gender = object.getString("gender");
                        email = object.getString("email");
                        phone = object.getString("phone");
                        cell = object.getString("cell");
                        nat = object.getString("nat");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastH("error: " + e.getMessage());
                    }

                    try {
                        JSONObject nameOb = object.getJSONObject("name");
                        title = nameOb.getString("title");
                        firstName = nameOb.getString("first");
                        lastName = nameOb.getString("last");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastH("error: " + e.getMessage());
                    }

                    JSONObject locOb = null;// = object.getJSONObject("location");

                    try {
                        locOb = object.getJSONObject("location");

                        city = locOb.getString("city");
                        state = locOb.getString("state");
                        country = locOb.getString("country");
                        postCode = locOb.getInt("postcode");

                        JSONObject streetOb = locOb.getJSONObject("street");
                        streetName = streetOb.getString("name");
                        streetNum = streetOb.getInt("number");

                        JSONObject timezoneOb = locOb.getJSONObject("timezone");
                        offset = timezoneOb.getString("offset");
                        description = timezoneOb.getString("description");

                        JSONObject coorOb = locOb.getJSONObject("coordinates");
                        longitudes = coorOb.getString("longitude");
                        latitudes = coorOb.getString("latitude");

                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastH("error: " + e.getMessage());
                    }

                    try {
                        JSONObject loginOb = object.getJSONObject("login");

                        uuid = loginOb.getString("uuid");
                        username = loginOb.getString("username");
                        password = loginOb.getString("password");
                        salt = loginOb.getString("salt");
                        md5 = loginOb.getString("md5");
                        sha1 = loginOb.getString("sha1");
                        sha256 = loginOb.getString("sha256");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastH("error:" + e.getMessage());
                    }

                    try {
                        JSONObject dobOb = object.getJSONObject("dob");

                        dateD = dobOb.getString("date");
                        ageD = dobOb.getInt("age");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastH("error:" + e.getMessage());
                    }

                    try {
                        JSONObject registerOb = object.getJSONObject("registered");

                        dateR = registerOb.getString("date");
                        ageR = registerOb.getInt("age");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastH("error:" + e.getMessage());
                    }

                    try {
                        JSONObject idOb = object.getJSONObject("id");
                        idName = idOb.getString("name");
                        idValue = idOb.getString("value");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastH("error:" + e.getMessage());
                    }

                    try {
                        JSONObject picOb = object.getJSONObject("picture");
                        thumbnail = picOb.getString("large");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastH("error:" + e.getMessage());
                    }

                }
                handler.post(() -> progressDialog.dismiss());

                postDetails();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errMsg = error.toString();

                if (errMsg.contains("UnknownHost")) {
                    showToastH("Error: No Internet");
                } else {
                    errMsg = error.getMessage();
                }
                handler.post(() -> progressDialog.dismiss());

                showToastH(errMsg);
            }
        });

        queue.add(objectRequest);

    }

    private void showToastH(String msg) {

        handler.post(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    private void postDetails() {
        handler.post(()->{
           String namePlateDetails = title+" "+firstName+" "+" "+lastName;
           String generalDetails = "Gender: "+gender+"\n\n"+"Email: "+email+"\n\n"+"Phone: "+phone+"\n\n"+"Cell: "+cell+"\n\nNAT: "+nat;
           String dobDetails = "Date: "+dateD+"\n\nAge: "+ageD;

           String placeDetails = "City: "+city+"\n\nState: "+state+"\n\nCountry: "+country+"\n\nPost code: "+postCode;
           String streetDetails = "Street name: "+streetName+"\n\nStreet number: "+streetNum;
           String coordinatesDetails = "Longitude: "+longitudes+"\n\nLatitudes: "+latitudes;
           String timezoneDetails = "Offset: "+offset+"\n\nDescription: "+description;

           String loginDetails = "UUID: "+uuid+"\n\nUsername: "+username+"\n\nPassword: "+password+"\n\nSalt: "+salt+
                   "\n\nMd5: "+md5+"\n\nSha1: "+sha1+"\n\nSha256: "+sha256;

            String idDetails = "Name: "+idName+"\n\nValue: "+idValue;
            String registerDetails = "Date: "+dateR+"\n\nAge: "+ageR;

            Picasso.get().load(thumbnail).into(binding.personIV);
            binding.namePlateTv.setText(namePlateDetails);
            binding.generalTv.setText(generalDetails);
            binding.dobTv.setText(dobDetails);

            binding.placeTv.setText(placeDetails);
            binding.streetTv.setText(streetDetails);
            binding.coordinateTv.setText(coordinatesDetails);
            binding.timeZoneTv.setText(timezoneDetails);
            binding.loginTv.setText(loginDetails);
            binding.idTv.setText(idDetails);
            binding.registerTv.setText(registerDetails);

            fullDetails = "\n\n"+namePlateDetails+"\n-----------------------\n\nGeneral Info:------\n\n"+generalDetails+
                    "\n\nImage link: "+thumbnail+
                    "\n\n\nDate Of Birth:------\n\n"+dobDetails+
                    "\n\n\nPlace Info:------\n\n"+placeDetails+
                    "\n\n\nStreet Info:------\n\n"+streetDetails+
                    "\n\n\nCoordinates:------\n\n"+coordinatesDetails+
                    "\n\n\nTime Zone Info: ------\n\n"+timezoneDetails+
                    "\n\n\nLogin Info:------"+loginDetails+
                    "\n\n\nID Info:------"+idDetails+
                    "\n\n\nRegistered Info:------\n\n"+registerDetails+
                    "\n\n\n---------- @matrix ----------";



        });
    }

    private void shareInfo(){
        Intent intent = new Intent();
        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,fullDetails);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Fake User");
        startActivity(intent);
    }
}