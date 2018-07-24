package com.example.ikit.gameboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NewLocationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    final EditText editText;
        View view = inflater.inflate(R.layout.new_location_fragment, container, false);
        editText = view.findViewById(R.id.new_location_edit_text);

        /* when we first click on the editText, clear it*/
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = getResources().getString(R.string.enter_new_location_edit_text);
                if(editText.getText().toString().equals(string)){
                    editText.setText("");
                }
            }
        });


        /* set the listener on the button to add the new location */
        Button button = view.findViewById(R.id.submit_new_location_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddLocation(editText.getText().toString().trim());
            }
        });
        return  view;
    }

    public void startAddLocation(String s){
        Intent intent = new Intent(getActivity(), AddNewLocation.class);
        Bundle extras = new Bundle();
        extras.putString("NamePlace", s);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
