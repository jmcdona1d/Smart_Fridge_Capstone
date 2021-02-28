package com.example.smartfridgeproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<FoodItem> foodList;
    private OnItemClickListener mListener;
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        public ImageView foodImage;
        public TextView foodName;
        public TextView entryDate;
        public TextView expiryDate;
        public ImageView status;
        public ExampleViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            entryDate = itemView.findViewById(R.id.entryDate);
            expiryDate = itemView.findViewById(R.id.expiryDate);
            status = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public ExampleAdapter(ArrayList<FoodItem> mExampleList){
        foodList = mExampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
            return new ExampleViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        FoodItem currentItem = foodList.get(position);
        holder.foodImage.setImageResource(currentItem.getImageResource());
        holder.status.setImageResource(currentItem.getFoodStatus());
        holder.entryDate.setText(formatter.format(currentItem.getEntryDate()));
        holder.expiryDate.setText(formatter.format(currentItem.getExpiryDate()));
        holder.foodName.setText(currentItem.getFoodName());
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public FoodItem getFoodItemAt(int position){
        return foodList.get(position);
    }
}
