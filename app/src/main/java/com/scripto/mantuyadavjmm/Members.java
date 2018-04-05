package com.scripto.mantuyadavjmm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Members extends AppCompatActivity {

    static Activity members;
    static List<List<String>> memberData = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Membership");

        mContext = this;
        members = this;

        Button butReg = findViewById(R.id.butReg);
        Button butSign = findViewById(R.id.butSign);

        butReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, UserInput.class).putExtra("tag", "reg"));
            }
        });

        butSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, UserInput.class).putExtra("tag", "log"));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
