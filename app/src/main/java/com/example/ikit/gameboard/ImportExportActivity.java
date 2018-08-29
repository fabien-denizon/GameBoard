package com.example.ikit.gameboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ikit.gameboard.data.DbHelper;
import com.example.ikit.gameboard.data.GameBoardContract;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.ikit.gameboard.data.Constantes.NAME_FILE_DATABASE_SAVED;

public class ImportExportActivity extends AppCompatActivity {

    private DbHelper dbHelper;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.import_export_layout);
        getActionBar();
        final SQLiteDatabase database;
        dbHelper = new DbHelper(this);
        database = dbHelper.getWritableDatabase();
        Button buttonImport = findViewById(R.id.import_data_base_button);
        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    importDataBase(database);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button buttonExport = findViewById(R.id.export_data_base_button);
        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDataBase(database);
            }
        });
    }

    public void importDataBase(SQLiteDatabase db) throws IOException {

        FileInputStream fis = null;
        TextView textView = findViewById(R.id.export_database_text_view);
        try {
            fis = getApplicationContext().openFileInput(NAME_FILE_DATABASE_SAVED);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        /* get the table */
        String[] table1 = sb.toString().split("<TABLE>");
        String[] table2 = new String[table1.length];
        String[] tableName = new String[table1.length];
        System.arraycopy(table1,0,table2,0,table1.length);
        String[] listColumn = new String[table1.length];

        /* as the first element of the split is null because the tag <TABLE> is the first word of the string, we have to start to 1 instead of 0 */
        for(int i=1; i<table1.length; i++){
            tableName[i-1] = table1[i].split("<COLUMN>")[0];
            listColumn[i-1] = table2[i].split("<COLUMN>")[1];
        }

        /* for each table, get the column and sore it in the nameColumns array */
        String[] table3 = new String [listColumn.length];
        String[] nameColumns = new String[listColumn.length];
        System.arraycopy(listColumn,0,table3,0,listColumn.length);
        String[] listValues = new String[listColumn.length];
        int j;
        for(j = 0; j< table3.length-1;j++){
            nameColumns[j] = table3[j].split("<VALUES>")[0];
            listValues[j] = listColumn[j].split("<VALUES>")[1];
        }

        /* for each column, split the values to retrieve each row*/
        String[] caracterToSeparate = {"%"};
        ArrayList<String[]> listRows = new ArrayList<>();
        for( j = 0 ; j < listValues.length -1;j++){
            listRows.add(listValues[j].split("%"));
            listRows.add(caracterToSeparate);
        }

        ArrayList<String> games = new ArrayList<>();



        ArrayList<String> dataToInsert = new ArrayList<>();
        ArrayList<String> column = new ArrayList<>();
        int counterColumn;
        /* insert into the database */
        int count;
        for(count = 0; count < tableName.length-1; count++){
            dataToInsert.clear();

            /* if it's the first iteration, we do not clear the arraylist column and games */
            if(count !=0 ){
                column.clear();
                games.clear();
            }
            column.addAll(Arrays.asList(nameColumns[count].split("#")));
            //textView.append("\n table name "+tableName[count]);
            dataToInsert.add(tableName[count]);

            for(counterColumn = 0; counterColumn<column.size();counterColumn++){
                //textView.append("\n columns name "+column.get(counterColumn).toString());
                dataToInsert.add(column.get(counterColumn));
            }


                dataToInsert.addAll(Arrays.asList(listRows.get(count*2)));

            addInDataBaseArray(dataToInsert, db);

        }
    }

    /* add in the database the table describe in the arraylist in parameter */
    public boolean addInDataBaseArray(ArrayList data, SQLiteDatabase db){
        boolean success = true;
        boolean firstTimeColumn = true;
        boolean firstTimeData = true;
        ArrayList<String> listColumn = new ArrayList<>();
        ArrayList<String> listData = new ArrayList<>();
        int sizeTable = 0;
        String tableName="";
        int indexListData;
        int typeOfString = 1; //1 -> table / 2 -> columns / 3 -> data
        TextView textView = findViewById(R.id.export_database_text_view);

        textView.append("\n--- addInDataBase ---");
        for(int i = 0; i<data.size()-1;i++){
            switch (typeOfString){
                case 1:
                    /* it is the table name */
                    textView.append("\nTABLE NAME\n "+data.get(i));
                    tableName = data.get(i).toString();
                    typeOfString++;
                    break;
                case 2:
                    /*it is a column name */
                    if(firstTimeColumn){
                        listColumn.clear();
                        textView.append("\nCOLUMN");
                        firstTimeColumn=false;
                        sizeTable = 0;
                    }
                    listColumn.add(data.get(i).toString());
                    sizeTable++;
                    textView.append("\n"+data.get(i));
                    /* if there is a # in the next array we will have data to insert */
                    if(data.get(i+1).toString().indexOf("#")>0){
                        typeOfString++;
                    }
                    break;
                case 3:
                    if(firstTimeData){
                        textView.append("\nDATA");
                        firstTimeData=false;
                    }
                    listData.clear();
                    listData.addAll(Arrays.asList(data.get(i).toString().split("#")));
                    for(indexListData = 0; indexListData<listData.size();indexListData++){
                        /* each time indexListData % sizeTable == 0, we have a new row */
                        if(indexListData%sizeTable == 0){
                            textView.append("\n");
                        }
                        textView.append("*"+listData.get(indexListData).toString());
                    }
                    break;
            }
        }

        return success;
    }

    /* return true if we manage to save the database in the file */

    public boolean exportDataBase(SQLiteDatabase db){
        TextView textView = findViewById(R.id.export_database_text_view);

        boolean errorOccured = true;
        String databaseSaved = new String();

        /* read all the table in the data base and store with this pattern
        * <TABLE> Name of the table
        * <COLUMN> Name of all the column of this table separate with #
        * <VALUES> All the values of the column in the order separate with # and separate each group by $
        * and repeat for each table */

        /* search and all the data in the table places */
        databaseSaved+=
                "<TABLE>"+ GameBoardContract.GameBoardEntry.TABLE_PLACES
                +"<COLUMN>"+ GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES+"#"
                + GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES;
        String[] projectionPlaces = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES,
                GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES
        };

        Cursor cursor = db.query(
                GameBoardContract.GameBoardEntry.TABLE_PLACES,
                projectionPlaces,
                null,
                null,
                null,
                null,
                null
        );
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            databaseSaved+="<VALUES>";
            do{
                databaseSaved+=cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES));
                databaseSaved+="#";
                databaseSaved+=cursor.getString(cursor.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES));
                databaseSaved+="%";
            }while(cursor.moveToNext());
            cursor.close();
        }else{
            errorOccured = false;
        }


        /* search and all the data in the table places */
        databaseSaved+=
                "<TABLE>"+ GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE
                        +"<COLUMN>"+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE;
        String[] projectionType = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE,
                GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE
        };

        Cursor cursorType = db.query(
                GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE,
                projectionType,
                null,
                null,
                null,
                null,
                null
        );
        if(cursorType.getCount()>0){
            cursorType.moveToFirst();
            databaseSaved+="<VALUES>";
            do{
                databaseSaved+=cursorType.getString(cursorType.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE));
                databaseSaved+="#";
                databaseSaved+=cursorType.getString(cursorType.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE));
                databaseSaved+="%";
            }while(cursorType.moveToNext());
            cursorType.close();
        }else{
            errorOccured = false;
        }


        /* search and all the data in the table places */
        databaseSaved+=
                "<TABLE>"+ GameBoardContract.GameBoardEntry.TABLE_GAMES
                        +"<COLUMN>"+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_DURATION+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_PLAYED+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_COMMENTS;
        String[] projectionGame = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_GAME,
                GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME,
                GameBoardContract.GameBoardEntry.COLUMN_DURATION,
                GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX,
                GameBoardContract.GameBoardEntry.COLUMN_PLAYED,
                GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST,
                GameBoardContract.GameBoardEntry.COLUMN_COMMENTS
        };

        Cursor cursorGame = db.query(
                GameBoardContract.GameBoardEntry.TABLE_GAMES,
                projectionGame,
                null,
                null,
                null,
                null,
                null
        );

        if(cursorGame.getCount()>0){
            cursorGame.moveToFirst();
            databaseSaved+="<VALUES>";
            do{
                databaseSaved+=cursorGame.getString(cursorGame.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME));
                databaseSaved+="#";
                databaseSaved+=cursorGame.getString(cursorGame.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME));
                databaseSaved+="#";
                databaseSaved+=cursorGame.getString(cursorGame.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_DURATION));
                databaseSaved+="#";
                databaseSaved+=cursorGame.getString(cursorGame.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX));
                databaseSaved+="#";
                databaseSaved+=cursorGame.getString(cursorGame.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_PLAYED));
                databaseSaved+="#";
                databaseSaved+=cursorGame.getString(cursorGame.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_WANT_TO_TEST));
                databaseSaved+="#";
                databaseSaved+=cursorGame.getString(cursorGame.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_COMMENTS));
                databaseSaved+="%";
            }while(cursorGame.moveToNext());
            cursorGame.close();
        }else {
            errorOccured = false;
        }

        /* search and all the data in the table LGT */
        databaseSaved+=
                "<TABLE>"+ GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_TYPE
                        +"<COLUMN>"+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT;
        String[] projectionLGT = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT,
                GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT
        };

        Cursor cursorLGT = db.query(
                GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_TYPE,
                projectionLGT,
                null,
                null,
                null,
                null,
                null
        );

        if (cursorLGT.getCount()>0){
            cursorLGT.moveToFirst();
            databaseSaved+="<VALUES>";
            do{
                databaseSaved+=cursorLGT.getString(cursorLGT.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT));
                databaseSaved+="#";
                databaseSaved+=cursorLGT.getString(cursorLGT.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT));
                databaseSaved+="%";
            }while(cursorLGT.moveToNext());
            cursorLGT.close();
        }else{
            errorOccured = false;
        }

        /* search and all the data in the table LGP */
        databaseSaved+=
                "<TABLE>"+ GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_PLACE
                        +"<COLUMN>"+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGP+"#"
                        + GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES_REF_LGP;
        String[] projectionLGP = {
                GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGP,
                GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES_REF_LGP
        };

        Cursor cursorLGP = db.query(
                GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_PLACE,
                projectionLGP,
                null,
                null,
                null,
                null,
                null
        );

        if (cursorLGP.getCount()>0){
            cursorLGP.moveToFirst();
            databaseSaved+="<VALUES>";
            do{
                databaseSaved+=cursorLGP.getString(cursorLGP.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGP));
                databaseSaved+="#";
                databaseSaved+=cursorLGP.getString(cursorLGP.getColumnIndex(GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES_REF_LGP));
                databaseSaved+="%";
            }while(cursorLGP.moveToNext());
            cursorLGP.close();
        }else {
            errorOccured = false;
        }

        /* if errorOccured is still true, then we can go on, and save the string in a file */

        textView.setText(databaseSaved);

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(NAME_FILE_DATABASE_SAVED, Context.MODE_PRIVATE);
            outputStream.write(databaseSaved.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        textView.append("\nle fichier "+NAME_FILE_DATABASE_SAVED+" a été sauvegardé à l'emplacement "+getFilesDir().getAbsolutePath()+"/"+NAME_FILE_DATABASE_SAVED);
        return errorOccured;
    }
}
