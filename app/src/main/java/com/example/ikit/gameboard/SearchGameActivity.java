package com.example.ikit.gameboard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ikit.gameboard.data.Constantes;
import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchGameActivity extends AppCompatActivity {
    public boolean firstClick = false;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_game);
        getActionBar();

        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        /* set up listeners on views*/
        /* erase the text from the edit text when first clicked*/
        final EditText editText = findViewById(R.id.search_game_name_edit_text);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!firstClick){
                    editText.setText("");
                    firstClick = true;
                }
            }
        });
        Button button = findViewById(R.id.search_game_submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchActivity();
            }
        });

        /* populate the spinners */
            //nb player
            Spinner nbPlayerSpinner = findViewById(R.id.search_game_nb_player_min_spinner);
            ArrayList<String> gameNbPlayer = new ArrayList<>();
            gameNbPlayer.add(Constantes.NO_CHOICE);
            gameNbPlayer.addAll(Arrays.asList(getResources().getStringArray(R.array.new_game_nb_player_populate_spinner)));
            ArrayAdapter<String> adapterNbPlayer = new ArrayAdapter<>(this, R.layout.custom_spinner_layout,gameNbPlayer);
            adapterNbPlayer.setDropDownViewResource(R.layout.custom_spinner_layout);
            nbPlayerSpinner.setAdapter(adapterNbPlayer);

            //duration
            Spinner durationSpinner = findViewById(R.id.search_game_duration_max_spinner);
            ArrayList<String> durationGame = new ArrayList<>();
            durationGame.add(Constantes.NO_CHOICE);
            durationGame.addAll(Arrays.asList(getResources().getStringArray(R.array.new_game_duration_populate_spinner)));
            ArrayAdapter<String> adapterDuration = new ArrayAdapter<>(this, R.layout.custom_spinner_layout,durationGame);
            adapterDuration.setDropDownViewResource(R.layout.custom_spinner_layout);
            durationSpinner.setAdapter(adapterDuration);

            //type
            Spinner typeSpinner =findViewById(R.id.search_game_type_spinner);
            ArrayList<String> gameType;
            gameType = getArrayListTypeGame(db);
            ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, R.layout.custom_spinner_layout, gameType);
            adapterType.setDropDownViewResource(R.layout.custom_spinner_layout);
            typeSpinner.setAdapter(adapterType);


    }

    /* check if at least one field has been completed, then start the activity to search */
    public void startSearchActivity(){
        Intent intent = new Intent(this, ResultSearchGameActivity.class);
        TextView textViewTmp;
        Spinner spinnerTmp;
        String tmp;
        CheckBox checkBox;

        boolean oneFiledCompleted = false;

        /* field to check */
            //game name
        String gameName;
        textViewTmp = findViewById(R.id.search_game_name_edit_text);
        gameName = textViewTmp.getText().toString();
        if(! gameName.equals(getResources().getString(R.string.search_game_name_edit_text))){
            oneFiledCompleted = true;
        }
            //game type
        String gameType;
        spinnerTmp = findViewById(R.id.search_game_type_spinner);
        gameType = spinnerTmp.getSelectedItem().toString();
        if(! gameType.equals(Constantes.NO_CHOICE)){
            oneFiledCompleted = true;
        }

            //duration
        int gameDuration;
        spinnerTmp = findViewById(R.id.search_game_duration_max_spinner);
        tmp = spinnerTmp.getSelectedItem().toString();
        if(! tmp.equals(Constantes.NO_CHOICE)){
            gameDuration = Integer.parseInt(tmp.split(" ")[0]);
            oneFiledCompleted = true;
        }else{
            //if duration == 0, it means we must not take the duration during the search
            gameDuration = 0;
        }

            //min player
        int minPlayer;
        spinnerTmp = findViewById(R.id.search_game_nb_player_min_spinner);
        tmp = spinnerTmp.getSelectedItem().toString();
        if(! tmp.equals(Constantes.NO_CHOICE)){
            minPlayer = Integer.parseInt(tmp);
            oneFiledCompleted = true;
        }else{
            //if minPlayer == 0, it means we must not take the minPlayer during the search
            minPlayer = 0;
        }

            //game to test
        boolean gameToTest;
        checkBox = findViewById(R.id.search_game_want_to_test_check_box);
        gameToTest = checkBox.isChecked();
        if(gameToTest){
            oneFiledCompleted = true;
        }

            //game we did not play
        boolean newGame;
        checkBox = findViewById(R.id.search_game_new_game_check_box);
        newGame = checkBox.isChecked();
        if(newGame){
            oneFiledCompleted = true;
        }

        //if the user has completed at least 1 field, we search the games
        if(oneFiledCompleted){
            //build the Bundle before starting the intent
            Bundle extras = new Bundle();
            extras.putString("gameName",gameName);
            extras.putString("gameType",gameType);
            extras.putInt("gameDuration", gameDuration);
            extras.putInt("minPlayer", minPlayer);
            extras.putBoolean("gameToTest",gameToTest);
            extras.putBoolean("newGame", newGame);
            intent.putExtras(extras);

            startActivity(intent);
        }//otherwise we ask him to put 1 information
        else{
            Toast.makeText(this, getResources().getString(R.string.search_game_one_field_needed),Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<String> getArrayListTypeGame(SQLiteDatabase database){
        ArrayList<String> typeGame = new ArrayList<>();
        String[] projection = {
                GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE
        };

        Cursor cursor = database.query(
                GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        //put a blank in the arraylist if the user do not want to choose any type game
        typeGame.add(Constantes.NO_CHOICE);
        cursor.moveToFirst();
        do{
            typeGame.add(cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE)));
        }while(cursor.moveToNext());

        return typeGame;
    }
}
