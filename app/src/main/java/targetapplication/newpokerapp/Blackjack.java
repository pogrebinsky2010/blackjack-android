package targetapplication.newpokerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.TextView.OnEditorActionListener;

/**
 * Created by talaba on 7/11/16.
 */
public class Blackjack extends AppCompatActivity implements OnEditorActionListener, SharedPreferences.OnSharedPreferenceChangeListener
{

    private Toolbar  toolbar;
    TextView playerScoreTextView;
    TextView dealerScoreTextView;
    LogicClass game = new LogicClass();
    ImageView centerCard;

    ImageView playerCardOne;
    ImageView playerCardTwo;
    ImageView playerCardThree;
    ImageView playerCardFour;
    ImageView playerCardFive;

    ImageView dealerCardOne;
    ImageView dealerCardTwo;
    ImageView dealerCardThree;
    ImageView dealerCardFour;
    ImageView dealerCardFive;

    Button hitButton;
    Button holdButton;
    Button newGameButton;

    RadioButton acesHighButton;
    RadioButton acesLowButton;

    //define Shared Preferences object
    private SharedPreferences savedValues;

    //define instance variables
    //TURN COUNT (used in displayGui())
    private int count = 3;

    private boolean dealerWins = false;
    private boolean cardsDealt = false;
    private boolean busted = false;
    private boolean playerWins = false;
    private boolean dealerHasGone = false; // THIS WILL TRACK WHEN I CAN CHECK FOR A WINNER
    private boolean playerHasGone = false; // THIS WILL TRACK WHEN I CAN CHECK FOR A WINNER
    private boolean playerHasAce = false;
    private boolean tenAdded = false;
    private int winningAmount = 21;

    private int dealerFirstCard = -1;
    private int dealerSecondCard = -1;
    private int dealerThirdCard = -1;
    private int dealerFourthCard = -1;
    private int dealerFifthCard = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_jack);

        // GETTING REFERENCES TO WIDGETS
        playerScoreTextView = (TextView)findViewById(R.id.playerScoreTextView);
        centerCard = (ImageView) findViewById(R.id.centerCard);
        playerCardOne = (ImageView) findViewById(R.id.playerCardOne);
        playerCardTwo = (ImageView) findViewById(R.id.playerCardTwo);
        playerCardThree = (ImageView) findViewById(R.id.playerCardThree);
        playerCardFour = (ImageView) findViewById(R.id.playerCardFour);
        playerCardFive = (ImageView) findViewById(R.id.playerCardFive);
        dealerScoreTextView = (TextView)findViewById(R.id.dealerScoreTextView);
        dealerCardOne = (ImageView) findViewById(R.id.dealerCardOne);
        dealerCardTwo = (ImageView) findViewById(R.id.dealerCardTwo);
        dealerCardThree = (ImageView) findViewById(R.id.dealerCardThree);
        dealerCardFour = (ImageView) findViewById(R.id.dealerCardFour);
        dealerCardFive = (ImageView) findViewById(R.id.dealerCardFive);
        hitButton = (Button)findViewById(R.id.hitButton);
        holdButton = (Button)findViewById(R.id.holdButton);
        acesHighButton = (RadioButton)findViewById(R.id.acesHighButton);
        acesLowButton = (RadioButton)findViewById(R.id.acesLowButton);
        newGameButton = (Button)findViewById(R.id.newGameButton);

        //make replica array for tracking used cards
        game.makeArray();

        //get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        //toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get the preferences from settings
        loadPreferences();
    }

    public void setWinningAmount(int amount)
    {
        winningAmount = amount;
    }

    //preferences from user changed settings in runtime
    public void loadPreferences()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String maxAmountString = settings.getString("winning_score", "21");
        setWinningAmount(Integer.valueOf(Integer.valueOf(maxAmountString)));
        game.setWinningScore(Integer.valueOf(maxAmountString));
        settings.registerOnSharedPreferenceChangeListener(Blackjack.this);//this will update my values after the initial onCreate
    }

    //display menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.black_jack, menu);
        return true;
    }

    //define option items actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.menu_about:
                Toast.makeText(this, "I'm Talaba and this is my version of blackjack enjoy!", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //store values on pause

    @Override
    public void onPause()
    {
        //saving instance variables
        Editor editor = savedValues.edit();
        // editor.putInt("index", game.getIndex());
        editor.putInt("dealerScore", game.getDealerScore());
        editor.putInt("playerScore", game.getPlayerScore());
        editor.putInt("playerFirstIndex", game.playerFirstIndex);
        editor.putInt("playerSecondIndex", game.playerSecondIndex);
        editor.putInt("playerThirdIndex", game.playerThirdIndex);
        editor.putInt("playerFourthIndex", game.playerFourthIndex);
        editor.putInt("playerFifthIndex", game.playerFifthIndex);
        editor.putBoolean("playerTurn", game.getPlayersTurn());
        editor.putBoolean("busted", game.isBust);
        editor.putInt("dealersFirstIndex", game.dealerFirstIndex);
        editor.putBoolean("cardsDealt", cardsDealt);
        editor.putInt("dealerFirstCard", dealerFirstCard);
        editor.putInt("dealerSecondCard", dealerSecondCard);
        editor.putInt("dealerThirdCard", dealerThirdCard);
        editor.putInt("dealerFourthCard", dealerFourthCard);
        editor.putInt("dealerFifthCard", dealerFifthCard);


        editor.commit();
        super.onPause();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //  game.setIndex(savedValues.getInt("index", 52));
        game.setPlayerScore(savedValues.getInt("playerScore", 0));
        game.setDealerScore(savedValues.getInt("dealerScore",0));
        int playerIndexOne = savedValues.getInt("playerFirstIndex", 52);
        int playerIndexTwo = savedValues.getInt("playerSecondIndex", 52);
        int playerIndexThree = savedValues.getInt("playerThirdIndex", 52);
        int playerIndexFour = savedValues.getInt("playerFourthIndex", 52);
        int playerIndexFive = savedValues.getInt("playerFifthIndex", 52);
        int dealerIndexOne = savedValues.getInt("dealersFirstIndex", 52);
        int dealerOne = savedValues.getInt("dealerFirstCard", 52);
        int dealerTwo = savedValues.getInt("dealerSecondCard", 52);
        int dealerThree = savedValues.getInt("dealerThirdCard", 52);
        int dealerFour = savedValues.getInt("dealerFourthCard", 52);
        int dealerFive = savedValues.getInt("dealerFifthCard", 52);

        dealerFirstCard = dealerOne;
        dealerSecondCard = dealerTwo;
        dealerThirdCard = dealerThree;
        dealerFourthCard = dealerFour;
        dealerFifthCard = dealerFive;

        cardsDealt = savedValues.getBoolean("cardsDealt", false);

        if(dealerIndexOne != -1)
        {
            dealerCardOne.setImageResource(images[dealerIndexOne]);
        }

        game.isBust = savedValues.getBoolean("busted", false);
        playerScoreTextView.setText(String.valueOf(savedValues.getInt("playerScore", 0)));

        if(playerIndexOne != -1)
        {
            playerCardOne.setImageResource(images[playerIndexOne]);
        }
        if(playerIndexTwo!= -1)
        {
            playerCardTwo.setImageResource(images[playerIndexTwo]);
        }
        if(playerIndexThree!= -1)
        {
            playerCardThree.setImageResource(images[playerIndexThree]);
        }
        if(playerIndexFour != -1)
        {
            playerCardFour.setImageResource(images[playerIndexFour]);
        }
        if(playerIndexFive!= -1)
        {
            playerCardFive.setImageResource(images[playerIndexFive]);
        }
        game.setPlayerTurn(savedValues.getBoolean("playerTurn", true));
        dealerScoreTextView.setText("??");


    }

    //ARRAY OF IMAGES     HEARTS
    int[] images = { R.drawable.ace_hearts, R.drawable.two_hearts, R.drawable.three_hearts, R.drawable.four_hearts, R.drawable.five_hearts, R.drawable.six_hearts, R.drawable.seven_hearts,
            R.drawable.eight_hearts, R.drawable.nine_hearts, R.drawable.ten_hearts, R.drawable.jack_hearts, R.drawable.queen_hearts, R.drawable.king_hearts,
            //DIAMONDS
            R.drawable.ace_diamonds, R.drawable.two_diamonds, R.drawable.three_diamonds, R.drawable.four_diamonds, R.drawable.five_diamonds, R.drawable.six_diamonds, R.drawable.seven_diamonds,
            R.drawable.eight_diamonds, R.drawable.nine_diamonds, R.drawable.ten_diamonds, R.drawable.jack_diamonds, R.drawable.queen_diamonds, R.drawable.king_diamonds,
            //SPADES
            R.drawable.ace_spades, R.drawable.two_spades, R.drawable.three_spades, R.drawable.four_spades, R.drawable.five_spades, R.drawable.six_spades, R.drawable.seven_spades,
            R.drawable.eight_spades, R.drawable.nine_spades, R.drawable.ten_spades, R.drawable.jack_spades, R.drawable.queen_spades, R.drawable.king_spades,
            //CLUBS
            R.drawable.ace_clubs, R.drawable.two_clubs, R.drawable.three_clubs, R.drawable.four_clubs, R.drawable.five_clubs, R.drawable.six_clubs, R.drawable.seven_clubs,
            R.drawable.eight_clubs, R.drawable.nine_clubs, R.drawable.ten_clubs, R.drawable.jack_clubs, R.drawable.queen_clubs, R.drawable.king_clubs,
            //CARD BACK
            R.drawable.card_back_three
    };



    public void dealOnClick(View v)
    {
        //DEALING THE CARDS TO PLAYER
        newGameButton.callOnClick();
        cardsDealt = true; // ENABLES HIT BUTTON
        hitButton.callOnClick(); //HIT
        hitButton.callOnClick(); //HIT
        game.endTurn();          //NOW SWITCHING PLAYERS TO DEAL TO DEALER
        count = 1;               //COUNT IS A TURN COUNT SO I CAN PUT THE CARD IN THE RESPECTIVE SLOT
        hitButton.callOnClick(); //HIT
        hitButton.callOnClick(); // HIT
        game.endTurn(); //PLAYERS TURN
    }

    public void hitOnClick(View v)
    {
        if(cardsDealt)
        {
            game.hit();
            displayGui();
        }
        else
        {
            Toast.makeText(this, "Please deal the cards first by pressing: Deal", Toast.LENGTH_LONG).show();
        }

    }
    public void holdOnClick(View v)
    {
        count = 3;//RESETTING TURN COUNT AFTER HOLD IS PRESSED GOING TO 3 B/C 2 CARDS WERE DEALT
        //THESE ARE USED TO INDICATE THAT THE TWO HAVE GONE AFTER THE INITIAL DEAL, SINCE THE DEAL SIMPLY CALLS HIT TWICE FOR EACH PLAYER
        playerHasGone = true;
        game.hold();
        cardsDealt = true;
        while(game.getDealerScore() < (winningAmount-4))
        {
            if(game.getPlayersTurn() == false)
            {
                hitButton.callOnClick();
            }

        }
        dealerHasGone = true;
        game.endTurn();
        if(!dealerWins && !playerWins)
        {
            displayGui();
        }

    }

    /**
     * User clicked to have aces as 11 instead of 1.
     * @param v View object sent in by android library.
     */
    public void highAcesOnClick(View v)
    {
        acesLowButton.setChecked(false);
        game.setAceHighButtonIsChecked(true);
        if(playerHasAce  && acesHighButton.isChecked() && !tenAdded )
        {
            tenAdded = true;
            game.setPlayerScore(game.getPlayerScore() + 10);
        }
    }

    public void lowAcesOnClick(View v)
    {
        acesHighButton.setChecked(false);
    }

    public void newGameOnClick(View v)
    {
        //      RESET SCORES
        game.setPlayerScore(0);
        game.setDealerScore(0);

        //DISPLAY RESET SCORES
        playerScoreTextView.setText(String.valueOf(game.getPlayerScore()));
        dealerScoreTextView.setText(String.valueOf(game.getDealerScore()));

        //RESET TURN COUNT
        count = 1;

        // RESET CENTER CARD
        game.setIndex(52); //53rd item in image array is card back

        // RESET PLAYERS CARDS
        playerCardOne.setImageResource(R.drawable.card_back_three);
        playerCardTwo.setImageResource(R.drawable.card_back_three);
        playerCardThree.setImageResource(R.drawable.card_back_three);
        playerCardFour.setImageResource(R.drawable.card_back_three);
        playerCardFive.setImageResource(R.drawable.card_back_three);

        // RESET DEALERS CARDS
        dealerCardOne.setImageResource(R.drawable.card_back_three);
        dealerCardTwo.setImageResource(R.drawable.card_back_three);
        dealerCardThree.setImageResource(R.drawable.card_back_three);
        dealerCardFour.setImageResource(R.drawable.card_back_three);
        dealerCardFive.setImageResource(R.drawable.card_back_three);

        // RESET CENTER CARD
        centerCard.setImageResource(R.drawable.card_back_three);

        // RESET BUST
        busted = false;

        // RESET DEAL
        cardsDealt = false;

        // RESET CHECK FOR WINNER UTILITY VARIABLES
        playerHasGone = false;
        dealerHasGone = false;

        // RESET WINNERS
        dealerWins = false;
        playerWins = false;

        // RESET DEALERS HIDDEN CARDS
        dealerSecondCard = -1;
        dealerThirdCard = -1;
        dealerFourthCard = -1;
        dealerFifthCard = -1;

        // RESET PLAYERS ACES
        playerHasAce = false;
        tenAdded = false;

        //RESET RADIO BUTTON
        acesHighButton.setChecked(false);
        //RESET TURN COUNT
        game.setPlayerTurnCount(0);

        //RESET ARRAY
        game.makeArray();
    }

    public void checkForBust()
    {
        if(game.getPlayerScore() > winningAmount){
            busted = true;
            centerCard.setImageResource(R.drawable.busted);
            playerScoreTextView.setText("Dealer WINS!! player one busted at: " + String.valueOf(game.getPlayerScore()));
            dealerWins = true;
            dealerScoreTextView.setText(String.valueOf(game.getDealerScore()));
            if(dealerSecondCard != -1)
            {
                dealerCardTwo.setImageResource(images[dealerSecondCard]);
            }

            if(dealerThirdCard != -1)
            {
                dealerCardThree.setImageResource(images[dealerThirdCard]);
            }

            if(dealerFourthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFourthCard]);
            }

            if(dealerFifthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFifthCard]);
            }
        }
        else if(game.getDealerScore() > winningAmount){
            busted = true;
            centerCard.setImageResource(R.drawable.dealer_bust);
            playerScoreTextView.setText("Player WINS!!!, player: " + game.getPlayerScore());
            playerWins = true;
            dealerScoreTextView.setText(String.valueOf(game.getDealerScore()));
            dealerCardTwo.setImageResource(images[dealerSecondCard]);
            if(dealerThirdCard != -1)
            {
                dealerCardThree.setImageResource(images[dealerThirdCard]);
            }
            if(dealerFourthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFourthCard]);
            }
            if(dealerFifthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFifthCard]);
            }
        }
    }
    public void checkForWinner()
    {
        if(game.getPlayerScore() == winningAmount && dealerHasGone && game.getDealerScore()< winningAmount)
        {
            //display player as winner
            dealerScoreTextView.setText("");
            playerScoreTextView.setText("Player WINS!, player: " + game.getPlayerScore() + " dealer: "+ game.getDealerScore());
            playerWins = true;
            centerCard.setImageResource(R.drawable.player_win);
            dealerScoreTextView.setText(String.valueOf(game.getDealerScore()));
            dealerCardTwo.setImageResource(images[dealerSecondCard]);
            if(dealerThirdCard != -1)
            {
                dealerCardThree.setImageResource(images[dealerThirdCard]);
            }
            if(dealerFourthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFourthCard]);
            }
            if(dealerFifthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFifthCard]);
            }
        }
        else if(game.getPlayerScore() == winningAmount && game.getDealerScore() == winningAmount){
            //display draw
            dealerScoreTextView.setText("");
            playerScoreTextView.setText("draw, player and dealer: "+game.getPlayerScore());
            dealerWins = true;
            playerWins = true;
            centerCard.setImageResource(R.drawable.draw_twenty_one);
        }
        else if(game.getDealerScore() == winningAmount && game.getPlayerScore() < winningAmount)
        {
            //display dealer as winner
            dealerScoreTextView.setText("");
            playerScoreTextView.setText("Dealer Wins, player: "+game.getPlayerScore() + " dealer: "+ game.getDealerScore());
            dealerWins = true;
            centerCard.setImageResource(R.drawable.dealer_has_21);
            dealerScoreTextView.setText(String.valueOf(game.getDealerScore()));
            dealerCardTwo.setImageResource(images[dealerSecondCard]);
            if(dealerThirdCard != -1)
            {
                dealerCardThree.setImageResource(images[dealerThirdCard]);
            }
            if(dealerFourthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFourthCard]);
            }
            if(dealerFifthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFifthCard]);
            }
        }
        else if((winningAmount-game.getPlayerScore())<(winningAmount-game.getDealerScore()) && dealerHasGone) //PLAYER IS CLOSER TO 21 OR WINNING AMOUNT (DEFAULT IS 21)
        {
            //display player as winner
            dealerScoreTextView.setText("");
            playerScoreTextView.setText("Player WINS! player: "+game.getPlayerScore() + " dealer: "+ game.getDealerScore());
            playerWins = true;
            centerCard.setImageResource(R.drawable.player_win);
            dealerScoreTextView.setText(String.valueOf(game.getDealerScore()));
            dealerCardTwo.setImageResource(images[dealerSecondCard]);
            if(dealerThirdCard != -1)
            {
                dealerCardThree.setImageResource(images[dealerThirdCard]);
            }
            if(dealerFourthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFourthCard]);
            }
            if(dealerFifthCard != -1) {
                dealerCardFour.setImageResource(images[dealerFifthCard]);
            }
        }
        else if((winningAmount-game.getPlayerScore())>(winningAmount-game.getDealerScore()) && playerHasGone) //DEALER SCORE IS CLOSER TO 21 OR WINNING AMOUNT (DEFAULT IS 21)
        {
            //display dealer as winner
            dealerScoreTextView.setText("");
            playerScoreTextView.setText("Dealer WINS! player: "+game.getPlayerScore() + "  dealer: "+ game.getDealerScore());
            dealerWins = true;
            centerCard.setImageResource(R.drawable.dealer_wins);
            dealerScoreTextView.setText(String.valueOf(game.getDealerScore()));
            dealerCardTwo.setImageResource(images[dealerSecondCard]);
            if(dealerThirdCard != -1)
            {
                dealerCardThree.setImageResource(images[dealerThirdCard]);
            }
            if(dealerFourthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFourthCard]);
            }
            if(dealerFifthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFifthCard]);
            }
        }
        else if(game.getPlayerScore() == game.getDealerScore() && playerHasGone && dealerHasGone)
        {
            //display draw
            dealerScoreTextView.setText("");
            playerScoreTextView.setText("draw player and dealer: "+game.getPlayerScore());
            dealerWins = true;
            playerWins = true;
            centerCard.setImageResource(R.drawable.draw_twenty_one);
            dealerScoreTextView.setText(String.valueOf(game.getDealerScore()));
            dealerCardTwo.setImageResource(images[dealerSecondCard]);
            if(dealerThirdCard != -1)
            {
                dealerCardThree.setImageResource(images[dealerThirdCard]);
            }
            if(dealerFourthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFourthCard]);
            }
            if(dealerFifthCard != -1)
            {
                dealerCardFour.setImageResource(images[dealerFifthCard]);
            }
        }
    }

    public void displayGui()
    {

        dealerScoreTextView.setText("??");

        if (game.getPlayersTurn())
        {
            centerCard.setImageResource(images[game.getIndex()]);
            switch(game.getIndex())
            {
                case 0:
                    game.setPlayerHasAce(true);
                    playerHasAce = true;
                    break;
                case 13:
                    game.setPlayerHasAce(true);
                    playerHasAce = true;
                    break;
                case 26:
                    game.setPlayerHasAce(true);
                    playerHasAce = true;
                    break;
                case 39:
                    game.setPlayerHasAce(true);
                    playerHasAce = true;
                    break;
                default:
                    break;
            }

            if(acesHighButton.isChecked() && playerHasAce)
            {
                playerScoreTextView.setText(String.valueOf(game.getPlayerScore() +10));
            }
            else
            {
                playerScoreTextView.setText(String.valueOf(game.getPlayerScore()));
            }

            if (count == 1) {
                playerCardOne.setImageResource(images[game.getIndex()]);
                count++;
            } else if (count == 2) {
                playerCardTwo.setImageResource(images[game.getIndex()]);
                count++;
            } else if (count == 3) {
                playerCardThree.setImageResource(images[game.getIndex()]);
                count++;
            } else if (count == 4) {
                playerCardFour.setImageResource(images[game.getIndex()]);
                count++;
            } else if (count == 5) {
                playerCardFive.setImageResource(images[game.getIndex()]);
                count = 1;
            }

        } else {

            if (count == 1) {
                dealerCardOne.setImageResource(images[game.getIndex()]);
                dealerFirstCard = game.getIndex();
                count++;
            }
            else if (count == 2) {
                dealerSecondCard = game.getIndex();
                count++;
            }
            else if (count == 3) {
                dealerThirdCard = game.getIndex();
                count++;
            }
            else if (count == 4) {
                dealerFourthCard = game.getIndex();
                count++;
            }
            else if (count == 5) {
                dealerFifthCard = game.getIndex();
                count = 1;
            }
        }
        checkForBust();
        if(busted)
        {
            //do not check for winner
        }
        else {
            checkForWinner();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        loadPreferences();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }
}


