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

import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

import java.util.ArrayList;

public class NewGameActivity extends AppCompatActivity{
    private DbHelper dbHelper;
    boolean firstClickEditTextComment = false;
    boolean firstClickEditTextGameName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);
        getActionBar();
        ArrayList<String> listPlaces;
        ArrayList<String> listGameType;
        Spinner spinner = findViewById(R.id.spinner_places_new_game);

        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        /* populate the spinners */
            //places
        listPlaces = listPlacesDataBase(db);

        /* populate the spinner with this list*/
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_layout, listPlaces);
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_layout);
        spinner.setAdapter(arrayAdapter);

            //nb player
        Spinner nbPlayerSpinner = findViewById(R.id.new_game_nb_player_spinner);
        ArrayAdapter<CharSequence> adapterNbPlayer = ArrayAdapter.createFromResource(this, R.array.new_game_nb_player_populate_spinner, R.layout.custom_spinner_layout);
        adapterNbPlayer.setDropDownViewResource(R.layout.custom_spinner_layout);
        nbPlayerSpinner.setAdapter(adapterNbPlayer);

            //duration
        Spinner durationSpinner = findViewById(R.id.new_game_duration_spinner);
        ArrayAdapter<CharSequence> adapterDuration = ArrayAdapter.createFromResource(this, R.array.new_game_duration_populate_spinner, R.layout.custom_spinner_layout);

        adapterDuration.setDropDownViewResource(R.layout.custom_spinner_layout);

        durationSpinner.setAdapter(adapterDuration);

            //game type
        Spinner typeSpinner = findViewById(R.id.new_game_type_spinner);
        listGameType = listGameType(db);
        ArrayAdapter<String> adapterGameType = new ArrayAdapter<>(this, R.layout.custom_spinner_layout, listGameType);
        adapterGameType.setDropDownViewResource(R.layout.custom_spinner_layout);
        typeSpinner.setAdapter(adapterGameType);
        /* set up listener on the edit text to erase the content when first click*/
            //comment
        final EditText editTextComment = findViewById(R.id.new_game_comments_edit_text);
        editTextComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstClickEditTextComment == false){
                    editTextComment.setText("");
                    firstClickEditTextComment=true;
                }
            }
        });

            //game name
        final EditText editTextGameName = findViewById(R.id.new_game_name_edit_text);
        editTextGameName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstClickEditTextGameName == false){
                    editTextGameName.setText("");
                    firstClickEditTextGameName = true;
                }
            }
        });

        /* lister on the button to submit */
        /* check the game name has been entered and is not empty */
        Button button = findViewById(R.id.new_game_add_game_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextGameName.getText().toString().trim();
                if(name.equals(getResources().getString(R.string.new_game_name_edit_text))
                        || name.isEmpty()){
                    Toast.makeText(NewGameActivity.this, getResources().getString(R.string.new_game_name_empty_error), Toast.LENGTH_LONG).show();
                }else {
                    addGameInDataBase();
                }
            }
        });

    }

    public ArrayList<String> listPlacesDataBase(SQLiteDatabase database){
        ArrayList<String> list = new ArrayList<>();
        /* set up the variable to interrogate the database */
        String[] projection = {
                GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES
        };

        /* retrieve all the places we have entered */
        Cursor cursor = database.query(
                GameBoardContract.GameBoardEntry.TABLE_PLACES,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        /* create the arraylist to return */
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                list.add(cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES)));
            }
            while(cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> listGameType(SQLiteDatabase database){
        ArrayList<String> list = new ArrayList<>();
        /* set up the variable to interrogate the database */
        String[] projection = {
                GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE
        };

        /* retrieve all the game type we have entered */
        Cursor cursor = database.query(
                GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        /* create the arraylist to return */
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                list.add(cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE)));
            }
            while(cursor.moveToNext());
        }
        return list;
    }

    /* put all fields in variable and start the function to add the new game in the database*/
    public void addGameInDataBase(){
        String name;
        String place;
        String comment;
        String type;
        boolean played;
        int duration;
        int maxPlayer;
        boolean gameToTest;
        String tmp;
        Spinner spinnerTmp;

        Intent intent = new Intent(this, AddNewGameActivity.class);
        EditText editText = findViewById(R.id.new_game_name_edit_text);
        name = editText.getText().toString().trim().toLowerCase();

        editText = findViewById(R.id.new_game_comments_edit_text);
        comment = editText.getText().toString().trim().toLowerCase();

        spinnerTmp = findViewById(R.id.spinner_places_new_game);
        place = spinnerTmp.getSelectedItem().toString();

        spinnerTmp = findViewById(R.id.new_game_duration_spinner);
        tmp = spinnerTmp.getSelectedItem().toString();
        tmp = tmp.split(" ")[0];
        duration = Integer.parseInt(tmp);

        spinnerTmp = findViewById(R.id.new_game_nb_player_spinner);
        tmp = spinnerTmp.getSelectedItem().toString();
        tmp = tmp.split(" ")[0];
        maxPlayer = Integer.parseInt(tmp);

        spinnerTmp = findViewById(R.id.new_game_type_spinner);
        type = spinnerTmp.getSelectedItem().toString();

        CheckBox checkBox = findViewById(R.id.new_game_played_check_box);
        played = checkBox.isChecked();

        checkBox = findViewById(R.id.new_game_to_test_check_box);
        gameToTest = checkBox.isChecked();

        /* put all the data in a Bundle*/
        Bundle extras = new Bundle();
        extras.putString("gameName", name);
        extras.putString("gameComment", comment);
        extras.putString("gamePlace", place);
        extras.putInt("gameDuration", duration);
        extras.putInt("gameNbPlayer",maxPlayer);
        extras.putBoolean("gamePlayed",played);
        extras.putString("gameType", type);
        extras.putBoolean("gameToTest",gameToTest);

        /* attach the bundle to the intent */
        intent.putExtras(extras);

        /* start activity */
        startActivity(intent);
    }
}
