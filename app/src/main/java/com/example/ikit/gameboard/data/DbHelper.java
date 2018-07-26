package com.example.ikit.gameboard.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.ikit.gameboard.data.Constantes.DB_NAME;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;

    public DbHelper(Context c) {

        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        /* ***************** */
        /* define the tables */
        /* ***************** */

        /* store the type of the game, eurogame, familial, expert, ... */
        String SQL_CREATE_GAME_TYPE_TABLE = "CREATE TABLE "+GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE+" ( "
                + GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE+" TEXT NOT NULL );";

        /* store the place where we can find different game */
        String SQL_CREATE_PLACES_TABLE = "CREATE TABLE "+ GameBoardContract.GameBoardEntry.TABLE_PLACES+ " ( "
                + GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GameBoardContract.GameBoardEntry.COLUMN_NAME_PLACES+" TEXT NOT NULL );";

        /* store important information about a game, counter played will be used to sort result by the amount of time we played game */
        String SQL_CREATE_GAME_TABLE = "CREATE TABLE "+ GameBoardContract.GameBoardEntry.TABLE_GAMES+ " ( "
                + GameBoardContract.GameBoardEntry.COLUMN_ID_GAME+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GameBoardContract.GameBoardEntry.COLUMN_GAME_NAME+" TEXT NOT NULL, "
                + GameBoardContract.GameBoardEntry.COLUMN_DURATION+" INTEGER NOT NULL, "
                + GameBoardContract.GameBoardEntry.COLUMN_NB_PLAYER_MAX+" INTEGER NOT NULL, "
                + GameBoardContract.GameBoardEntry.COLUMN_PLAYED+" INTEGER NOT NULL DEFAULT 0, "
                + GameBoardContract.GameBoardEntry.COLUMN_COMMENTS+" TEXT );";

        /* make the link between the table game and place */
        String SQL_CREATE_LINK_GAME_PLACE = "CREATE TABLE "+ GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_PLACE+" ( "
                + GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGP +" INTEGER NOT NULL, "
                + GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES_REF_LGP+" INTEGER NOT NULL, "
                + "FOREIGN KEY ( "+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGP+" ) REFERENCES "+ GameBoardContract.GameBoardEntry.TABLE_GAMES+ " ( "+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME+" ), "
                + "FOREIGN KEY ( "+ GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES_REF_LGP+" ) REFERENCES "+ GameBoardContract.GameBoardEntry.TABLE_PLACES+" ( "+ GameBoardContract.GameBoardEntry.COLUMN_ID_PLACES+" ));";

        /* make the link between the table game et type */
        String SQL_CREATE_LINK_GAME_TYPE = "CREATE TABLE "+ GameBoardContract.GameBoardEntry.TABLE_LINK_GAME_TYPE+" ( "
                + GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT+" INTEGER NOT NULL, "
                + GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT+" INTEGER NOT NULL, "
                + "FOREIGN KEY ( "+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_REF_LGT+" ) REFERENCES "+ GameBoardContract.GameBoardEntry.TABLE_GAMES+" ( "+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME+" ), "
                + "FOREIGN KEY ( "+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE_REF_LGT+" ) REFERENCES "+ GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE+" ( "+ GameBoardContract.GameBoardEntry.COLUMN_ID_GAME_TYPE+" ));";

        /* we create the different tables*/
        db.execSQL(SQL_CREATE_GAME_TYPE_TABLE);
        db.execSQL(SQL_CREATE_PLACES_TABLE);
        db.execSQL(SQL_CREATE_GAME_TABLE);
        db.execSQL(SQL_CREATE_LINK_GAME_PLACE);
        db.execSQL(SQL_CREATE_LINK_GAME_TYPE);

        /* ************************************** */
        /* insert default value for the game type */
        /* ************************************** */
        ContentValues cv = new ContentValues();
        cv.put(GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE,
                GameBoardContract.GameBoardEntry.TYPE_AMBIANCE);
        db.insert(GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE, null, cv);
        cv.clear();
        cv.put(GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE,
                GameBoardContract.GameBoardEntry.TYPE_FAMILIAL);
        db.insert(GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE, null, cv);
        cv.clear();
        cv.put(GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE,
                GameBoardContract.GameBoardEntry.TYPE_FAMILIAL_PLUS);
        db.insert(GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE, null, cv);
        cv.clear();
        cv.put(GameBoardContract.GameBoardEntry.COLUMN_GAME_TYPE,
                GameBoardContract.GameBoardEntry.TYPE_EXPERT);
        db.insert(GameBoardContract.GameBoardEntry.TABLE_GAME_TYPE,null,cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}