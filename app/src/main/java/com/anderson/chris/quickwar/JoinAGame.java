package com.anderson.chris.quickwar;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class JoinAGame extends AppCompatActivity {
    Button btnJoinGame;
    EditText txtUserInputGameId;
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String EXTRA_GAME_ID = "EXTRA_GAME_ID";
    String gameId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_agame);

        btnJoinGame = (Button) findViewById(R.id.btnConnectToGame);
        txtUserInputGameId = (EditText) findViewById(R.id.txtUserInputGameID);

        btnJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToGame();
            }
        });


    txtUserInputGameId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handled = true;
                gameId = txtUserInputGameId.getText() + "";
                connectToGame();
            }
            return handled;
        }
    });

//            (new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            gameId = txtUserInputGameId.getText().toString();
//            TextView text = (TextView) findViewById(R.id.txtJoinAGameLabel);
//            text.setText(gameId);
//            return false;
//        }
//    });
    }

    public void connectToGame() {

        //if can connect
        Intent intent = new Intent(this, UserName.class);

        Bundle extras = new Bundle();

        extras.putString(EXTRA_GAME_ID, gameId);

        intent.putExtra(BUNDLE_EXTRAS, extras);

        startActivity(intent);
        finish();

        //else try again
    }
}
