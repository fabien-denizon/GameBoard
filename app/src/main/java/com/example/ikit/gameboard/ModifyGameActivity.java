package com.example.ikit.gameboard;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

public class ModifyGameActivity extends AppCompatActivity{

    private DbHelper dbHelper;
    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.modify_data_layout);
        getActionBar();
        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Bundle extras = getIntent().getExtras();

        String gameName = extras.getString("name");
        String comment = extras.getString("comment");
        int idGame = extras.getInt("id");
        String nbPlayer = extras.getString("maxPlayer");
        String gameType = extras.getString("type");
        String duration = extras.getString("duration");
        boolean played = extras.getBoolean("played");
        boolean wantToTest = extras.getBoolean("toTest");

        /* change the type of the variable that can not be entered into the database*/
        int durationGame = Integer.parseInt(duration.split(" ")[0]);
        int nbMaxPlayer = Integer.parseInt(nbPlayer.split(" ")[0]);
        int gamePlayed;
        if(played){
            gamePlayed = 1;
        }else{
            gamePlayed = 0;
        }

        int gameToTest;
        if(wantToTest){
            gameToTest = 1;
        }else{
            gameToTest = 0;
        }

        int idGameType;
        idGameType = findIdGameType(gameType, db);

        TextView textView = findViewById(R.id.modify_data_display_information_text_view);
        textView.append("gameName "+gameName
        +"\ncomment "+comment
        +"\nidGame "+idGame
        +"\nnbPlayer "+nbPlayer
        +"\ngameType "+gameType
        +"\nduration "+duration
        +"\nplayed "+played
        +"\nwantToTest "+wantToTest);
        /* if we found the game type of the game we can continue*/
        if(idGameType >= 0){
            int returnUpdate;

            /* create the contentvalues to update the data*/
            ContentValues cv = new ContentValues();
            cv.put(GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME,gameName);
            cv.put(GameBoardContract.GameBoardEntry.COLUMN_COMMENTS,comment);
            cv.put(GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX,nbMaxPlayer);
            cv.put(GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST,gameToTest);
            cv.put(GameBoardContract.GameBoardEntry.COLUMN_PLAYED,gamePlayed);
            cv.put(GameBoardContract.GameBoardEntry.COLUMN_DURATION,durationGame);
            String whereClause = ""+GameBoardContract.GameBoardEntry.COLUMN_ID_GAME+" = ?";
            String[] whereArgs = {Integer.toString(idGame)};

            returnUpdate = db.update(GameBoardContract.GameBoardEntry.TABLE_GAMES,
                    cv,
                    whereClause,
                    whereArgs);
            textView.append("\nNombre de lignes mises à jour "+returnUpdate);
            if(returnUpdate > 0){
                /* if we manage to update the table game now udpate the table linkGameType */
                cv.clear();
                cv.put(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT,idGameType);
                String whereClauseLGT = ""+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT+" = ?";
                String[] whereArgsLGT = {Integer.toString(idGame)};
                returnUpdate = db.update(GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_TYPE,
                        cv,
                        whereClauseLGT,
                        whereArgsLGT);
                if(returnUpdate>0){
                    textView.append("\n"+getResources().getString(R.string.modify_data_success));
                }else{
                    textView.append("\n"+getResources().getString(R.string.modify_data_error_updating_data));
                }
            }else{
                textView.append("\n"+getResources().getString(R.string.modify_data_error_updating_data));
            }
        }else{
            /* otherwise we do not change the data and tell the user*/
            textView.append("\n"+getResources().getString(R.string.modify_data_error_updating_data));
        }


    }

    /* have the id for the gameType*/
    public int findIdGameType(String type, SQLiteDatabase database){
        String[] projection = {GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE};
        String whereClause = ""+ GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE+" = ?";
        String[] whereArgs = {type};

        Cursor cursor = database.query(GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        if(cursor.getCount() >0){
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE));
        }else{
            return -1;
        }

    }
}
