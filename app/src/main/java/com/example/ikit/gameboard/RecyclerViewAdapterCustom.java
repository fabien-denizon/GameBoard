package com.example.ikit.gameboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapterCustom extends RecyclerView.Adapter<RecyclerViewAdapterCustom.ViewHolder>{
    private ArrayList<String> listItem;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;


    public RecyclerViewAdapterCustom(Context c, ArrayList<String> list){
        layoutInflater = LayoutInflater.from(c);
        listItem = list;
    }


    @NonNull
    @Override
    public RecyclerViewAdapterCustom.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_game_recycler_view, parent, false);
        return new RecyclerViewAdapterCustom.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterCustom.ViewHolder holder, int position) {
        String gameName = listItem.get(position);
        holder.textView.setText(gameName);
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;

        public ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.game_recycler_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null ){
                itemClickListener.onItemClick(v, getAdapterPosition());
            }

        }
    }
    public String getItem(int id){
        return listItem.get(id);
    }

    public void setClickListener(ItemClickListener clickListener){
        itemClickListener = clickListener;
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
