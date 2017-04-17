package com.anderson.chris.quickwar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HowToPlay extends AppCompatActivity {

    TextView txtInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        registerListeners();

    }

    public void registerListeners(){
        txtInstructions = (TextView) findViewById(R.id.txtInstructions);
    }
}
