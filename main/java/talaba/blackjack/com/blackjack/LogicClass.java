package talaba.blackjack.com.blackjack;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Random;

/**
 * Created by talaba on 7/12/16.
 */
public class LogicClass extends AppCompatActivity {

    //makeing an array of booleans so i can keep track of which cards were drawn
    private boolean aceHearts, aceSpades, aceClubs , aceDiamonds, twoHearts,twoSpades,twoClubs,twoDiamonds,threeHearts, threeClubs,threeSpades, threeDiamonds, fourHearts, fourClubs, fourSpades, fourDiamonds,
            fiveHearts, fiveClubs, fiveSpades, fiveDiamonds, sixHearts, sixClubs, sixSpades, sixDiamonds,sevenHearts, sevenClubs, sevenSpades, sevenDiamonds, eightHearts, eightClubs, eightSpades, eightDiamonds,
            nineHearts, nineClubs, nineSpades, nineDiamonds, tenHearts, tenClubs ,tenSpades, tenDiamonds, jackHearts, jackClubs, jackSpades, jackDiamonds, queenHearts, queenClubs, queenSpades, queenDiamonds,
            kingHearts, kingClubs, kingSpades, kingDiamonds;
    boolean[] replicaImages={aceHearts, twoHearts, threeHearts, fourHearts, fiveHearts, sixHearts, sevenHearts, eightHearts, nineHearts, tenHearts, jackHearts, queenHearts,kingHearts, aceDiamonds
    , twoDiamonds, threeDiamonds, fourDiamonds, fiveDiamonds, sixDiamonds, sevenDiamonds, eightDiamonds, nineDiamonds, tenDiamonds, jackDiamonds, queenDiamonds, kingDiamonds, aceSpades, twoSpades, threeSpades,
            fourSpades, fiveSpades, sixSpades, sevenSpades, eightSpades, nineSpades, tenSpades, jackSpades, queenSpades, kingSpades, aceClubs, twoClubs, threeClubs, fourClubs, fiveClubs, sixClubs, sevenClubs,
            eightClubs, nineClubs, tenClubs, jackClubs, queenClubs, kingClubs};

    // random number for the indexing of the array of cards, and for getting the value of each card
    Random rand = new Random();

    private String player = "na";
    private int randomNum;
    private int playerScore = 0;
    private int dealerScore = 0;
    private int imageIndex = 52;
    private boolean dealersTurn = false;
    private boolean playersTurn = true;
    public boolean isBust = false;
    private boolean winner = false;
    private boolean playerHasAce = false;
    private boolean aceHighButtonIsChecked = false;
    public int playerFirstCard = 52;
    private int playerTurnCount = 0;
    private int winningScore = 21;

    //PUBLIC VARIABLE TO BE ACCESSED BY SAVED PREFERENCES, TO SAVE EXTRA CODE OF 6  GETTERS AND 6 SETTERS AS WELL AS 24 CALLS
    public int playerFirstIndex = -1;
    public int playerSecondIndex = -1;
    public int playerThirdIndex = -1;
    public int playerFourthIndex = -1;
    public int playerFifthIndex = -1;
    public int dealerFirstIndex = -1;


    public void setPlayerTurnCount(int _turnCount)
    {
        playerTurnCount = _turnCount;
    }
    public int getPlayerTurnCount() {return playerTurnCount;}

    //GETTERS
    public int getDealerScore(){return dealerScore;}
    public boolean getPlayersTurn(){return playersTurn;}
    public int getPlayerScore(){return playerScore;}

    //SETTERS
    public void setPlayerScore(int _score)
    {
        playerScore = _score;
    }
    public void setPlayerTurn(boolean tOf){playersTurn = tOf;}
    public void setDealerScore(int _score)
    {
        dealerScore = _score;
    }
    public void setAceHighButtonIsChecked(boolean tOf){aceHighButtonIsChecked = tOf;}
    public void setPlayerHasAce(boolean tOf){playerHasAce = tOf;}
    public void setIndex(int _index)
    {
        imageIndex = _index;
    }
    public void setWinningScore(int _winningScore) {winningScore = _winningScore;}

    //  INITIALIZING ARRAY
    public void makeArray()
    {
        for (int i = 0; i < 52; i++) {
            replicaImages[i] = true;
        }
    }

    public void hit()
    {
        //RANDOM NUMBER IS USED FOR IMAGE INDEXING AS WELL AS SCORING
         createRandomNumber();
        if(playersTurn) {
            playerScore += getValueOfCard(randomNum);
            playerTurnCount++;

            //KEEPING TRACK OF THE TURN COUNT SO I CAN SAVE THE VARIABLES FOR WHEN USER ROTATES DEVICE
            switch(playerTurnCount)
            {
                case 1:
                    playerFirstIndex = randomNum;
                    break;
                case 2:
                    playerSecondIndex = randomNum;
                    break;
                case 3:
                    playerThirdIndex = randomNum;
                    break;
                case 4:
                    playerFourthIndex = randomNum;
                    break;
                case 5:
                    playerFifthIndex = randomNum;
                    break;
                default:
                    break;
            }


            if(playerScore > winningScore)
            {
                isBust = true;
            }
        }else{
            dealerScore += getValueOfCard(randomNum);
            dealerFirstIndex = randomNum;
            if(dealerScore > winningScore)
            {
                isBust = true;
            }
        }

    }
    public void hold()
    {
        endTurn();
    }


    //USE FOR ARRAY INDEX AND VALUE OF CARD (VALUE WILL USE MODULUS OPERATOR)
    public void createRandomNumber()
    {
        int firstRandom = rand.nextInt(52);
        if(replicaImages[firstRandom]==true)
        {
            replicaImages[firstRandom]=false;
            randomNum = firstRandom;
            imageIndex = randomNum;
            Log.i("hello", String.valueOf(firstRandom));
        }
        else
        {
            int tmpInt;
            while(replicaImages[firstRandom]==false)
            {
              tmpInt= rand.nextInt(52);
                firstRandom = tmpInt;
            }
            randomNum = firstRandom;
            imageIndex = randomNum;
            replicaImages[firstRandom] =false;

        }
    }

    //VALUE OF CARD
    public int getValueOfCard(int randomNumber)
    {

        int value = (randomNumber%13) + 1;
        Log.i("hello", String.valueOf(value));
        if(value<10) {
            return value;
        }else{
            return 10;
        }
    }

    //INDEX OF CARD
    public int getIndex()
    {
        return imageIndex;
    }

    public void switchPlayers()
    {
        if(playersTurn == true)
        {
            playersTurn = false;
            dealersTurn = true;
        }
        else{
            dealersTurn = false;
            playersTurn = true;
        }
    }
    public void endTurn()
    {
        switchPlayers();
    }
}
