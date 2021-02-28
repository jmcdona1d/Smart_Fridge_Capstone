package com.example.smartfridgeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.*;
import java.text.SimpleDateFormat;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements InsertItem.InsertItemListener {
    //foodList is the list of food that forms the recyclerview
    private ArrayList<FoodItem> foodList;

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonInsert;

    private boolean canDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFoodList();
        buildRecyclerView();

        buttonInsert = findViewById(R.id.button_insert);

        canDelete=false;

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("", Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), false);
                //InsertItem editTextInsert = new InsertItem();
                //String givenName = editTextInsert.getText().toString();
                //insertItem(givenName, foodList.size());
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    public void openDialog(String name, Date insertion, Date expiry, boolean deleteOld) {
        InsertItem insertItem = new InsertItem();
        Bundle args = new Bundle();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        args.putString("name", name);
        args.putString("insertion", formatter.format(insertion));
        args.putString("expiry", formatter.format(expiry));
        args.putBoolean("deleteOld", deleteOld);
        insertItem.setArguments(args);

        insertItem.show(getSupportFragmentManager(), "New Food Item");

    }

    public void insertItem (String name, Date insertionDate, Date expiryDate, int position){
        //SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        //String strInsertionDate = formatter.format(insertionDate);
        //String strExpiryDate = formatter.format(expiryDate);
        foodList.add(position, new FoodItem(R.drawable.eggplant, name, insertionDate, expiryDate));
        mAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position){
        foodList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text){
        foodList.get(position).changeText1(text);
        mAdapter.notifyItemChanged(position);
    }

    public void createFoodList() {
        foodList = new ArrayList<>();
        foodList.add(new FoodItem(R.drawable.turnips, "Turnips", new Date(121, 1, 27), new Date(121, 3, 27)));
        foodList.add(new FoodItem(R.drawable.eggplant, "Eggplant", new Date(121, 1, 17), new Date(121, 3, 7)));
        foodList.add(new FoodItem(R.drawable.shrimp, "Shrimp", new Date(121, 2, 27), new Date(121, 2, 27)));
    }
    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(foodList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                //changeItem(position, "Clicked");
                System.out.println("The food name is "+foodList.get(position).getFoodName());
                openDialog(foodList.get(position).getFoodName(), foodList.get(position).getEntryDate(), foodList.get(position).getExpiryDate(), true);
                removeItem(position);
            }
        });
    }

    @Override
    public void applyChanges(String food, Date insertionDate, Date expiryDate) {
        insertItem(food, insertionDate, expiryDate, foodList.size());
    }
}