package com.example.ikit.gameboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ikit.gameboard.data.DbHelper;

public class SearchGameActivity extends AppCompatActivity {
    public boolean firstClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_game);
        getActionBar();

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
            ArrayAdapter<CharSequence> adapterNbPlayer = ArrayAdapter.createFromResource(this, R.array.search_game_nb_min_player_spinner, android.R.layout.simple_spinner_item);
            adapterNbPlayer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            nbPlayerSpinner.setAdapter(adapterNbPlayer);

            //duration
            Spinner durationSpinner = findViewById(R.id.search_game_duration_max_spinner);
            ArrayAdapter<CharSequence> adapterDuration = ArrayAdapter.createFromResource(this, R.array.search_game_duration_spinner, android.R.layout.simple_spinner_item);
            adapterNbPlayer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            durationSpinner.setAdapter(adapterDuration);

    }

    /* check if at least one field has been completed, then start the activity to search */
    public void startSearchActivity(){
        Intent intent = new Intent(this, ResultSearchGameActivity.class);
        boolean oneFiledCompleted = false;
        /* field to check */
            //game name
            //game type
            //duration
            //min player
        startActivity(intent);
    }
}
