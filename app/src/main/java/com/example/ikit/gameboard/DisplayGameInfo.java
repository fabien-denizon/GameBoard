package com.example.ikit.gameboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DisplayGameInfo extends AppCompatActivity {

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        String gameName;
        gameName = getIntent().getExtras().getString("gameName");
        setContentView(R.layout.display_game_info);
        getActionBar();
        TextView textView = findViewById(R.id.display_game_info_text_view);
        textView.setText("Nom du jeu passé en paramètre : "+gameName);
    }
}
