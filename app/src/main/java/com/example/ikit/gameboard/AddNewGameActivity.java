package com.example.ikit.gameboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AddNewGameActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        getActionBar();
        setContentView(R.layout.add_new_game_data_base);
    }
}
