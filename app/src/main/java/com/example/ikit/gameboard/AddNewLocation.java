package com.example.ikit.gameboard;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

public class AddNewLocation extends AppCompatActivity {
    private DbHelper dbHelper;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        boolean alreadyEntered;
        SQLiteDatabase db;
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.new_location);
        getActionBar();

        TextView textView = findViewById(R.id.new_location_display_information_text_view);
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        /* retrieve the name of the place */
        String PlaceToAdd;
        PlaceToAdd = getIntent().getExtras().getString("NamePlace").toString().trim();
        /* delete the case to prevent error in the future */
        PlaceToAdd = PlaceToAdd.toLowerCase();

        /* check if the place is already in the database before trying to insert it*/
        alreadyEntered = placeAlreadyExist(PlaceToAdd, db);
        if(alreadyEntered){
            textView.setText(getResources().getString(R.string.place_already_exist));
        }else{
            addNewPlaceInDataBase(PlaceToAdd, db);
        }

    }

    /* return true if the place is already in the database*/
    public boolean placeAlreadyExist(String place, SQLiteDatabase db){
        /* search in the data base an entry with the name place */
        String[] projection = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES,
                GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES
        };

        String whereClause = ""+ GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES+" = ?";
        String[] whereArgs = {place};

        Cursor cursor = db.query(GameBoardContract.GameBoardEntry.TABLE_PLACES,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null);

        /* if the cursor have more than 1 entry, we already have a place with that name */
        return (cursor.getCount() != 0);

    }

    public void addNewPlaceInDataBase(String place, SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        long returnReq;

        TextView textView = findViewById(R.id.new_location_display_information_text_view);
        contentValues.put(GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES, place);
        returnReq= db.insert(GameBoardContract.GameBoardEntry.TABLE_PLACES,
        null,
        contentValues);
        /* if returnReq if != than -1, we succeed to insert the place in the database*/
        if(returnReq != -1){
            textView.setText(getResources().getString(R.string.add_new_place_success));
        }
        else{
            textView.setText(getResources().getString(R.string.add_new_place_fail));
        }
    }
}
