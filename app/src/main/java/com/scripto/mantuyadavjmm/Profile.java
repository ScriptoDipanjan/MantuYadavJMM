package com.scripto.mantuyadavjmm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class Profile extends AppCompatActivity {

    ScrollView scrollList, scrollDetails;
    LinearLayout linList;
    EditText editSearch;
    TextView textError;
    FloatingActionButton fabAdd;
    LayoutInflater inflater;
    InputMethodManager imm;
    DBManager db;
    Cursor user;
    Handler mHandler;
    String profile, voterID, phone;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        Intent intent = getIntent();
        profile = intent.getStringExtra("profile");
        voterID = intent.getStringExtra("voterID");
        phone = intent.getStringExtra("phone");

        mContext = this;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        db = new DBManager(mContext);
        mHandler = new Handler();

        scrollList = findViewById(R.id.scrollList);
        scrollDetails = findViewById(R.id.scrollDetails);

        linList = findViewById(R.id.linList);

        textError = findViewById(R.id.textError);

        editSearch = findViewById(R.id.editSearch);
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editSearch.getText().toString().length() > 0) {
                    createchild(String.valueOf(editSearch.getText().toString()));
                    editSearch.requestFocus();

                } else
                    createchild("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext, UserInput.class).putExtra("tag", "reg"), 0);
            }
        });

        //Log.e("Profile", profile);
        if (!profile.equals("admin")) {
            user = db.getMember("");
            textError.setVisibility(GONE);
            String voterID = user.getString(user.getColumnIndex("voterid"));
            String name = user.getString(user.getColumnIndex("name"));
            String phone = user.getString(user.getColumnIndex("phone"));
            String ward = user.getString(user.getColumnIndex("ward"));
            String status = user.getString(user.getColumnIndex("status"));
            String age = user.getString(user.getColumnIndex("age"));
            String ver = "";
            int textColor;
            if (status.equals("0")) {
                ver = "Profile is not verified";
                textColor = Color.RED;
            } else {
                ver = "Profile is verified";
                textColor = getResources().getColor(R.color.colorAccent);
            }

            TextView textVoterID = findViewById(R.id.textVoterID);
            TextView textName = findViewById(R.id.textName);
            TextView textPhone = findViewById(R.id.textPhone);
            TextView textWard = findViewById(R.id.textWard);
            TextView textVer = findViewById(R.id.textVer);
            TextView textAge = findViewById(R.id.textAge);

            textName.setText("Name: " + name);
            textPhone.setText("Phone Number: " + phone);
            textVoterID.setText("Voter ID: " + voterID);
            textVer.setText(ver);
            textWard.setText("Ward No.: " + ward);
            textAge.setText("Age: " + age);

            textVer.setTextColor(textColor);

        } else if (profile.equals("admin")) {
            getSupportActionBar().setTitle("Members");
            scrollDetails.setVisibility(GONE);
            scrollList.setVisibility(View.VISIBLE);
            //fabAdd.setVisibility(View.VISIBLE);

            createchild("");
        }

    }

    private void createchild(final String data) {
        linList.removeAllViews();
        linList.addView(editSearch);
        user = db.getMember(data);

        if (user.getCount() > 0) {
            textError.setVisibility(GONE);
            int i = 0;
            while (i < user.getCount()) {
                final String voterID = user.getString(user.getColumnIndex("voterid"));
                final String name = user.getString(user.getColumnIndex("name"));
                final String phone = user.getString(user.getColumnIndex("phone"));
                final String ward = user.getString(user.getColumnIndex("ward"));
                final String status = user.getString(user.getColumnIndex("status"));
                final String age = user.getString(user.getColumnIndex("age"));
                String ver = "";
                int textColor;
                if (status.equals("0")) {
                    ver = "Not Verified";
                    textColor = Color.RED;
                } else {
                    ver = "Verified";
                    textColor = getResources().getColor(R.color.colorAccent);
                }

                RelativeLayout child = (RelativeLayout) inflater.inflate(R.layout.child_profile, linList, false);
                child.setBackgroundResource(R.drawable.child_dialog);

                TextView textName = (TextView) child.findViewById(R.id.textName);
                textName.setText(name);

                TextView textID = (TextView) child.findViewById(R.id.textID);
                textID.setText(voterID);

                TextView textVerify = (TextView) child.findViewById(R.id.textVerify);
                textVerify.setText(ver);
                textVerify.setTextColor(textColor);

                child.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        final Dialog showMember = new Dialog(mContext);
                        showMember.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        showMember.setContentView(R.layout.dialog_approval);

                        Button butApproval = (Button) showMember.findViewById(R.id.butApproval);
                        butApproval.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userApprove(showMember, data, voterID);
                            }
                        });

                        String ver = "";
                        int textColor;
                        if (status.equals("0")) {
                            ver = "Profile Unverified";
                            textColor = Color.RED;
                        } else {
                            ver = "Verified Profile";
                            textColor = getResources().getColor(R.color.colorAccent);
                            butApproval.setVisibility(GONE);
                        }

                        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                        showMember.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                        Rect displayRectangle = new Rect();
                        Window window = showMember.getWindow();
                        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                        TextView textVer = (TextView) showMember.findViewById(R.id.textVer);
                        textVer.setText(ver);
                        textVer.setTextColor(textColor);

                        TextView textName = (TextView) showMember.findViewById(R.id.textName);
                        textName.setText("Name: " + name);

                        TextView textPhone = (TextView) showMember.findViewById(R.id.textPhone);
                        textPhone.setText("Phone Number: " + phone);

                        TextView textAge = (TextView) showMember.findViewById(R.id.textAge);
                        textAge.setText("Age: " + age);

                        TextView textVoterID = (TextView) showMember.findViewById(R.id.textVoterID);
                        textVoterID.setText("Voter ID: " + voterID);

                        TextView textWard = (TextView) showMember.findViewById(R.id.textWard);
                        textWard.setText("Ward No.: " + ward);

                        Button closeImage = (Button) showMember.findViewById(R.id.closeImage);
                        closeImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showMember.dismiss();
                            }
                        });

                        showMember.getWindow().setBackgroundDrawable(null);
                        showMember.show();
                    }
                });

                linList.addView(child);
                user.moveToNext();
                i++;
            }
        }
    }


    private void userApprove(final Dialog showMember, final String dataInput, final String voterID) {

        Methods.showDialog(mContext, getString(R.string.wait));

        String tag_string_req = "req_data";

        StringRequest strReq = new StringRequest(Request.Method.POST, Methods.URL_MEMBER, new Response.Listener<String>() {

            @Override
            public void onResponse(final String data) {
                //Log.e("Response", data.toString());

                try {
                    JSONObject dataResp = new JSONObject(data);
                    JSONArray dataResponse = new JSONArray(dataResp.getString("Update"));
                    JSONObject postDataResponse = dataResponse.getJSONObject(0);

                    final String error = postDataResponse.getString("ERROR");
                    final String errorMsg = postDataResponse.getString("ERRORMSG");

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (error.equals("false")) {
                                vibrate(50);
                                showMember.dismiss();
                                linList.removeAllViews();
                                userData(dataInput);

                            } else if (error.equals("true")) {
                                vibrate(100);
                            }

                            Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }, 1100);

                    Methods.hideDialog(1000);

                } catch (JSONException e) {
                    e.printStackTrace();
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

                //Log.e("Params", String.valueOf(params));
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    private void userData(final String dataInput) {

        Methods.showDialog(mContext, getString(R.string.wait));

        String tag_string_req = "req_data";

        StringRequest strReq = new StringRequest(Request.Method.POST, Methods.URL_MEMBER, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e("Response", data.toString());

                try {
                    JSONObject dataResp = new JSONObject(data);
                    JSONArray dataResponse = new JSONArray(dataResp.getString("User"));
                    db.deleteMember();
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

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            vibrate(100);
                            createchild(dataInput);
                        }
                    }, 1000);

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

                //Log.e("Params", String.valueOf(params));
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

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
        vibrate(100);
        new AlertDialog.Builder(mContext)
                .setTitle("Confirmation")
                .setMessage("\nLogout & leave this page?")
                .setNegativeButton("Log Out", new DialogInterface.OnClickListener() {
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
