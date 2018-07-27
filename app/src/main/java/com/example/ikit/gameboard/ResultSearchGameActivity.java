package com.example.ikit.gameboard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

import java.util.ArrayList;

public class ResultSearchGameActivity extends AppCompatActivity implements recyclerViewAdapterCustom.ItemClickListener{
    private DbHelper dbHelper;
    private RecyclerView recyclerView;
    private recyclerViewAdapterCustom recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.result_search_game);
        getActionBar();

        /* initiate recycler view*/
        ArrayList<String> listGame = new ArrayList<>();
        recyclerView = findViewById(R.id.result_search_game_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        /* initiate the database*/
        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_GAME,
                GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME,
                GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST,
                GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX,
                GameBoardContract.GameBoardEntry.COLUMN_DURATION,
                GameBoardContract.GameBoardEntry.COLUMN_PLAYED,
                GameBoardContract.GameBoardEntry.COLUMN_COMMENTS
        };
        String whereClause = "";
        String[] whereArgs = {};
        Cursor cursor = db.query(GameBoardContract.GameBoardEntry.TABLE_GAMES,
                projection,
                null,
                null,
                null,
                null,
                null);
        TextView textView = findViewById(R.id.search_game_temporaire);
        if(cursor.getCount() >0){
            int idGame;
            int wantToTest;
            String gameName;
            int nbPlayer;
            int duration;
            int played;
            String comments;
            cursor.moveToFirst();
            do{
                idGame = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME));
                wantToTest = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST));
                gameName = cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME));
                nbPlayer = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX));
                duration = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_DURATION));
                played = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_PLAYED));
                comments = cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_COMMENTS));
                textView.append("ID GAME :"+idGame);
                textView.append("\nGAME NAME :"+gameName);
                textView.append("\nWANT TO TEST :"+wantToTest);
                textView.append("\nPLAYER MAX :"+nbPlayer);
                textView.append("\nDURATION :"+duration);
                textView.append("\nPLAYED :"+played);
                textView.append("\nCOMMENTS :"+comments);
                listGame.add(gameName);
            }while (cursor.moveToNext());
        }else{
            textView.setText("pas de résultats");
        }
        recyclerViewAdapter = new recyclerViewAdapterCustom(this, listGame);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, DisplayGameInfo.class);
        String game = recyclerViewAdapter.getItem(position);
        Bundle extras = new Bundle();
        extras.putString("gameName", game);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
