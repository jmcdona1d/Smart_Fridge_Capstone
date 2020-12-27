package com.example.smartfridgeproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<FoodItem> foodList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        public ImageView foodImage;
        public TextView foodName;
        public TextView entryDate;
        public TextView expiryDate;
        public ImageView status;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            entryDate = itemView.findViewById(R.id.entryDate);
            expiryDate = itemView.findViewById(R.id.expiryDate);
            status = itemView.findViewById(R.id.status);
        }
    }

    public ExampleAdapter(ArrayList<FoodItem> mExampleList){
        foodList = mExampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        FoodItem currentItem = foodList.get(position);

        holder.foodImage.setImageResource(currentItem.getImageResource());
        holder.status.setImageResource(currentItem.getFoodStatus());
        holder.entryDate.setText(currentItem.getEntryDate());
        holder.expiryDate.setText(currentItem.getExpiryDate());
        holder.foodName.setText(currentItem.getFoodName());
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
