package com.example.ikit.gameboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ImportExportActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.import_export_layout);
        getActionBar();
    }
}
