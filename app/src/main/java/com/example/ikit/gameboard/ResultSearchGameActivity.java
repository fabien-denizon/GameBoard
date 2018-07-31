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

import com.example.ikit.gameboard.data.Constantes;
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
        TextView textViewInformation = findViewById(R.id.search_game_informations_text_view);
        /* get the data from the bundle */
        String name;
        String type;
        int maxDuration;
        int minPlayer;
        boolean toTest;
        int gameToTest;
        name = getIntent().getExtras().getString("gameName");
        type = getIntent().getExtras().getString("gameType");
        maxDuration = getIntent().getExtras().getInt("gameDuration");
        minPlayer = getIntent().getExtras().getInt("minPlayer");
        toTest = getIntent().getExtras().getBoolean("gameToTest");
        /* convert the boolean into int */
        if(toTest){
            gameToTest = 1;
        }else{
            gameToTest = 0;
        }

        /* initiate recycler view*/
        ArrayList<String> listGameName = new ArrayList<>();
        recyclerView = findViewById(R.id.result_search_game_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        /* initiate the database*/
        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        /* ************************************* */
        /* search the games with gameType wanted */
        /* ************************************* */

        /* if no game type was entered replace it with * */
        String whereClauseGetIdType = ""+ GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE+" = ?";
        String[] whereArgsGetIdType = {type};
        if(type.equals(Constantes.NO_CHOICE)){
            whereClauseGetIdType = null;
            whereArgsGetIdType = null;
        }
        String[] projectionGetIdType = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE
        };

        Cursor cursorGetIdType = db.query(
                GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE,
                projectionGetIdType,
                whereClauseGetIdType,
                whereArgsGetIdType,
                null,
                null,
                null,
                null
        );
        String idType;
        if(cursorGetIdType.getCount()>0){
            cursorGetIdType.moveToFirst();
            idType = cursorGetIdType.getString(cursorGetIdType.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE));
            /* **************************************** */
            /* search all the game with the type idType */
            /* **************************************** */
            String[] projectionGetGameWithType = {
                    GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT
            };
            String whereClauseGetGameWithType = ""+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT+" = ?";
            String[] whereArgsGetGameWithType = {idType};

            Cursor cursorGetGameWithType = db.query(
                    GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_TYPE,
                    projectionGetGameWithType,
                    whereClauseGetGameWithType,
                    whereArgsGetGameWithType,
                    null,
                    null,
                    null,
                    null
            );
            ArrayList<String> listGameWithType = new ArrayList<>();
            if(cursorGetGameWithType.getCount()>0){
                cursorGetGameWithType.moveToFirst();
                do{
                    listGameWithType.add(cursorGetGameWithType.getString(cursorGetGameWithType.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT)));
                }while (cursorGetGameWithType.moveToNext());
                /* ************************************************* */
                /* search all the game with all the other parameters */
                /* ************************************************* */
                String[] projection = {
                        GameBoardContract.GameBoardEntry.COLUMN_ID_GAME,
                        GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME,
                };
                String whereClause = "";
                ArrayList<String> whereArgsList = new ArrayList<>();
                /* build the whereClause and whereArgs*/
                boolean andNeeded = false;
                if(! name.equals(getResources().getString(R.string.search_game_name_edit_text))){
                    whereClause += ""+ GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME+" = ?";
                    andNeeded = true;
                    whereArgsList.add(name);
                }

                if(minPlayer > 0){
                    if(andNeeded){
                        whereClause += " AND ";
                    }
                    whereClause += " "+ GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX+" >= ?";
                    andNeeded = true;
                    whereArgsList.add(Integer.toString(minPlayer));
                }

                if(gameToTest == 1){
                    if(andNeeded){
                        whereClause += " AND ";
                    }
                    whereClause += GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST+" = ?";
                    andNeeded = true;
                    whereArgsList.add(Integer.toString(gameToTest));
                }

                if(maxDuration > 0){
                    if(andNeeded){
                        whereClause += " AND ";
                    }
                    whereClause += GameBoardContract.GameBoardEntry.COLUMN_DURATION+" <= ?";
                    whereArgsList.add(Integer.toString(maxDuration));
                }
                /* convert the whereArgsList into String[]*/
                String[] whereArgs = new String[whereArgsList.size()];
                whereArgs = whereArgsList.toArray(whereArgs);
                Cursor cursor = db.query(GameBoardContract.GameBoardEntry.TABLE_GAMES,
                        projection,
                        whereClause,
                        whereArgs,
                        null,
                        null,
                        null);
                ArrayList<String> listId = new ArrayList<>();
                if(cursor.getCount() >0){
                    int idGame;
                    String gameName;
                    cursor.moveToFirst();
                    do{
                        idGame = cursor.getInt(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME));
                        gameName = cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME));
                        textViewInformation.append("\nID GAME :"+idGame);
                        textViewInformation.append("\nGAME NAME :"+gameName);
                        listId.add(Integer.toString(idGame));
                        listGameName.add(gameName);
                    }while (cursor.moveToNext());
                    /* compare the listGameWithType with listId and create a new list with the matching listGameName*/
                    ArrayList<String> finalList = new ArrayList<>();
                    for(String s1 : listId){
                        for(String s2 : listGameWithType){
                            if(s1.equals(s2)){
                                /* get the name of the game and put it in the finalList, corresponding to name of the game which is in the same index than the index of the listId*/
                                finalList.add(listGameName.get(listId.indexOf(s1)));
                            }
                        }
                    }
                    recyclerViewAdapter = new recyclerViewAdapterCustom(this, listGameName);
                    recyclerViewAdapter.setClickListener(this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(dividerItemDecoration);
                }else{
                    textViewInformation.append("\n"+getResources().getString(R.string.search_game_no_result_with_other_type));
                }
                cursor.close();
            }
            else{
                textViewInformation.append("\n"+getResources().getString(R.string.search_game_no_result_with_other_type));
            }
            cursorGetGameWithType.close();
        }else{
            textViewInformation.append("\n"+getResources().getString(R.string.search_game_no_result_with_type));
        }
        cursorGetIdType.close();;
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
