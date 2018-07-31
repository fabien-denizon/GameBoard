package com.example.ikit.gameboard;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ikit.gameboard.data.DbHelper;

public class DisplayGameInfo extends AppCompatActivity {
    private DbHelper dbHelper;
    private boolean dataChanged; //true if one field has been modified

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.display_game_info);
        getActionBar();
        dataChanged = false;
        /* set the onclick listener on the button */
        Button buttonModify = findViewById(R.id.display_game_info_modify_button);
        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataModified();
            }
        });
        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String gameName;
        gameName = getIntent().getExtras().getString("gameName");
        /* search all information about the game */

        /* display the information */
        TextView textView = findViewById(R.id.display_game_info_text_view);
    }

    public void checkDataModified(){

    }
}
