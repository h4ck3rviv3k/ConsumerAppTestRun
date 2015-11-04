package com.pkg.consumerapptestrun.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pkg.consumerapptestrun.R;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String[] API_LIST = {"http://dev1.fabhotels.com/api/main/getteasers", "http://dev1.fabhotels.com/api/hotels-in-new-delhi/", "http://dev1.fabhotels.com/api/hotels-in-new-delhi/?checkIn=16+Oct+2015&checkOut=18+Oct+2015", "http://dev1.fabhotels.com/api/hotels-in-new-delhi/fabhotel-new-delhi-station.html", "http://dev1.fabhotels.com/api/search?checkIn=16+Oct+2015&checkOut=18+Oct+2015&rooms=1&occupancy[]=1&city=New+Delhi", "http://dev1.fabhotels.com/api/catalog/calculateprice?occupancy[]=1&propertyId=21&rooms=1&checkIn=16+Oct+2015&checkOut=19+Oct+2015&room_category=1"};
    // Tag used to cancel the request
    String tag_json_obj = "json_obj_req";
    public static final String TAG = AppController.class
            .getSimpleName();
    private LinearLayout linear;
    private String errorLog="crash \r\n reporting ";
    private int TOTAL_API_COUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        initializeView();
    }


    public void initializeView() {
        linear = (LinearLayout) findViewById(R.id.linear);
        Button runBtn = (Button) findViewById(R.id.btnRun);
        runBtn.setOnClickListener(listner);
    }

    View.OnClickListener listner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            linear.removeAllViews();
            TOTAL_API_COUNT = API_LIST.length;
            for (String index : API_LIST) {
                sendRequest(index);
            }
        }
    };

    /*Send RequestSequentially*/
    public void sendRequest(final String requestUrl) {
        String url = requestUrl;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, requestUrl + "" + response.toString());
                        addTextViewDynamically(requestUrl + ": ", "SUCCESS", true);
                        TOTAL_API_COUNT--;

                        /*if (TOTAL_API_COUNT == 0)
                           showDialog();*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                errorLog += error.getMessage() + "\r\n ";
                addTextViewDynamically(requestUrl + ": ", "FAILED", true);
                TOTAL_API_COUNT--;

                if (TOTAL_API_COUNT == 0)
                    showDialog();
            }
        });

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    /*Add Views dynamically in linear layout*/
    public void addTextViewDynamically(String text, String result, boolean status) {

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        LayoutParams mparams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mparams.topMargin = 25;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(mparams);

        LayoutParams lparams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lparams.bottomMargin = 10;
        lparams.weight = 1;

        TextView textAPI = new TextView(getApplicationContext());
        textAPI.setLayoutParams(lparams);
        textAPI.setText(text);
        textAPI.setTextColor(Color.BLACK);
        linearLayout.addView(textAPI);


        TextView state = new TextView(getApplicationContext());
        state.setLayoutParams(lparams);
        state.setText(result);
        state.setTextColor(status ? getResources().getColor(R.color.colorSucess) : getResources().getColor(R.color.colorfail));
        linearLayout.addView(state);

        linear.addView(linearLayout);
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.dialog_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.parse("mailto:?subject=API error logs&body=" + errorLog);
                        intent.setData(data);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
