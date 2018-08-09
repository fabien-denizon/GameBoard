package com.example.ikit.gameboard;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

public class DeleteGameActivity extends AppCompatActivity {

    private DbHelper dbHelper;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.delete_game_layout);
        getActionBar();
        dbHelper = new DbHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        String gameName;
        final int idGame;
        TextView textViewInformation = findViewById(R.id.delete_game_ask_confirmation_text_view);
        /*retrieve data from the bundle */
        gameName = getIntent().getExtras().getString("gameName");
        idGame = getIntent().getExtras().getInt("gameId");
        /* add the name of the game to the text view to ask confirmation */
        textViewInformation.append(" "+gameName);

        /* set up listener*/
        Button buttonYes = findViewById(R.id.delete_game_yes_button);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGame(idGame, db);
            }
        });

        Button buttonNo = findViewById(R.id.delete_game_no_button);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    public void deleteGame(int id, SQLiteDatabase database){
        int returnReq;
        boolean error = false;
        TextView textView = findViewById(R.id.delete_game_display_information_text_view);

        /* delete the game in the game table */
        String whereClause = ""+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME+" = ?";
        String[] whereArgs = {Integer.toString(id)};
        returnReq = database.delete(GameBoardContract.GameBoardEntry.TABLE_GAMES,
                whereClause,
                whereArgs);
        if(returnReq <1 ){
            /* tell the user an error has occured */
            error = true;
        }else{
            /* delete the game in the LGT */
            whereClause = ""+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT+" = ?";
            returnReq = database.delete(GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_TYPE,
                    whereClause,
                    whereArgs);
            if(returnReq <1 ){
                /* tell the user an error has occured */
                error = true;

            }else{
                /* delete the game in the LGP */
                whereClause = ""+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGP+" = ?";
                returnReq = database.delete(GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_PLACE,
                        whereClause,
                        whereArgs);
                if(returnReq <1 ){
                    /* tell the user an error has occured */
                    error = true;
                }
            }
        }

        if(error){
            textView.append(""+getResources().getString(R.string.delete_game_error_delete_game));

        }else{
            /*tell the user it has been a sucess */
            TextView textViewInformation = findViewById(R.id.delete_game_ask_confirmation_text_view);
            textViewInformation.setText("");
            textView.setText(""+getResources().getString(R.string.delete_game_success_delete_game));
            /* and hide the 2 buttons */
            Button buttonYes = findViewById(R.id.delete_game_yes_button);
            buttonYes.setVisibility(View.GONE);
            Button buttonNo = findViewById(R.id.delete_game_no_button);
            buttonNo.setVisibility(View.GONE);
        }
    }

    /* if the user clicked no, send hmi to the main page*/
    public void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
