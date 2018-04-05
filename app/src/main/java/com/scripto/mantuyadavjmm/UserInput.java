package com.scripto.mantuyadavjmm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInput extends AppCompatActivity {

    EditText editName, editPhone, editEmail, editAddress, editPassword, editPin, editVoterID, editAge;
    String title, ward, name, phone, age, email, address, password, pin, voterID, tag, type;
    TextView textMsg;
    Spinner spinWard;
    Button butInput;
    InputMethodManager imm;
    DBManager db;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);

        mContext = this;
        imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        db = new DBManager(mContext);

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editVoterID = findViewById(R.id.editVoterID);
        editAge = findViewById(R.id.editAge);
        editEmail = findViewById(R.id.editEmail);
        editAddress = findViewById(R.id.editAddress);
        editPassword = findViewById(R.id.editPassword);
        editPin = findViewById(R.id.editPin);
        textMsg = findViewById(R.id.textMsg);
        spinWard = findViewById(R.id.spinWard);
        butInput = findViewById(R.id.butInput);

        Intent intent = getIntent();
        tag = intent.getStringExtra("tag");

        if (tag.matches("reg")) {
            title = "Register";
            editName.setVisibility(View.VISIBLE);
            editAge.setVisibility(View.VISIBLE);
            butInput.setText(title);
            getWard();
        } else if (tag.matches("log")) {
            title = "Login";
            butInput.setText(title);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);


        spinWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ward = parent.getItemAtPosition(position).toString();
                //Log.e(String.valueOf(position), Members.ward);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        butInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                if (tag.matches("reg")) {
                    String msg = "";
                    name = editName.getText().toString();
                    phone = editPhone.getText().toString();
                    voterID = editVoterID.getText().toString();
                    age = editAge.getText().toString();
                    //Members.address = editAddress.getText().toString();
                    //Members.email = editEmail.getText().toString();
                    //Members.password = editPassword.getText().toString();
                    //Members.pin = editPin.getText().toString();

                    if (name.isEmpty() || phone.isEmpty() || voterID.isEmpty() || age.isEmpty()) {
                        msg = "\t*Please fill all fields";
                        vibrate(100);

                    } else if (spinWard.getSelectedItemPosition() == 0) {
                        msg = "\t*Select Ward";
                        vibrate(100);

                    } else {
                        msg = "";
                        userData();
                        vibrate(50);
                    }

                    textMsg.setText(msg);

                } else if (tag.matches("log")) {
                    voterID = editVoterID.getText().toString();
                    phone = editPhone.getText().toString();

                    if (voterID.isEmpty() || phone.isEmpty()) {
                        textMsg.setText("\t*Please fill all fields");
                        vibrate(100);

                    } else {
                        userData();
                    }
                }

            }
        });

    }

    private void getWard() {
        Methods.showDialog(mContext, getString(R.string.wait));
        String tag_string_req = "req_token";

        StringRequest strReq = new StringRequest(Request.Method.POST, Methods.URL_DATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e("Response", data.toString());

                try {
                    JSONObject dataResp = new JSONObject(data);

                    String wardList = dataResp.getString("wardList");
                    String[] ward = wardList.split(",");
                    ArrayList wardSpinner = new ArrayList();
                    wardSpinner.add("Select Ward");
                    for (int i = 0; i < ward.length; i++) {
                        wardSpinner.add(ward[i]);
                    }
                    ArrayAdapter<String> wardAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, wardSpinner);
                    wardAdapter.setDropDownViewResource(R.layout.spinner_item);

                    spinWard.setAdapter(wardAdapter);
                    spinWard.setSelection(0);
                    spinWard.setVisibility(View.VISIBLE);

                    Methods.hideDialog(1000);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(" Token Error", error.getMessage());
                Toast.makeText(mContext, "Please check your network connection \n& try again later...", Toast.LENGTH_SHORT).show();
                finish();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                //params.put("AccessID", accessid);

                //Log.e("Params", String.valueOf(params));
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void userData() {

        Methods.showDialog(mContext, getString(R.string.wait));

        String tag_string_req = "req_data";

        StringRequest strReq = new StringRequest(Request.Method.POST, Methods.URL_MEMBER, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e("Response", data.toString());


                if (tag.matches("reg")) {
                    try {
                        JSONObject dataResp = new JSONObject(data);
                        JSONArray dataResponse = new JSONArray(dataResp.getString("Register"));
                        JSONObject postDataResponse = dataResponse.getJSONObject(0);

                        final String error = postDataResponse.getString("ERROR");
                        final String errorMsg = postDataResponse.getString("ERRORMSG");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (error.equals("false")) {
                                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                                    vibrate(50);
                                    finish();
                                } else if (error.equals("true")) {
                                    textMsg.setText("\t*" + errorMsg);
                                    vibrate(100);
                                }
                            }
                        }, 1200);

                        Methods.hideDialog(1000);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (tag.matches("log")) {
                    try {
                        JSONObject dataResp = new JSONObject(data);
                        JSONArray dataResponse = new JSONArray(dataResp.getString("User"));
                        //Log.e("dataResponse", dataResponse.toString());
                        if (dataResponse.length() == 0) {
                            textMsg.setText("\t*User not found!!!");
                            vibrate(100);
                            Methods.hideDialog(500);
                        } else if (dataResponse.length() > 0) {
                            db.deleteMember();

                            if (dataResponse.getJSONObject(0).has("ID")) {
                                type = "user";
                            } else {
                                type = "admin";
                            }

                            for (int i = 0; i < dataResponse.length(); i++) {
                                JSONObject postDataResponse = dataResponse.getJSONObject(i);
                                //Log.e("User " + i, postDataResponse.toString());
                                String voterID = postDataResponse.getString("VoterID");
                                String name = postDataResponse.getString("Name");
                                String phone = postDataResponse.getString("Phone");
                                String age = postDataResponse.getString("Age");
                                String status = postDataResponse.getString("Status");
                                String ward = postDataResponse.getString("Ward");

                                db.addMember(voterID, name, phone, age, status, ward);
                            }
                            changeActivity(type);
                        }


                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Data Error", error.getMessage());
                Toast.makeText(mContext, "Please check your network connection \n& try again later...", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("VoterID", voterID);
                params.put("Phone", phone);

                if (tag.matches("reg")) {
                    params.put("Name", name);
                    params.put("Age", age);
                    params.put("Ward", ward);
                }

                //Log.e("Params", String.valueOf(params));
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void changeActivity(String type) {
        startActivity(new Intent(mContext, Profile.class).putExtra("profile", type).putExtra("voterID", voterID).putExtra("phone", phone));
        Methods.hideDialog(500);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        exitBack();
        return true;
    }

    @Override
    public void onBackPressed() {
        exitBack();
    }

    private void exitBack() {
        new AlertDialog.Builder(mContext)
                .setTitle("Confirmation")
                .setMessage("\nDiscard data & leave this page?")
                .setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setPositiveButton("Stay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }

}
