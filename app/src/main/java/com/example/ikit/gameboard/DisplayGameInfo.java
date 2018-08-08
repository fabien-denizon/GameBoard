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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

import java.util.ArrayList;

public class DisplayGameInfo extends AppCompatActivity {
    private DbHelper dbHelper;
    private String gameName;
    private String comments;
    private int nbPlayer;
    private int duration;
    private boolean played;
    private boolean wantToTest;
    private int idGame;
    private String gameType;
    private boolean allowModification = true;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.display_game_info);
        getActionBar();
        gameName = getIntent().getExtras().getString("gameName");

        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /* search all information about the game */
        getDataGame(db);

        /* disable fields  */
        allowModification();

        /* set up listener to allow to change only with long click */
            //the name of the game
        EditText editTextGameName = findViewById(R.id.display_game_info_game_name_edit_text);
        editTextGameName.setText(gameName);
            //the comment of the game
        EditText editTextComment = findViewById(R.id.display_game_info_comments_edit_text);
        editTextComment.setText(comments);
        /* initialize all other field */
            //game type  textViewGameType.setText(getResources().getString(R.string.display_info_type_game)+gameType);
        setUpSpinnerGameType(db);

            //duration
       setUpSpinnerGameDuration();

            //max player
        setUpSpinnerMaxPlayer();

            //ever played
        CheckBox checkBoxPlayed = findViewById(R.id.display_game_info_game_played_check_box);
        checkBoxPlayed.setChecked(played);

            //want to test
        CheckBox checkBoxToTest = findViewById(R.id.display_game_info_want_to_test_check_box);
        checkBoxToTest.setChecked(wantToTest);

            //allow modification or not when the checkbox is check
        CheckBox checkBoxAllowModification = findViewById(R.id.display_game_info_allow_modification_check_box);
        checkBoxAllowModification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                allowModification();
            }
        });

        /* set the onclick listener on the buttons */
        Button buttonModify = findViewById(R.id.display_game_info_modify_button);
        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startModifyActivity();
            }
        });

        Button buttonDelete = findViewById(R.id.display_game_info_delete_button);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGame();
            }
        });

        gameName = getIntent().getExtras().getString("gameName");

    }


    /* put all data about the game in the variable */
    public boolean getDataGame(SQLiteDatabase database){
        TextView textView = findViewById(R.id.display_game_info_text_view);
        String[] projection = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_GAME,
                GameBoardContract.GameBoardEntry.COLUMN_COMMENTS,
                GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX,
                GameBoardContract.GameBoardEntry.COLUMN_DURATION,
                GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST,
                GameBoardContract.GameBoardEntry.COLUMN_PLAYED
        };
        String whereClause = ""+ GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME+" = ? ";
        String[] whereArgs = {gameName};
        Cursor cursor = database.query(
                GameBoardContract.GameBoardEntry.TABLE_GAMES,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        if(cursor.getCount() > 0 ){
            cursor.moveToFirst();
            idGame = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME));
            comments = cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_COMMENTS));
            nbPlayer = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX));
            duration = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_DURATION));
            played = (cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_PLAYED))==1);
            wantToTest = (cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST))==1);

            cursor.close();
            /* search now the game type */
                String[] projectionGameType = {
                        GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT
                };
                String whereClauseGameType = ""+GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT+" = ?";
                String[] whereArgsGameType = {Integer.toString(idGame)};
                Cursor cursorGameType = database.query(
                        GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_TYPE,
                        projectionGameType,
                        whereClauseGameType,
                        whereArgsGameType,
                        null,
                        null,
                        null
                );
                if(cursorGameType.getCount() > 0 ){
                    cursorGameType.moveToFirst();
                    int refGameType;
                    refGameType = cursorGameType.getInt(cursorGameType.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT));
                    cursorGameType.close();
                    // now get the type of the game
                    String[] projectionType = {
                            GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE
                    };
                    String whereClauseType = ""+GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE+" = ?";
                    String[] whereArgsType = {Integer.toString(refGameType)};
                    Cursor cursorType = database.query(
                            GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE,
                            projectionType,
                            whereClauseType,
                            whereArgsType,
                            null,
                            null,
                            null
                    );
                    if(cursorType.getCount()>0){
                        cursorType.moveToFirst();
                        gameType = cursorType.getString(cursorType.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE));
                        cursorType.close();
                    }
                    else{
                        textView.append("\n"+R.string.display_info_error_search_name_type);
                        return false;
                    }
                }else{
                    textView.append("\n"+R.string.display_info_error_search_game_type_lgt);
                    return false;
                }
            return true;
        }else{
            //if we do not have result, tell the user
            textView.setText(""+R.string.display_info_error_search_database);
            return false;
        }
    }

    public void deleteGame(){
        Intent intent = new Intent(this, deleteGameActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("gameId",idGame);
        intent.putExtras(extras);
        startActivity(intent);
    }

    /* build a list with the duration of the game as first item */
    public void setUpSpinnerGameDuration(){
        String substring;
        ArrayList<String> arrayList = new ArrayList<>();
        String[] strings = getResources().getStringArray(R.array.search_game_duration_spinner);
        for(int i = 0; i< strings.length; i++){
            substring = strings[i].split(" ")[0];
            if(substring.equals(Integer.toString(duration))){
                arrayList.add(0,strings[i]);
            }else{
                arrayList.add(strings[i]);
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,arrayList);
        Spinner spinner= findViewById(R.id.display_game_info_duration_spinner);
        spinner.setAdapter(arrayAdapter);
    }

    /* build a list with the game type of the game as first item */
    public void setUpSpinnerGameType(SQLiteDatabase database){
        ArrayList<String> listType = listGameType(database);
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0; i< listType.size(); i++){
            if(listType.get(i).equals(gameType)){
                arrayList.add(0,listType.get(i));
            }else{
                arrayList.add(listType.get(i));
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,arrayList);
        Spinner spinner= findViewById(R.id.display_game_info_game_type_spinner);
        spinner.setAdapter(arrayAdapter);
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

    /* build a list with nb of player as first item */
    public void setUpSpinnerMaxPlayer(){
        String substring;
        ArrayList<String> arrayList = new ArrayList<>();
        String[] strings = getResources().getStringArray(R.array.new_game_nb_player_populate_spinner);
        for(int i = 0; i< strings.length; i++){
            substring = strings[i].split(" ")[0];
            if(substring.equals(Integer.toString(nbPlayer))){
                arrayList.add(0,strings[i]);
            }else{
                arrayList.add(strings[i]);
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,arrayList);
        Spinner spinner= findViewById(R.id.display_game_info_nb_player_spinner);
        spinner.setAdapter(arrayAdapter);
    }

    //make all the field available/disable to modifications
    public void allowModification(){
        EditText editTextName = findViewById(R.id.display_game_info_game_name_edit_text);
        Spinner spinnerGameType = findViewById(R.id.display_game_info_game_type_spinner);
        Spinner spinnerDuration = findViewById(R.id.display_game_info_duration_spinner);
        Spinner spinnerMaxPlayer = findViewById(R.id.display_game_info_nb_player_spinner);
        EditText editTextComment = findViewById(R.id.display_game_info_comments_edit_text);
        CheckBox checkBoxGamePlayed = findViewById(R.id.display_game_info_game_played_check_box);
        CheckBox checkBoxWantToTest = findViewById(R.id.display_game_info_want_to_test_check_box);

        if(allowModification){
            editTextName.setCursorVisible(false);
            editTextName.setFocusable(false);
            editTextName.setFocusableInTouchMode(false);

            editTextComment.setCursorVisible(false);
            editTextComment.setFocusable(false);
            editTextComment.setFocusableInTouchMode(false);

            spinnerGameType.setEnabled(false);

            spinnerDuration.setEnabled(false);

            spinnerMaxPlayer.setEnabled(false);

            checkBoxGamePlayed.setEnabled(false);

            checkBoxWantToTest.setEnabled(false);
            allowModification = false;
        }else{
            editTextName.setCursorVisible(true);
            editTextName.setFocusable(true);
            editTextName.setFocusableInTouchMode(true);

            editTextComment.setCursorVisible(true);
            editTextComment.setFocusable(true);
            editTextComment.setFocusableInTouchMode(true);

            spinnerGameType.setEnabled(true);

            spinnerDuration.setEnabled(true);

            spinnerMaxPlayer.setEnabled(true);

            checkBoxGamePlayed.setEnabled(true);

            checkBoxWantToTest.setEnabled(true);
            allowModification = true;
        }
    }

    public void startModifyActivity(){
        Intent intent = new Intent(this, ModifyGameActivity.class);
        Bundle extras = new Bundle();

        EditText editTextTmp = findViewById(R.id.display_game_info_comments_edit_text);
        String tmp = editTextTmp.getText().toString().trim();
        extras.putString("comment",tmp);

        editTextTmp = findViewById(R.id.display_game_info_game_name_edit_text);
        tmp = editTextTmp.getText().toString().trim();
        extras.putString("name",tmp);

        extras.putInt("id",idGame);

        Spinner spinnerTmp = findViewById(R.id.display_game_info_duration_spinner);
        tmp = spinnerTmp.getSelectedItem().toString();
        extras.putString("duration",tmp);

        spinnerTmp = findViewById(R.id.display_game_info_game_type_spinner);
        tmp = spinnerTmp.getSelectedItem().toString();
        extras.putString("type",tmp);

        spinnerTmp = findViewById(R.id.display_game_info_nb_player_spinner);
        tmp = spinnerTmp.getSelectedItem().toString();
        extras.putString("maxPlayer",tmp);

        boolean booleanTmp;
        CheckBox checkBoxTmp = findViewById(R.id.display_game_info_game_played_check_box);
        booleanTmp = checkBoxTmp.isChecked();
        extras.putBoolean("played",booleanTmp);

        checkBoxTmp = findViewById(R.id.display_game_info_want_to_test_check_box);
        booleanTmp = checkBoxTmp.isChecked();
        extras.putBoolean("toTest",booleanTmp);

        intent.putExtras(extras);
        startActivity(intent);
    }
}
