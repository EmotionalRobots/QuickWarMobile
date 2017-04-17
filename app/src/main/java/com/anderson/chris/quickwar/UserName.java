package com.anderson.chris.quickwar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class UserName extends AppCompatActivity {

    Button btnStartGame;
    EditText txtUserInputPlayerId;
    String gameID;
    String playerName = "Anonymous";
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String EXTRA_GAME_ID = "EXTRA_GAME_ID";
    private static final String EXTRA_PLAYER_NAME = "EXTRA_PLAYER_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);


        Bundle extrasReceived = getIntent().getBundleExtra(BUNDLE_EXTRAS);
        //store continent locally
        gameID = extrasReceived.getString(EXTRA_GAME_ID);

        btnStartGame = (Button) findViewById(R.id.btnStartGame);
        txtUserInputPlayerId = (EditText) findViewById(R.id.txtUserIDInput);
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToGame();
            }
        });


        txtUserInputPlayerId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = true;
                    playerName = txtUserInputPlayerId.getText() + "";
                    connectToGame();
                }
                return handled;
            }
        });
    }

    public void connectToGame(){

        //if can connect
        Intent intent = new Intent(this, GamePlay.class);

        Bundle extras = new Bundle();

        extras.putString(EXTRA_GAME_ID, gameID);

        extras.putString(EXTRA_PLAYER_NAME, playerName);

        intent.putExtra(BUNDLE_EXTRAS, extras);

        startActivity(intent);
        finish();

        //else try again
    }
}
