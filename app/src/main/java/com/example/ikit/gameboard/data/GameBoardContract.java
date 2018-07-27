package com.example.ikit.gameboard.data;

import android.provider.BaseColumns;

public class GameBoardContract {
    public static final class GameBoardEntry implements BaseColumns {

        /* name for the columns and the table gameType */
        public static final String TABLE_GAME_TYPE = "gameTypeTable";
        public static final String COLUMN_ID_GAME_TYPE ="gameType_Id";
        public static final String COLUMN_GAME_TYPE = "gameType";

        /* data to insert in the gameType table */
        public static final String TYPE_AMBIANCE = "ambiance";
        public static final String TYPE_FAMILIAL = "familial";
        public static final String TYPE_FAMILIAL_PLUS = "familial plus";
        public static final String TYPE_EXPERT = "expert";

        /* name for the columns and the table Places */
        public static final String TABLE_PLACES = "placesTable";
        public static final String COLUMN_ID_PLACES = "places_Id";
        public static final String COLUMN_NAME_PLACES = "places";

        /* name for the columns and the table linkGameType */
        public static final String TABLE_LINK_GAME_TYPE = "linkGameType";
        public static final String COLUMN_ID_GAME_TYPE_REF_LGT = "ref_gameType_Id_lgt";
        public static final String COLUMN_ID_GAME_REF_LGT = "ref_game_Id_lgt";

        /* name for the columns and the table game */
        public static final String TABLE_GAMES = "games";
        public static final String COLUMN_ID_GAME = "games_Id";
        public static final String COLUMN_GAME_NAME = "gameName";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_NB_PLAYER_MAX = "nbPlayerMax";
        public static final String COLUMN_PLAYED = "played";
        public static final String COLUMN_COMMENTS = "comments";
        public static final String COLUMN_WANT_TO_TEST="to_test";

        /* name for the columns and the table linkGamePlace */
        public static final String TABLE_LINK_GAME_PLACE = "linkGamePlace";
        public static final String COLUMN_ID_GAME_REF_LGP = "ref_game_Id_lgp";
        public static final String COLUMN_ID_PLACES_REF_LGP = "ref_place_Id_lgp";


    }
}
