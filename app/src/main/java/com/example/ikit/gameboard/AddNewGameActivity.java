package com.example.ikit.gameboard;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

public class AddNewGameActivity extends AppCompatActivity{

    private DbHelper dbHelper;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        getActionBar();
        setContentView(R.layout.add_new_game_data_base);

        dbHelper = new DbHelper(this);

        /* variable for the database */
        long returnReq;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /* variable for the bundle */
        String name;
        String place;
        String comment;
        int duration;
        int nbPlayer;
        boolean played;
        boolean gameToTest;
        String type;

        /* get the data from the bundle*/
        Bundle extras = getIntent().getExtras();
        name = extras.getString("gameName");
        place = extras.getString("gamePlace");
        comment = extras.getString("gameComment");
        duration = extras.getInt("gameDuration");
        nbPlayer = extras.getInt("gameNbPlayer");
        played = extras.getBoolean("gamePlayed");
        type = extras.getString("gameType");
        gameToTest = extras.getBoolean("gameToTest");
        /* convert the boolean into int for the database */
        int alreadyPlayed = 0;
        if(played){
            alreadyPlayed = 1;
        }
        int wantToTest = 0;
        if(gameToTest){
            wantToTest = 1;
        }

        /* if there is no comment entered, set empty instead of the sentence */
        if(comment.equals(getResources().getString(R.string.new_game_comments_edit_text))){
            comment = "";
        }

        /* insert the game in the table */
        contentValues.put(GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME,name);
        contentValues.put(GameBoardContract.GameBoardEntry.COLUMN_COMMENTS,comment);
        contentValues.put(GameBoardContract.GameBoardEntry.COLUMN_DURATION,duration);
        contentValues.put(GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX,nbPlayer);
        contentValues.put(GameBoardContract.GameBoardEntry.COLUMN_PLAYED,alreadyPlayed);
        contentValues.put(GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST,wantToTest);

        returnReq = db.insert(GameBoardContract.GameBoardEntry.TABLE_GAMES,
                null,
                contentValues);
        if(returnReq == -1 ){
            Toast.makeText(this, ""+getResources().getString(R.string.error_insert_game_data_base),Toast.LENGTH_LONG).show();
        }

        /* insert the gametype in table */
        int gameId;
            //retrieve the key of the game
        String[] projection = {GameBoardContract.GameBoardEntry.COLUMN_ID_GAME};
        String whereClause = ""+ GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME+" = ?";
        String[] whereArgs = {name};

        Cursor cursor = db.query(
                GameBoardContract.GameBoardEntry.TABLE_GAMES,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        gameId = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME));
            //retrieve the key of the type
        cursor.close();
        String[] projection2 = {GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE};
        String whereClause2 = ""+ GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE+" = ?";
        String[] whereArgs2 = {type};
        Cursor cursor2 = db.query(
                GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE,
                projection2,
                whereClause2,
                whereArgs2,
                null,
                null,
                null
        );
        cursor2.moveToFirst();
        int gameTypeId;
        gameTypeId = cursor2.getInt(cursor2.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE));
        cursor2.close();

        //insert in the table link
        long returnReq2;
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT,gameTypeId);
        contentValues1.put(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT,gameId);
        returnReq2 = db.insert(GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_TYPE,null,contentValues1);

        if(returnReq2 == -1){
            /* fail */
            Toast.makeText(this, getResources().getString(R.string.error_insert_game_data_base), Toast.LENGTH_LONG).show();
        }

        /* insert the game in the table link place */
            //get the id for the place
        String[] projection3 = {GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES};
        String whereClause3 = ""+ GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES+" = ?";
        String[] whereArgs3 = {place};
        int placeId;
        Cursor cursor3 = db.query(
            GameBoardContract.GameBoardEntry.TABLE_PLACES,
            projection3,
            whereClause3,
            whereArgs3,
            null,
            null,
            null
        );
        if(cursor3.getCount() <1){
            Toast.makeText(this, ""+getResources().getString(R.string.new_game_error_get_id_place),Toast.LENGTH_LONG).show();
        }else{
            cursor3.moveToFirst();
            placeId = cursor3.getInt(cursor3.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES));
            cursor3.close();
            //insert into the table
            long returnReq3;
            ContentValues contentValues3 = new ContentValues();
            contentValues3.put(GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES_REF_LGP,placeId);
            contentValues3.put(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGP,gameId);
            returnReq3 = db.insert(GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_PLACE,null,contentValues3);
            if(returnReq3 == -1){
                Toast.makeText(this, getResources().getString(R.string.new_game_error_insert_link_place),Toast.LENGTH_LONG).show();
            }
        }
    }
}
