package com.anderson.chris.quickwar;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class GamePlay extends AppCompatActivity {

    Button btnFlipCard, btnSendCard;
    TextView txtCardsRemainingNum, txtWarAlert;
    boolean isPlayer1;
    int myDeckSize = 26;
    int sentCardsNum = 0;
    ImageView imgCardDown, imgNewCard;
    private static final String EXTRA_PLAYER_NAME = "EXTRA_PLAYER_NAME";
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String EXTRA_GAME_ID = "EXTRA_GAME_ID";
    Firebase mGameDatabase, mRootDatabase, mSentCardsRef, mPlayer1Database, mPlayer2Database, myDatabase, mNumOfCardsRemainingDatabase;

    int myPlayerNumber = 0;
    String gameID;
    String playerName;
    int currentDeckIndex = 1;
    Card currentCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        Firebase.setAndroidContext(this);

        Bundle extrasReceived = getIntent().getBundleExtra(BUNDLE_EXTRAS);
        //store continent locally
        gameID = extrasReceived.getString(EXTRA_GAME_ID);
        playerName = extrasReceived.getString(EXTRA_PLAYER_NAME);

        Firebase.setAndroidContext(this);
        mRootDatabase = new Firebase("https://quickwar-a9fde.firebaseio.com/");
        mGameDatabase = new Firebase("https://quickwar-a9fde.firebaseio.com/" + gameID);
        mNumOfCardsRemainingDatabase = new Firebase("https://quickwar-a9fde.firebaseio.com/" + gameID + "/NumOfCardsRemaining");
        mPlayer1Database = new Firebase("https://quickwar-a9fde.firebaseio.com/" + gameID + "/Player1");
        mPlayer2Database = new Firebase("https://quickwar-a9fde.firebaseio.com/" + gameID + "/Player2");

        //getPlayerDeterminant
        registerListeners();
        whatPlayerAmI();

        //will also initialize the first time
    }

    public void whatPlayerAmI() {
        Firebase numDatabase = mGameDatabase.child("NumOfPlayers");
        numDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                changeMyPlayerNumber(dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //Assign player1/player 2 values
    public void changeMyPlayerNumber(int x) {
        x++;
        myPlayerNumber = x;
        if (x == 1) {
            isPlayer1 = true;
            //add players name
            mPlayer1Database.child("NAME").setValue(playerName);
            setCurrentCard();

        } else if (x == 2) {
            isPlayer1 = false;
            //add players name
            mPlayer2Database.child("NAME").setValue(playerName);
            setCurrentCard();

        } else {
            setCurrentCard();
        }
        mGameDatabase.child("NumOfPlayers").setValue(x);
    }

    //retrieves next card from database
    public void setCurrentCard() {

        if (isPlayer1) {
            Firebase temp = mPlayer1Database.child(currentDeckIndex + "");

            temp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Card card = dataSnapshot.getValue(Card.class);

                    Log.d("PEZ CARD NAME: ", card.getXmlCardName() + "");
                    setCurrentCardHelper(dataSnapshot.getValue(Card.class));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        } else {
            Firebase temp = mPlayer2Database.child(currentDeckIndex + "");

            temp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Card card = dataSnapshot.getValue(Card.class);
                    setCurrentCardHelper(dataSnapshot.getValue(Card.class));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        currentDeckIndex++;
    }

    public void setCurrentCardHelper(Card myCard) {
        currentCard = myCard;
    }

    public void flipCard() {
        int id = getResources().getIdentifier(currentCard.getXmlCardName(), "drawable", getPackageName());
        Log.d("VALUE: ", currentCard.getCardValue() + "");
        imgNewCard.setImageResource(id);
    }

    //attempts to send card. Will only send if you have not already sent one yet
    public void sendCardAttempt() {
        //check if your last card has been processed yet
        if (isPlayer1) {
            Firebase temp = mGameDatabase.child("SCORE").child("PLAYER1").child("PLAY_CHECK");

            temp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try{
                    String x = dataSnapshot.getValue(String.class);
                    if (x.equals("TRUE")) {
                        Toast.makeText(getBaseContext(), "Please wait!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        sendCard();
                    }

                }

                    catch(Exception e){

                }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        //check if card has been processed yet (player 2)
        else {
            Firebase temp = mGameDatabase.child("SCORE").child("PLAYER2").child("PLAY_CHECK");

            temp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        String x = dataSnapshot.getValue(String.class);
                        if (x.equals("TRUE")) {
                            Toast.makeText(getBaseContext(), "Please wait!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            sendCard();
                        }
                    }

                    catch(Exception e){

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

    }


    //actually sends the card
    public void sendCard() {
        sentCardsNum++;
        myDeckSize--;
        imgNewCard.setImageResource(android.R.color.transparent);
        if (myDeckSize >= -1) {
            setCurrentCard();
            Firebase numOfCardsSentDatabase;

            if (isPlayer1) {
                mPlayer1Database.child("LastSentCard").setValue(currentCard);
                mGameDatabase.child("SCORE").child("PLAYER1").child("PLAY_CHECK").setValue("TRUE");
            } else {
                mPlayer2Database.child("LastSentCard").setValue(currentCard);
                mGameDatabase.child("SCORE").child("PLAYER2").child("PLAY_CHECK").setValue("TRUE");
            }
        } else
            finish();
    }

    public void registerListeners() {
        btnFlipCard = (Button) findViewById(R.id.btnFlipCard);
        btnSendCard = (Button) findViewById(R.id.btnSendCard);
        imgNewCard = (ImageView) findViewById(R.id.imgNewCard);
        imgCardDown = (ImageView) findViewById(R.id.imgCardDown);
        txtCardsRemainingNum = (TextView) findViewById(R.id.txtCardsRemainingNum);
        txtCardsRemainingNum.setText(myDeckSize + "");
        txtWarAlert = (TextView) findViewById(R.id.txtWarAlert);


        btnFlipCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

        btnSendCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCardAttempt();
            }
        });
        mNumOfCardsRemainingDatabase.setValue(26);

        //main purpose of this is to listen for when a war happens, so that myDeckSize will reflect the -4 cards from the war
        mNumOfCardsRemainingDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myDeckSize = dataSnapshot.getValue(Integer.class);
                txtCardsRemainingNum.setText(myDeckSize + "");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        checkWar();
    }

    public void checkWar() {

        Firebase temp = mGameDatabase.child("SCORE").child("ISWAR");
        temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String x = dataSnapshot.getValue(String.class);
                try {
                    if (x.equals("TRUE")) {
                        showWarAlert();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void showWarAlert() {

        new CountDownTimer(3000, 100) {

            public void onTick(long millisUntilFinished) {
                txtWarAlert.setText("WAR!!!");
                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(3); //You can manage the blinking time with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                txtWarAlert.startAnimation(anim);
            }

            public void onFinish() {
                txtWarAlert.setText("");
            }
        }.start();
    }
}