package com.example.ikit.gameboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ikit.gameboard.data.DbHelper;

public class MainActivity extends AppCompatActivity {
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(this);

        //set up the listener on the 3 main text view
        TextView textView  = findViewById(R.id.main_search_game);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchGame();
            }
        });

        textView  = findViewById(R.id.main_new_game);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        textView  = findViewById(R.id.main_new_location);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewLocation();
            }
        });
    }

    public void startNewGame(){
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);

    }

    public void startSearchGame(){
        Intent intent = new Intent(this, SearchGameActivity.class);
        startActivity(intent);

    }

    public void startNewLocation(){
        Intent intent = new Intent(this, NewLocationActivity.class);
        startActivity(intent);

    }
}
